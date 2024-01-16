package org.example;

import java.awt.*;

public class GridCell {

    public GridCell() {
        this.active = false;
        this.activeFor = 0;
    }

    private boolean active;
    private boolean setOnCalcFinished;
    private int activeFor;

    private final Color ACTIVE_1_COLOR = Color.GREEN;
    private final Color ACTIVE_5_COLOR = Color.GRAY;

    private final Color INACTIVE_COLOR = Color.WHITE;


    public void calcFinished() {
        if (active && setOnCalcFinished) {
            activeFor += 1;
        }
        if (!setOnCalcFinished) {
            activeFor = 0;
        }
        active = setOnCalcFinished;
    }
    public void setOnCalcFinished(boolean state) { setOnCalcFinished = state; }
    public void setState(boolean state) {
        setOnCalcFinished = state;
        calcFinished();
    }
    public void toggle() { active = !active; }
    public Color getColor() {
        if (active) {
            if (activeFor >= 5) {
                return ACTIVE_5_COLOR;
            }
            return ACTIVE_1_COLOR;
        }
        return INACTIVE_COLOR;
    }

    public boolean isActive() { return active; }
}
