package com.pischyk.task5.validator;

public class PortValidator {
    public boolean isValidate(int[] value) {
        boolean isValid = !(value[2] < value[3] || value[3] < 0 || value[0] < 1 || value[1] < 1);
        return isValid;
    }
}
