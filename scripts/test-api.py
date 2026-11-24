"""Smoke-test the deployed API with Python's standard library."""
import argparse
import base64
import json
import os
from urllib.error import HTTPError
from urllib.request import HTTPRedirectHandler, Request, build_opener


class RejectRedirects(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def request(base_url, path, body=None, auth=None, content_type="application/json"):
    headers = {"Accept": "application/json"}
    if auth is not None:
        headers["Authorization"] = auth
    if body is not None:
        headers["Content-Type"] = content_type
    req = Request(f"{base_url.rstrip('/')}/{path}", body, headers)
    try:
        response = build_opener(RejectRedirects).open(req, timeout=35)
    except HTTPError as error:
        response = error
    with response:
        if response.headers.get_content_type() != "application/json":
            raise AssertionError(f"{path}: expected JSON, got HTTP {response.status}")
        return response.status, json.load(response), response.headers


def basic(user, password):
    token = base64.b64encode(f"{user}:{password}".encode()).decode("ascii")
    return f"Basic {token}"


def expect_error(response, status, code):
    actual_status, body, headers = response
    if (actual_status, body.get("error", {}).get("code")) != (status, code):
        raise AssertionError(f"Expected {status} {code}, got HTTP {actual_status} {body!r}")
    if status == 401 and not headers.get("WWW-Authenticate", "").startswith("Basic "):
        raise AssertionError("Missing Basic authentication challenge")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--api", default="http://127.0.0.1:8080/darija/api")
    parser.add_argument("--live", action="store_true", help="Expect a real translation with the server's Gemini key")
    args = parser.parse_args()
    user = os.environ.get("DARIJA_API_USERNAME")
    password = os.environ.get("DARIJA_API_PASSWORD")
    if not user or not password:
        parser.error("Set DARIJA_API_USERNAME and DARIJA_API_PASSWORD to match the server.")
    auth = basic(user, password)

    valid = json.dumps({"text": "Good morning.\nSee you tomorrow!", "sourceLanguage": "English"}).encode()
    status, health, _ = request(args.api, "health")
    if (status, health) != (200, {"status": "ok"}):
        raise AssertionError(f"Unexpected health response: {status} {health!r}")
    print("PASS public health")

    for credentials in (None, basic(user + "-wrong", password), "Basic ***"):
        expect_error(request(args.api, "translate", valid, credentials), 401, "unauthorized")
    expect_error(request(args.api, "translate", b"{}", None), 401, "unauthorized")
    print("PASS missing, incorrect and malformed credentials are rejected before validation")

    for invalid in (b"{}", b'{"text":', b'{"text":" "}'):
        expect_error(request(args.api, "translate", invalid, auth), 400, "invalid_request")
    expect_error(request(args.api, "translate", valid, auth, "text/plain"), 415, "unsupported_media_type")
    expect_error(request(args.api, "translate", b" " * 65_537, auth), 413, "request_too_large")
    print("PASS correct credentials reach request validation")

    result = request(args.api, "translate", valid, auth)
    if args.live:
        status, body, _ = result
        translation = body.get("translation")
        if status != 200 or not isinstance(translation, str) or not translation.strip():
            raise AssertionError(f"Expected a live translation, got HTTP {status} {body!r}")
        print(f"PASS Gemini returned a translation\n{translation}")
    else:
        expect_error(result, 503, "provider_unavailable")
        print("PASS authenticated valid input reaches the provider configuration check")


if __name__ == "__main__":
    main()
