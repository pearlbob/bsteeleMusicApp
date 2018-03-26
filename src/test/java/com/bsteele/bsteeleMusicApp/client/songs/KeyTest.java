package com.bsteele.bsteeleMusicApp.client.songs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class KeyTest {

    @Test
    public void getKeyByValue() {
        for (int i = -6; i <= 6; i++) {
            Key key = Key.getKeyByValue(i);
            assertEquals(i, key.getKeyValue());
            System.out.println(i + " " + key.name()
                    + " toString: " + key.toString()
                    + " html: " + key.toHtml()
            );
            System.out.print("\t");
            for (int j = 0; j < 7; j++) {
                ScaleNote sn = key.getMajorScaleByNote(j);
                String s = sn.toString();
                System.out.print(s);
                if (s.toString().length() < 2)
                    System.out.print(" ");
                System.out.print(" ");
            }
            System.out.println();

            System.out.print("\t");
            for (int j = 0; j < 12; j++) {
                ScaleNote sn = key.getScalebyHalfStep(j);
                String s = sn.toString();
                System.out.print(s);
                if (s.toString().length() < 2)
                    System.out.print(" ");
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}