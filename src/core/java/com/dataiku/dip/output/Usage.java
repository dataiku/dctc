package com.dataiku.dip.output;

import static com.dataiku.dip.output.PrettyString.eol;

public class Usage {
    public void print(String command, String msg) {
    }

    public void print(String message) {
        boolean hasPrint = false;
        int index = 0;
        StringBuilder sb = new StringBuilder();
        index = indent(sb, firstLineIndentSize);
        for (String word: message.split(" ")) {
            if (index + word.length() >= termSize && hasPrint) {
                sb.append(eol());
                index = indent(sb, indentSize);
                sb.append(word);
            }
            else {
                if (hasPrint) {
                    sb.append(separator);
                }
                hasPrint = true;
                sb.append(word);
            }
            index += word.length() + separator.length();
        }
        System.out.print(sb.toString());
        if (index != 0) {
            System.out.println();
        }
        System.out.flush();
    }


    // Getterrs/Setters
    public int getFirstLineIndentSize() {
        return firstLineIndentSize;
    }
    public void setFirstLineIndentsize(int firstLineIndentSize) {
        this.firstLineIndentSize = firstLineIndentSize;
    }
    public int getIndentSize() {
        return indentSize;
    }
    public void setIndentSize(int indentSize) {
        this.indentSize = indentSize;
    }
    public String getSeparator() {
        return separator;
    }
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    public String getIndentString() {
        return indentString;
    }
    public void setIndentString(String indentString) {
        this.indentString = indentString;
    }
    public String getTextIndentSeparator() {
        return textIndentSeparator;
    }
    public void setTextIndentSeparator(String textIndentSeparator) {
        this.textIndentSeparator = textIndentSeparator;
    }
    public int getTermSize() {
        return termSize;
    }
    public void setTermSize(int termSize) {
        this.termSize = termSize;
    }

    // Private methods
    private int indent(StringBuilder sb, int indentSize) {
        for (int i = 0; i < indentSize; ++i) {
            sb.append(indentString);
        }
        sb.append(textIndentSeparator);
        return indentSize * indentString.length()
            + textIndentSeparator.length();
    }

    // Attributes
    private String separator = " ";
    private String indentString = " ";
    private String textIndentSeparator = "";
    private int firstLineIndentSize = 2;
    private int indentSize = 2;
    private int termSize = 80;
}
