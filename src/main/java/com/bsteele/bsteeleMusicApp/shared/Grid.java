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
        ArrayList<T> row;
        try {
            row = grid.get(y);
            if (row == null) {
                row = new ArrayList<>();
                grid.add(y, row);
            }
        } catch (IndexOutOfBoundsException ex) {
            row = new ArrayList<>();
            grid.add(y, row);
        }

        row.add(x, t);
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

    public void clear() {
        grid.clear();
    }
    private ArrayList<ArrayList<T>> grid = new ArrayList<>();
}
