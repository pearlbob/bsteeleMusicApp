package com.bsteele.bsteeleMusicApp.shared.songs;

import org.junit.Test;

import static org.junit.Assert.*;

public class SectionTest {

    @Test
    public void parse() {

    }

    @Test
    public void getDescription() {
        boolean first = true;
        for ( Section section : Section.values()){
            if ( first)
                first=false;
            else
                System.out.print(", ");
            System.out.print(section.getFormalName()+":");
            //System.out.println(" "+section.toString());
            String s = section.getAbbreviation();
            if ( s != null )
                System.out.print(" "+s);
        }
        System.out.println();
    }
}