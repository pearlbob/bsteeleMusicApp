package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.vectomatic.file.File;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongFile implements Comparable<SongFile>
{
    public SongFile(File file)
    {
        this.file = file;

        final RegExp fileVersionRegExp = RegExp.compile("^(.*)(?: \\(([0-9]+)\\)).songlyrics$");
        MatchResult mr = fileVersionRegExp.exec(file.getName());
        if (mr != null) {
            songTitle = mr.getGroup(1);
            switch (mr.getGroupCount()) {
                case 3:
                    versionNumber = Integer.parseInt(mr.getGroup(2));
                    break;
                default:
                    versionNumber = 0;
                    break;
            }
        } else {
            songTitle = file.getName();     //  default only
            versionNumber = 0;
        }
    }

    public File getFile()
    {
        return file;
    }


    public int getVersionNumber()
    {
        return versionNumber;
    }

    public String getSongTitle()
    {
        return songTitle;
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(SongFile o)
    {
        int ret = songTitle.compareTo(o.songTitle);
        if (ret != 0)
            return ret;
        if (versionNumber != o.versionNumber)
            return versionNumber < o.versionNumber ? 1 : -1;
        return 0;
    }

    private final File file;
    private final String songTitle;
    private final int versionNumber;
}
