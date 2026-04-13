package com.drawshapes.ui;

import com.drawshapes.model.ShapeType;
import com.drawshapes.model.StrokeStyle;
import java.awt.*;
import javax.swing.*;

public class SettingsPanel extends JTabbedPane {
    private DrawingCanvas canvas;

    public SettingsPanel(DrawingCanvas canvas) {
        this.canvas = canvas;
        addTab("Line & Fill Settings", createSettingsTab());
        addTab("Shapes & Tools", createToolsTab());
    }

    private JPanel createSettingsTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton strokeColorBtn = new JButton("Stroke Color");
        strokeColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Select Stroke Color", Color.BLACK);
            if (c != null) canvas.setCurrentStrokeColor(toHex(c));
        });

        JButton fillColorBtn = new JButton("Fill Color");
        fillColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Select Fill Color", Color.WHITE);
            if (c != null) canvas.setCurrentFillColor(toHex(c));
        });

        panel.add(strokeColorBtn);
        panel.add(fillColorBtn);

        panel.add(new JLabel("Thickness:"));
        Integer[] thicknesses = {1, 2, 3, 4, 5, 8, 10};
        JComboBox<Integer> thicknessCombo = new JComboBox<>(thicknesses);
        thicknessCombo.setSelectedItem(2);
        thicknessCombo.addActionListener(e -> canvas.setCurrentThickness((Integer) thicknessCombo.getSelectedItem()));
        panel.add(thicknessCombo);

        panel.add(new JLabel("Style:"));
        StrokeStyle[] styles = StrokeStyle.values();
        JComboBox<StrokeStyle> styleCombo = new JComboBox<>(styles);
        styleCombo.addActionListener(e -> canvas.setCurrentStyle((StrokeStyle) styleCombo.getSelectedItem()));
        panel.add(styleCombo);

        return panel;
    }

    private JPanel createToolsTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton(panel, "Select", e -> canvas.setCurrentTool("SELECT"));
        addButton(panel, "Rectangle", e -> { canvas.setCurrentTool("RECTANGLE"); canvas.spawnShape(ShapeType.RECTANGLE); });
        addButton(panel, "Circle", e -> { canvas.setCurrentTool("CIRCLE"); canvas.spawnShape(ShapeType.CIRCLE); });
        addButton(panel, "Triangle", e -> { canvas.setCurrentTool("TRIANGLE"); canvas.spawnShape(ShapeType.TRIANGLE); });
        addButton(panel, "Line", e -> canvas.setCurrentTool("LINE"));
        addButton(panel, "Curve", e -> canvas.setCurrentTool("CURVE"));
        addButton(panel, "Fill Tool", e -> canvas.setCurrentTool("FILL"));

        return panel;
    }

    private void addButton(JPanel panel, String label, java.awt.event.ActionListener al) {
        JButton btn = new JButton(label);
        btn.addActionListener(al);
        panel.add(btn);
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
