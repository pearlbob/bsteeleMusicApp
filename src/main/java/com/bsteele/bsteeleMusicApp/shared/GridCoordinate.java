package com.bsteele.bsteeleMusicApp.shared;

public class GridCoordinate {
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

    private final int row;
    private final int col;
}
