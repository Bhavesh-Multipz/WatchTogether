package com.instaconnect.android.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.instaconnect.android.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class DownloadFileHelper extends AsyncTask<Void, Void, String> {
    private final String TAG = getClass().getSimpleName();
    private String downloadUrl;
    private OnTaskCompleted listener;
    private Context context;

    public DownloadFileHelper(Context context, String downloadUrl, OnTaskCompleted listener) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        File file = null;
        try {
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

// expect HTTP 200 OK, so we don't mistakenly save error report
// instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

// this will be useful to display download percentage
// might be -1: server did not report the length
            int fileLength = connection.getContentLength();

// download the file
            input = connection.getInputStream();

            String filePath = "";
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                filePath = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name)).getAbsolutePath();
            } else {
                filePath = new File(context.getFilesDir(), context.getString(R.string.app_name)).getAbsolutePath();
            }

            File dir = new File(filePath);
            dir.mkdirs();

            Random r = new Random();
            int i1 = r.nextInt(1000 - 1) + 65;

//file = new File(dir, Constants.APP_NAME.replace(" ", "_") + "_" + i1 + ".pdf");
            file = new File(dir, System.currentTimeMillis() + ".jpeg");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }

            output = new FileOutputStream(file);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
// allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
// publishing the progress....
                if (fileLength > 0) // only if total length is known
//publishProgress((int) (total * 100 / fileLength));
                    Log.e(TAG, "Progress : " + (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

/* Log.e(TAG, "doInBackground(){..}");
Bitmap bitmap = null;
OutputStream output;
File file = null;
try {
URL url = new URL(downloadUrl);
bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());

String filePath = "";
String state = Environment.getExternalStorageState();
if (Environment.MEDIA_MOUNTED.equals(state)) {
filePath = new File(Environment.getExternalStorageDirectory(), Constants.APP_NAME).getAbsolutePath();
} else {
filePath = new File(context.getFilesDir(), Constants.APP_NAME).getAbsolutePath();
}

File dir = new File(filePath);
dir.mkdirs();

Random r = new Random();
int i1 = r.nextInt(1000 - 1) + 65;

file = new File(dir, Constants.APP_NAME.replace(" ", "_") + "_" + i1 + downloadUrl.substring(0, downloadUrl.lastIndexOf('.')));
if (file.exists()) {
file.delete();
file.createNewFile();
} else {
file.createNewFile();
}

try {
FileOutputStream out = new FileOutputStream(file);
bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
out.flush();
out.close();
} catch (Exception e) {
e.printStackTrace();
}

} catch (IOException e) {
Log.e(TAG, e.getMessage());
}*/
        return file.getAbsolutePath();
    }

    @Override
    protected void onPostExecute(String path) {
        listener.onTaskCompleted(path);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(String saveFilePath);
    }
}