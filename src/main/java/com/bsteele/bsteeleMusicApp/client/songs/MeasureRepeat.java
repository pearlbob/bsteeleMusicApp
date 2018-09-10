package com.bsteele.bsteeleMusicApp.client.songs;

import com.bsteele.bsteeleMusicApp.client.Grid;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class MeasureRepeat extends MeasureSequenceItem
{

    MeasureRepeat(/*@Nonnull SectionVersion sectionVersion, */@Nonnull ArrayList<Measure> measures, int repeats)
    {
        super(measures);
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
    public ArrayList<String> generateInnerHtml(Key key, int tran, boolean expandRepeats)
    {
        ArrayList<String> ret = new ArrayList<>();

        if (measures == null || measures.isEmpty())
            return ret;

        if (expandRepeats) {
            for (int r = 0; r < repeats; r++) {
                MeasureNode lastMeasureNode = null;
                MeasureNode measureNode = null;
                int i = 0;
                for (; i < measures.size(); i++) {
                    if (i > 0 && i % MusicConstant.measuresPerDisplayRow == 0) {
                        ret.add("\n");
                        lastMeasureNode = null;
                    }

                    measureNode = measures.get(i);

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
                    if (i % MusicConstant.measuresPerDisplayRow == MusicConstant.measuresPerDisplayRow - 1 && i < measures
                            .size() - 1)
                    {
                        // ret.add("|");
                        ret.add("\n");
                    }
                }
                while (i % MusicConstant.measuresPerDisplayRow != 0) {
                    ret.add("");
                    i++;
                }
                ret.add("\n");
            }
        } else {
            MeasureNode lastMeasureNode = null;
            MeasureNode measureNode;
            int i = 0;
            for (; i < measures.size(); i++) {
                if (i > 0 && i % MusicConstant.measuresPerDisplayRow == 0) {
                    ret.add("\n");
                    lastMeasureNode = null;
                }

                measureNode = measures.get(i);

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
                if (i % MusicConstant.measuresPerDisplayRow == MusicConstant.measuresPerDisplayRow - 1
                        && i < measures.size() - 1)
                {
                    ret.add("|");
                    ret.add("\n");
                }
            }
            while (i % MusicConstant.measuresPerDisplayRow != 0) {
                ret.add("");
                i++;
            }
            if (measures.size() > MusicConstant.measuresPerDisplayRow)
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
        for (MeasureNode measureNode : measures) {
            if (grid.lastRowSize() >= MusicConstant.measuresPerDisplayRow + 1) {
                if (measures.size() > MusicConstant.measuresPerDisplayRow) {
                    grid.add(new MeasureRepeatExtension());
                }
                grid.addTo(0, grid.getRowCount(), chordSection);
            }
            measureNode.addToGrid(grid, chordSection);
        }

        for (int measureCount = measures.size(); measureCount % MusicConstant.measuresPerDisplayRow != 0; measureCount++)
            grid.add(new MeasureComment());
        if (measures.size() > MusicConstant.measuresPerDisplayRow) {
            for (int measureCount = measures.size(); measureCount % MusicConstant.measuresPerDisplayRow != 0; measureCount++)
                grid.add(new MeasureComment());
            grid.add(new MeasureRepeatExtension());
        }
        grid.add(new MeasureRepeatMarker(repeats));
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
