package com.drawshapes.ui;

import com.drawshapes.io.JsonPersistence;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class MainFrame extends JFrame {
    private DrawingCanvas canvas;
    private SettingsPanel settings;
    private JsonPersistence persistence = new JsonPersistence();

    private JComboBox<String> timelineCombo;
    private boolean updatingCombo = false;

    public MainFrame() {
        setTitle("DrawShapes Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());

        canvas = new DrawingCanvas();
        canvas.setOnStateChange(this::updateTimelineCombo);
        settings = new SettingsPanel(canvas);

        add(settings, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> {
            canvas.undo();
            updateTimelineCombo();
        });
        JButton redoBtn = new JButton("Redo");
        redoBtn.addActionListener(e -> {
            canvas.redo();
            updateTimelineCombo();
        });

        timelineCombo = new JComboBox<>();
        updateTimelineCombo();
        timelineCombo.addActionListener(e -> {
            if (!updatingCombo) {
                canvas.switchToBranch(timelineCombo.getSelectedIndex());
            }
        });

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> handleSave());
        JButton loadBtn = new JButton("Load");
        loadBtn.addActionListener(e -> handleLoad());

        bottomPanel.add(undoBtn);
        bottomPanel.add(redoBtn);
        bottomPanel.add(new JLabel("Timeline:"));
        bottomPanel.add(timelineCombo);
        bottomPanel.add(saveBtn);
        bottomPanel.add(loadBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void updateTimelineCombo() {
        updatingCombo = true;
        timelineCombo.removeAllItems();
        int count = canvas.getBranchCount();
        for (int i = 0; i < count; i++) {
            timelineCombo.addItem("Timeline " + (i + 1));
        }
        timelineCombo.setSelectedIndex(canvas.getCurrentBranchIndex());
        updatingCombo = false;
    }

    private void handleSave() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                persistence.save(f, canvas.getShapes());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        }
    }

    private void handleLoad() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                canvas.setShapes(persistence.load(f));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
            }
        }
    }
}
