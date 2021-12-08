package com.pischyk.task5.state;

import com.pischyk.task5.entity.Ship;
import com.pischyk.task5.exception.PortException;

import java.io.IOException;

public interface ShipState {
    void waitingState(Ship ship);

    void receptionState(Ship ship) throws PortException, IOException;

    void endState(Ship ship) throws PortException, IOException;
}
