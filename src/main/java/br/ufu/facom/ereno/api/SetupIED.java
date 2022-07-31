package br.ufu.facom.ereno.api;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        request.setCharacterEncoding("UTF-8");

        String json = new Gson().toJson(this);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("{");
        out.println("   \"message\" : \"Novo ERENO Configuration File (ECL) recebido!\"");
        out.println("}");

        iedName = request.getParameter("iedName");
        gocbRef = request.getParameter("gocbRef");
        datSet = request.getParameter("datSet");
        minTime = request.getParameter("minTime");
        maxTime = request.getParameter("maxTime");
        timestamp = request.getParameter("timestamp");
        stNum = request.getParameter("stNum");
        sqNum = request.getParameter("sqNum");

        out.println("<h2> Clique para baixar o ECL do IED <a href=\"setup-ied\" download>" + iedName + "</a></h2>");

        out.println("<h1>" + "Done!" + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}