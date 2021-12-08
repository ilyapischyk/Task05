package com.pischyk.task5.entity;

import com.pischyk.task5.exception.PortException;
import com.pischyk.task5.reader.PortReader;
import com.pischyk.task5.validator.PortValidator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Port {
    private static Logger logger = LogManager.getLogger();
    private static Port instance;
    private final int TASK_PERIOD = 500;
    private int containerCapacity;
    private int containerNumber;
    private List<Pier> piers;
    private ArrayDeque<Ship> ships;
    private Semaphore semaphore;
    private static final AtomicBoolean instanceInitialized = new AtomicBoolean(false);

    private Port() throws PortException, IOException {
        PortReader portReader = new PortReader();
        int[] value = portReader.readFromFile("src\\main\\resources\\port.properties");
        PortValidator validator = new PortValidator();
        if (!validator.isValidate(value)) {
            logger.log(Level.ERROR, "Incorrect data " + value);
            throw new PortException("Incorrect data " + value);
        }
        int countOfPiers = value[0];
        int countOfShips = value[1];
        containerCapacity = value[2];
        containerNumber = value[3];
        ships = new ArrayDeque<>(countOfShips);
        for (int i = 0; i < countOfShips; i++) {
            ships.add(new Ship());
        }
        piers = new ArrayList<>(countOfPiers);
        for (int i = 0; i < countOfPiers; i++) {
            piers.add(new Pier());
        }
        semaphore = new Semaphore(countOfPiers, true);
        checkSchedule();
        logger.log(Level.INFO, "Create port " + this);
    }

    public static Port getInstance() throws PortException, IOException {
        while (instance == null) {
            if (instanceInitialized.compareAndSet(false, true)) {
                instance = new Port();
            }
        }
        return instance;
    }

    public Semaphore getSemaphore() {
        return semaphore;
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

    public List<Pier> getPiers() {
        return piers;
    }

    public ArrayDeque<Ship> getShips() {
        return ships;
    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }

    public void removeShip(Ship ship) {
        ships.remove(ship);
    }

    public void load(Ship ship) {
        if (ship.getDifference() <= containerNumber) {
            containerNumber -= ship.getDifference();
            ship.setContainerNumber(ship.getContainerCapacity());
        } else {
            ship.setContainerNumber(ship.getContainerNumber() + this.getDifference());
            containerNumber = 0;
        }
        logger.log(Level.INFO, "Number of containers after loading: ship " + ship.getShipId() + " = " +
                ship.getContainerNumber() + "; port = " + containerNumber);
    }

    public void unload(Ship ship) {
        if (this.getDifference() > ship.getContainerNumber()) {
            containerNumber += ship.getContainerNumber();
            ship.setContainerNumber(0);
        } else {
            ship.setContainerNumber(ship.getContainerNumber() - this.getDifference());
            containerNumber = containerCapacity;
        }
        logger.log(Level.INFO, "Number of containers after loading: ship " + ship.getShipId() + " = " +
                ship.getContainerNumber() + "; port = " + containerNumber);
    }

    private void checkSchedule() {
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.log(Level.INFO, "Number of containers in port = " + containerNumber);
                long percent = 100 * containerNumber / containerCapacity;
                if (percent > 75 || percent < 25) {
                    containerNumber = containerCapacity / 2;
                }
            }
        };
        timer.schedule(timerTask, 0, TASK_PERIOD);
    }
}
