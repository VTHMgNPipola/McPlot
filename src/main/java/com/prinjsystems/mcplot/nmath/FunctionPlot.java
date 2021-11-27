package com.prinjsystems.mcplot.nmath;

import java.awt.geom.Path2D;

public class FunctionPlot {
    private Path2D.Double path;
    private boolean visible;
    private double startX;
    private double endX;

    public Path2D.Double getPath() {
        return path;
    }

    public void setPath(Path2D.Double path) {
        this.path = path;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }
}
