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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "DshbdAddmovieServlet", urlPatterns = "/api/addmovie")
public class DshbdAddmovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String year1 = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String birthdate = request.getParameter("birthdate");
        String genre = request.getParameter("genre");

        int year = Integer.parseInt(year1);
        String starid = "";
        int starid1 = 0;
        String movieid = "";
        int movieid1 = 0;
        int genreid = 0;

        // Output stream to STDOUT

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            //Query the max starid to find the id that needs to be inserted
            String queryA = "select title\n" +
                    "from movies\n" +
                    "where title = ? and year = ? and director = ?;";
            PreparedStatement statement = dbcon.prepareStatement(queryA);
            statement.setString(1, title);
            statement.setInt(2, year);
            statement.setString(3, director);
            ResultSet rsA = statement.executeQuery();
            if(rsA.next()) {
                System.out.println("the movie has existed");
                response.getWriter().write("The movie has existed!");
            }else {
                String query1 = "select max(id)\n" +
                        " from movies;";
                statement = dbcon.prepareStatement(query1);
                ResultSet rs1 = statement.executeQuery();
                if(rs1.next()) {
                    movieid = rs1.getString("max(id)");
                    movieid = movieid.substring(2);
                    movieid1 = Integer.parseInt(movieid);
                    movieid1 ++;
                    movieid = String.valueOf(movieid1);
                    movieid = "tt0" + movieid;
                    System.out.println(movieid);
                }

                String query2 = "select *\n" +
                        "from stars\n" +
                        "where name = ?;";
                statement = dbcon.prepareStatement(query2);
                statement.setString(1, star);
                ResultSet rs2 = statement.executeQuery();
                if(rs2.next()) {
                    System.out.println("the star has existed");
                    starid = rs2.getString("id");
                }else {
                    String query = "select max(id)\n" +
                            "from stars;";
                    statement = dbcon.prepareStatement(query);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()) {
                        starid = rs.getString("max(id)");
                        starid = starid.substring(2);
                        starid1 = Integer.parseInt(starid);
                        starid1 ++;
                        starid = String.valueOf(starid1);
                        starid = "nm" + starid;
                        System.out.println(starid);
                    }
                    String insert = "INSERT INTO stars VALUES(?,?,?);\n";
                    statement = dbcon.prepareStatement(insert);
                    statement.setString(1, starid);
                    statement.setString(2, star);
                    statement.setString(3, birthdate);
                    int n = statement.executeUpdate();
                    System.out.println("the result of insert star sql:"+n);
                }

                String query3 = "select *\n" +
                        "from genres\n" +
                        "where name = ?;";
                statement = dbcon.prepareStatement(query3);
                statement.setString(1, genre);
                ResultSet rs3 = statement.executeQuery();
                if(rs3.next()) {
                    System.out.println("the genre has existed");
                    String genreid1 = rs3.getString("id");
                    genreid = Integer.parseInt(genreid1);
                }else {
                    String query = "select max(id)\n" +
                            "from genres;";
                    statement = dbcon.prepareStatement(query);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()) {
                        String genreid1 = rs.getString("max(id)");
                        genreid = Integer.parseInt(genreid1);
                        genreid ++;
                        System.out.println(genreid);
                    }
                    String insert = "INSERT INTO genres VALUES(?,?);\n";
                    statement = dbcon.prepareStatement(insert);
                    statement.setInt(1, genreid);
                    statement.setString(2, genre);
                    int n = statement.executeUpdate();
                    System.out.println("the result of insert genre sql:"+n);
                }

                String calladdmovie = "{CALL add_movie(?, ?, ?, ?, ?, ?)}";
                CallableStatement stmt = dbcon.prepareCall(calladdmovie);
                stmt.setString(1, movieid);
                stmt.setString(2, title);
                stmt.setInt(3, year);
                stmt.setString(4, director);
                stmt.setString(5, starid);
                stmt.setInt(6, genreid);

                // Perform the insert
                stmt.executeQuery();

                System.out.println("the result of call store procedure sql:");
                response.getWriter().write("pop in the movie ID: "+movieid+", title: "+title+", year: "+year+", director: "+director+", starname: "+star+", genre:" +genre);
                stmt.close();
            }

            statement.close();
            dbcon.close();

        } catch (Exception e) {
            // write error message JSON object to output
            response.getWriter().write("Wrong Information");
            e.getStackTrace();
            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }

    }
}
