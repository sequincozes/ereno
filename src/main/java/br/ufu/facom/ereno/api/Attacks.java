package br.ufu.facom.ereno.api;

import br.ufu.facom.ereno.Util;
import com.google.gson.Gson;

import javax.servlet.Servlet;
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

@WebServlet(name = "attacks", value = "/attacks")
public class Attacks extends HttpServlet {
    public static class ECF { // ERENO Configurarion File (ECF)
        public static boolean legitimate;
        public static boolean randomReplay;
        public static boolean masqueradeOutage;
        public static boolean masqueradeDamage;
        public static boolean randomInjection;
        public static boolean inverseReplay;
        public static boolean highStNum;
        public static boolean flooding;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loadConfigs(getServletContext());
        request.setCharacterEncoding("UTF-8");
        String json = new Gson().toJson(this);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // handles the parameters from header (e.g., from HTML form)
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

        if (ECF.legitimate || ECF.randomInjection || ECF.randomReplay || ECF.inverseReplay || ECF.masqueradeDamage ||
                ECF.masqueradeOutage || ECF.flooding || ECF.highStNum) { // it was handled parameters from header
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

        writeConfigs("attacks");
        response.sendRedirect(request.getContextPath() + "/datasets");
    }


    public void writeConfigs(String name) {
        Gson gson = new Gson();
        try {
            Util.startWriting(getServletContext().getRealPath("ecf/" + name + ".json"));
            Util.write(gson.toJson(this, Attacks.class));
            Util.finishWriting();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void loadConfigs(ServletContext servletContext) { // used within servlet contexts
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Path.of(servletContext.getRealPath("ecf/attacks.json")));
            gson.fromJson(reader, ECF.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void loadConfigs() { // Used outside the servlet contexts
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Path.of("webapp/ecf/attacks.json"));
            gson.fromJson(reader, ECF.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
    }

}