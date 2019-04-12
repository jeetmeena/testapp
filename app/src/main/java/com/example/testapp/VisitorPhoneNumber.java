package com.example.testapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisitorPhoneNumber.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisitorPhoneNumber#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitorPhoneNumber extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mPhoneNumber;
    private Spinner mCounterCode;
    private String mCode="1";
    private Button mSignIn;
    private ImageButton mButtonStartCamera;
    private EditText mEditTextPersonName;
    private ImageView mUserImage;
    private File mBitmapImage;
    private   ViewGroup rootView;
    public VisitorPhoneNumber() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorPhoneNumber.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitorPhoneNumber newInstance(String param1, String param2) {
        VisitorPhoneNumber fragment = new VisitorPhoneNumber();
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
        rootView= (ViewGroup) inflater.inflate(R.layout.fragment_visitor_phone_number, container, false);
        mSignIn=rootView.findViewById(R.id.button_sign_in);
        mCounterCode=rootView.findViewById(R.id.spinner_counter_code);
        mPhoneNumber=rootView.findViewById(R.id.editText_phone_number);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.counter_code, android.R.layout.simple_spinner_item);
        // Specify the layout to use
        mUserImage=rootView.findViewById(R.id.userImage);

        mEditTextPersonName=rootView.findViewById(R.id.edittext_username);
        mButtonStartCamera=rootView.findViewById(R.id.start_camera_view);
        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.avatarimage);
        // convert drawable to bitmap
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
         //Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        //mUserImage.setImageResource(R.drawable.avatarimage);
        setImageBas(bitmap,null);
        mButtonStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    insertCameraFragment();
                    try {
                        mBitmapImage.delete();
                    }catch (Exception e){}
                }catch (Exception e){}
            }
        });

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCounterCode.setAdapter(adapter);
        mCode= mCounterCode.getSelectedItem().toString();
        mListener.onSendMessegToVisitor("Pleas Choose Your Counter phone Co");
        mCounterCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             mCode= mCounterCode.getSelectedItem().toString();
            }

            @Override
                public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       // setSpinner();
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new VerifyOtp());
                String mPhoneNumbers=mPhoneNumber.getText().toString();
                mPhoneNumbers=mPhoneNumbers.replaceAll("\\s", ""); // using built in method
                String pattern = "\\d+";
                // Create a Pattern object
                Pattern r = Pattern.compile(pattern);
                // Now create matcher object.
                Matcher m = r.matcher(mPhoneNumbers);
                final String mPersonName=mEditTextPersonName.getText().toString();
                if (mListener != null && !mPersonName.isEmpty()&& mBitmapImage!=null&& mPhoneNumbers.length()==10&&m.matches()) {
                    mListener.onUserInformation(mCode,mPhoneNumbers,mBitmapImage,mPersonName);
                }

                Toast.makeText(getActivity(), "pn", Toast.LENGTH_SHORT).show();

            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void  setImageBas(Bitmap bitmap1,Uri uri){
           // Drawable d = new BitmapDrawable(bitmap1);
            //Drawable drawable = getActivity().getResources().getDrawable(R.drawable.avatarimage);
           // convert drawable to bitmap
            //Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            mUserImage.setImageBitmap(bitmap1);
            if(uri!=null){
            //    Toast.makeText(getActivity(), "sokdgkk", Toast.LENGTH_SHORT).show();
              //  mUserImage.setImageURI(uri);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                mUserImage.setImageBitmap(bitmap);

            }

    }
    public void userCapturedImage(File userImage) {
        this.mBitmapImage=userImage;
        mUserImage=rootView.findViewById(R.id.userImage);
        Uri uriFromPath = Uri.fromFile(new File(userImage.getAbsolutePath()));

            Bitmap myBitmap = BitmapFactory.decodeFile(userImage.getAbsolutePath());
            setImageBas(myBitmap,uriFromPath);




    }
    private void insertCameraFragment(){

        mListener.onFragmentInteraction(new VisitorImageCapture());
    }

    public void setClearViews() {
        mBitmapImage=null;
        mEditTextPersonName.getText().clear();
        mEditTextPersonName.getText().clear();
        mUserImage.setImageBitmap(BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.avatarimage));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        void onUserInformation(String mCode ,String mPhoneNumbers ,File userImage, String userName);
        void onSendMessegToVisitor(String showMessag);
        void onCheckPhoneNumber(String mCounterCode,String mPhoneNumber);
    }
}
