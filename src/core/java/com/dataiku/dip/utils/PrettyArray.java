package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;

public class PrettyArray {
    public enum Align {
        LEFT
        , RIGHT
        , CENTER
    };
    public void add(List<String> row) {
        matrix.add(row);
    }
    public void print() {
        List<Integer> colSize = new ArrayList<Integer>();
        for (List<String> row: matrix) {
            for (int i = 0; i < row.size(); ++i) {
                String str = row.get(i);
                if (colSize.size() == i) {
                    colSize.add(str.length());
                }
                else {
                    colSize.set(i, Math.max(colSize.get(i), str.length()));
                }
            }
        }
        for (List<String> row: matrix) {
            int i = 0;
            if (line.contains(i)) {
                line(colSize);
            }
            int size = row.size();
            System.out.print(beginChar);
            for (String cell: row) {
                int indentSize = colSize.get(i) - cell.length();
                boolean isLast = i + 1 == size;
                if (isLast) {
                    if (indentLast) {
                        if (i + 1 == colSize.size()) {
                            print(cell, indentSize, endChar);
                        }
                        else {
                            print(cell, indentSize, betweenChar);
                        }
                    }
                    else {
                        print(cell, -1, endChar);
                    }
                }
                else {
                    print(cell, indentSize, betweenChar);
                }

                ++i;
            }
            if (indentLast) {
                for (int j = size; j < colSize.size(); ++j) {
                    indent(colSize.get(j));
                    if (j + 1 != colSize.size()) {
                        indent(betweenChar.length());
                    }
                    else {
                        System.out.print(endChar);
                    }
                }
            }
            System.out.println();
        }
        if (trailingLine) {
            line(colSize);
        }
    }

    public void rawPrint() {
        for (List<String> row: matrix) {
            for (String cell: row) {
                System.out.print(cell + "	");
            }
            System.out.println();
        }
    }

    public String getTab() {
        return tab;
    }
    public void setTab(String tab) {
        this.tab = tab;
    }
    public void setBeginChar(String beginChar) {
        this.beginChar = beginChar;
    }
    public void setBetweenChar(String betweenChar) {
        this.betweenChar = betweenChar;
    }
    public void setEndChar(String endChar) {
        this.endChar = endChar;
    }
    public void setLineChar(String lineChar) {
        this.lineChar = lineChar;
    }
    public void setLineCellSeparator(String lineCellSeparator) {
        this.lineCellSeparator = lineCellSeparator;
    }
    public void setLineBeginChar(String lineBeginChar) {
        this.lineBeginChar = lineBeginChar;
    }
    public void setLineEndChar(String lineEndChar) {
        this.lineEndChar = lineEndChar;
    }
    public void setIndentLast(boolean indentLast) {
        this.indentLast = indentLast;
    }
    public void setAlign(Align align) {
        this.align = align;
    }
    public void addLineAt(int idx) {
        line.add(idx);
    }
    public void setTrailingLine(boolean trailingLine) {
        this.trailingLine = trailingLine;
    }

    public String getBeginChar() {
        return beginChar;
    }
    public String getBetweenChar() {
        return betweenChar;
    }
    public String getEndChar() {
        return endChar;
    }
    public String getLineChar() {
        return lineChar;
    }
    public String getLineCellSeparator() {
        return lineCellSeparator;
    }
    public String getLineBeginChar() {
        return lineBeginChar;
    }
    public String getLineEndChar() {
        return lineEndChar;
    }
    public boolean getIndentLast() {
        return indentLast;
    }
    public Align getAlign() {
        return align;
    }
    public boolean getTrailingLine() {
        return trailingLine;
    }

    // private
    private void indent(int size) {
        for (int i = 0; i < size; ++i) {
            System.out.print(tab);
        }
    }
    private void indent(int size, Align from) {
        if (align == Align.CENTER) {
            if (from == Align.LEFT) {
                indent(size / 2);
            }
            else {
                indent(size / 2 + (size % 2));
            }
        }
        else {
            if (align != from) {
                indent(size);
            }
        }
    }
    private void print(String cell, int indentSize, String separator) {
        indent(indentSize, Align.LEFT);
        System.out.print(cell);
        indent(indentSize, Align.RIGHT);
        System.out.print(separator);
    }

    private void line(List<Integer> colsSize) {
        System.out.print(lineBeginChar);
        int cellNumber = 0;
        for (Integer colSize: colsSize) {
            for (int i = 0; i < colSize; ++i) {
                System.out.print(lineChar);
            }
            ++cellNumber;
            if (cellNumber >= colsSize.size()) {
                System.out.print(lineEndChar);
            }
            else {
                System.out.print(lineCellSeparator);
            }
        }
        System.out.println();
    }

    private List<List<String>> matrix = new ArrayList<List<String>>();
    private List<Integer> line = new ArrayList<Integer>();
    private String tab = " ";
    private String beginChar = "| ";
    private String betweenChar = " | ";
    private String endChar = " |";
    private String lineChar = "-";
    private String lineCellSeparator = "-+-";
    private String lineBeginChar = "|-";
    private String lineEndChar = "-|";
    private boolean indentLast = true;
    private Align align = Align.LEFT;
    private boolean trailingLine = false;
}
