package br.ufu.facom.ereno;

import java.io.*;

public class Util {
    static BufferedWriter bw;
    static boolean replace = true;

    protected static void write(String line) throws IOException {
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

    protected static void finishWriting() throws IOException {
        bw.close();
    }
}
