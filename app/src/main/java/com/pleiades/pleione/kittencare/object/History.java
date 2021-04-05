package com.pleiades.pleione.kittencare.object;

public class History {
    public int historyType, reference; // direct
    public int month, date, hour, minute; // indirect

    public History(int historyType, int reference) {
        this.historyType = historyType;
        this.reference = reference;
    }
}