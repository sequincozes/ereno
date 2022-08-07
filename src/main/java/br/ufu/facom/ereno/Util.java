package br.ufu.facom.ereno;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;

public class Util {
    static BufferedWriter bw;
    static boolean replace = true;

    public static void write(String line) throws IOException {
        bw.write(line);
        bw.newLine();
    }

    public static void startWriting(String filename) throws IOException {
        File fout = new File(filename);
        if (!fout.exists()) {
//            fout.mkdir();
            fout.getParentFile().mkdirs();
            System.out.println("Directory created at: " + filename);
        }
        FileOutputStream fos = new FileOutputStream(fout, !replace);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
    }

    public static void finishWriting() throws IOException {
        bw.close();
    }

    public static String listFiles(File file) throws IOException {
        String html = "<ul>";

        for (File f : file.listFiles()) {
            String downloadLink = "downloads" + "/" + f.getName();
            float filesize = Files.size(f.toPath());
            DecimalFormat df = new DecimalFormat("#.#");

            html = html + "<li> ";
            html = html + "<a download href=\"" + downloadLink + "\">";
            html = html + f.getName() + "<a/>";
            if (filesize > 1) {
                html = html + " (" + df.format(filesize / 1024) + " KB)";
            } else if (filesize/1024 > 1){
                html = html + " (" + df.format(filesize / 1024 / 1024) + " MB)";
            } else if (filesize/1024/1024 > 1){
                html = html + " (" + df.format(filesize / 1024 / 1024 / 1024) + " GB)";
            }
            html = html + "</li>";
        }
        return html + "</ul>";
    }
}
