package com.jautumn.downloader;

import java.io.IOException;

public interface Downloader {

    DownloadResult download(String downloadURL) throws IOException, InterruptedException;

    DownloadResult download(String downloadURL, String proxyHost, int proxyPort) throws IOException, InterruptedException;
}
