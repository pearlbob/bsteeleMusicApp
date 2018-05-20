package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.core.client.GWT;

import javax.annotation.Nonnull;
import java.util.ArrayList;

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


    public void setMeasureNodes(ArrayList<MeasureNode> measureNodes) {
        this.measureNodes = measureNodes;
        measures = null;
    }


    protected ArrayList<MeasureNode> measureNodes;
}
