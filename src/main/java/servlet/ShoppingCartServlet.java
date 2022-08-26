package servlet;

import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * handles POST requests to store session information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> previousQuan = (ArrayList<String>) session.getAttribute("previousQuan");
        response.getWriter().write(String.join(",", previousItems) + ',' + String.join(",",previousQuan));
    }

    /**
     * handles GET requests to add and show the item list information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String quan = request.getParameter("quan");
        String title = request.getParameter("title");
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        @SuppressWarnings("unchecked")
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> previousQuan = (ArrayList<String>) session.getAttribute("previousQuan");
        if(Integer.parseInt(quan)>=0)
            synchronized (previousItems) {
                for(int i=0; i<previousItems.size(); i++) {
                    String items = previousItems.get(i);
                    if(items.equals(title)) {
                        previousQuan.set(i, quan);
                        if(quan.equals("0")) {
                            previousQuan.remove(i);
                            previousItems.remove(i);
                        }
                        break;
                    }
                }
            }
        response.getWriter().write(String.join(",", previousItems)+',' + String.join(",", previousQuan));
    }
}