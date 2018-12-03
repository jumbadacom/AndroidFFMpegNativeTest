package com.example.nativetest;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.appyvet.materialrangebar.RangeBar;
import com.example.video.VideoProcess;

import java.io.File;
import java.text.MessageFormat;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PageTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageOneFragment extends Fragment implements MainActivity.FragmentInformer, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String mSelectedPath;
    private AppCompatButton mBtnTrim, mBtnChoose, mBtnFront, mBtnBack, mBtnSpeed, mBtnRotate;
    AppCompatEditText mEnTerText;
    private VideoView mVideoView;
    private RangeBar mRangeBar;
    private AppCompatTextView mAppCompatTextView;
    private VideoProcess mVideoProcess;
    private OnFragmentInteractionListener mListener;

    public PageOneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FFmpegFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PageOneFragment newInstance(String param1, String param2) {
        PageOneFragment fragment = new PageOneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.mVideoProcess = VideoProcess.getInstance();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        activity.addInformer(this);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        mVideoView = (VideoView) view.findViewById(R.id.videoView);
        mRangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        mAppCompatTextView = (AppCompatTextView) view.findViewById(R.id.textTv);
        mEnTerText = (AppCompatEditText) view.findViewById(R.id.editTextEnter);
        mBtnChoose = (AppCompatButton) view.findViewById(R.id.btnChoose);
        mBtnTrim = (AppCompatButton) view.findViewById(R.id.btnTrim);
        mBtnFront = (AppCompatButton) view.findViewById(R.id.btnFrontRec);
        mBtnBack = (AppCompatButton) view.findViewById(R.id.btnBackRec);
        mBtnSpeed = (AppCompatButton) view.findViewById(R.id.btnSpeed);
        mBtnRotate = (AppCompatButton) view.findViewById(R.id.btnRotate);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBtnChoose.setOnClickListener(this);
        mBtnFront.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnSpeed.setOnClickListener(this);
        mBtnRotate.setOnClickListener(this);
        mBtnTrim.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).removeInformer();
        mListener = null;
    }


    void onVideoSelected(Uri selectedVideoUri) {
        DebugLog.write();
        //final String selectedPath = getPath(selectedImageUri);
        mSelectedPath = selectedVideoUri.getPath();
        DebugLog.write(mSelectedPath);
        mAppCompatTextView.setText(mSelectedPath);
        getDuration(mSelectedPath);
        prepareVideoView(mSelectedPath);

    }

    @Override
    public void frontVideoReady(File file, String path) {
        DebugLog.write();
        prepareVideoView(path);
        getDuration(path);
        mSelectedPath = path;
        new Thread() {
            @Override
            public void run() {
                super.run();
                DebugLog.write();
            }
        };
    }

    @Override
    public void backVideoReady(File file, String path) {
        DebugLog.write();
        prepareVideoView(path);
        getDuration(path);
        mSelectedPath = path;
        new Thread() {
            @Override
            public void run() {
                super.run();
                DebugLog.write();
            }
        };
    }

    @Override
    public void frontVideoLoadingError() {
        DebugLog.write();
    }

    @Override
    public void backVideoLoadingError() {
        DebugLog.write();
    }


    void prepareVideoView(String selectedPath) {
        mVideoView.setVideoPath(selectedPath);
        mVideoView.setMediaController(new MediaController(this.getContext()));
        mVideoView.seekTo(100);
    }

    Integer getDuration(String selectedPath) {
        try {
            int duration = mVideoProcess.getDuration(selectedPath);
            DebugLog.write("duration=" + duration);
            mRangeBar.setTickEnd(duration);
            return duration;
        } catch (Exception e) {
            DebugLog.write(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        DebugLog.write();
        if (v == mBtnChoose) {
            DebugLog.write("choose from file system");
            mListener.choose();
        } else if (v == mBtnFront) {
            DebugLog.write("get front square record");
            mListener.loadFrontVideo();
        } else if (v == mBtnBack) {
            DebugLog.write("get back square record");
            mListener.loadBackVideo();
        } else if (v == mBtnTrim) {
            DebugLog.write("trim the video");
            if (mSelectedPath != null) {
                final String output = new File(new File(mSelectedPath).getParent(), "trimmed.mp4").toString();
                final ProgressDialog progressDialog = new ProgressDialog(PageOneFragment.this.getContext());
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            DebugLog.write(MessageFormat.format("trim() -> path={0} output={1} leftIndex={2} rightIndex={3}", mSelectedPath,
                                    output, mRangeBar.getLeftIndex(), mRangeBar.getRightIndex()));
                            int result = mVideoProcess.trim(mSelectedPath, output, mRangeBar.getLeftIndex(), mRangeBar.getRightIndex());
                            DebugLog.write("trim result= " + result);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    mVideoView.setVideoPath(output);
                                    mVideoView.setMediaController(new MediaController(PageOneFragment.this.getContext()));
                                    mVideoView.seekTo(100);
                                    mVideoView.requestFocus(0);
                                    mVideoView.start();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            DebugLog.write(e.getMessage());
                        }
                    }
                }.start();
            }

        } else if (v == mBtnSpeed) {
            DebugLog.write("speed up the video");
            if (mSelectedPath != null) {
                final String output = new File(new File(mSelectedPath).getParent(), "speed.mp4").toString();
                final ProgressDialog progressDialog = new ProgressDialog(PageOneFragment.this.getContext());
                int defaultSpeed = 2;
                int speed = getEnteredText() == null ? defaultSpeed : getEnteredText();
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            DebugLog.write(MessageFormat.format("speed() -> path={0} output={1} speed={2} ", mSelectedPath, output, speed));
                            int result = mVideoProcess.speedOfVideo(mSelectedPath, output, speed);
                            DebugLog.write(result);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    mVideoView.setVideoPath(output);
                                    mVideoView.setMediaController(new MediaController(PageOneFragment.this.getContext()));
                                    mVideoView.seekTo(100);
                                    mVideoView.requestFocus(0);
                                    mVideoView.start();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            DebugLog.write(e.getMessage());
                        }
                    }
                }.start();
            }
        } else if (v == mBtnRotate) {
            DebugLog.write("rotate the video");
        }

    }


    private Integer getEnteredText() {
        try {
            return Integer.parseInt(mEnTerText.getText().toString());
        } catch (NumberFormatException e) {
            DebugLog.write(e.getMessage());
            return null;
        } finally {
            mEnTerText.getText().clear();
        }

    }
}
