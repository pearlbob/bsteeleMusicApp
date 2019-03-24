package com.bsteele.bsteeleMusicApp.client.util;

import com.bsteele.bsteeleMusicApp.shared.util.StringTriple;

import java.util.ArrayList;

public class StringTripleToHtmlTable {

    public static final String toHtmlTable(StringTriple titles, ArrayList<StringTriple> arrayList){
        StringBuilder sb = new StringBuilder();

        sb.append("<table>\n");
        if ( titles!=null )
            sb.append("<tr><th>"+titles.getA()+"</th><th>"+titles.getB()+"</th><th>"+titles.getC()+"<tr><th>\n");

        for ( StringTriple stringTriple : arrayList){
            if ( stringTriple==null )
                continue;
            sb.append("<tr><td>")
                    .append(stringTriple.getA()).append("</td><td>")
                    .append(stringTriple.getB()).append("</td><td>")
                    .append(stringTriple.getC()).append("</td></tr>\n");
        }
        sb.append("</table>\n");
        return sb.toString();
    }
}
