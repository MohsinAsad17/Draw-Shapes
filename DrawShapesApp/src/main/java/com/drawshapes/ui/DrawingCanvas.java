package com.drawshapes.ui;

import com.drawshapes.model.Shape;
import com.drawshapes.model.ShapeType;
import com.drawshapes.model.StrokeStyle;
import com.drawshapes.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class DrawingCanvas extends JPanel {
    private List<Shape> shapes = new ArrayList<>();
    private Shape selectedShape;
    private int handleIndex = -1;
    private Point lastMousePos;
    private String currentStrokeColor = "#000000";
    private String currentFillColor = "#FFFFFF";
    private int currentThickness = 2;
    private StrokeStyle currentStyle = StrokeStyle.SOLID;
    private String currentTool = "RECTANGLE";
    private UndoManager undoManager;
    private Runnable onStateChange;

    public DrawingCanvas() {
        setLayout(null);
        setBackground(Color.WHITE);
        undoManager = new UndoManager(shapes);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    public void setOnStateChange(Runnable r) { this.onStateChange = r; }

    private void notifyStateChange() {
        if (onStateChange != null) onStateChange.run();
    }

    private void handleMousePressed(MouseEvent e) {
        lastMousePos = e.getPoint();
        if ("SELECT".equals(currentTool)) {
            if (selectedShape != null) {
                handleIndex = getHandleAt(e.getPoint());
                if (handleIndex != -1) return;
            }
            selectedShape = null;
            for (int i = shapes.size() - 1; i >= 0; i--) {
                if (shapes.get(i).contains(e.getPoint())) {
                    selectedShape = shapes.get(i);
                    break;
                }
            }
        } else if ("FILL".equals(currentTool)) {
            for (int i = shapes.size() - 1; i >= 0; i--) {
                if (shapes.get(i).contains(e.getPoint())) {
                    shapes.get(i).setFilled(true);
                    shapes.get(i).setFillColor(currentFillColor);
                    undoManager.addState(shapes);
                    notifyStateChange();
                    break;
                }
            }
        } else if ("LINE".equals(currentTool) || "CURVE".equals(currentTool)) {
            Shape newShape = new Shape(ShapeType.valueOf(currentTool), 0, 0, 0, 0);
            newShape.setStrokeColor(currentStrokeColor);
            newShape.setStrokeThickness(currentThickness);
            newShape.setStrokeStyle(currentStyle);
            newShape.getPoints().add(e.getPoint());
            shapes.add(newShape);
            selectedShape = newShape;
        }
        repaint();
    }

    private void handleMouseDragged(MouseEvent e) {
        if ("SELECT".equals(currentTool) && selectedShape != null) {
            int dx = e.getX() - lastMousePos.x;
            int dy = e.getY() - lastMousePos.y;
            if (handleIndex == -1) {
                selectedShape.setX(selectedShape.getX() + dx);
                selectedShape.setY(selectedShape.getY() + dy);
                for (Point p : selectedShape.getPoints()) {
                    p.translate(dx, dy);
                }
            } else {
                resizeShape(selectedShape, handleIndex, dx, dy);
            }
        } else if (("LINE".equals(currentTool) || "CURVE".equals(currentTool)) && selectedShape != null) {
            if ("LINE".equals(currentTool)) {
                if (selectedShape.getPoints().size() > 1) {
                    selectedShape.getPoints().set(1, e.getPoint());
                } else {
                    selectedShape.getPoints().add(e.getPoint());
                }
            } else {
                selectedShape.getPoints().add(e.getPoint());
            }
        }
        lastMousePos = e.getPoint();
        repaint();
    }

    private void handleMouseReleased(MouseEvent e) {
        if (("LINE".equals(currentTool) || "CURVE".equals(currentTool)) && selectedShape != null) {
            undoManager.addState(shapes);
            notifyStateChange();
        } else if ("SELECT".equals(currentTool) && selectedShape != null) {
            undoManager.addState(shapes);
            notifyStateChange();
        }
        handleIndex = -1;
    }

    private void resizeShape(Shape s, int h, int dx, int dy) {
        switch (h) {
            case 0: s.setX(s.getX() + dx); s.setY(s.getY() + dy); s.setWidth(s.getWidth() - dx); s.setHeight(s.getHeight() - dy); break;
            case 1: s.setY(s.getY() + dy); s.setHeight(s.getHeight() - dy); break;
            case 2: s.setY(s.getY() + dy); s.setWidth(s.getWidth() + dx); s.setHeight(s.getHeight() - dy); break;
            case 3: s.setWidth(s.getWidth() + dx); break;
            case 4: s.setWidth(s.getWidth() + dx); s.setHeight(s.getHeight() + dy); break;
            case 5: s.setHeight(s.getHeight() + dy); break;
            case 6: s.setX(s.getX() + dx); s.setWidth(s.getWidth() - dx); s.setHeight(s.getHeight() + dy); break;
            case 7: s.setX(s.getX() + dx); s.setWidth(s.getWidth() - dx); break;
        }
    }

    private int getHandleAt(Point p) {
        if (selectedShape == null) return -1;
        Rectangle b = selectedShape.getAwtShape().getBounds();
        Point[] handles = {
            new Point(b.x, b.y), new Point(b.x + b.width / 2, b.y), new Point(b.x + b.width, b.y),
            new Point(b.x + b.width, b.y + b.height / 2), new Point(b.x + b.width, b.y + b.height),
            new Point(b.x + b.width / 2, b.y + b.height), new Point(b.x, b.y + b.height),
            new Point(b.x, b.y + b.height / 2)
        };
        for (int i = 0; i < handles.length; i++) {
            if (p.distance(handles[i]) < 5) return i;
        }
        return -1;
    }

    public void spawnShape(ShapeType type) {
        int w = 100, h = 100;
        Shape s = new Shape(type, getWidth() / 2 - w / 2, getHeight() / 2 - h / 2, w, h);
        s.setStrokeColor(currentStrokeColor);
        s.setStrokeThickness(currentThickness);
        s.setStrokeStyle(currentStyle);
        shapes.add(s);
        undoManager.addState(shapes);
        notifyStateChange();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Shape s : shapes) {
            drawShape(g2, s);
        }

        if (selectedShape != null && "SELECT".equals(currentTool)) {
            drawHandles(g2, selectedShape);
        }
    }

    private void drawShape(Graphics2D g2, Shape s) {
        java.awt.Shape awtShape = s.getAwtShape();
        if (s.isFilled() && s.getFillColor() != null) {
            g2.setColor(Color.decode(s.getFillColor()));
            g2.fill(awtShape);
        }

        g2.setColor(Color.decode(s.getStrokeColor()));
        Stroke stroke;
        float[] dash = {10.0f};
        switch (s.getStrokeStyle()) {
            case DASHED: stroke = new BasicStroke(s.getStrokeThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f); break;
            case WAVY: 
                stroke = new BasicStroke(s.getStrokeThickness()); 
                break;
            default: stroke = new BasicStroke(s.getStrokeThickness()); break;
        }
        g2.setStroke(stroke);
        g2.draw(awtShape);
    }

    private void drawHandles(Graphics2D g2, Shape s) {
        Rectangle b = s.getAwtShape().getBounds();
        int hs = 6;
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(1));
        int[] xs = {b.x, b.x + b.width / 2, b.x + b.width, b.x + b.width, b.x + b.width, b.x + b.width / 2, b.x, b.x};
        int[] ys = {b.y, b.y, b.y, b.y + b.height / 2, b.y + b.height, b.y + b.height, b.y + b.height, b.y + b.height / 2};
        for (int i = 0; i < 8; i++) {
            g2.fillRect(xs[i] - hs / 2, ys[i] - hs / 2, hs, hs);
        }
        g2.drawRect(b.x, b.y, b.width, b.height);
    }

    public void setCurrentStrokeColor(String c) { this.currentStrokeColor = c; }
    public void setCurrentFillColor(String c) { this.currentFillColor = c; }
    public void setCurrentThickness(int t) { this.currentThickness = t; }
    public void setCurrentStyle(StrokeStyle s) { this.currentStyle = s; }
    public void setCurrentTool(String t) { this.currentTool = t; }

    public void undo() { shapes = undoManager.undo(); selectedShape = null; repaint(); }
    public void redo() { shapes = undoManager.redo(); selectedShape = null; repaint(); }
    
    public int getBranchCount() { return undoManager.getBranchCount(); }
    public int getCurrentBranchIndex() { return undoManager.getCurrentBranchIndex(); }
    public void switchToBranch(int i) { shapes = undoManager.getCurrentState(); undoManager.switchToBranch(i); shapes = undoManager.getCurrentState(); repaint(); }
    
    public List<Shape> getShapes() { return shapes; }
    public void setShapes(List<Shape> shapes) { 
        this.shapes = shapes; 
        undoManager = new UndoManager(shapes);
        repaint(); 
    }
}
