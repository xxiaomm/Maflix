package servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "DashBoardServlet", urlPatterns = "/api/dashboard")
public class DashBoardServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query
            String query = "select table_name, column_name, column_type\n" +
                    "from INFORMATION_SCHEMA.Columns \n" +
                    "where table_schema='moviedb';";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String table_name = rs.getString("table_name");
                String column_name = rs.getString("column_name");
                String column_type = rs.getString("column_type");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table_name);
                jsonObject.addProperty("column_name", column_name);
                jsonObject.addProperty("column_type", column_type);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String starid = "";
        String name = request.getParameter("name");
        String birthdate = request.getParameter("birthdate");



        int starid1 = 0;

        // Output stream to STDOUT

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            //Query the max starid to find the id that needs to be inserted
            String query1 = "select max(id)\n" +
                    " from stars;";
            PreparedStatement statement = dbcon.prepareStatement(query1);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                starid = rs.getString("max(id)");
                starid = starid.substring(2);
                starid1 = Integer.parseInt(starid);
                starid1 ++;
                starid = String.valueOf(starid1);
                starid = "nm" + starid;
                System.out.println(starid);
                System.out.println(starid1);
            }

            if(birthdate=="") {
                String insert = "INSERT INTO stars VALUES(?,?,null);\n";
                statement = dbcon.prepareStatement(insert);
                statement.setString(1, starid);
                statement.setString(2, name);
                int n = statement.executeUpdate();
                System.out.println("the result of insert star sql:"+n);
                response.getWriter().write("pop in the star ID: "+starid+", star name:" + name+ ",  birth date: null");
            }else {
                String insert = "INSERT INTO stars VALUES(?,?,?);\n";
                statement = dbcon.prepareStatement(insert);
                statement.setString(1, starid);
                statement.setString(2, name);
                statement.setString(3, birthdate);
                // Perform the insert
                int n = statement.executeUpdate();
                System.out.println("the result of insert star sql:"+n);
                response.getWriter().write("pop in the star ID: "+starid+", star name:" + name+ ",  birth date:" +birthdate);
            }

            statement.close();
            dbcon.close();

        } catch (Exception e) {
            // write error message JSON object to output
            response.getWriter().write("Wrong Information");

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }

    }
}
