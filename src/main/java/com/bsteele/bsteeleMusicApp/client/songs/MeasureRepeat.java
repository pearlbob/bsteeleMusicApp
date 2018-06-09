package com.bsteele.bsteeleMusicApp.client.songs;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeat extends MeasureSequenceItem {

    MeasureRepeat(@Nonnull SectionVersion sectionVersion, @Nonnull ArrayList<MeasureNode> measureNodes, int repeats) {
        super(sectionVersion, measureNodes);
        this.repeats = repeats;
    }

    @Override
    public int getTotalMeasures() {
        return repeats * super.getTotalMeasures();
    }


    public final int getRepeats() {
        return repeats;
    }


    public final void setRepeats(int repeats) {
        this.repeats = repeats;
    }


    @Override
    public String toHtml() {
        StringBuilder sb = new StringBuilder();

        if (measureNodes != null && !measureNodes.isEmpty()) {
            MeasureNode lastMeasureNode = null;
            int i = 0;
            for (; i < measureNodes.size(); i++) {
                if (i > 0 && i % measuresPerLine == 0) {
                    sb.append("<tr><td></td>");
                    lastMeasureNode = null;
                }
                sb.append("<td>");

                MeasureNode measureNode = measureNodes.get(i);
                if (measureNode.equals(lastMeasureNode))
                    sb.append("-");
                else
                    sb.append(measureNode.toHtml());
                lastMeasureNode = measureNode;
                sb.append("</td>");

                if (i % measuresPerLine == measuresPerLine - 1 && i < measureNodes.size() - 1) {
                    sb.append("<td>|</td></tr>\n");
                }
            }
            while (i % measuresPerLine != 0) {
                sb.append("<td></td>");
                i++;
            }
            sb.append("<td>");
            if (i > measuresPerLine)
                sb.append("| ");
            sb.append("x" + repeats);
            sb.append("</td></tr>\n");
        }
        return sb.toString();
    }

    @Override
    public boolean isSingleItem() { return false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasureRepeat that = (MeasureRepeat) o;
        return repeats == that.repeats && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), repeats);
    }

    @Override
    public String toString() {
        return super.toString() + "x" + repeats + " ";
    }

    private int repeats;
}
