package com.jautumn.downloader;

import lombok.Data;

@Data
public class DownloadResult {
    private byte[] data;
    private long size;
    private String url;
}
