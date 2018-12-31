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

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString()
    {
        return "SongChordGridSelection("+row+","+col+")";
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
