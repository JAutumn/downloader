package com.jautumn.downloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.logging.Logger;

import com.jautumn.utils.IOUtils;
import com.jautumn.utils.TimeWatch;

public class DefaultDownloader implements Downloader {
    private static Logger logger = Logger.getLogger(DefaultDownloader.class.getName());
    public static TimeWatch timeWatch = new TimeWatch("Download process time left: ");

    private static final String PROXY_TEST_URL = "http://fileshare1300.depositfiles.com/auth-1480312511cd398cf0e03a35d8f21696-5.135.164.181-50403777-165180612-guest/FS130-2/learning-java-by-building-android-games.zip";
    private static final String TEST_URL = "http://fileshare1300.depositfiles.com/auth-148032591613f3b80ac4ff2acbf6ed84-95.68.140.75-50502529-165180612-guest/FS130-2/learning-java-by-building-android-games.zip";
    private static int ATTEMPT_NUMBER;
    private static int MAX_ATTEMPT_NUMBER = 100;
    private boolean connected;
    private long fileLength;

    public static void main(String[] args) throws InterruptedException, IOException {
//        System.out.println(new DefaultDownloader().download(TEST_URL));
        timeWatch.start();
        System.out.println(new DefaultDownloader().download(TEST_URL));
//        System.out.println(new DefaultDownloader().download(PROXY_TEST_URL, "5.135.164.181", 3128));
        timeWatch.stop();
        System.out.println(timeWatch.getTimeLeft());
    }

    @Override
    public DownloadResult download(String downloadURL, String proxyHost, int proxyPort) throws IOException, InterruptedException {
        URL url = new URL(downloadURL);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setConnectTimeout(5000);
        return startDownload(connection);
    }

    private DownloadResult startDownload(HttpURLConnection connection) throws InterruptedException {
        if (ATTEMPT_NUMBER == MAX_ATTEMPT_NUMBER) {
            throw new RuntimeException("max attempt number reached");
        }
        DownloadResult bookResult = null;
        try {
            ATTEMPT_NUMBER++;

            logger.info("Connection attempt: " + ATTEMPT_NUMBER);

            int responseCode = connection.getResponseCode();
            logger.info("Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new RuntimeException("forbidden");
            }

            if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                throw new RuntimeException("Service unavailable");
            }


            if (HttpURLConnection.HTTP_OK == responseCode) {
                bookResult = new DownloadResult();
                bookResult.setData(IOUtils.connectionToByteArray(connection));
                bookResult.setSize(connection.getContentLengthLong());
                bookResult.setUrl(connection.getURL().getPath());
                connection.disconnect();
                logger.info("dowloading finished");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bookResult;
    }

    @Override
    public DownloadResult download(String downloadURL) throws InterruptedException, IOException {
        URL url = new URL(downloadURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return startDownload(connection);
    }
}
