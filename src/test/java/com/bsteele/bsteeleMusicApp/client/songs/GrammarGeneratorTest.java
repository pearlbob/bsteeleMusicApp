package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.ChordAnticipationOrDelay;
import com.bsteele.bsteeleMusicApp.shared.songs.ChordDescriptor;
import com.bsteele.bsteeleMusicApp.shared.songs.Section;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class GrammarGeneratorTest extends TestCase
{

    @Test
    public void testGrammarGenerate()
    {
        System.out.println("| <Chord: (\"-\"                          //  repeat measures\n" +
                "    |([\"A\"-\"G\"](\"b\"|\"s\"|\"#\"|\"♭\"|\"♯\"){0,1} //  scale notes");

        System.out.print(ChordDescriptor.generateGrammar());
        System.out.println("{0,1}\n");

        System.out.print(ChordAnticipationOrDelay.generateGrammar());
         System.out.println("{0,1}  //    0 is major\n" +
                "    |\".\"    //  dot for repeat on the next beat\n" +
                "    )+      //  arbitrary number to fit wild measures in 12/8\n" +
                "    ) >"   );
        System.out.println();
        
        System.out.print("| ");
        System.out.println(Section.generateGrammar());
    }
}