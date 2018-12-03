package com.example.nativetest;


import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.nativetest.BuildConfig.DEBUG;


public class DebugLog {

    private static final int MAX_INDEX = 4000;
    private static final int MIN_INDEX = 3000;
    private boolean isClosed;


    public DebugLog(boolean isClosed) {
        this.isClosed = isClosed;
        final StackTraceElement stackTrace = new Exception().getStackTrace()[1];
        String fileName = stackTrace.getFileName();
        if (fileName == null)
            fileName = "";  // It is necessary if you want to use proguard obfuscation.
        final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                + stackTrace.getLineNumber() + ")";

        Log.d("***", info);
    }

    public void log() {
        if (DEBUG) {
            if (isClosed) return;
            final StackTraceElement stackTrace = new Exception().getStackTrace()[1];
            String fileName = stackTrace.getFileName();
            if (fileName == null)
                fileName = "";  // It is necessary if you want to use proguard obfuscation.
            final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                    + stackTrace.getLineNumber() + ")";

            Log.d("***", info);
        }

    }

    public void log(@Nullable Object message) {
        if (DEBUG) {
            if (!isClosed) {
                final StackTraceElement stackTrace = new Exception().getStackTrace()[1];
                String fileName = stackTrace.getFileName();
                if (fileName == null)
                    fileName = "";  // It is necessary if you want to use proguard obfuscation.
                final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                        + stackTrace.getLineNumber() + ")";

                String fullMessage = String.valueOf(message);

                if (fullMessage.length() > MAX_INDEX) {

                    String theSubstring = fullMessage.substring(0, MAX_INDEX);
                    int theIndex = MAX_INDEX;

                    // Try to find a substring break at a line end.
                    // theIndex = theSubstring.lastIndexOf('\n');
                    if (theIndex >= MIN_INDEX) {
                        theSubstring = theSubstring.substring(0, theIndex);
                    } else {
                        theIndex = MAX_INDEX;
                    }
                    //log the substring
                    Log.d("***", info + " : " + String.valueOf(theSubstring));

                    // Recursively log the remainder.
                    log(fullMessage.substring(theIndex));

                } else {
                    Log.d("***", info + " : " + String.valueOf(message));
                }

            }
        }
    }


    /**
     * Writes debugging log.
     */

    public static void write() {
        if (DEBUG) {
            final StackTraceElement stackTrace = new Exception().getStackTrace()[1];
            String fileName = stackTrace.getFileName();
            if (fileName == null)
                fileName = "";  // It is necessary if you want to use proguard obfuscation.
            final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                    + stackTrace.getLineNumber() + ")";

            Log.d("***", info);
        }
    }

    public static void write(final Object message) {
        if (DEBUG) {
            final StackTraceElement stackTrace = new Exception().getStackTrace()[1];
            String fileName = stackTrace.getFileName();
            if (fileName == null)
                fileName = "";  // It is necessary if you want to use proguard obfuscation.
            final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                    + stackTrace.getLineNumber() + ")";

            String fullMessage = String.valueOf(message);

            if (fullMessage.length() > MAX_INDEX) {

                String theSubstring = fullMessage.substring(0, MAX_INDEX);
                int theIndex = MAX_INDEX;

                // Try to find a substring break at a line end.
                // theIndex = theSubstring.lastIndexOf('\n');
                if (theIndex >= MIN_INDEX) {
                    theSubstring = theSubstring.substring(0, theIndex);
                } else {
                    theIndex = MAX_INDEX;
                }
                //log the substring
                Log.d("***", info + " : " + String.valueOf(theSubstring));

                // Recursively log the remainder.
                write(fullMessage.substring(theIndex));

            } else {
                Log.d("***", info + " : " + String.valueOf(message));
            }


        }

    }

    public static void write(final String tag, final Object message) {
        if (DEBUG) {
            final StackTraceElement stackTrace = new Exception().getStackTrace()[1];
            String fileName = stackTrace.getFileName();
            if (fileName == null)
                fileName = "";  // It is necessary if you want to use proguard obfuscation.
            final String info = stackTrace.getMethodName() + " (" + fileName + ":"
                    + stackTrace.getLineNumber() + ")";
            final String searcherMark = " *** ";
            Log.d("_" + tag, info + searcherMark + " : " + String.valueOf(message));
        }

    }
}


