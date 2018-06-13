package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.util.CssConstants;
import com.bsteele.bsteeleMusicApp.shared.Util;
import com.google.gwt.core.client.GWT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureSequenceItem extends MeasureNode {


    public MeasureSequenceItem(@Nonnull SectionVersion sectionVersion, @Nonnull ArrayList<MeasureNode> measureNodes) {
        super(sectionVersion);
        this.measureNodes = measureNodes;
    }


    @Override
    public final ArrayList<MeasureNode> getMeasureNodes() {
        return measureNodes;
    }


    @Override
    public ArrayList<Measure> getMeasures() {
        if (measures == null) {
            measures = new ArrayList<>();
            if (measureNodes != null)
                for (MeasureNode measureNode : measureNodes) {
                    ArrayList<Measure> childMeasures = measureNode.getMeasures();
                    if (childMeasures == null)
                        continue;
                    GWT.log("add child: " + childMeasures.size());
                    if (childMeasures != null)
                        measures.addAll(childMeasures);
                }
        }
        GWT.log("total measures: " + measures.size());
        return measures;
    }


    @Override
    public int getTotalMeasures() {
        int ret = 0;

        for (MeasureNode measureNode : measureNodes)
            ret += measureNode.getTotalMeasures();
        return ret;
    }


    public final void setMeasureNodes(ArrayList<MeasureNode> measureNodes) {
        this.measureNodes = measureNodes;
        measures = null;
    }

    @Override
    public boolean isSingleItem() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof MeasureSequenceItem)) return false;
        MeasureSequenceItem that = (MeasureSequenceItem) o;
        return Objects.equals(measureNodes, that.measureNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measureNodes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (measureNodes != null)
            for (MeasureNode measureNode : measureNodes) {
                sb.append(measureNode.toString()).append(" ");
            }
        sb.append("} ");
        return sb.toString();
    }

    @Override
    public String generateHtml(Key key, int tran) {
        StringBuilder sb = new StringBuilder();

        if (measureNodes != null && !measureNodes.isEmpty()) {
            MeasureNode lastMeasureNode = null;
            int measuresOnThisLine = 0;
            for (int i = 0; i < measureNodes.size(); i++) {
                MeasureNode measureNode = measureNodes.get(i);

                if (measureNode.isSingleItem()) {
                    if (i > 0 && measuresOnThisLine == 0) {
                        sb.append("<tr><td></td>");
                        lastMeasureNode = null;
                    }

                    sb.append("<td class=\"" + CssConstants.style + "section"
                            + measureNode.getSectionVersion().getSection().getAbbreviation() + "Class\" id=\""
                            +"C.\" >");

                    if (measureNode.equals(lastMeasureNode))
                        sb.append("-");
                    else
                        sb.append(measureNode.generateHtml(key, tran));
                    lastMeasureNode = measureNode;
                    sb.append("</td>");

                    if (measuresOnThisLine % measuresPerLine == measuresPerLine - 1 && i < measureNodes.size() - 1) {
                        sb.append("</tr>\n");
                    }
                    measuresOnThisLine = Util.mod(measuresOnThisLine + 1, measuresPerLine);
                } else {
                    if (measuresOnThisLine > 0) {
                        //  fill with empty measures to end of line
                        while (measuresOnThisLine % measuresPerLine < measuresPerLine - 1) {
                            sb.append("<td></td>");
                            measuresOnThisLine++;
                        }
                        sb.append("</tr><tr><td></td>\n");
                    } else if (i > 0)
                        sb.append("<tr><td></td>\n");
                    sb.append(measureNode.generateHtml(key, tran));
                    measuresOnThisLine = 0;
                }
            }
        }

        return sb.toString();
    }

    protected ArrayList<MeasureNode> measureNodes;
}
