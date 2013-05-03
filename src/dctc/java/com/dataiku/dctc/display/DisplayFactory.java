package com.dataiku.dctc.display;

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
        } else if (type.equals("list")) {
            return new ListDisplay();
        } else if (type.equals("pretty") || type.equals("auto")) {
            try {
                // NCurses is not always available
                return new NCursesDisplay();
            } catch (Throwable t) {
                return new LessSimpleDisplay();
            }
        } else {
            return new EmptyDisplay();
        }
    }

    private String type;
}
