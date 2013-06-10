package com.dataiku.dctc.display;

import com.dataiku.dip.utils.PrettyString;

public class DisplayFactory {
    public DisplayFactory(String type) {
        setType(type);
    }
    public void setType(String type) {
        this.type = type;
    }

    public ThreadedDisplay build() {
        if (type == null) type = "auto";
        if (type.equals("simple")) {
            return new SimpleDisplay();
        }
        else if (type.equals("list")  || !PrettyString.isInteractif()) {
            return new ListDisplay();
        }
        else if (type.equals("tty-pretty")) {
            return new LessSimpleDisplay();
        }
        else if (type.equals("pretty") || type.equals("auto")) {
            try {
                // NCurses is not always available
                return new NCursesDisplay();
            }
            catch (Throwable t) {
                t.printStackTrace();
                System.err.println(t.getMessage());
                return new LessSimpleDisplay();
            }
        }
        else {
            return new EmptyDisplay();
        }
    }

    private String type;
}
