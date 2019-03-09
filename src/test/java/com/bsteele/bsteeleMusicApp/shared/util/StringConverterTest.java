package com.bsteele.bsteeleMusicApp.shared.util;


import java.io.UnsupportedEncodingException;

public class StringConverterTest {

    public static void printBytes(byte[] array, String name) {
        for (int k = 0; k < array.length; k++) {
            System.out.println(name + "[" + k + "] = " + "0x" +
                    UnicodeFormatter.byteToHex(array[k]));
        }
    }

    public static void main(String[] args) {

        System.out.println(System.getProperty("file.encoding"));
        String original = new String("CÃ¢â„¢Â\u00AD ECÃ¢â„¢Â\u00ADDÃ¢â„¢Â\u00ADGÃ¢â„¢Â\u00AD AÃ¢â„¢Â\u00AD ");

        System.out.println("original = " + original);
        System.out.println();

        try {
            byte[] utf8Bytes = original.getBytes("Windows-1252");
            byte[] defaultBytes = original.getBytes();

            String roundTrip = new String(utf8Bytes, "UTF8");
            System.out.println("roundTrip = " + roundTrip);

            String roundTrip2 = new String(roundTrip.getBytes("Windows-1252"), "UTF8");
            System.out.println("roundTrip2 = " + roundTrip2);


//            System.out.println();
//            printBytes(utf8Bytes, "utf8Bytes");
//            System.out.println();
//            printBytes(defaultBytes, "defaultBytes");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    } // main

}