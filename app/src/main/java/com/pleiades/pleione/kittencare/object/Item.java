package com.pleiades.pleione.kittencare.object;

public class Item implements Comparable<Item> {
    public int itemCode, itemType, quantity;

    public Item(int itemCode, int itemType, int quantity) {
        this.itemCode = itemCode;
        this.itemType = itemType;
        this.quantity = quantity;
    }

    @Override
    public int compareTo(Item item) {
        return Integer.compare(this.itemCode, item.itemCode);
    }
}