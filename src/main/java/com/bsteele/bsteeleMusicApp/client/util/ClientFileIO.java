package com.bsteele.bsteeleMusicApp.client.util;

import com.bsteele.bsteeleMusicApp.shared.FileWriter;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class ClientFileIO implements FileWriter {

    @Override
    public void write(String fileName, String contents) {
        saveDataAs( fileName,  contents);
    }

    /**
     * Native function to write the data to the local file system
     *
     * @param filename filename to be written
     * @param data the data to be written
     */
    public static final native void saveDataAs(String filename, String data) /*-{
        var data = new Blob([data], {type: 'text/plain'});
        // If we are replacing a previously generated file we need to
        // manually revoke the object URL to avoid memory leaks.
        //if (textFile !== null) {
        //    window.URL.revokeObjectURL(textFile);
        //}

        var textFile = window.URL.createObjectURL(data);
//    if (downloadlink === null) {
//        downloadlink = document.createElement("a");
//        downloadlink.style = "display:none";
//    }
        var downloadlink = document.createElement("a");
        downloadlink.style = "display:none";
        downloadlink.download = filename;
        downloadlink.href = textFile;
        downloadlink.click();
    }-*/;


}
