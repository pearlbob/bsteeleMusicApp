/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A generic grid used to store data presenations to the user.
 *
 * @param <T> the type of data stored in the grid
 * @author bob
 */
public class Grid<T> {

    public Grid() {
    }

    /**
     * Deep copy not as a constructor or hide if from javascript.
     *
     * @param other the grid to copy
     * @return the copy
     */
    public Grid<T> deepCopy(Grid<T> other) {
        if (other == null)
            return null;
        int rLimit = other.getRowCount();
        for (int r = 0; r < rLimit; r++) {
            ArrayList<T> row = other.getRow(r);
            int colLimit = row.size();
            for (int c = 0; c < colLimit; c++) {
                addTo(c, r, row.get(c));
            }
        }
        return this;
    }

    public boolean isEmpty() {
        return grid.isEmpty();
    }

    public final void addTo(int x, int y, T t) {
        ArrayList<T> row;
        while (y >= grid.size()) {
            //  addTo a new row to the grid
            row = new ArrayList<>();
            grid.add(grid.size(), row);
        }

        row = grid.get(y);
        if (x == row.size())
            row.add(t);
        else
            row.add(x, t);
    }

    public final void add(T t) {
        if (grid.isEmpty())
            addTo(0, 0, t);
        else {
            int r = grid.size() - 1;
            ArrayList<T> row = grid.get(r);
            addTo(row.size(), r, t);
        }
    }

    public final void set(int x, int y, T t) {
        if (y >= grid.size()) {
            throw new ArrayIndexOutOfBoundsException("row is not incremental: y: " + y + " vs " + grid.size());
        }

        ArrayList<T> row = grid.get(y);
        row.set(x, t);
    }

    public final int lastRowSize() {
        if (grid.isEmpty())
            return 0;
        return getRow(grid.size() - 1).size();
    }

    @Override
    public String toString() {
        return "Grid{" + grid + '}';
    }

    public final T get(int x, int y) {
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

    @Override
    public int hashCode() {
        return Objects.hashCode(this.grid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Grid<?> other = (Grid<?>) obj;
        return Objects.equals(this.grid, other.grid);
    }

    private final ArrayList<ArrayList<T>> grid = new ArrayList<>();
    private static final String emptyStringArray[] = new String[0];
}
