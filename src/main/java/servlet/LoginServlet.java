package servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import javax.sql.DataSource;

import entity.User;
import org.jasypt.util.password.StrongPasswordEncryptor;
import recaptcha.RecaptchaVerifyUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is declared as LoginServlet in web annotation,
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //response.setContentType("application/json");
        //PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
       try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
        	JsonObject responseJsonObject = new JsonObject();
        	responseJsonObject.addProperty("status", "fail");
    		responseJsonObject.addProperty("message", "verify recaptcha first");
    		response.getWriter().write(responseJsonObject.toString());
            return;
        }

        try {
            Connection dbcon = dataSource.getConnection();

            if(!username.equals("b@email.com")) {

                String query = "select password from customers where email=?;";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();

                if(rs.next()) {
                    String pw = rs.getString("password");
                    boolean success = new StrongPasswordEncryptor().checkPassword(password, pw);
                    if(success) {
                        // Login succeeds
                        // Set this user into current session
                        String sessionId = ((HttpServletRequest) request).getSession().getId();
                        Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
                        request.getSession().setAttribute("user", new User(username));

                        JsonObject responseJsonObject = new JsonObject();
                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");

                        response.getWriter().write(responseJsonObject.toString());
                    } else {
                        // Login fails
                        JsonObject responseJsonObject = new JsonObject();
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "user doesn't exist or incorrect password");
                        response.getWriter().write(responseJsonObject.toString());
                    }}
                else {
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "user doesn't exist or incorrect password");
                    response.getWriter().write(responseJsonObject.toString());
                }
            } else {
                //employee login
                String query = "select password from employees where email=?;";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) {
                    String pw = rs.getString("password");
                    boolean success = new StrongPasswordEncryptor().checkPassword(password, pw);
                    if(success) {
                        // Login succeeds
                        // Set this user into current session
                        String sessionId = ((HttpServletRequest) request).getSession().getId();
                        Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
                        request.getSession().setAttribute("user", new User(username));

                        JsonObject responseJsonObject = new JsonObject();
                        responseJsonObject.addProperty("status", "employee");
                        responseJsonObject.addProperty("message", "success");

                        response.getWriter().write(responseJsonObject.toString());
                    } else {
                        // Login fails
                        JsonObject responseJsonObject = new JsonObject();
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "employee doesn't exist or incorrect password");
                        response.getWriter().write(responseJsonObject.toString());
                    }}
                else {
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "employee doesn't exist or incorrect password");
                    response.getWriter().write(responseJsonObject.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //out.close();

    }
}
