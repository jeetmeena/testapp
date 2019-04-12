package com.example.testapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerifyOtp.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerifyOtp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyOtp extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button mButtonOtpVerify;
    private EditText mEditTextOtp;
    public VerifyOtp() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifyOtp.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyOtp newInstance(String param1, String param2) {
        VerifyOtp fragment = new VerifyOtp();
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
       View view= inflater.inflate(R.layout.fragment_verify_otp, container, false);
            mButtonOtpVerify=view.findViewById(R.id.button_verify_otp);
            mEditTextOtp=view.findViewById(R.id.edittext_otp);
            mButtonOtpVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pattern = "\\d+";
                    // Create a Pattern object
                    String mVerificationCode=mEditTextOtp.getText().toString();
                    Pattern r = Pattern.compile(pattern);
                    // Now create matcher object.
                    Matcher m = r.matcher(mVerificationCode);
                    if (mListener != null&& mVerificationCode.length() == 6 &&m.matches() ) {
                        //mListener.onFragmentInteraction(new VisitorInformationFragment());
                        mListener.onVerifyVerificationCode(mVerificationCode);
                    }
                }
            });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction(Fragment newFragment);
        void onVerifyVerificationCode(String mVerificationCode);
    }
}
