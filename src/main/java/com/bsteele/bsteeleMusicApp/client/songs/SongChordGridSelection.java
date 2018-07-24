package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongChordGridSelection
{

    public SongChordGridSelection(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public SongChordGridSelection(HTMLTable.Cell cell)
    {
        this.row = cell.getRowIndex();
        this.col = cell.getCellIndex();
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }
    
    private int row;
    private int col;
}
