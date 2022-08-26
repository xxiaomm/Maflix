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
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * handles POST requests to store session information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        Long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles GET requests to add and show the item list information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        System.out.println(item);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        @SuppressWarnings("unchecked")
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> previousQuan = (ArrayList<String>) session.getAttribute("previousQuan");
        if (previousItems == null) {
            previousItems = new ArrayList<>();
            previousQuan = new ArrayList<>();
            previousItems.add(item);
            previousQuan.add("1");
            session.setAttribute("previousItems", previousItems);
            session.setAttribute("previousQuan", previousQuan);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                Boolean flag = true;
                for(int i=0; i<previousItems.size(); i++) {
                    String items = previousItems.get(i);
                    if(items.equals(item)) {
                        flag = false;
                        Integer number = Integer.valueOf(previousQuan.get(i))+1;
                        previousQuan.set(i, String.valueOf(number));
                        break;
                    }
                }
                if(flag) {
                    previousItems.add(item);
                    previousQuan.add("1");
                }
            }
        }
        response.getWriter().write(String.join(",", previousItems) + ',' + String.join(",",previousQuan));
    }

}
