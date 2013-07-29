package com.dataiku.dctc.utils;

import java.io.IOException;
import java.io.OutputStream;

public class CharUtils {
    public static void showNonPrintable(OutputStream out
                                        , byte x_
                                        , boolean showTabulation)
        throws IOException {
        int x = x_ >= 0 ? x_ : (256 + x_);
        assert x >= 0
            : "x >= 0";
        assert x <= 255
            : "x <= 255";
        if (x >= 32) {
            if (x < 127) {
                out.write((char) x);
            }
            else if (x == 127) {
                out.write('^');
                out.write('?');
            }
            else {
                out.write('M');
                out.write('-');
                if (x >= 128 + 32) {
                    if (x < 128 + 127) {
                        out.write((char) x - 128);
                    }
                    else {
                        out.write('^');
                        out.write('?');
                    }
                }
                else {
                    out.write('^');
                    out.write((char) x - 128 + 64);
                }
            }
        }
        else if (x == '\t' && !showTabulation) {
            out.write('\t');
        }
        else if (x == '\n') {
            out.write('\n');
        }
        else {
            out.write('^');
            out.write((char) x + 64);
        }
    }
}
