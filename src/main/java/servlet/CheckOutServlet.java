package servlet;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.sun.xml.internal.ws.api.server.InstanceResolver;


import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is declared as LoginServlet in web annotation,
 * which is mapped to the URL pattern /api/login
 */
@WebServlet(name = "CheckOutServlet", urlPatterns = "/api/check-out")
public class CheckOutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String first_name = request.getParameter("first-name");
        String last_name = request.getParameter("last-name");
        String credit_card = request.getParameter("credit-card");
        String expiration_date = request.getParameter("expiration-date");

        HttpSession session = request.getSession();
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> previousQuan = (ArrayList<String>) session.getAttribute("previousQuan");
        int len = previousItems.size();
        //response.setContentType("application/json");
        //PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();

            String query = "select firstName, lastName, expiration from creditcards where id=?;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1, credit_card);
            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                String fn = rs.getString("firstName");
                String ln = rs.getString("lastName");
                Date exp_date = rs.getDate("expiration");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String exp = sdf.format(exp_date);

                if(fn.equals(first_name)&&ln.equals(last_name)&&exp.equals(expiration_date)) {
                    System.out.println(len);
                    for(int i=0; i<len;i++) {
                        String query2 = "select m.id\n" +
                                "from movies as m\n" +
                                "where m.title = ?;";
                        System.out.println(i);
                        statement = dbcon.prepareStatement(query2);
                        statement.setString(1, previousItems.get(i));
                        ResultSet rs2 = statement.executeQuery();

                        if (rs2.next()) {
                            String movieId = rs2.getString("id");
                            System.out.println(movieId);

                            String query3 = "select ct.id\n" +
                                    "from customers as ct join creditcards as cd on ct.ccId = cd.id\n" +
                                    "where cd.id =?;";
                            statement = dbcon.prepareStatement(query3);
                            statement.setString(1, credit_card);
                            ResultSet rs3 = statement.executeQuery();

                            if(rs3.next()) {
                                String customerId = rs3.getString("id");
                                System.out.println(customerId);
                                Date currentTime = new Date();
                                String ct = sdf.format(currentTime);

                                String insert = "INSERT INTO sales VALUES(null,?,?,?);";
                                statement = dbcon.prepareStatement(insert);
                                statement.setString(1, customerId);
                                statement.setString(2, movieId);
                                statement.setString(3, ct);
                                int n = statement.executeUpdate();
                                System.out.println("the result of insert sql:"+n);



                                String query4 = "select id from sales where saleDate = ? and customerId = ? and movieId =?;";
                                statement = dbcon.prepareStatement(query4);
                                statement.setString(1, ct);
                                statement.setString(2, customerId);
                                statement.setString(3, movieId);
                                ResultSet rs4 = statement.executeQuery();

                                if(rs4.next()) {
                                    String id = rs4.getString("id");
                                    response.getWriter().write(id + ',' + previousQuan.get(i) + ',' + previousItems.get(i) + ',' + ct + ',');
                                }
                            }
                        }
                    }

                    //purchase success
                } else {
                    //purchase fails
                    response.getWriter().write("Wrong Information");
                }}
            else {
                //purchase fails
                response.getWriter().write("Wrong Information");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        //out.close();

    }
}
