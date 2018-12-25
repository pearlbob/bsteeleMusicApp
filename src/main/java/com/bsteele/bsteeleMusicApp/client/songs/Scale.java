package com.bsteele.bsteeleMusicApp.client.songs;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum Scale
{
    //  NOTE: all offsets count from 1 to match typical music theory
    major(true, new ScaleFormula[]{
            ScaleFormula.R,
            ScaleFormula.f2,
            ScaleFormula.f3,
            ScaleFormula.f4,
            ScaleFormula.f5,
            ScaleFormula.f6,
            ScaleFormula.f7
    }),
    majorPentatonic(true, new ScaleFormula[]{
            ScaleFormula.R,
            ScaleFormula.f2,
            ScaleFormula.f3,
            ScaleFormula.f5,
            ScaleFormula.f6
    }),
    majorBlues(true, new ScaleFormula[]{
            ScaleFormula.R,
            ScaleFormula.f2,
            ScaleFormula.m3,
            ScaleFormula.f3,
            ScaleFormula.f5,
            ScaleFormula.m6
    }),
    jazz(true, new ScaleFormula[]{        //   Dominant Bebop Scale
            ScaleFormula.R,
            ScaleFormula.f2,
            ScaleFormula.f3,
            ScaleFormula.f4,
            ScaleFormula.f5,
            ScaleFormula.f6,
            ScaleFormula.b7,
            ScaleFormula.f7
    }),
    minor(false, new ScaleFormula[]{
            ScaleFormula.R,
            ScaleFormula.f2,
            ScaleFormula.m3,
            ScaleFormula.f4,
            ScaleFormula.f5,
            ScaleFormula.m6,
            ScaleFormula.b7
    }),
    minorPentatonic(false, new ScaleFormula[]{
            ScaleFormula.R,
            ScaleFormula.m3,
            ScaleFormula.f4,
            ScaleFormula.f5,
            ScaleFormula.b7
    }),
    minorBlues(false, new ScaleFormula[]{   //    hexatonic
            ScaleFormula.R,
            ScaleFormula.m3,
            ScaleFormula.f4,
            ScaleFormula.b5,
            ScaleFormula.f5,
            ScaleFormula.b7
    }),;

    Scale(boolean isMajor, ScaleFormula formula[])
    {
        this.isMajor = isMajor;
        this.formula = formula;
    }

    public boolean isMajor()
    {
        return isMajor;
    }

    public ScaleFormula[] getFormula()
    {
        return formula;
    }

    private final boolean isMajor;
    private final ScaleFormula[] formula;

}
