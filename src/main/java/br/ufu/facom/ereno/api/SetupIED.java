package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.utils.GSVDatasetWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "setupIed", value = "/setup-ied")
public class SetupIED extends HttpServlet {

    public static class ECF { // ERENO Configurarion File (ECF)
        public static String iedName;
        public static String gocbRef;
        public static String datSet;
        public static String minTime;
        public static String maxTime;
        public static String timestamp;
        public static String stNum;
        public static String sqNum;


        public static void loadConfigs() { // Used outside the servlet contexts
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();

            try {
                Reader reader = Files.newBufferedReader(
                        Path.of(System.getProperty("user.dir") +
                                "/src/main/webapp/ecf/setup-ied.json"));
                gson.fromJson(reader, ECF.class);
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
                        "/src/main/webapp/ecf/setup-ied.json");
                GSVDatasetWriter.write(gson.toJson(new ECF(), ECF.class));
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
                GSVDatasetWriter.startWriting(servletContext.getRealPath("ecf/setup-ied.json"));
                GSVDatasetWriter.write(gson.toJson(new ECF(), ECF.class));
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
                Reader reader = Files.newBufferedReader(Path.of(servletContext.getRealPath("ecf/setup-ied.json")));
                gson.fromJson(reader, ECF.class);
                return gson.toJson(new ECF());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }


    public void init() {

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

        if (request.getParameter("iedName") == null) { // handles the JSON body (e.g., from API)
            new Gson().fromJson(request.getReader(), ECF.class);
            out.println("{");
            out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
            out.println("}");
        } else { // handles the parameters from header (e.g., from HTML form)
            ECF.iedName = request.getParameter("iedName");
            ECF.gocbRef = request.getParameter("gocbRef");
            ECF.datSet = request.getParameter("datSet");
            ECF.minTime = request.getParameter("minTime");
            ECF.maxTime = request.getParameter("maxTime");
            ECF.timestamp = request.getParameter("timestamp");
            ECF.stNum = request.getParameter("stNum");
            ECF.sqNum = request.getParameter("sqNum");

            out.println("<body><html>");
            out.println("<h2> Clique para baixar o ECL do IED <a href=\"setup-ied\" download>" + ECF.iedName + "</a></h2>");
            out.println("<h1>" + "Done!" + "</h1>");
            out.println("</body></html>");

            if (GSVDatasetWriter.english) {
                response.sendRedirect(request.getContextPath() + "/en/goose-message.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/goose-message.jsp");
            }

        }
        Logger.getLogger("SetupIED").info("updating IED " + ECF.iedName + "...");
        ECF.writeConfigs(getServletContext());
        Logger.getLogger("SetupIED").info("IED " + ECF.iedName + " updated.");
    }

}