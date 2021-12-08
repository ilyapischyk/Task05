package com.pischyk.task5.util;

public class IdGenerator {
    private static int counter;

    private IdGenerator() {
    }

    public static int generateId() {
        return ++counter;
    }
}
