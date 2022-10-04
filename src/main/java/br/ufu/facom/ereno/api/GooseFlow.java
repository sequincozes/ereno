package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.Util;
import br.ufu.facom.ereno.messages.Goose;
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

@WebServlet(name = "gooseFlow", value = "/goose-flow")
public class GooseFlow extends HttpServlet {
    public static class ECF { // ERENO Configurarion File (ECF)
        public static String goID;
        public static int numberOfMessages;
        public static String ethSrc;
        public static String ethDst;
        public static String ethType;
        public static String gooseAppid;
        public static String TPID;
        public static boolean ndsCom;

        public static boolean test;
        public static boolean cbstatus;


        public static void loadConfigs() { // Used outside the servlet contexts
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();

            try {
                Reader reader = Files.newBufferedReader(
                        Path.of(System.getProperty("user.dir") +
                                "/src/main/webapp/ecf/goose-flow.json"));
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
                Util.startWriting(System.getProperty("user.dir") +
                        "/src/main/webapp/ecf/goose-flow.json");
                Util.write(gson.toJson(new ECF(), ECF.class));
                Util.finishWriting();
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
                Util.startWriting(servletContext.getRealPath("ecf/goose-flow.json"));
                Util.write(gson.toJson(new ECF(), ECF.class));
                Util.finishWriting();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static void loadConfigs(ServletContext servletContext) { // used within servlet contexts
            GsonBuilder gsonBuilder = new GsonBuilder();
            // This is for reading static fields
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson gson = gsonBuilder.create();
            try {
                System.out.println("ECF GoID: " + ECF.goID);

                Reader reader = Files.newBufferedReader(Path.of(servletContext.getRealPath("ecf/goose-flow.json")));
                ECF ecf = gson.fromJson(reader, ECF.class);
                System.out.println("ECF GoID: " + ecf.goID);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ECF.loadConfigs(getServletContext());
        request.setCharacterEncoding("UTF-8");
        String json = new Gson().toJson(this);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (request.getParameter("goID") == null) { // handles the JSON body (e.g., from API)
            ECF gooseFlow = new Gson().fromJson(request.getReader(), ECF.class);
            out.println("{");
            out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
            out.println("}");
        } else { // handles the parameters from header (e.g., from HTML form)
            ECF.goID = request.getParameter("goID");
            ECF.numberOfMessages = Integer.parseInt(request.getParameter("numberOfMessages"));
            ECF.ethSrc = request.getParameter("ethSrc");
            ECF.ethDst = request.getParameter("ethDst");
            ECF.ethType = request.getParameter("ethType");
            ECF.gooseAppid = request.getParameter("gooseAppid");
            ECF.TPID = request.getParameter("TPID");

            if (request.getParameter("test") == null) {
                ECF.test = false;
            } else {
                if (request.getParameter("test").equals("on")) {
                    ECF.test = true;
                } else {
                    ECF.test = false;
                }
            }

            if (request.getParameter("ndsCom") == null) {
                ECF.ndsCom = false;
            } else {
                if (request.getParameter("ndsCom").equals("on")) {
                    ECF.ndsCom = true;
                } else {
                    ECF.ndsCom = false;
                }
            }
            if (request.getParameter("cbstatus") == null) {
                ECF.cbstatus = false;
            } else {
                if (request.getParameter("cbstatus").equals("on")) {
                    ECF.cbstatus = true;
                } else {
                    ECF.cbstatus = false;
                }
            }

            out.println("<body><html>");
            out.println("<h2> Clique para baixar o ECL do fluxo <a href=\"goose-flow\" download>" + ECF.goID + "</a></h2>");
            out.println("<h1>" + "Done!" + "</h1>");
            out.println("</body></html>");


            if (Util.english) {
                response.sendRedirect(request.getContextPath() + "/en/upload-samples.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/upload-samples.jsp");
            }

        }
        Logger.getLogger("GooseFlow").info("updating Goose Flow " + ECF.goID + "...");
        ECF.writeConfigs(getServletContext());
        Logger.getLogger("GooseFlow").info("Goose flow " + ECF.goID + " updated.");
    }


}