package com.example.video;

public class VideoProcess {

    private static final VideoProcess INSTANCE;

    static {
        INSTANCE = new VideoProcess();
        System.loadLibrary("vpl");
    }
    public static VideoProcess getInstance() {
        return INSTANCE;
    }

    private VideoProcess() {

    }

    public native String message();

    public native int getDuration(String input) throws Exception;

    public native int remux(String input, String output) throws Exception;

    public native int mergeAudioWithVideoWithoutTranscoding(String inputV, String inputA, String output) throws Exception;

    public native int trim(String input, String output, double start, double end) throws Exception;

    public native int rotateDisplayMatrix(String input, String output, double rotation) throws Exception;

    public native int speedOfVideo(String input, String output, int coefficient) throws Exception;
}
