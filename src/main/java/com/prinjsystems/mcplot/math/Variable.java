package com.prinjsystems.mcplot.math;

import java.io.Serializable;

public class Variable implements Serializable {
    private static final long serialVersionUID = -2185401147960943765L;

    private String name;
    private double value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
