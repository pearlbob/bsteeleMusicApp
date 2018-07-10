package com.bsteele.bsteeleMusicApp.client.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class SongMoment
{
    SongMoment(int sequenceNumber, LyricSection lyricSection, MeasureNode measureNode, Measure measure,
               int repeat, int repeatMax)
    {
        this.sequenceNumber = sequenceNumber;
        this.lyricSection = lyricSection;
        this.measureNode = measureNode;
        this.measure = measure;
        this.repeat = repeat;
        this.repeatMax = repeatMax;
    }

    public int getSequenceNumber()
    {
        return sequenceNumber;
    }

    public LyricSection getLyricSection()
    {
        return lyricSection;
    }

    public Measure getMeasure()
    {
        return measure;
    }

    public MeasureNode getMeasureNode()
    {
        return measureNode;
    }

    public int getRepeat()
    {
        return repeat;
    }

    public int getRepeatMax()
    {
        return repeatMax;
    }

    private int sequenceNumber;

    private int repeat;
    private int repeatMax;

    private LyricSection lyricSection;
    private MeasureNode measureNode;
    private Measure measure;
}
