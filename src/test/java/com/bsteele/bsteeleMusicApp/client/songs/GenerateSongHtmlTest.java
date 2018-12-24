package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class GenerateSongHtmlTest
{

    @Test
    public void generateFileFormat()
    {
        GenerateSongHtml generateSongHtml = new GenerateSongHtml();

        String fileFormat =                    generateSongHtml.generateFileFormat();
        assertTrue( fileFormat.length() > 9000);
        //System.out.println(fileFormat);
    }
}