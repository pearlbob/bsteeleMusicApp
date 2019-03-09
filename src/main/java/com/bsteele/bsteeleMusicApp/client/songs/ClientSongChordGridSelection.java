package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.SongChordGridSelection;
import com.google.gwt.user.client.ui.HTMLTable;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ClientSongChordGridSelection {

    public static final SongChordGridSelection getSongChordGridSelection(HTMLTable.Cell cell) {
        return new SongChordGridSelection(cell.getRowIndex(), cell.getCellIndex());
    }


}
