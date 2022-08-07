package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.Util;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "setupIed", value = "/setup-ied")
public class SetupIED extends HttpServlet {

    private String iedName;
    private String gocbRef;
    private String datSet;
    private String minTime;
    private String maxTime;
    private String timestamp;
    private String stNum;
    private String sqNum;

    public void init() {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loadConfigs("setup-ied");
        request.setCharacterEncoding("UTF-8");
        String json = new Gson().toJson(this);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (request.getParameter("iedName") == null) { // handles the JSON body (e.g., from API)
            SetupIED setupIED = new Gson().fromJson(request.getReader(), SetupIED.class);
            updateConfigs(setupIED);

            out.println("{");
            out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
            out.println("}");
        } else { // handles the parameters from header (e.g., from HTML form)
            iedName = request.getParameter("iedName");
            gocbRef = request.getParameter("gocbRef");
            datSet = request.getParameter("datSet");
            minTime = request.getParameter("minTime");
            maxTime = request.getParameter("maxTime");
            timestamp = request.getParameter("timestamp");
            stNum = request.getParameter("stNum");
            sqNum = request.getParameter("sqNum");

            out.println("<body><html>");
            out.println("<h2> Clique para baixar o ECL do IED <a href=\"setup-ied\" download>" + iedName + "</a></h2>");
            out.println("<h1>" + "Done!" + "</h1>");
            out.println("</body></html>");

            response.sendRedirect(request.getContextPath() + "/goose-message.jsp");

        }
        Logger.getLogger("SetupIED").info("updating IED " + iedName + "...");
        writeConfigs("setup-ied");
        Logger.getLogger("SetupIED").info("IED " + iedName + " updated.");
    }

    public void writeConfigs(String name) {
        Gson gson = new Gson();
        try {
            Util.startWriting(getServletContext().getRealPath("ecf/" + name + ".json"));
            Util.write(gson.toJson(this, SetupIED.class));
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
            SetupIED setupIED = gson.fromJson(reader, SetupIED.class);
            Logger.getLogger("IEDName").info("Loading setup for " + setupIED.iedName);
            updateConfigs(setupIED);
        } catch (NullPointerException n) {
            try {
                Reader reader = Files.newBufferedReader(Path.of( name ));
                SetupIED setupIED = gson.fromJson(reader, SetupIED.class);
                Logger.getLogger("IEDName").info("Loading setup for " + setupIED.iedName);
                updateConfigs(setupIED);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateConfigs(SetupIED setupIED) {
        this.iedName = setupIED.iedName;
        this.gocbRef = setupIED.gocbRef;
        this.datSet = setupIED.datSet;
        this.minTime = setupIED.minTime;
        this.maxTime = setupIED.maxTime;
        this.timestamp = setupIED.timestamp;
        this.stNum = setupIED.stNum;
        this.sqNum = setupIED.sqNum;
    }

    public void destroy() {
    }


    public String getIedName() {
        return iedName;
    }

    public String getGocbRef() {
        return gocbRef;
    }

    public String getDatSet() {
        return datSet;
    }

    public String getMinTime() {
        return minTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStNum() {
        return stNum;
    }

    public String getSqNum() {
        return sqNum;
    }

}