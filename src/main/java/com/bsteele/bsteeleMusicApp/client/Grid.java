/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.client;

import jsinterop.annotations.JsType;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author bob
 * @param <T>
 */
@JsType
public class Grid<T>
{

    public Grid()
    {
    }

    /**
     * Deep copy not as a constructor or hide if from javascript.
     *
     * @param other
     * @return
     */
    public Grid<T> deepCopy(Grid<T> other)
    {
        if ( other == null )
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

    /**
     * Generate a string array equivalent for javascript
     *
     * @return
     */
    public String[][] getJavascriptCopy()
    {
        String[][] ret = new String[grid.size()][0];
        for (int i = 0; i < grid.size(); i++) {
            ret[i] = grid.get(i).toArray(emptyStringArray);
        }
        return ret;
    }

    public boolean isEmpty()
    {
        return grid.isEmpty();
    }

    public final void addTo(int x, int y, T t)
    {
        if (y > grid.size()) {
            throw new ArrayIndexOutOfBoundsException("row is not incremental: y: " + y + " vs " + grid.size());
        }
        ArrayList<T> row;
        if (y == grid.size()) {
            //  addTo a new row to the grid
            row = new ArrayList<>();
            grid.add(y, row);
        } else {
            row = grid.get(y);
        }

        if (x == row.size())
            row.add(t);
        else
            row.add(x, t);
    }

    public final void add(T t)
    {
        if (grid.isEmpty())
            addTo(0, 0, t);
        else {
            int r = grid.size() - 1;
            ArrayList<T> row = grid.get(r);
            addTo(row.size(), r, t);
        }
    }

    public final void set(int x, int y, T t)
    {
        if (y >= grid.size()) {
            throw new ArrayIndexOutOfBoundsException("row is not incremental: y: " + y + " vs " + grid.size());
        }

        ArrayList<T> row = grid.get(y);
        row.set(x, t);
    }

    public final int lastRowSize()
    {
        if (grid.isEmpty())
            return 0;
        return getRow(grid.size() - 1).size();
    }

    @Override
    public String toString()
    {
        return "Grid{" + "grid=" + grid + '}';
    }

    public final T get(int x, int y)
    {
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

    public int getRowCount()
    {
        return grid.size();
    }

    public ArrayList<T> getRow(int y)
    {
        try {
            return grid.get(y);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

    public void clear()
    {
        grid.clear();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.grid);
    }

    @Override
    public boolean equals(Object obj)
    {
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
