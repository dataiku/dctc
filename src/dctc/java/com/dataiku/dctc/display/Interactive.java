package com.dataiku.dctc.display;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Interactive {
    public static boolean ask(String cmd, String question, String yes, String no) {
        BufferedReader i = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.err.print(question);
            System.err.flush();
            String line;
            try {
                line = i.readLine();
            } catch (IOException e) {
                System.err.println("dctc " + cmd + ": Internal error.");
                return false;
            }
            if (yes.indexOf(line) != -1) {
                return true;
            } else if (no.indexOf(line) != -1) {
                return false;
            }
        }
    }

    public static String askString(String question) {
        BufferedReader i = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.err.print(question);
            System.err.flush();
            String line;
            try {
                line = i.readLine();
            } catch (IOException e) {
                throw new Error("Unexpected error", e);
            }
            if (line == null) {
                System.err.println();
                return "";
            }
            return line.replace("\n", "");
        }
    }
}
