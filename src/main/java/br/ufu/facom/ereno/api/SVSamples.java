package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.Extractor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(name = "svSamples", value = "/sv-samples")
public class SVSamples extends HttpServlet {
    private String message;

    public void init() {
        message = "Generating your SV Samples...";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uploadPath = getServletContext().getRealPath("sv_payload_files");
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
        try {
            Logger.getLogger("Parts").info(String.valueOf(request.getParts().size()));

            for (Part part : request.getParts()) {
                String fileName = part.getSubmittedFileName();
                part.write(uploadPath + File.separator + fileName);
                Logger.getLogger(fileName).info("Uploading part...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ServletException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        response.sendRedirect(request.getContextPath() + "/attack-definitions.jsp");

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