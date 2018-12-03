package com.example.nativetest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.video.VideoProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.MessageFormat;

import static com.example.nativetest.MainActivity.VideoType.BACK_RECORD;
import static com.example.nativetest.MainActivity.VideoType.FRONT_RECORD;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, Handler.Callback {


    static {
        //   System.loadLibrary("native-lib");
    }


    public static final int REQUEST_TAKE_GALLERY_VIDEO = 1001;
    public static final int MSG_FRONT_FILE_READY = 3001;
    public static final int MSG_BACK_FILE_READY = 3002;
    public static final int MSG_FRONT_FILE_ERROR = 5001;
    public static final int MSG_BACK_FILE_ERROR = 5002;

    private FragmentInformer mInformer;
    private VideoProcess mVideoProcess=VideoProcess.getInstance();
    private File mFrontFile;
    private File mBackFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DebugLog.write();
        if (Build.VERSION.SDK_INT > 22) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        DebugLog.write("message="+mVideoProcess.message());
        // DebugLog.write("JNI="+jni());
        // DebugLog.write("Rubberband="+testRubberBand());
        findViewById(R.id.btnPageOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLog.write();
                addFragmentOne();

            }
        });
        findViewById(R.id.btnPageTwo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLog.write();
                addFragmentTwo();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.write(MessageFormat.format("requestCode={0} resultCode={1}", requestCode, resultCode));
        if (resultCode == RESULT_OK) {
            DebugLog.write();
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                DebugLog.write();
                FragmentManager fm = getSupportFragmentManager();
                PageOneFragment fragmentTrim = (PageOneFragment) fm.findFragmentByTag(PageOneFragment.class.getSimpleName());
                fragmentTrim.onVideoSelected(data.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath(Uri uri) {
        DebugLog.write(uri.toString());
        String[] projection = {MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = null;
        try {
            ContentResolver cr = getContentResolver();
            if (cr == null) {
                DebugLog.write("null cr");
            } else {
                DebugLog.write("not null cr");
            }
            cursor = cr.query(uri, projection, null, null, null);
            if (cursor != null) {

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                DebugLog.write(column_index);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else {
                DebugLog.write();
                return null;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                DebugLog.write();
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission denied to access your location.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public native String jni();

    public native int testRubberBand();


    @Override
    public void onFragmentInteraction(String name) {
        DebugLog.write(name);
    }

    @Override
    public void choose() {
        DebugLog.write();
        Intent intent = new Intent();
        intent.setType("file/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose file..."), MainActivity.REQUEST_TAKE_GALLERY_VIDEO);
    }

    @Override
    public void loadFrontVideo() {
        DebugLog.write();
        if (mFrontFile == null) {
            DebugLog.write();
            runOnUiThread(new LoadProcessRunnable(VideoType.FRONT_RECORD, this));
        } else {
            DebugLog.write();
            mInformer.frontVideoReady(mFrontFile, mFrontFile.getAbsolutePath());
        }
    }


    @Override
    public void loadBackVideo() {
        DebugLog.write();
        if (mBackFile == null) {
            DebugLog.write();
            runOnUiThread(new LoadProcessRunnable(VideoType.BACK_RECORD, this));
        } else {
            DebugLog.write();
            mInformer.backVideoReady(mBackFile, mBackFile.getAbsolutePath());
        }

    }

    void addFragmentOne() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        PageOneFragment fragment = PageOneFragment.newInstance("1", "2");
        transaction.replace(R.id.containerMain, fragment, PageOneFragment.class.getSimpleName());
        transaction.addToBackStack(PageOneFragment.class.getSimpleName());
        transaction.commit();

    }

    void addFragmentTwo() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        PageTwoFragment fFmpegFragment = PageTwoFragment.newInstance("1", "2");
        transaction.replace(R.id.containerMain, fFmpegFragment, PageTwoFragment.class.getSimpleName());
        transaction.addToBackStack(PageTwoFragment.class.getSimpleName());
        transaction.commit();

    }


    File loadBack() throws IOException, NullPointerException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open("videos/back_record.mp4");
        File file = createFile("temp_back.mp4");
        OutputStream o1 = new FileOutputStream(file);
        byte[] buffer = new byte[4 * 1024]; // or other buffer size
        int read;
        while ((read = is.read(buffer)) != -1) {
            o1.write(buffer, 0, read);
        }
        o1.flush();
        o1.close();
        is.close();
        DebugLog.write("size" + (file.length() / 1024) + "kb");
        return file;
    }

    File loadFront() throws IOException, NullPointerException {
        AssetManager assetManager = getAssets();
        File file = createFile("temp_front.mp4");
        InputStream is = assetManager.open("videos/front_record.mp4");
        OutputStream o1 = new FileOutputStream(file);
        byte[] buffer = new byte[4 * 1024]; // or other buffer size
        int read;
        while ((read = is.read(buffer)) != -1) {
            o1.write(buffer, 0, read);
        }
        o1.flush();
        o1.close();
        is.close();
        DebugLog.write("size=" + (file.length() / 1024) + "kb");
        return file;
    }

    private File createFile(String fileName) throws NullPointerException, IOException {
        File file = new File(getFilesDir(), fileName);
        if (file == null) throw new NullPointerException("Media File has not been created.");
        if (file.exists()) {
            file.delete();
            file.createNewFile();
            DebugLog.write("not null " + file.getAbsolutePath());
        } else {
            DebugLog.write("not null " + file.getAbsolutePath());

        }
        return file;

    }

    @Override
    public boolean handleMessage(Message msg) {
        DebugLog.write();
        switch (msg.what) {
            case MSG_FRONT_FILE_READY:
                mInformer.frontVideoReady(mFrontFile, (String) msg.obj);
                break;
            case MSG_BACK_FILE_READY:
                mInformer.backVideoReady(mBackFile, (String) msg.obj);
                break;
            case MSG_FRONT_FILE_ERROR:
                mInformer.frontVideoLoadingError();
                break;
            case MSG_BACK_FILE_ERROR:
                mInformer.backVideoLoadingError();
                break;
        }
        return false;
    }


    private class LoadProcessRunnable implements Runnable {
        Handler.Callback mCallback;
        @VideoType
        int mType;

        LoadProcessRunnable(@VideoType int type, Handler.Callback callback) {
            mType = type;
            mCallback = callback;
        }

        @Override
        public void run() {
            File file = null;
            String path = "";
            Message msg = Message.obtain();
            boolean hasException = false;
            try {
                switch (mType) {
                    case VideoType.FRONT_RECORD:
                        file = loadFront();
                        mFrontFile = file;
                        path = file.getAbsolutePath();
                        msg.what = MSG_FRONT_FILE_READY;
                        break;
                    case VideoType.BACK_RECORD:
                        file = loadBack();
                        mBackFile = file;
                        msg.what = MSG_BACK_FILE_READY;
                        path = file.getAbsolutePath();
                        break;
                }
            } catch (IOException io) {
                DebugLog.write(io.getMessage());
                hasException = true;
            } catch (NullPointerException ne) {
                DebugLog.write(ne.getMessage());
                hasException = true;
            } catch (Exception ex) {
                DebugLog.write(ex.getMessage());
                hasException = true;
            }
            if (hasException) {
                msg.what = (mType == FRONT_RECORD) ? MSG_FRONT_FILE_ERROR : MSG_BACK_FILE_ERROR;
                msg.obj = null;
            } else {
                msg.obj = path;

            }
            mCallback.handleMessage(msg);


        }
    }

    @Retention(RetentionPolicy.CLASS)
    @IntDef({FRONT_RECORD, BACK_RECORD})
    public @interface VideoType {
        int FRONT_RECORD = 0;
        int BACK_RECORD = 1;
    }


    interface FragmentInformer {

        void frontVideoReady(File file, String path);

        void backVideoReady(File file, String path);

        void frontVideoLoadingError();

        void backVideoLoadingError();

    }

    public void addInformer(FragmentInformer informer) {
        mInformer = null;
        mInformer = informer;
    }

    public void removeInformer() {
        mInformer = null;
    }
}
