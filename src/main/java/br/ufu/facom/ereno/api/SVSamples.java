package br.ufu.facom.ereno.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "setupIed", value = "/setup-ied")
public class SVSamples extends HttpServlet {
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