package ma.darija.server;

import jakarta.json.JsonObject;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/errors/authentication")
public class AuthenticationErrorServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getDispatcherType() != DispatcherType.ERROR) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int status = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        JsonObject body = status == HttpServletResponse.SC_UNAUTHORIZED
                ? ApiFailure.body("unauthorized", "Enter valid API credentials.")
                : ApiFailure.body("auth_unavailable", "Service authentication is not configured.");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(body.toString());
    }
}
