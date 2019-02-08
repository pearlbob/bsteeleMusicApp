package com.bsteele.bsteeleMusicApp.shared.songs;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public enum ScaleFormula
{
    R,
    b2,
    f2("2"),
    m3,
    f3("3"),
    f4("4"),
    b5,
    f5("5"),
    m6,
    f6("6"),
    b7,
    f7("7");

    public String getShortName()
    {
        return shortName;
    }
    ScaleFormula(){
                shortName = name();
    }
    ScaleFormula(String shortName){
        this.shortName =  shortName;
    }
    private final String shortName;


}
