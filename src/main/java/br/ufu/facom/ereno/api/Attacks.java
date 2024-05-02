package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

@WebServlet(name = "attacks", value = "/attacks")
public class Attacks extends HttpServlet {
    public static class ECF { // ERENO Configurarion File (ECF)
        public static String datasetName;

        public static boolean legitimate;
        public static boolean randomReplay;
        public static boolean masqueradeOutage;
        public static boolean masqueradeDamage;
        public static boolean randomInjection;
        public static boolean inverseReplay;
        public static boolean highStNum;
        public static boolean flooding;
        public static boolean grayhole;
        public static boolean stealthyInjection;

        public static void loadConfigs() { // Used outside the servlet contexts
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();

            try {
                Reader reader = Files.newBufferedReader(
                        Path.of(System.getProperty("user.dir") +
                                "/src/main/webapp/ecf/attacks.json"));
                ECF ecf = gson.fromJson(reader, ECF.class);
                Logger logger = Logger.getLogger("ECF");
                logger.info("Attacks.ECF.legitimate: " + ecf.legitimate);
                logger.info("Attacks.ECF.randomReplay: " + ecf.randomReplay);
                logger.info("Attacks.ECF.inverseReplay: " + ecf.inverseReplay);
                logger.info("Attacks.ECF.masqueradeOutage: " + ecf.masqueradeOutage);
                logger.info("Attacks.ECF.masqueradeDamage: " + ecf.masqueradeDamage);
                logger.info("Attacks.ECF.highStNum: " + ecf.highStNum);
                logger.info("Attacks.ECF.flooding: " + ecf.flooding);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static void writeConfigs() { // Used outside the servlet contexts
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            try {
                GSVDatasetWriter.startWriting(System.getProperty("user.dir") +
                        "/src/main/webapp/ecf/attacks.json");
                GSVDatasetWriter.write(gson.toJson(new ECF(), Attacks.ECF.class));
                GSVDatasetWriter.finishWriting();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static void writeConfigs(ServletContext servletContext) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            try {
                GSVDatasetWriter.startWriting(servletContext.getRealPath("ecf/attacks.json"));
                GSVDatasetWriter.write(gson.toJson(new ECF(), Attacks.ECF.class));
                GSVDatasetWriter.finishWriting();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static String loadConfigs(ServletContext servletContext) { // used within servlet contexts
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            try {
                Reader reader = Files.newBufferedReader(Path.of(servletContext.getRealPath("ecf/attacks.json")));
                gson.fromJson(reader, ECF.class);
                return gson.toJson(new ECF());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = ECF.loadConfigs(getServletContext());
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // handles the parameters from header (e.g., from HTML form)
        ECF.datasetName = request.getParameter("datasetName");

        if (request.getParameter("legitimate") == null) {
            ECF.legitimate = false;
        } else {
            ECF.legitimate = request.getParameter("legitimate").equals("on");
        }

        if (request.getParameter("random-replay") == null) {
            ECF.randomReplay = false;
        } else {
            ECF.randomReplay = request.getParameter("random-replay").equals("on");
        }

        if (request.getParameter("random-injection") == null) {
            ECF.randomInjection = false;
        } else {
            ECF.randomInjection = request.getParameter("random-injection").equals("on");
        }

        if (request.getParameter("masquerade-outage") == null) {
            ECF.masqueradeOutage = false;
        } else {
            ECF.masqueradeOutage = request.getParameter("masquerade-outage").equals("on");
        }

        if (request.getParameter("masquerade-damage") == null) {
            ECF.masqueradeDamage = false;
        } else {
            ECF.masqueradeDamage = request.getParameter("masquerade-damage").equals("on");
        }

        if (request.getParameter("inverse-replay") == null) {
            ECF.inverseReplay = false;
        } else {
            ECF.inverseReplay = request.getParameter("inverse-replay").equals("on");
        }

        if (request.getParameter("high-st-num") == null) {
            ECF.highStNum = false;
        } else {
            ECF.highStNum = request.getParameter("high-st-num").equals("on");
        }

        if (request.getParameter("flooding") == null) {
            ECF.flooding = false;
        } else {
            ECF.flooding = request.getParameter("flooding").equals("on");
        }

        if (request.getParameter("grayhole") == null) {
            ECF.grayhole = false;
        } else {
            ECF.grayhole = request.getParameter("grayhole").equals("on");
        }

        if (request.getParameter("stealthy-injection") == null) {
            ECF.stealthyInjection = false;
        } else {
            ECF.stealthyInjection = request.getParameter("stealthy-injection").equals("on");
        }

        if (ECF.legitimate || ECF.randomInjection || ECF.randomReplay || ECF.inverseReplay || ECF.masqueradeDamage ||
                ECF.masqueradeOutage || ECF.flooding || ECF.highStNum || ECF.grayhole || ECF.stealthyInjection) { // it was handled parameters from header
            out.println("<body><html>");
            out.println("<h2> Clique para baixar o ECL do fluxo <a href=\"attacks\" download> Attacks </a></h2>");
            out.println("<h1>" + "Done!" + "</h1>");
            out.println("</body></html>");
        } else {  // handles the JSON body (e.g., from API)
            new Gson().fromJson(request.getReader(), ECF.class);
            out.println("{");
            out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
            out.println("}");
        }

        ECF.writeConfigs(getServletContext());

        response.sendRedirect(request.getContextPath() + "/datasets");

    }

    public void destroy() {
    }

}