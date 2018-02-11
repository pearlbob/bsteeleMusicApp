/*
 * Copyright 2018 Robert Steele at bsteele.com
 */
package com.bsteele.bsteeleMusicApp.shared;

import java.util.ArrayList;
import java.util.Objects;
import jsinterop.annotations.JsType;

/**
 *
 * @author bob
 * @param <T>
 */
@JsType
public class Grid<T> {

  public Grid() {
  }

  /**
   * Deep copy not as a constructor or hide if from javascript.
   *
   * @param other
   * @return
   */
  public Grid<T> deepCopy(Grid<T> other) {
    int rLimit = other.getRowCount();
    for (int r = 0; r < rLimit; r++) {
      ArrayList<T> row = other.getRow(r);
      int colLimit = row.size();
      for (int c = 0; c < colLimit; c++) {
        add(c, r, row.get(c));
      }
    }
    return this;
  }

  /**
   * Generate a string array equivalent for javascript
   * @return 
   */
  public String[][] getJavasriptCopy() {
    String[][] ret = new String[grid.size()][0];
    for (int i = 0; i < grid.size(); i++) {
      ret[i] = grid.get(i).toArray(emptyStringArray);
    }
    return ret;
  }
  
  public boolean isEmpty(){
    return grid.isEmpty();
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
  
    @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + Objects.hashCode(this.grid);
    return hash;
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
