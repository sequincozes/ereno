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

@WebServlet(name = "attacks", value = "/attacks")
public class Attacks extends HttpServlet {
    private boolean legitimate;
    private boolean randomReplay;
    private boolean masqueradeOutage;
    private boolean masqueradeDamage;
    private boolean randomInjection;
    private boolean inverseReplay;
    private boolean highStNum;
    private boolean flooding;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loadConfigs("attacks");
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
            this.legitimate = false;
        } else {
            this.legitimate = request.getParameter("legitimate").equals("on");
        }

        if (request.getParameter("random-replay") == null) {
            this.randomReplay = false;
        } else {
            this.randomReplay = request.getParameter("random-replay").equals("on");
        }


        if (request.getParameter("random-injection") == null) {
            this.randomInjection = false;
        } else {
            this.randomInjection = request.getParameter("random-injection").equals("on");
        }

        if (request.getParameter("masquerade-outage") == null) {
            this.masqueradeOutage = false;
        } else {
            this.masqueradeOutage = request.getParameter("masquerade-outage").equals("on");
        }

        if (request.getParameter("masquerade-damage") == null) {
            this.masqueradeDamage = false;
        } else {
            this.masqueradeDamage = request.getParameter("masquerade-damage").equals("on");
        }

        if (request.getParameter("inverse-replay") == null) {
            this.inverseReplay = false;
        } else {
            this.inverseReplay = request.getParameter("inverse-replay").equals("on");
        }

        if (request.getParameter("inverse-replay") == null) {
            this.highStNum = false;
        } else {
            this.highStNum = request.getParameter("inverse-replay").equals("on");
        }

        if (request.getParameter("flooding") == null) {
            this.flooding = false;
        } else {
            this.flooding = request.getParameter("flooding").equals("on");
        }

        if (legitimate || randomInjection || randomReplay || inverseReplay || masqueradeDamage ||
                masqueradeOutage || flooding || highStNum) { // it was handled parameters from header
            out.println("<body><html>");
            out.println("<h2> Clique para baixar o ECL do fluxo <a href=\"attacks\" download> Attacks </a></h2>");
            out.println("<h1>" + "Done!" + "</h1>");
            out.println("</body></html>");
        } else {  // handles the JSON body (e.g., from API)
            Attacks attacks = new Gson().fromJson(request.getReader(), Attacks.class);
            updateConfigs(attacks);
            out.println("{");
            out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
            out.println("}");
        }
        writeConfigs("attacks");
        response.sendRedirect(request.getContextPath() + "/datasets");
//        response.sendRedirect(request.getContextPath() + "/download-datasets.jsp");
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

    public void loadConfigs(String name) {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Path.of(getServletContext().getRealPath("ecf/" + name + ".json")));
            Attacks Attacks = gson.fromJson(reader, Attacks.class);
            updateConfigs(Attacks);
        } catch (NullPointerException n) {
            try {
                Reader reader = Files.newBufferedReader(Path.of(name));
                Attacks Attacks = gson.fromJson(reader, Attacks.class);
                updateConfigs(Attacks);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateConfigs(Attacks attacks) {
        this.legitimate = attacks.legitimate;
        this.randomReplay = attacks.randomReplay;
        this.randomInjection = attacks.randomInjection;
        this.inverseReplay = attacks.inverseReplay;
        this.masqueradeOutage = attacks.masqueradeOutage;
        this.masqueradeDamage = attacks.masqueradeDamage;
        this.flooding = attacks.flooding;
        this.highStNum = attacks.highStNum;
    }

    public void destroy() {
    }

    public boolean isLegitimate() {
        return legitimate;
    }

    public boolean isRandomReplay() {
        return randomReplay;
    }

    public boolean isMasqueradeOutage() {
        return masqueradeOutage;
    }

    public boolean isMasqueradeDamage() {
        return masqueradeDamage;
    }

    public boolean isRandomInjection() {
        return randomInjection;
    }

    public boolean isHighStNum() {
        return highStNum;
    }

    public boolean isFlooding() {
        return flooding;
    }

    public boolean isInverseReplay() {
        return inverseReplay;
    }


}