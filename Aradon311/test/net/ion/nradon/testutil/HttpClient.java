package net.ion.nradon.testutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import net.ion.nradon.Radon;

public class HttpClient {

    private static final int BUFFER_SIZE_IF_NO_CONTENT_LENGTH_HEADER = 1024;

    public static URLConnection httpGet(Radon webServer, String path) throws IOException {
        URL url = new URL(webServer.getConfig().publicUri().toURL(), path);
        return url.openConnection();
    }

    public static URLConnection httpGetAcceptCompressed(Radon webServer, String path) throws IOException {
        URLConnection result = httpGet(webServer, path);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) result;
        httpUrlConnection.addRequestProperty("Accept-Encoding", "gzip");
        return result;
    }

    public static URLConnection httpPost(Radon webServer, String path, String body) throws IOException {
        URL url = new URL(webServer.getConfig().publicUri().toURL(), path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setDoOutput(true);
        urlConnection.getOutputStream().write(body.getBytes("UTF8"));
        return urlConnection;
    }

    public static URLConnection httpPostCompressed(Radon webServer, String path, String body) throws IOException {
        URL url = new URL(webServer.getConfig().publicUri().toURL(), path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Content-Encoding", "gzip");
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setDoOutput(true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        gzip.write(body.getBytes("UTF8"));
        gzip.close();

        urlConnection.getOutputStream().write(baos.toByteArray());
        return urlConnection;
    }

    public static String contents(URLConnection urlConnection) throws IOException {
        int length = urlConnection.getContentLength();
        byte[] buffer = new byte[length == -1 ? BUFFER_SIZE_IF_NO_CONTENT_LENGTH_HEADER : length];

        int read = 0;
        while (length == -1 || read < length) {
            int more = urlConnection.getInputStream().read(buffer, read, buffer.length - read);
            if(more == -1) {
                break;
            } else {
                read += more;
            }
        }
        urlConnection.getInputStream().close();

        if (length != -1 && length != read) {
            throw new IOException("Content-Length header (" + length + ") did not match actual length (" + read + ")");
        }
        return new String(buffer, 0, read);
    }

    public static String decompressContents(URLConnection urlConnection) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream gzipReader = new GZIPInputStream(urlConnection.getInputStream());
        byte[] buffer = new byte[BUFFER_SIZE_IF_NO_CONTENT_LENGTH_HEADER];
        int read = 0;

        while ((read = gzipReader.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }

        return new String(baos.toByteArray(), "UTF-8");
    }

	public static HttpsURLConnection httpsGet(Radon radon, String path) throws IOException{
        URL wsUrl = radon.getConfig().publicUri().toURL();
        URL url = new URL("https", "localhost", wsUrl.getPort(), path);
        return (HttpsURLConnection) url.openConnection();
	}

}
