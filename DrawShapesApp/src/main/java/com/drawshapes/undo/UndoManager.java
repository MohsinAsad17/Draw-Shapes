package com.drawshapes.undo;

import com.drawshapes.model.Shape;
import java.util.ArrayList;
import java.util.List;

public class UndoManager {
    private static class Node {
        List<Shape> state;
        Node parent;
        List<Node> children = new ArrayList<>();

        Node(List<Shape> state, Node parent) {
            this.state = state;
            this.parent = parent;
        }
    }

    private Node current;

    public UndoManager(List<Shape> initialState) {
        current = new Node(copyState(initialState), null);
    }

    public void addState(List<Shape> state) {
        Node newNode = new Node(copyState(state), current);
        current.children.add(newNode);
        current = newNode;
    }

    public List<Shape> undo() {
        if (current.parent != null) {
            current = current.parent;
        }
        return copyState(current.state);
    }

    public List<Shape> redo() {
        if (!current.children.isEmpty()) {
            // Standard redo picks the most recent child
            current = current.children.get(current.children.size() - 1);
        }
        return copyState(current.state);
    }
    
    public void switchToBranch(int index) {
        if (current.parent != null && index < current.parent.children.size()) {
            current = current.parent.children.get(index);
        }
    }

    public int getCurrentBranchIndex() {
        if (current.parent == null) return 0;
        return current.parent.children.indexOf(current);
    }

    public int getBranchCount() {
        if (current.parent == null) return 1;
        return current.parent.children.size();
    }

    private List<Shape> copyState(List<Shape> state) {
        List<Shape> copy = new ArrayList<>();
        for (Shape s : state) {
            copy.add(s.copy());
        }
        return copy;
    }

    public List<Shape> getCurrentState() {
        return copyState(current.state);
    }
}
