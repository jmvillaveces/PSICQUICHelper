package de.mpg.biochem.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class BiochemFileUtils extends FileUtils {

	/**
     * Copies bytes from the URL <code>source to a file
     * <code>destination. The directories up to destination
     * will be created if they don't already exist.
     * <p>
     * Warning: this method does not set a connection or read timeout and thus
     * might block forever. Use {@link #copyURLToFile(URL, File, int, int)}
     * with reasonable timeouts to prevent this.
     *
     * @param source  the <code>URL to copy bytes from, must not be null
     * @param destination  the non-directory <code>File to write bytes to
     *  (possibly overwriting), must not be <code>null
     * @param append - if true, then bytes will be added to the end of the file rather than overwriting
     * @throws IOException if <code>source URL cannot be opened
     * @throws IOException if <code>destination is a directory
     * @throws IOException if <code>destination cannot be written
     * @throws IOException if <code>destination needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     */
    public static void copyURLToFile(URL source, File destination, boolean append) throws IOException {
        InputStream input = source.openStream();
        copyInputStreamToFile(input, destination, append);
    }
	
    /**
     * Copies bytes from the URL <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist.
     *
     * @param source  the <code>URL</code> to copy bytes from, must not be {@code null}
     * @param destination  the non-directory <code>File</code> to write bytes to
     *  (possibly overwriting), must not be {@code null}
     * @param connectionTimeout the number of milliseconds until this method
     *  will timeout if no connection could be established to the <code>source</code>
     * @param readTimeout the number of milliseconds until this method will
     *  timeout if no data could be read from the <code>source</code>
     * @param append - if true, then bytes will be added to the end of the file rather than overwriting
     * @throws IOException if <code>source</code> URL cannot be opened
     * @throws IOException if <code>destination</code> is a directory
     * @throws IOException if <code>destination</code> cannot be written
     * @throws IOException if <code>destination</code> needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     * @since 2.0
     */
    public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout, boolean append) throws IOException {
        URLConnection connection = source.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        InputStream input = connection.getInputStream();
        copyInputStreamToFile(input, destination, append);
    }
    
    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist.
     *
     * @param source  the <code>InputStream</code> to copy bytes from, must not be {@code null}
     * @param destination  the non-directory <code>File</code> to write bytes to
     *  (possibly overwriting), must not be {@code null}
     * @param append - if true, then bytes will be added to the end of the file rather than overwriting
     * @throws IOException if <code>destination</code> is a directory
     * @throws IOException if <code>destination</code> cannot be written
     * @throws IOException if <code>destination</code> needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     * @since 2.0
     */
    public static void copyInputStreamToFile(InputStream source, File destination, boolean append) throws IOException {
        try {
            FileOutputStream output = openOutputStream(destination, append);
            try {
                IOUtils.copy(source, output);
                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(source);
        }
    }
}
