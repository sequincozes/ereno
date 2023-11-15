package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(name = "svSamples", value = "/sv-samples")
public class SVSamples extends HttpServlet {

    public static boolean generateSV;
    private String message;

    public void init() {
        message = "Generating your SV Samples...";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("sv") != null && request.getParameter("sv").equals("on")) {
            generateSV = true;
        }

        if (request.getParameter("custom") != null && request.getParameter("custom").equals("on")) {
            String uploadPath = getServletContext().getRealPath("sv_payload_files");
            Logger.getLogger("Upload foler").info(uploadPath);
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();
            try {
                Logger.getLogger("Parts").info(String.valueOf(request.getParts().size()));

                for (Part part : request.getParts()) {
                    String fileName = part.getSubmittedFileName();
                    part.write(uploadPath + File.separator + fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (ServletException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                System.out.println("Skipped because null");
                e.printStackTrace();
            }
            Logger.getLogger("SVSamples").info("SV file upload complete...");
        } else {
            Logger.getLogger("SVSamples").info("Skipped without upload new file for SV.");
        }

        if (GSVDatasetWritter.english) {
            response.sendRedirect(request.getContextPath() + "/en/attack-definitions.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/attack-definitions.jsp");
        }
    }


    public void destroy() {
    }
}