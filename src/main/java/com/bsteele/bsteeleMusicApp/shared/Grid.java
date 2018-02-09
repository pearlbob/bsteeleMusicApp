/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import java.util.ArrayList;

/**
 *
 * @author bob
 */
public class Grid<T> {

    public void add(int x, int y, T t) {
        if (y > grid.size()) {
            throw new ArrayIndexOutOfBoundsException("row is not incremental: y: " + y + " vs " + grid.size());
        }
        ArrayList<T> row;
        if (y == grid.size()) {
            //  add a new row to the grid
            row = new ArrayList<>();
            grid.add(y, row);
        } else {
            row = grid.get(y);
        }

        row.add(x, t);
    }

    @Override
    public String toString() {
        return "Grid{" + "grid=" + grid + '}';
    }

    public T get(int x, int y) {
        try {
            ArrayList<T> row = grid.get(y);
            if (row == null) {
                return null;
            }
            return row.get(x);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public int getRowCount() {
        return grid.size();
    }

    public ArrayList<T> getRow(int y) {
        try {
            return grid.get(y);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public void clear() {
        grid.clear();
    }
    private final ArrayList<ArrayList<T>> grid = new ArrayList<>();
}
