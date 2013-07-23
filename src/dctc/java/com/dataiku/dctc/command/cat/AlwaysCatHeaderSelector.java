package com.dataiku.dctc.command.cat;

import com.dataiku.dctc.file.GFile;

public class AlwaysCatHeaderSelector implements CatHeaderPrinter {
    public void print(GFile file) {
        if (first) {
            first = false;
        }
        else {
            System.out.println();
        }
        System.out.print("==> ");
        System.out.print(file.givenName());
        System.out.println(" <==");
    }

    private boolean first = true;
}
