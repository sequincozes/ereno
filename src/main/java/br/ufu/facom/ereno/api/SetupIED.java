package br.ufu.facom.ereno.api;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "setupIed", value = "/setup-ied")
public class SetupIED extends HttpServlet {
    private String message;

    public void init() {
        message = "You are setting up the IED: ";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
//        UC00GooseOnly.run("test3.arff");
        String name = request.getParameter("iedName");
        out.println("<h2>" + name + "</h1>");
        out.println("<h1>" + "Done!" + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}