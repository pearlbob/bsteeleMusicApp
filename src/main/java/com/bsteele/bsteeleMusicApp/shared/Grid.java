/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import java.util.ArrayList;

/**
 *
 * @author bob
 * @param <T>
 */
public class Grid<T> {

    public Grid() {
    }
    
    /**
     * Copy constructor for a deep copy.
     * @param other
     */
   public Grid(Grid<T> other) {
        int rLimit = other.getRowCount();
        for (int r = 0; r < rLimit; r++) {
            ArrayList<T> row = other.getRow(r);
            int colLimit = row.size();
            for (int c = 0; c < colLimit; c++) {
                add( c, r, row.get(c));
            }
        }
    }

    public final void add(int x, int y, T t) {
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

    public void set(int x, int y, T t) {
        if (y >= grid.size()) {
            throw new ArrayIndexOutOfBoundsException("row is not incremental: y: " + y + " vs " + grid.size());
        }

        ArrayList<T> row = grid.get(y);
        row.set(x, t);
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
