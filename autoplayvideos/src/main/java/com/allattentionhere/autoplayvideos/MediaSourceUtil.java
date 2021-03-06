package com.allattentionhere.autoplayvideos;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MediaSourceUtil {

    @Nullable
    public static String getExtension(@NonNull Uri uri) {
        String path = uri.getLastPathSegment();
        if (path == null) {
            return "";
        }

        int periodIndex = path.lastIndexOf('.');
        if (periodIndex == -1 && uri.getPathSegments().size() > 1) {
            //Checks the second to last segment to handle manifest urls (e.g. "TearsOfSteelTeaser.ism/manifest")
            path = uri.getPathSegments().get(uri.getPathSegments().size() - 2);
            periodIndex = path.lastIndexOf('.');
        }

        //If there is no period, prepend one to the last segment in case it is the extension without a period
        if (periodIndex == -1) {
            periodIndex = 0;
            path = "." + uri.getLastPathSegment();
        }

        String rawExtension = "";
        rawExtension = path.substring(periodIndex);
        return rawExtension.toLowerCase();
    }
}