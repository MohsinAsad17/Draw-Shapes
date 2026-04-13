package com.drawshapes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Shape {
    private ShapeType type;
    private int x, y, width, height;
    private List<Point> points = new ArrayList<>();
    private String strokeColor = "#000000";
    private String fillColor = null;
    private boolean isFilled = false;
    private int strokeThickness = 2;
    private StrokeStyle strokeStyle = StrokeStyle.SOLID;

    // Constructors
    public Shape() {}

    public Shape(ShapeType type, int x, int y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Getters and Setters
    public ShapeType getType() { return type; }
    public void setType(ShapeType type) { this.type = type; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }

    public String getStrokeColor() { return strokeColor; }
    public void setStrokeColor(String strokeColor) { this.strokeColor = strokeColor; }

    public String getFillColor() { return fillColor; }
    public void setFillColor(String fillColor) { this.fillColor = fillColor; }

    public boolean isFilled() { return isFilled; }
    public void setFilled(boolean filled) { isFilled = filled; }

    public int getStrokeThickness() { return strokeThickness; }
    public void setStrokeThickness(int strokeThickness) { this.strokeThickness = strokeThickness; }

    public StrokeStyle getStrokeStyle() { return strokeStyle; }
    public void setStrokeStyle(StrokeStyle strokeStyle) { this.strokeStyle = strokeStyle; }

    @JsonIgnore
    public java.awt.Shape getAwtShape() {
        switch (type) {
            case RECTANGLE:
                return new Rectangle(x, y, width, height);
            case CIRCLE:
                return new java.awt.geom.Ellipse2D.Float(x, y, width, height);
            case TRIANGLE:
                Polygon triangle = new Polygon();
                triangle.addPoint(x + width / 2, y);
                triangle.addPoint(x, y + height);
                triangle.addPoint(x + width, y + height);
                return triangle;
            case LINE:
            case CURVE:
                Path2D path = new Path2D.Float();
                if (!points.isEmpty()) {
                    path.moveTo(points.get(0).x, points.get(0).y);
                    for (int i = 1; i < points.size(); i++) {
                        path.lineTo(points.get(i).x, points.get(i).y);
                    }
                }
                return path;
            default:
                return null;
        }
    }

    public boolean contains(Point p) {
        java.awt.Shape s = getAwtShape();
        if (s == null) return false;
        if (type == ShapeType.LINE || type == ShapeType.CURVE) {
            // For lines, check proximity
            return s.getBounds2D().intersects(p.x - 5, p.y - 5, 10, 10);
        }
        return s.contains(p);
    }

    public Shape copy() {
        Shape copy = new Shape();
        copy.type = this.type;
        copy.x = this.x;
        copy.y = this.y;
        copy.width = this.width;
        copy.height = this.height;
        copy.points = new ArrayList<>(this.points);
        copy.strokeColor = this.strokeColor;
        copy.fillColor = this.fillColor;
        copy.isFilled = this.isFilled;
        copy.strokeThickness = this.strokeThickness;
        copy.strokeStyle = this.strokeStyle;
        return copy;
    }
}
