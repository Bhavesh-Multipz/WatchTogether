package com.instaconnect.android.ytextractor;

import java.util.Objects;

public class YtFile {

    private Format format;
    private String url = "";
    private int position;

    YtFile(Format format, String url, int position) {
        this.format = format;
        this.url = url;
        this.position = position;
    }

    /**
     * The url to download the file.
     */
    public String getUrl() {
        return url;
    }

    public int getPosition(){
        return position;
    }

    /**
     * Format data for the specific file.
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Format data for the specific file.
     */
    @Deprecated
    public Format getMeta() {
        return format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YtFile ytFile = (YtFile) o;

        if (!Objects.equals(format, ytFile.format)) return false;
        return Objects.equals(url, ytFile.url);
    }

    @Override
    public int hashCode() {
        int result = format != null ? format.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "YtFile{" +
                "format=" + format +
                ", url='" + url + '\'' +
                '}';
    }
}
