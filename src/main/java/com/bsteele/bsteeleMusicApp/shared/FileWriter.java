package com.bsteele.bsteeleMusicApp.shared;

/** An interface for writing files that allows shared code to write files in the client, server, and tests.
 *
 */
public interface FileWriter {
    /**
     * Write the given contents to the given fileName.  The directory location is implied by the environment.
     *
     * @param fileName name of the file to be written
     * @param contents the contents to be written
     */
    void write(String fileName, String contents);
}
