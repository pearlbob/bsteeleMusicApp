package com.bsteele.bsteeleMusicApp.shared;

public class GridCoordinate implements Comparable<GridCoordinate> {
    public GridCoordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public final int getRow() {
        return row;
    }

    public final int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

    @Override
    public boolean equals(Object obj) {
        try {
            GridCoordinate o = (GridCoordinate) obj;
            return row == o.row && col == o.col;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash = (83 * hash + row) % (1 << 31);
        hash = (17 * hash + col) % (1 << 31);

        return hash;
    }

    @Override
    public int compareTo(GridCoordinate o) {
        if (row != o.row) {
            return row < o.row ? -1 : 1;
        }

        if (col != o.col) {
            return col < o.col ? -1 : 1;
        }
        return 0;
    }

    private final int row;
    private final int col;

}
