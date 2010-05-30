package com.yourmediashelf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
private static final int BUFF_SIZE = 100000;

    /**
     * A static 100K buffer used by the copy operation.
     */
    private static final byte[] buffer = new byte[BUFF_SIZE];

    /**
     * Copy an InputStream to an OutputStream.
     * While this method will automatically close the destination OutputStream,
     * the caller is responsible for closing the source InputStream.
     *
     * @param source
     * @param destination
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
     * @see "http://java.sun.com/docs/books/performance/1st_edition/html/JPIOPerformance.fm.html#22980"
     */
    public static boolean copy(InputStream source, OutputStream destination) {
        try {
            while (true) {
                synchronized (buffer) {
                    int amountRead = source.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    destination.write(buffer, 0, amountRead);
                }
            }
            destination.flush();
            destination.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
	public static File createTempDir(String prefix, File directory) throws IOException {
        File tempFile = File.createTempFile(prefix, "", directory);
        if (!tempFile.delete())
            throw new IOException();
        if (!tempFile.mkdir())
            throw new IOException();
        return tempFile;
    }
}
