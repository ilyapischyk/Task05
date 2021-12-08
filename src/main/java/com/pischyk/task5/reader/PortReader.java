package com.pischyk.task5.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PortReader {

    public int[] readFromFile(String filename) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        Properties properties = new Properties();
        properties.load(fileInputStream);
        int[] value = new int[4];
        value[0] = Integer.parseInt(properties.getProperty("countOfPorts"));
        value[1] = Integer.parseInt(properties.getProperty("countOfShips"));
        value[2] = Integer.parseInt(properties.getProperty("containerCapacity"));
        value[3] = Integer.parseInt(properties.getProperty("containerNumber"));
        return value;
    }
}
