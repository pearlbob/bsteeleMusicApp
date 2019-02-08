package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum NoteDuration
{
    whole(1.0),
    half(1.0 / 2),
    quarter(1.0 / 4),
    eighth(1.0 / 8),
    sixteenth(1.0 / 16),
    thirtySecond(1.0 / 32),
    sixtyFourth(1.0 / 64);

    NoteDuration(double durationInBeats)
    {
        this.durationInBeats = durationInBeats;
    }

    public double getFractionOfBeat()
    {
        return durationInBeats;
    }

    private final double durationInBeats;
}
