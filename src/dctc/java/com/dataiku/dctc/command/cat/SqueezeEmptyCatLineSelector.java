package com.dataiku.dctc.command.cat;

class SqueezeEmptyCatLineSelector implements CatLineSelector {
    public boolean needPrint(String line) {
        if (prevReturn) {
            if (line.isEmpty()) {
                return false;
            }
            else {
                prevReturn = false;
                return true;
            }
        }
        else {
            prevReturn = line.isEmpty();
            return true;
        }
    }

    private boolean prevReturn;
}
