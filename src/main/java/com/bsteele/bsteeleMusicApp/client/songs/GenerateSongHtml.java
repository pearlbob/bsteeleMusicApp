package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.shared.Util;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class GenerateSongHtml
{

    public String generateAllChordHtml()
    {
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

    private String chordComponentsToString(TreeSet<ChordComponent> chordComponents)
    {
        StringBuilder sb = new StringBuilder();
        for (ChordComponent chordComponent : chordComponents) {
            if (!chordComponent.equals(chordComponents.first()))
                sb.append(" ");
            sb.append(chordComponent.getShortName());
        }
        return sb.toString();
    }

    private String chordComponentScaleNotesToString(Key key, ScaleChord scaleChord)
    {
        StringBuilder sb = new StringBuilder();
        TreeSet<ChordComponent> chordComponents = scaleChord.getChordComponents();
        for (ChordComponent chordComponent : chordComponents) {
            if (!chordComponent.equals(chordComponents.first()))
                sb.append(" ");
            sb.append(key.getScaleNoteByHalfStep(key.getHalfStep() + chordComponent.getHalfSteps()));
        }
        return sb.toString();
    }

    public String generateAllScalesHtml()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n"
                        + "<th>Key</th>"
        );

        for (Scale scale : Scale.values()) {
            sb.append("<th>&nbsp;</th>");
            sb.append("<th colspan=\"").append(scale.getFormula().length).append("\">")
                    .append(Util.camelCaseToReadable(scale.name()))
                    .append("</th>");
        }
        sb.append("</tr>\n");

        sb.append(
                "<tr><th></th>");
        for (Scale scale : Scale.values()) {
            ScaleFormula formulas[] = scale.getFormula();
            sb.append("<th> </th>");
            for (ScaleFormula sf : formulas) {
                sb.append("<th>").append(sf.getShortName()).append("</th>");
            }
        }
        sb.append("</tr>\n");

        for (Key key : Key.values()) {
            String s = key.sharpsFlatsToString();
            if (s.length() > 0)
                s = "(" + s + ")";
            sb.append("<tr><td>").append(key.toString() + " " + s).append("</td>");

            for (Scale scale : Scale.values()) {
                ScaleFormula formulas[] = scale.getFormula();
                sb.append("<td>&nbsp;</td>");
                Key mKey = key;
                if (!scale.isMajor())
                    mKey = key.getMinorKey();
                for (ScaleFormula sf : formulas) {
                    ScaleNote scaleNote = key.getScaleNoteByHalfStep(mKey.getHalfStep() + sf.ordinal());
                    sb.append("<td>").append(scaleNote.toString()).append("</td>");
                }
            }

            sb.append("</tr>\n");
        }
        sb.append("  </table>\n");
        return sb.toString();
    }

    public String generateAllTonicHtml()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<p> </p>\n");

        //  major
        sb.append(
                "<p>Major</p>"
                        + "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Key</th><th>I</th>" +
                        "<th>ii</th>" +
                        "<th>iii</th>" +
                        "<th>IV</th>" +
                        "<th>V</th>" +
                        "<th>VI</th>" +
                        "<th>vii</th>" +
                        "</tr>");

        for (Key key : Key.values()) {
            sb.append("<tr><td style=\"padding: 15px; \">" + key.toString() + " " + key.sharpsFlatsToString() +
                    "</td>");
            for (MusicConstant.MajorDiatonic majorDiatonic : MusicConstant.MajorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMajorDiatonicByDegree(majorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<td style=\"padding: 15px; \">"
                        + builtScaleChord.toString()
                        + "</td>");
            }
            sb.append("</tr>\n");
        }
        sb.append("  </table>\n");
        sb.append("<p> </p>\n");

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
            for (MusicConstant.MajorDiatonic majorDiatonic : MusicConstant.MajorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMajorDiatonicByDegree(majorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<tr><td" + style + ">" + key.toString() + " " + key.sharpsFlatsToString() + "</td><td>"
                        + majorDiatonic.name()
                        + "</td><td" + style + ">"
                        + builtScaleChord.toString()
                        + "</td><td" + style + ">");
                boolean first = true;
                for (ChordComponent chordComponent : builtScaleChord.getChordComponents()) {
                    if (first)
                        first = false;
                    else
                        sb.append(" ");
                    sb.append(chordComponent.getShortName());
                }
                sb.append("</td><td" + style + ">\n");

                sb.append(chordComponentScaleNotesToString(key.getKeyByHalfStep(builtScaleChord.getScaleNote()
                        .getHalfStep()), builtScaleChord));
                sb.append("</td></tr>\n");
            }
        }
        sb.append(
                "  </table>\n");

        //  minor
        sb.append(
                "<p>Minor</p>"
                        + "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
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
                    + " " + key.sharpsFlatsToString() + "</td>");

            for (MusicConstant.MinorDiatonic minorDiatonic : MusicConstant.MinorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMinorDiatonicByDegree(minorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<td style=\"padding: 15px; \">"
                        + builtScaleChord.toString()
                        + "</td>");
            }
            sb.append("</tr>\n");
        }
        sb.append("  </table>\n");
        sb.append("<p> </p>\n");


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
            for (MusicConstant.MinorDiatonic minorDiatonic : MusicConstant.MinorDiatonic.values()) {

                ScaleChord builtScaleChord = key.getMinorDiatonicByDegree(minorDiatonic.ordinal());

                ArrayList<ScaleChord> scaleChords = new ArrayList<>();
                scaleChords.add(builtScaleChord);

                sb.append("<tr><td" + style + ">" + key.getMinorScaleChord().toString() + " " + key
                        .sharpsFlatsToString() + "</td><td>"
                        + minorDiatonic.name()
                        + "</td><td" + style + ">"
                        + builtScaleChord.toString()
                        + "</td><td" + style + ">");
                boolean first = true;
                for (ChordComponent chordComponent : builtScaleChord.getChordComponents()) {
                    if (first)
                        first = false;
                    else
                        sb.append(" ");
                    sb.append(chordComponent.getShortName());
                }
                sb.append("</td><td" + style + ">\n");

                sb.append(chordComponentScaleNotesToString(key.getKeyByHalfStep(builtScaleChord.getScaleNote()
                        .getHalfStep()), builtScaleChord));
                sb.append("</td></tr>\n");
            }
        }
        sb.append(
                "  </table>\n");
        return sb.toString();
    }

}
