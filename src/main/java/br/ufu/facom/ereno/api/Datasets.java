package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.scenarios.SingleSource;
import br.ufu.facom.ereno.utils.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(name = "datasets", value = "/datasets")
public class Datasets extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Logger.getLogger("dataset").info("Generating dataset... ");
        response.setContentType("text/html");
        Attacks.ECF.loadConfigs(getServletContext());
        GooseFlow.ECF.loadConfigs(getServletContext());
        SetupIED.ECF.loadConfigs(getServletContext());

        SingleSource.scriptForGooseAndSV(getServletContext().getRealPath("downloads/" + Attacks.ECF.datasetName + ".arff"), SVSamples.generateSV);

        if (Util.english) {
            response.sendRedirect(request.getContextPath() + "/en/download-datasets.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/download-datasets.jsp");
        }
    }

}
