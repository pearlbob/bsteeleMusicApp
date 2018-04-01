package com.bsteele.bsteeleMusicApp.shared;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class Util {
    public static final int mod( int n, int modulus)  {
        n = n % modulus;
        if ( n < 0 )
            n += modulus;
        return n;
    }
}
