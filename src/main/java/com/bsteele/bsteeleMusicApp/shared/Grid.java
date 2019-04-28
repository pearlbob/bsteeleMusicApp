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
                set(c, r, row.get(c));
            }
        }
        return this;
    }

    public boolean isEmpty() {
        return grid.isEmpty();
    }

    public final void set(int x, int y, T t) {
        if ( x < 0 )
            x = 0;
        if ( y < 0 )
            y = 0;

        while (y >= grid.size()) {
            //  addTo a new row to the grid
            grid.add(grid.size(), new ArrayList<>());
        }

        ArrayList<T> row = grid.get(y);
        if (x == row.size())
            row.add(t);
        else {
            while ( x > row.size()-1)
                row.add(null);
            row.set(x, t);
        }
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
