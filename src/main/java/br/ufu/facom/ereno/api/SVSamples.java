package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.Extractor;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "svSamples", value = "/sv-samples")
public class SVSamples extends HttpServlet {
    private String message;

    public void init() {
        message = "Generating your SV Samples...";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + message + "</h1>");
            String svPayload = getServletContext().getRealPath("sv_payload_files/second_1.csv");
            String datasetLocation = getServletContext().getRealPath("downloads/result.arff");
            Extractor.scriptForSV(svPayload, datasetLocation);
            out.println("<h1>" + "Done: download it <a href=\"downloads/result.arff\"> here</a>!" + "</h1>");

        } catch (IOException e) {
            out.println("<h1>" + "Error! " + e.getMessage());
            e.printStackTrace();
        }
        out.println("</body></html>");
    }

    public void destroy() {
    }
}