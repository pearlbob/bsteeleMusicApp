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
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
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
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
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
        sb.append( "<p> </p>\n");

        //  major
        sb.append(
                "<p>Major</p>"
                +"<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Key</th><th>I</th>" +
                        "<th>ii</th>" +
                        "<th>iii</th>" +
                        "<th>IV</th>" +
                        "<th>V</th>" +
                        "<th>VI</th>" +
                        "<th>vii</th>" +
                        "</tr>");

        for (Key key : Key.values()) {
            sb.append("<tr><td style=\"padding: 15px; \">" + key.toString() + " "+key.sharpsFlatsToString()+"</td>" );
            for ( MusicConstant.MajorDiatonic majorDiatonic : MusicConstant.MajorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMajorDiatonicByDegree(majorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<td style=\"padding: 15px; \">"
                        + builtScaleChord.toString()
                        + "</td>");
            }
            sb.append("</tr>\n" );
        }
        sb.append( "  </table>\n");
        sb.append( "<p> </p>\n");

        //  details
        sb.append(
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Key</th>" +
                        "<th>Tonic</th>" +
                        "<th>Chord</th>" +
                        "<th>Formula</th>" +
                        "<th>Notes</th>" +
                        "</tr>");

        String style = " style=\"padding-left: 15px; padding-right: 15px;\"";
        for (Key key : Key.values()) {
            for ( MusicConstant.MajorDiatonic majorDiatonic : MusicConstant.MajorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMajorScaleChord();

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<tr><td"+style+">" + key.toString() + " "+key.sharpsFlatsToString()+"</td><td>"
                        + majorDiatonic.name()
                        + "</td><td"+style+">"
                        + builtScaleChord.toString()
                        + "</td><td"+style+">");
                boolean first = true;
                for (ChordComponent chordComponent : builtScaleChord.getChordComponents() )
                {
                    if ( first)
                        first=false;
                    else
                        sb.append(" ");
                    sb.append(chordComponent.getShortName());
                }
                sb.append("</td><td"+style+">\n" );

                sb.append( chordComponentScaleNotesToString( key.getKeyByHalfStep(builtScaleChord.getScaleNote().getHalfStep()), builtScaleChord) );
                sb.append("</td></tr>\n" );
            }
        }
        sb.append(
                "  </table>\n");

        //  minor
        sb.append(
                "<p>Minor</p>"
                        +"<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Key</th><th>i</th>" +
                        "<th>ii</th>" +
                        "<th>III</th>" +
                        "<th>iv</th>" +
                        "<th>v</th>" +
                        "<th>VI</th>" +
                        "<th>VII</th>" +
                        "</tr>");

        for (Key key : Key.values()) {
            sb.append("<tr><td style=\"padding: 15px; \">" + key.getMinorScaleChord().toString()
                    + " "+key.sharpsFlatsToString()+"</td>" );
            
            for ( MusicConstant.MinorDiatonic minorDiatonic : MusicConstant.MinorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMinorDiatonicByDegree(minorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<td style=\"padding: 15px; \">"
                        + builtScaleChord.toString()
                        + "</td>");
            }
            sb.append("</tr>\n" );
        }
        sb.append( "  </table>\n");
        sb.append( "<p> </p>\n");



        //  details
        sb.append(
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Key</th>" +
                        "<th>Tonic</th>" +
                        "<th>Chord</th>" +
                        "<th>Formula</th>" +
                        "<th>Notes</th>" +
                        "</tr>");

         style = " style=\"padding-left: 15px; padding-right: 15px;\"";
        for (Key key : Key.values()) {
            for ( MusicConstant.MinorDiatonic minorDiatonic : MusicConstant.MinorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMinorDiatonicByDegree(minorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<tr><td"+style+">" + key.getMinorScaleChord().toString() + " "+key.sharpsFlatsToString()+"</td><td>"
                        + minorDiatonic.name()
                        + "</td><td"+style+">"
                        + builtScaleChord.toString()
                        + "</td><td"+style+">");
                boolean first = true;
                for (ChordComponent chordComponent : builtScaleChord.getChordComponents() )
                {
                    if ( first)
                        first=false;
                    else
                        sb.append(" ");
                    sb.append(chordComponent.getShortName());
                }
                sb.append("</td><td"+style+">\n" );

                sb.append( chordComponentScaleNotesToString( key.getKeyByHalfStep(builtScaleChord.getScaleNote().getHalfStep()), builtScaleChord) );
                sb.append("</td></tr>\n" );
            }
        }
        sb.append(
                "  </table>\n");
        return sb.toString();
    }

}
