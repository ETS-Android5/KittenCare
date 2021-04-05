package com.pleiades.pleione.kittencare.object;

public class Costume {
    public int costumeCode, costumeType;
    public boolean isUnlocked = false;

    public Costume(int costumeCode, int costumeType) {
        this.costumeCode = costumeCode;
        this.costumeType = costumeType;
    }
}
