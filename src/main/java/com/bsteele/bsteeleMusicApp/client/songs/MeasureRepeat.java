package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;
import com.bsteele.bsteeleMusicApp.client.util.CssConstants;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeat extends MeasureSequenceItem
{

    MeasureRepeat(@Nonnull SectionVersion sectionVersion, @Nonnull ArrayList<MeasureNode> measureNodes, int repeats)
    {
        super(sectionVersion, measureNodes);
        this.repeats = repeats;
    }

    @Override
    public int getTotalMoments()
    {
        return repeats * super.getTotalMoments();
    }


    public final int getRepeats()
    {
        return repeats;
    }


    public final void setRepeats(int repeats)
    {
        this.repeats = repeats;
    }

    @Override
    public String generateHtml(@NotNull SongMoment songMoment, @NotNull Key key, int tran)
    {
        StringBuilder sb = new StringBuilder();

        if (measureNodes != null && !measureNodes.isEmpty()) {
            MeasureNode lastMeasureNode = null;
            int i = 0;
            MeasureNode measureNode = null;
            for (; i < measureNodes.size(); i++) {
                if (i > 0 && i % measuresPerLine == 0) {
                    sb.append("<tr><td></td>");
                    lastMeasureNode = null;
                }

                measureNode = measureNodes.get(i);

                sb.append("<td class=\"" + CssConstants.style + "section"
                        + measureNode.getSectionVersion().getSection().getAbbreviation() + "Class\" id=\""
                        + "C.\" >");

                if (measureNode.equals(lastMeasureNode))
                    sb.append("-");
                else
                    sb.append(measureNode.generateHtml(songMoment, key, tran));
                lastMeasureNode = measureNode;
                sb.append("</td>");

                if (i % measuresPerLine == measuresPerLine - 1 && i < measureNodes.size() - 1) {
                    sb.append("<td>|</td></tr>\n");
                    sb.append("<tr><td></td><td colspan=\"10\">" +
                            "<canvas id=\"bassLine" + "fixmeHere"
                            + "\" width=\"800\" height=\"150\" style=\"border:1px solid #000000;\"/></tr>");
                }
            }
            while (i % measuresPerLine != 0) {
                sb.append("<td></td>");
                i++;
            }

            sb.append("<td class=\"" + CssConstants.style
                    + "section"
                    + lastMeasureNode.getSectionVersion().getSection().getAbbreviation()
                    + "Class\" "
                    + " style=\"border-right: 0px solid black;\">");
            if (i > measuresPerLine)
                sb.append("| ");
            sb.append("x" + repeats);
            sb.append("</td></tr>\n");
            if (measureNode != null)
                sb.append("<tr><td></td><td colspan=\"10\">" +
                        "<canvas id=\"bassLine" + "fixmeHere"
                        + "\" width=\"800\" height=\"150\" style=\"border:1px solid #000000;\"/></tr>");
        }
        return sb.toString();
    }

    @Override
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats)
    {
        ArrayList<String> ret = new ArrayList<>();

        if (measureNodes == null || measureNodes.isEmpty())
            return ret;

        if (expandRepeats) {
            for (int r = 0; r < repeats; r++) {
                MeasureNode lastMeasureNode = null;
                MeasureNode measureNode = null;
                int i = 0;
                for (; i < measureNodes.size(); i++) {
                    if (i > 0 && i % measuresPerLine == 0) {
                        ret.add("\n");
                        lastMeasureNode = null;
                    }

                    measureNode = measureNodes.get(i);

                    if (measureNode.isSingleItem()) {
                        if (measureNode.equals(lastMeasureNode))
                            ret.add("-");
                        else
                            ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                        lastMeasureNode = measureNode;
                    } else {
                        ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                        lastMeasureNode = null;
                    }
                    if (i % measuresPerLine == measuresPerLine - 1 && i < measureNodes.size() - 1) {
                        // ret.add("|");
                        ret.add("\n");
                    }
                }
                while (i % measuresPerLine != 0) {
                    ret.add("");
                    i++;
                }
                ret.add("\n");
            }
        } else {
            MeasureNode lastMeasureNode = null;
            MeasureNode measureNode;
            int i = 0;
            for (; i < measureNodes.size(); i++) {
                if (i > 0 && i % measuresPerLine == 0) {
                    ret.add("\n");
                    lastMeasureNode = null;
                }

                measureNode = measureNodes.get(i);

                if (measureNode.isSingleItem()) {
                    if (measureNode.equals(lastMeasureNode))
                        ret.add("-");
                    else
                        ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                    lastMeasureNode = measureNode;
                } else {
                    ret.addAll(measureNode.generateInnerHtml(key, tran, expandRepeats));
                    lastMeasureNode = null;
                }
                if (i % measuresPerLine == measuresPerLine - 1 && i < measureNodes.size() - 1) {
                    ret.add("|");
                    ret.add("\n");
                }
            }
            while (i % measuresPerLine != 0) {
                ret.add("");
                i++;
            }
            if (measureNodes.size() > measuresPerLine)
                ret.add("|");
            ret.add("x" + repeats);
            ret.add("\n");
        }

        return ret;
    }

    @Override
    public void addToGrid(@Nonnull Grid<MeasureNode> grid, @Nonnull ChordSection chordSection)
    {
        //  fixme: improve repeats.addToGrid()
        int measureCount = 0;
        for (MeasureNode measureNode : measureNodes) {
            measureCount++;
            if (grid.lastRowSize() >= MusicConstant.measuresPerDisplayRow + 1) {
                if ( measureNodes.size() > MusicConstant.measuresPerDisplayRow) {
                    grid.add(new MeasureComment(chordSection.getSectionVersion(),"|"));
                }
                grid.addTo(0, grid.getRowCount(), chordSection);
            }
            measureNode.addToGrid(grid, chordSection);
        }
        if ( measureNodes.size() > MusicConstant.measuresPerDisplayRow) {
            grid.add(new MeasureComment(chordSection.getSectionVersion(),"|"));
        }
        grid.add(new MeasureComment(chordSection.getSectionVersion(),"x"+repeats));
    }

    @Override
    public boolean isSingleItem()
    {
        return false;
    }

    @Override
    public boolean isRepeat()
    {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasureRepeat that = (MeasureRepeat) o;
        return repeats == that.repeats && super.equals(o);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), repeats);
    }

    @Override
    public String toString()
    {
        return super.toString() + "x" + repeats + " ";
    }

    private int repeats;
}
