package com.cookandroid.final_project_core;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

// 갤러리 접근과 관련된 class 이다. (오픈소스 이용)

public class MediaScanner {
    private Context ctxt;
    private String file_Path;
    private MediaScannerConnection mMediaScanner;
    private MediaScannerConnection.MediaScannerConnectionClient mMediaScannerClient;





    public static MediaScanner newInstance(Context context)
    {
        return new MediaScanner (context);
    }

    private MediaScanner (Context context) {

        ctxt = context;

    }

    public void mediaScanning(final String path) {
        if (mMediaScanner == null) {
            mMediaScannerClient = new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override public void onMediaScannerConnected() {
                    mMediaScanner.scanFile(file_Path, null);
                }

                @Override public void onScanCompleted(String path, Uri uri) {

                    mMediaScanner.disconnect();
                }
            };

            mMediaScanner = new MediaScannerConnection(ctxt, mMediaScannerClient);
        }

        file_Path = path;

        mMediaScanner.connect();
    }
}
