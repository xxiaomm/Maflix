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

// Declaring a WebServlet called SearchServlet, which maps to url "/api/search"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
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

        // Retrieve parameter id from url request.
        String genre = request.getParameter("genre");
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String name = request.getParameter("name");
        String offset1 = request.getParameter("offset");
        String limit1 = request.getParameter("limit");

        int offset = Integer.parseInt(offset1);
        int limit = Integer.parseInt(limit1);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "select m.id, m.title, group_concat(s.id) as StarId, group_concat(s.name) as StarName, group_concat(distinct  g.name) as GenreName, m.year, m.director, r.rating\n" +
                    "from movies as m join ratings as r on m.id = r.movieId\n" +
                    "				 join stars_in_movies as sm on m.id = sm.movieId\n" +
                    "				 join stars as s on sm.starId = s.id\n" +
                    "                join genres_in_movies as gm on m.id = gm.movieId\n" +
                    "				 join genres as g on gm.genreId = g.id\n" +
                    "where g.name like ? and m.title like ? and m.year like ? and m.director like ? and s.name like ? \n" +
                    "group by m.id, m.title, m.year, m.director, r.rating\n" +
                    "order by r.rating desc\n" +
                    "limit ?\n" +
                    "offset ?;";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, '%' + genre + '%');
            statement.setString(2, title + '%');
            statement.setString(3, '%' + year + '%');
            statement.setString(4, '%' + director + '%');
            statement.setString(5, '%' + name + '%');
            statement.setInt(6, limit);
            statement.setInt(7, offset);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");
                String movie_genres = rs.getString("GenreName");
                String movie_stars = rs.getString("StarName");
                String movie_starsId = rs.getString("StarId");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("movie_starsId", movie_starsId);

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

}
