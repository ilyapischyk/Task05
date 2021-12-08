package com.pischyk.task5.entity;

public class Pier {
    private boolean isEmpty;

    public Pier() {
        isEmpty = true;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty() {
        isEmpty = !isEmpty;
    }
}
