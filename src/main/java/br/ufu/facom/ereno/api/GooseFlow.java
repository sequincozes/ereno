package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.Util;
import com.google.gson.Gson;

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
    private String goID;
    private int numberOfMessages;
    private String ethSrc;
    private String ethDst;
    private String ethType;
    private String gooseAppid;

    private String TPID;
    private boolean ndsCom;
    private boolean test;
    private boolean cbstatus;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loadConfigs("goose-flow");
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
            GooseFlow gooseFlow = new Gson().fromJson(request.getReader(), GooseFlow.class);
            updateConfigs(gooseFlow);

            out.println("{");
            out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
            out.println("}");
        } else { // handles the parameters from header (e.g., from HTML form)
            this.goID = request.getParameter("goID");
            this.numberOfMessages = Integer.parseInt(request.getParameter("numberOfMessages"));
            this.ethSrc = request.getParameter("ethSrc");
            this.ethDst = request.getParameter("ethDst");
            this.ethType = request.getParameter("ethType");
            this.gooseAppid = request.getParameter("gooseAppid");
            this.TPID = request.getParameter("TPID");

            if (request.getParameter("test") == null) {
                this.test = false;
            } else {
                if (request.getParameter("test").equals("on")) {
                    this.test = true;
                } else {
                    this.test = false;
                }
            }

            if (request.getParameter("ndsCom") == null) {
                this.ndsCom = false;
            } else {
                if (request.getParameter("ndsCom").equals("on")) {
                    this.ndsCom = true;
                } else {
                    this.ndsCom = false;
                }
            }
            if (request.getParameter("cbstatus") == null) {
                this.cbstatus = false;
            } else {
                if (request.getParameter("cbstatus").equals("on")) {
                    this.cbstatus = true;
                } else {
                    this.cbstatus = false;
                }
            }

            out.println("<body><html>");
            out.println("<h2> Clique para baixar o ECL do fluxo <a href=\"goose-flow\" download>" + goID + "</a></h2>");
            out.println("<h1>" + "Done!" + "</h1>");
            out.println("</body></html>");

            response.sendRedirect(request.getContextPath() + "/upload-samples.jsp");

        }
        Logger.getLogger("GooseFlow").info("updating Goose Flow " + goID + "...");
        writeConfigs("goose-flow");
        Logger.getLogger("GooseFlow").info("Goose flow " + goID + " updated.");
    }

    public void writeConfigs(String name) {
        Gson gson = new Gson();
        try {
            Util.startWriting(getServletContext().getRealPath("ecf/" + name + ".json"));
            Util.write(gson.toJson(this, GooseFlow.class));
            Util.finishWriting();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void loadConfigs(String name) {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Path.of(getServletContext().getRealPath("ecf/" + name + ".json")));
            GooseFlow gooseFlow = gson.fromJson(reader, GooseFlow.class);
            Logger.getLogger("GooseFlow").info("Loading setup for " + gooseFlow.goID);
            updateConfigs(gooseFlow);
        } catch (NullPointerException n) {
            try {
                Reader reader = Files.newBufferedReader(Path.of(name));
                GooseFlow gooseFlow = gson.fromJson(reader, GooseFlow.class);
                Logger.getLogger("GooseFlow").info("Loading setup for " + gooseFlow.goID);
                updateConfigs(gooseFlow);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateConfigs(GooseFlow gooseFlow) {
        this.goID = gooseFlow.goID;
        this.numberOfMessages = gooseFlow.numberOfMessages;
        this.ethSrc = gooseFlow.ethSrc;
        this.ethDst = gooseFlow.ethDst;
        this.ethType = gooseFlow.ethType;
        this.gooseAppid = gooseFlow.gooseAppid;
        this.TPID = gooseFlow.TPID;
        this.test = gooseFlow.test;
        this.ndsCom = gooseFlow.ndsCom;
        this.cbstatus = gooseFlow.cbstatus;
    }


    public String getGoID() {
        return goID;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public String getEthSrc() {
        return ethSrc;
    }

    public String getEthDst() {
        return ethDst;
    }

    public String getEthType() {
        return ethType;
    }

    public String getGooseAppid() {
        return gooseAppid;
    }

    public String getTPID() {
        return TPID;
    }

    public boolean isNdsCom() {
        return ndsCom;
    }

    public boolean isTest() {
        return test;
    }

    public boolean isCbstatus() {
        return cbstatus;
    }


}