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


    public String generateFileFormat()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<p> </p>\n");

        sb.append("<h1>General File Specifications</h1>\n");
        sb.append(
                "<p>All songs are stored in files with the \".songlyrics\" file name extension."
                        + "The file format is compliant with JSON (https://www.json.org/)." +
                        "  Note that this includes escaping all appropriate characters. " +
                        " This includes characters like  " +
                        "    '\"'," +
                        "    '\\'," +
                        "    '/'," +
                        " and other special characters." +
                        "</p>\n");

        sb.append(
                "<p>" +
                        "Multiple songs can be written in a single file as a JSON array of songs." +
                        "</p>\n");

        sb.append(
                "<p>" +
                        "File information for songs can be written as a JSON object with the following name/value " +
                        "pairs:" +
                        "</p>\n");
        sb.append(
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Name</th>" +
                        "<th>Type</th>" +
                        "<th>Value Description</th>" +
                        "<th>Notes</th>" +
                        "</tr>\n");
        sb.append("<tr><td><code>file</code></td><td>JSON String</td>\n" +
                "<td>File name of the song's file as it exists in the local operating system.</td>" +
                "<td></td></tr>\n");
        sb.append("<tr><td><code>lastModifiedDate</code></td><td>JavaScript JSDate</td>\n" +
                "<td>The number of milliseconds from since the Unix epoch (00:00:00 UTC on 1 January 1970)." +
                " See javascript File.lastModified.</td>" +
                "<td></td></tr>\n");
        sb.append("<tr><td><code>song</code></td><td>JSON object</td>\n" +
                "<td>The song attributes as described below.</td>" +
                "<td></td></tr>\n");
        sb.append("</table>\n");

        sb.append(
                "<p>" +
                        "Musical notes or keys can be noted with either the lowercase 'b' or the Unicode music flat sign " +
                        "'♭' (U+266D). " +
                        "Musical notes or keys can be noted with either the  sharp '#' or the Unicode music sharp sign '♯' " +
                        "(U+266F). " +
                        "</p>\n");
        sb.append(
                "<p>" +
                        "Song unique identifiers are built from the song's title and artist without concern for " +
                        "capitalization" +
                        " or white space.  Songs with the same song id will be over written by new songs." +
                        "</p>\n");

        sb.append("<h1>Song Specifications</h1>\n");

        sb.append(
                "<p>" +
                        "Song attributes for songs are be written as a JSON object with the following name/value " +
                        "pairs:" +
                        "</p>\n");
        sb.append(
                "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                        "<tr><th>Name</th>" +
                        "<th>Type</th>" +
                        "<th>Value Description</th>" +
                        "<th>Notes</th>" +
                        "</tr>\n");
        sb.append("<tr><td><code>title</code></td><td>JSON String</td>\n" +
                "<td>The song's title as the user would know it.</td>" +
                "<td>For search purposes, titles beginning with \"The\" will have " +
                "the preposition swapped to the end of the title after a comma.</td></tr>\n");
        sb.append("<tr><td><code>artist</code></td><td>JSON String</td>\n" +
                "<td>The artist of the song.</td>" +
                "<td>For search purposes, artist names beginning with \"The\" will have " +
                "the preposition swapped to the end of the title after a comma.</td></tr>\n");
        sb.append("<tr><td><code>copyright</code></td><td>JSON String</td>\n" +
                "<td>The copyright notice of the owner.</td>" +
                "<td>Copyrights are often difficult to find and may not be proper legally. " +
                "I would appreciate all users to provide a reasonable effort to find the proper copyright. " +
                "At the moment, the software only insists that it be non-null.  Please do make an effort. " +
                "If you Google the song title plus the word \"lyrics\", Google will often provide a copyright. " +
                "</td></tr>\n");

        sb.append("<tr><td><code>key</code></td><td>JSON String</td>\n" +
                "<td>The major key the song is provided in designated as one of the following: ");
        {
            boolean first = true;
            for (Key key : Key.values()) {
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(key.toString());
            }
        }
        sb.append("</td>" +
                "<td>This will be extended to minor keys eventually.</td></tr>\n");


        sb.append("<tr><td><code>defaultBpm</code></td><td>integer</td>\n" +
                "<td>The song's default beats per minute, i.e. the song's tempo.</td>" +
                "<td>The term default indicates my intention to eventually allow section tempo changes." +
                " Currently the tempo is restricted between 50 and 400 BPM inclusive." +
                "</td></tr>\n");

        sb.append("<tr><td><code>timeSignature</code></td><td>int + \"/\" + int</td>\n" +
                "<td>The song's time signature in the common form of the number of beats in a measure " +
                "over which note value gets the beatbeats per minute.</td>" +
                "<td>Known signatures include \"2/4\", \"3/4\", \"4/4\" (the default), and \"6/8\".</td></tr>\n");

        sb.append("<tr><td><code>chords</code></td><td>JSON array of strings</td>\n" +
                "<td>The song's chord structure written in the chord markup language described below.</td>" +
                "<td>Generally speaking, each string represents the section identifiers and chords as they " +
                "are to be presented to the user. Do not include the carriage return or newline character." +
                "  The application may" +
                "adjust their presentation.</td></tr>\n");

        sb.append("<tr><td><code>lyrics</code></td><td>JSON array of strings</td>\n" +
                "<td>The song's lyric sections written in temporal order and" +
                " in the lyric markup language described below.</td>" +
                "<td>Do not include the carriage return or newline character. The application may" +
                "adjust their presentation</td></tr>\n");

        sb.append("</table>\n");

        sb.append("<h1>Chord Markup Language</h1>\n");
        sb.append(
                "<p>The chord markup language typically has a format of: " +
                        "</p>\n" +
                        "<p><code>(section version? ':' measure*)+</code>" +
                        "</p>\n" +
                        "Measures need to be separated from each other by whitespace." +
                        "  There are exceptions for repeats as described below." +
                        "</p>"
        );
        {
            sb.append("<h2>Sections</h2>\n");
            sb.append(
                    "<p>Sections can be identified by either their name or abreviation as follows:" +
                            "</p>"
            );
            sb.append(
                    "<table border=\"2\" style=\"border-collapse: collapse;\">\n" +
                            "<tr><th>Name</th>" +
                            "<th>Abreviation</th>" +
                            "<th>Formal Name</th>" +
                            "<th>Description</th>" +
                            "</tr>\n");
            for (Section section : Section.values()) {
                sb.append("<tr><td><code>")
                        .append(section.name())
                        .append("</code></td><td><code>")
                        .append(section.getAbbreviation())
                        .append("</code></td><td>")
                        .append(section.getFormalName())
                        .append("</td><td>")
                        .append(section.getDescription())
                        .append("</td>\n");
            }
            sb.append("</table>\n");
        }
        sb.append(
                "<p>Capitalization is not significant to section identification.</p>"
        );
        sb.append(
                "<p>Section versions can be identified by a single digit immediately following the section." +
                        "Sections without a version id will be considered an additional section.</p>"
        );
        sb.append("<h2>Measures</h2>\n");
        sb.append(
                "<p>Measures are collections of chords meant to be played within the beats defined by" +
                        " the time signature.  They are separated by whitespace.  " +
                        "Chords not separated by whitespace are meant to be played within the same measure of time." +
                        " Rules for their sharing of this time across the beats will be described below." +
                        "</p>"
        );
        sb.append(
                "<p>Chords are in the form of: " +
                        "</p>\n" +
                        "<p><code>scaleNote chordDescriptor</code>" +
                        "</p>\n"
        );
        sb.append("<p>A scaleNote is one of: ");
        {
            boolean first = true;
            for (ScaleNote scaleNote : ScaleNote.values()) {
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(scaleNote.toString());
            }
        }
        sb.append(".</p>\n");

        sb.append("<p>A chordDescriptor is one of: ");
        {
            boolean first = true;
            for (ChordDescriptor chordDescriptor : ChordDescriptor.values()) {
                if (first)
                    first = false;
                else
                    sb.append(", ");
                sb.append(chordDescriptor.toString());
            }
        }
        sb.append(".  A missing chord descriptor will be understood to be a major chord." +
                " Suggestion: choose the button \"Show All Chords\" above to see all possible chords.</p>\n");
        sb.append(
                "<p>Capitalization is significant to scaleNote identification and chord description.</p>"
        );
        sb.append("<p>A part of the measure where no chord is to be played is noted with a capital X." +
                " This can also be used to indicate a pause.</p>\n");
        sb.append("<p>Note: Annotations for anticipations (pushes) and delays are planned but not part of the markup " +
                " language as yet.</p>\n");
        sb.append("<p>A measure followed by a slash '/' and a chord without whitespace " +
                "will be interpreted as an inversion over the measure." +
                " This typically is a note played by the bass or piano but no other instrument.</p>\n");
        sb.append("<p>In a measure with a single chord, it is to be played over the entire measure.</p>\n");
        sb.append("<p>In a measure with a multiple chords, the chords are to be played evenly over the measure." +
                " When is this not the intention, periods '.' can be used to repeat the prior chord for one more" +
                " beat.  The number of chords and repeats should total to the number of beats per measure.</p>\n");
        sb.append("<p>A measure defined by a single minus sign '-' is to be a repeat of the prior measure.</p>\n");
        sb.append("<h2>Repeats</h2>\n");
        sb.append("<p>A measure line that ends with a 'x' and a number is a repeat.  The line is to be repeated " +
                "the number of counts indicated by the number.</p>\n");
        sb.append("<h2>Multiline Repeats</h2>\n");
        sb.append("<p>If a measure line ends with a vertical bar '|' it will be included with the following " +
                "line or lines in a repeat. By convention, the last line of the repeat should also have a " +
                "vertical bar '|' before the 'x' and a number of the repeat.</p>\n");

        sb.append("<h1>Lyric Markup Language</h1>\n");
        sb.append("<p>The lyric markup language is of the form:</p>\n");
        sb.append("<p><code>sectionVersion: lyrics</code></p>\n");
        sb.append("<p>The sectionVersion is to be the sectionVersion name or abbreviated name as defined above. " +
                "Section versions used in the lyrics should be defined in the chords.  All chord sectionVersions defined" +
                " should be used in the lyrics.</p>\n");
        sb.append("<p>Note that the parsing makes the use of colon ':' problematic in the general lyrics." +
                " Typically it will not happen but the word in front of the colon should not match any of the " +
                "sectionVersion names.  A space in front of the colon will fix all this.</p>\n");
        sb.append("<p>Note that the current implementation requires the sectionVersion to be on it's own line.  " +
                "This restriction will likely be lifted in the future.</p>\n");

        sb.append("<h2>Suggestions</h2>\n");
        sb.append("<p>Writing all songs will provide a sample file format for examination.  To write all songs," +
                " choose Options, Write All Songs. See the button below.</p>\n");

        return sb.toString();
    }

}
