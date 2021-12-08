package com.pischyk.task5.entity;

import com.pischyk.task5.exception.PortException;
import com.pischyk.task5.state.ShipState;
import com.pischyk.task5.state.impl.WaitingState;
import com.pischyk.task5.util.IdGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Ship implements Callable {
    private static Logger logger = LogManager.getLogger();
    private int shipId;
    private int containerCapacity;
    private int containerNumber;
    private ShipState shipState;
    private boolean isLoaded;
    private boolean isUnloaded;

    public Ship() {
        this.shipId = IdGenerator.generateId();
        this.containerCapacity = (int) (900 * Math.random()) + 100;
        this.containerNumber = (int) (this.containerCapacity * Math.random());
        double random = Math.random();
        if (random > 0.5) {
            isLoaded = true;
            random = Math.random();
            isUnloaded = random > 0.5;
        } else {
            isLoaded = false;
            isUnloaded = true;
        }
    }

    public int getShipId() {
        return shipId;
    }

    public int getContainerCapacity() {
        return containerCapacity;
    }

    public int getContainerNumber() {
        return containerNumber;
    }

    public int getDifference() {
        return containerCapacity - containerNumber;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isUnloaded() {
        return isUnloaded;
    }

    public void setContainerNumber(int containerNumber) {
        this.containerNumber = containerNumber;
    }

    public void setShipState(ShipState shipState) {
        this.shipState = shipState;
    }

    @Override
    public String call() throws PortException, IOException {
        Port port = Port.getInstance();
        Semaphore semaphore = port.getSemaphore();
        List<Pier> piers = port.getPiers();
        try {
            this.shipState = WaitingState.getInstance();
            shipState.waitingState(this);
            semaphore.acquire();
            int number = -1;
            synchronized (piers) {
                for (int i = 0; i < piers.size(); i++) {
                    if (piers.get(i).isEmpty()) {
                        piers.get(i).setEmpty();
                        number = i;
                        break;
                    }
                }
                shipState.receptionState(this);
                TimeUnit.MILLISECONDS.sleep(1000);
                piers.get(number).setEmpty();
            }
            semaphore.release();
            shipState.endState(this);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            logger.log(Level.INFO, e.getMessage());
        }
        return "";
    }
}