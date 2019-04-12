package com.example.testapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.camerakit.CameraKitView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisitorImageCapture.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisitorImageCapture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitorImageCapture extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
     private OnFragmentInteractionListener mListener;
    private CameraKitView mCameraKitView;
    private ImageButton mButtonCamera;
    private Bitmap mBitmap;
    public VisitorImageCapture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorImageCapture.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitorImageCapture newInstance(String param1, String param2) {
        VisitorImageCapture fragment = new VisitorImageCapture();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener callback){
        this.mListener=callback;
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
        View view=inflater.inflate(R.layout.fragment_visitor_image_capture, container, false);
        mCameraKitView=view.findViewById(R.id.canera);
        mButtonCamera=view.findViewById(R.id.button_camera);
        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                        File savedPhoto = new File(Environment.getExternalStorageDirectory(), "photo_"+currentDateFormat()+".jpg");
                        try {

                            FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                            outputStream.write(bytes);

                            outputStream.close();
                            if(savedPhoto!=null){
                                mListener.onBackpressFragment(savedPhoto);

                            }
                           // Toast.makeText(getActivity(), "writefile", Toast.LENGTH_SHORT).show();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


        mCameraKitView.setErrorListener(new CameraKitView.ErrorListener() {
            @Override
            public void onError(CameraKitView cameraKitView, CameraKitView.CameraException e) {
                Toast.makeText(getActivity(), "imageCapture"+e, Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }
    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
           // mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraKitView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mCameraKitView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraKitView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCameraKitView.onStop();
       // Toast.makeText(getActivity(), "CamreraViewAStop", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mCameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBackpressFragment(File userImage);

    }

}
