package com.bsteele.bsteeleMusicApp.client.songs;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class GenerateSongHtml {

    public String generateAllChordHtml() {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<table border=\"1\" >\n" +
                        "<tr><th>Chord</th>" +
                        "<th>Formula</th>" +
                        "<th>Key</th>" +
                        "<th>Notes</th>" +
                        "</tr>");

        for (ScaleNote sn : ScaleNote.values()) {
            for (ChordDescriptor cd : ChordDescriptor.values()) {
                ScaleChord builtScaleChord = new ScaleChord(sn, cd);

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);
                Key key = Key.guessKey(scaleChords);

                sb.append("<tr><td>" + builtScaleChord.toString() + "</td><td>"
                        + chordComponentsToString(builtScaleChord.getChordComponents())
                        + "</td><td>"
                        + key.getKeyScaleNote().toString()
                        + "</td><td>"
                        + chordComponentScaleNotesToString(key, builtScaleChord)
                        + "</td></tr>\n"
                );
            }
        }
        sb.append(
                "  </table>\n");
        return sb.toString();
    }

    private String chordComponentsToString(TreeSet<ChordComponent> chordComponents) {
        StringBuilder sb = new StringBuilder();
        for (ChordComponent chordComponent : chordComponents) {
            if (!chordComponent.equals(chordComponents.first()))
                sb.append(" ");
            sb.append(chordComponent.getShortName());
        }
        return sb.toString();
    }

    private String chordComponentScaleNotesToString(Key key, ScaleChord scaleChord) {
        StringBuilder sb = new StringBuilder();
        TreeSet<ChordComponent> chordComponents = scaleChord.getChordComponents();
        for (ChordComponent chordComponent : chordComponents) {
            if (!chordComponent.equals(chordComponents.first()))
                sb.append(" ");
            sb.append(key.getScaleNoteByHalfStep(key.getHalfStep() + chordComponent.getHalfSteps()));
        }
        return sb.toString();
    }

    public String generateAllScalesHtml() {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<table border=\"1\" >\n" +
                        "<tr><th>Chord</th>" +
                        "<th>Key</th>" +
                        "<th colspan=\"7\">Major Notes</th>" +
                        "<th> </th>" +
                        "<th colspan=\"7\">Minor Notes</th>" +
                        "</tr>\n");
        sb.append(
                "<tr><th> </th><th> </th>" +
                        "<th>R</th><th>2</th><th>3</th><th>4</th><th>5</th><th>6</th><th>7</th>" +
                        "<th> </th>" +
                        "<th>R</th><th>2</th><th>m3</th><th>4</th><th>5</th><th>m6</th><th>m7</th>" +
                        "</tr>\n");
        for (ScaleNote sn : ScaleNote.values()) {

            sb.append("<tr><td>").append(sn.toString()).append("</td>");

            //  major
            ScaleChord builtScaleChord = new ScaleChord(sn, ChordDescriptor.major);
            ArrayList<ScaleChord> scaleChords = new ArrayList<>();
            scaleChords.add(builtScaleChord);
            Key key = Key.guessKey(scaleChords);
            sb.append("<td>").append(key.toString()).append("</td>");
            for (int j = 0; j < MusicConstant.notesPerScale; j++) {
                sb.append("<td>");
                String s = key.getMajorScaleByNote(j).toString();
                sb.append(s);
                sb.append("</td>\n");
            }
            sb.append("</td><td> </td>");

            //  minor
            char lastC = ' ';
            builtScaleChord = new ScaleChord(sn, ChordDescriptor.minor);
            scaleChords = new ArrayList<>();
            scaleChords.add(builtScaleChord);
            key = Key.guessKey(scaleChords);
            for (int j = 0; j < MusicConstant.notesPerScale; j++) {
                sb.append("<td");
                String s = key.getMinorScaleByNote(j).toString();
                char c = s.charAt(0);
                if (c == lastC)
                    sb.append(" style=\"background-color: cyan;\"");
                sb.append(">");
                lastC = c;
                sb.append(s);
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append(
                "  </table>\n");
        return sb.toString();
    }

    public String generateAllTonicHtml() {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<table border=\"1\" >\n" +
                        "<tr><th>Key</th>" +
                        "<th>Tonic</th>" +
                        "<th>Chord</th>" +
                        "<th>Formula</th>" +
                        "<th>Notes</th>" +
                        "</tr>");

        for (Key key : Key.values()) {
            for ( MusicConstant.Diatonic diatonic: MusicConstant.Diatonic.values()) {

                ScaleChord builtScaleChord = key.getDiatonicByDegree(diatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<tr><td>" + key.toString() + " "+key.sharpsFlatsToString()+"</td><td>"
                        +diatonic.name()
                        + "</td><td>"
                        + builtScaleChord.toString()
                        + "</td><td>");
                boolean first = true;
                for (ChordComponent chordComponent : builtScaleChord.getChordComponents() )
                {
                    if ( first)
                        first=false;
                    else
                        sb.append(" ");
                    sb.append(chordComponent.getShortName());
                }
                sb.append("</td><td>\n" );

                sb.append( chordComponentScaleNotesToString( key.getKeyByHalfStep(builtScaleChord.getScaleNote().getHalfStep()), builtScaleChord) );
                sb.append("</td></tr>\n" );
            }
        }
        sb.append(
                "  </table>\n");
        return sb.toString();
    }

}
