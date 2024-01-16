package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameOfLife extends JPanel {

    private static final int CELL_SIZE = 10; // Size of each cell in the grid
    private static final int GRID_WIDTH = 100; // Number of cells in the grid horizontally
    private static final int GRID_HEIGHT = 100; // Number of cells in the grid vertically
    private static final Color BORDER_COLOR = Color.GRAY; // Color of the grid border
    private final boolean ALTERNATIVE_RULE_SET = false;

    private static GameOfLife singleton;

    private final GridCell[][] gridCells;
    private Timer timer;
    private boolean autoRun = false;
    private int autoRunDelay = 500;
    private boolean autoRunDelayValueUpdated = false;

    public GameOfLife() {
        timer = new Timer();
        gridCells = new GridCell[GRID_WIDTH][GRID_HEIGHT];

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                gridCells[x][y] = new GridCell();
            }
        }

        // Add mouse listener to the canvas
        addMouseListener(new java.awt.event.MouseAdapter() {
            private Vector2 startPos;

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int x = e.getX() / CELL_SIZE;
                    int y = e.getY() / CELL_SIZE;
                    toggleGridCell(x, y);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("registered start of right click");
                    startPos = new Vector2(e.getX() / CELL_SIZE, e.getY() / CELL_SIZE);
                    System.out.println(startPos);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && startPos != null) {
                    Vector2 endPos = new Vector2(e.getX() / CELL_SIZE, e.getY() / CELL_SIZE);
                    System.out.println("Registered end of right click");
                    System.out.println("End pos: " + endPos);
                    if (endPos.x > startPos.x) {
                        for (int x = startPos.x; x < endPos.x; x++) {
                            if (endPos.y > startPos.y) {
                                for (int y = startPos.y; y < endPos.y; y++) {
                                    setGridCellState(new Vector2(x, y), true);
                                }
                            } else {
                                for (int y = endPos.y; y < startPos.y; y++) {
                                    setGridCellState(new Vector2(x, y), true);
                                }
                            }
                        }
                    }
                    else {
                        for (int x = endPos.x; x < startPos.x; x++) {
                            if (endPos.y > startPos.y) {
                                for (int y = startPos.y; y < endPos.y; y++) {
                                    setGridCellState(new Vector2(x, y), true);
                                }
                            } else {
                                for (int y = endPos.y; y < startPos.y; y++) {
                                    setGridCellState(new Vector2(x, y), true);
                                }
                            }
                        }
                    }
                    startPos = null;
                    repaint();
                }
            }

        });

        // Create UI
        setLayout(new BorderLayout());
        add(UIBuilder.createTopButtons(), BorderLayout.NORTH);
//        add(UIBuilder.createBottomButtons(), BorderLayout.SOUTH);
    }

    public void autoRunDelayValueChanged(int newVal) {
        autoRunDelay = newVal;
        autoRunDelayValueUpdated = true;
    }
    private void autoRunLoop() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
                if (autoRunDelayValueUpdated) {
                    timer.cancel();
                    autoRunDelayValueUpdated = false;
                    autoRunLoop();
                }
            }
        }, 0, autoRunDelay);
    }
    public void toggleAutoRunner() {
        if (autoRun) {
            timer.cancel();
        } else {
            autoRunLoop();
        }
        autoRun = !autoRun;
    }

    public void clear() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                setGridCellState(new Vector2(x, y), false);
            }
        }
        repaint();
        if (autoRun) {
            toggleAutoRunner();
        }
    }
    public void update() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {

                Vector2 cellPos = new Vector2(x, y);
                GridCell cellToCheck = getGridCell(cellPos);

                int activeSurroundingCells = getActiveSurroundingCellCount(cellPos);
                setGridCellStateOnCalcFinished(cellPos, checkCellState(cellToCheck, activeSurroundingCells));
            }
        }
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                gridCells[x][y].calcFinished();
            }
        }
        repaint();
    }

    private boolean checkCellState(GridCell cell, int surroundingCount) {
        if (ALTERNATIVE_RULE_SET) {
            return switch (surroundingCount) {
                case 0, 2, 4, 6, 8 -> false;
                case 1, 3, 5, 7 -> true;
                default -> false;
            };
        }
        if (cell.isActive()) {
            return switch (surroundingCount) {
                case 0, 1 -> false; //loneliness
                case 2, 3 -> true;
                default -> false; //over population
            };
        }
        else {
            return surroundingCount == 3;
        }
    }

    private int getActiveSurroundingCellCount(Vector2 callPos) {
        return (int)(getSurroundingCells(callPos).stream().filter(cell -> cell.isActive()).count());
    }
    private List<GridCell> getSurroundingCells(Vector2 cellPos) {
        List<GridCell> surroundingCells = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Vector2 posToCheck = cellPos.add(new Vector2(x, y));
                if (x == 0 && y == 0) {
                    continue; //skip center cell because it's the cell to check
                }
                if (isValidCoordinates(posToCheck)) {
                    surroundingCells.add(getGridCell(posToCheck));
                }
            }
        }
        return surroundingCells;
    }

    public void toggleGridCell(int x, int y) {
        if (isValidCoordinates(x, y)) {
            gridCells[x][y].toggle();
            repaint(); // Redraw the grid after updating the color
        }
    }
    private boolean isValidCoordinates(int x, int y) {
        return x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT;
    }
    private boolean isValidCoordinates(Vector2 pos) { return isValidCoordinates(pos.x, pos.y); }
    private GridCell getGridCell(Vector2 pos) { return gridCells[pos.x][pos.y]; }
    private void setGridCellStateOnCalcFinished(Vector2 pos, boolean state) { gridCells[pos.x][pos.y].setOnCalcFinished(state); }
    private void setGridCellState(Vector2 pos, boolean state) { gridCells[pos.x][pos.y].setState(state); }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the grid
        g2d.setColor(Color.WHITE); // Set the cell color to white
        g2d.fillRect(0, 0, GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE); // Fill the entire canvas

        // Draw the border
        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(0, 0, GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);

        // Draw the rectangles with their respective colors
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                g2d.setColor(gridCells[x][y].getColor());
                g2d.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw the vertical grid lines
        g2d.setColor(BORDER_COLOR);
        for (int x = 0; x <= GRID_WIDTH * CELL_SIZE; x += CELL_SIZE) {
            g2d.drawLine(x, 0, x, GRID_HEIGHT * CELL_SIZE);
        }

        // Draw the horizontal grid lines
        g2d.setColor(BORDER_COLOR);
        for (int y = 0; y <= GRID_HEIGHT * CELL_SIZE; y += CELL_SIZE) {
            g2d.drawLine(0, y, GRID_WIDTH * CELL_SIZE, y);
        }
    }

    public static GameOfLife getInstance() { return singleton; }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        frame.setResizable(true);

        GameOfLife game = new GameOfLife();
        singleton = game;
        frame.add(game);

        frame.setVisible(true);
    }
}