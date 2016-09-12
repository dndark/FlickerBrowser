package com.example.chenl.flickerbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by chenl on 9/12/2016.
 */

enum DownloadStatus { IDLE, PROCESSING, NOT_INTIALISED, FAILED_OR_EMPTY, OK }
public class GetRawData {
    private String LOG_TAG = GetRawData.class.getSimpleName();
    private String mRawURL;
    private String mData;
    private DownloadStatus mDownloadStatus;

    public GetRawData(String mRawData) {
        this.mRawURL = mRawData;
        this.mDownloadStatus = DownloadStatus.IDLE;
    }

    public void reset() {
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mData = null;
        this.mRawURL = null;
    }

    public String getmData() {
        return mData;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    public void execute(){
        this.mDownloadStatus = DownloadStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        downloadRawData.execute(mRawURL);
    }
    public class DownloadRawData extends AsyncTask<String, Void, String>{

        protected void onPostExecute(String webData){
            mData = webData;
            Log.v(LOG_TAG, "Data return was " + mData);
            if (mData == null){
                if (mRawURL == null){
                    mDownloadStatus = DownloadStatus.NOT_INTIALISED;
                }else{
                    mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
                }
            } else{
                mDownloadStatus = DownloadStatus.OK;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (params == null)
                return null;
            try {
                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                StringBuffer buffer  = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                return buffer.toString();

            } catch (IOException e){
                Log.e(LOG_TAG, "error" + e);
                return null;
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "ERROR closing stream", e);
                    }
                }
            }
        }
    }
}
