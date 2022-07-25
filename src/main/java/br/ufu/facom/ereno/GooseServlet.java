package br.ufu.facom.ereno;

import br.ufu.facom.ereno.usecasesExtractors.UC00GooseOnly;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "gooseServlet", value = "/goose-servlet")
public class GooseServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Generating Goose Messages...";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
//        UC00GooseOnly.run("test3.arff");
        out.println("<h1>" + "Done!" + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}