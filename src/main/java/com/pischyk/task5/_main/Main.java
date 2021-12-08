package com.pischyk.task5._main;

import com.pischyk.task5.entity.Port;
import com.pischyk.task5.entity.Ship;
import com.pischyk.task5.exception.PortException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        Port port;
        try {
            port = Port.getInstance();
            Iterator<Ship> iterator = port.getShips().iterator();
            ExecutorService service = Executors.newFixedThreadPool(port.getShips().size());
            List<Future> futures = new ArrayList<>();
            while (iterator.hasNext()) {
                Ship ship = iterator.next();
                Future future = service.submit(ship);
                futures.add(future);
            }
            service.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PortException e) {
            e.printStackTrace();
        }
    }
}
