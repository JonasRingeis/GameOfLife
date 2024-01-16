package org.example;

import javax.swing.*;
import java.awt.*;

public class UIBuilder {
    private static final int DELAY_MIN = 10;
    private static final int DELAY_MAX = 1000;
    private static final int DELAY_INIT = 500;

    public static JPanel createTopButtons() {
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> GameOfLife.getInstance().clear());

        JButton nextStepButton = new JButton("Next Step");
        nextStepButton.addActionListener(e -> GameOfLife.getInstance().update());

        JButton startStopButton = new JButton("Start/ Stop");
        startStopButton.addActionListener(e -> GameOfLife.getInstance().toggleAutoRunner());

        JSlider updateDelaySlider = new JSlider(JSlider.HORIZONTAL, DELAY_MIN, DELAY_MAX, DELAY_INIT);
        updateDelaySlider.addChangeListener(e -> GameOfLife.getInstance().autoRunDelayValueChanged(((JSlider) e.getSource()).getValue()));
        updateDelaySlider.setMajorTickSpacing(10);
        updateDelaySlider.setMinorTickSpacing(1);
        updateDelaySlider.setBorder(BorderFactory.createTitledBorder("Update Delay"));

        //Put All in One Layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(clearButton);
        buttonPanel.add(nextStepButton);
        buttonPanel.add(startStopButton);
        buttonPanel.add(updateDelaySlider);
        return buttonPanel;
    }
    public static JPanel createBottomButtons() {
        return null;
    }
}
