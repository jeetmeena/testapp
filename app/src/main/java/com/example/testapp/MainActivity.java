package com.example.testapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements VisitorPhoneNumber.OnFragmentInteractionListener ,VerifyOtp.OnFragmentInteractionListener
,VisitorImageCapture.OnFragmentInteractionListener{
    private CoordinatorLayout coordinatorLayout;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuthSettings mFirebaseAuthSettings;
    private PhoneAuthProvider mPhoneAuthProvider;
    private DatabaseReference mDatabase;
    private File mCompressedImage;
    private FirebaseStorage mStorage;
    StorageReference mStorageRef; // Create a storage reference from our app
    StorageReference riversRef;
    DatabaseReference mVisitorDatabaseRef;
    UploadTask    mUploadTask;
    DatabaseReference refPhoneData;
    private String mVisitorName;
    private String mVisitorPhoneNumber;
    private String mVisitorId;
    static MainActivity mInstance;
    private boolean fileisCopressed=false;
    private boolean otpIsSand=false;
    private int subscreensOnTheStack=0;
    static int MY_PERMISSIONS_REQUEST_READ_WRITE=22;
     VisitorPhoneNumber mVisitorPhoneNumberFragment;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks   mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Toast.makeText(MainActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
            upLoadImage(true);
            onBackPressed();
            setSowMessag("Phone Number is Verified");
        }
         @Override
        public void onVerificationFailed(FirebaseException e) {
            //Toast.makeText(MainActivity.this, "faileed ", Toast.LENGTH_SHORT).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request  specifies an invalid phone number
                // ...
                setSowMessag("Invalid request");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
                setSowMessag("Server Is Busy..");
            }
            else {
                // Invalid request  specifies an invalid phone number(incorrect OTP is entered)
                // ...
                upLoadImage(false);
                onBackPressed();
                otpIsSand=false;
                setSowMessagWithActionn("OTP is incorrect",R.string.snackbar_action_retry);

            }

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId=s;
            mResendingToken=forceResendingToken;
            setSowMessag("Otp Send to user phone");
            //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            loadFragment(new VerifyOtp());//user enters the verification code that Firebase sent to the user's phone
            otpIsSand=true;

        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            //timeout duration specified to verifyPhoneNumber has passed without(30 seconds time has passed)
            upLoadImage(false);
            onBackPressed();
            setSowMessagWithActionn("OTP is incorrect",R.string.snackbar_action_retry);
         }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance=this;
        coordinatorLayout=findViewById(R.id.my_cordinatorlayout);
        FirebaseApp.initializeApp(this);
        mFirebaseAuth= FirebaseAuth.getInstance();
        mFirebaseAuthSettings = mFirebaseAuth.getFirebaseAuthSettings();
        mPhoneAuthProvider = PhoneAuthProvider.getInstance();
        setCheckSelfPermission();
        mStorage= FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        refPhoneData=FirebaseDatabase.getInstance().getReference("Visitor_PhoneNumber");
        mVisitorDatabaseRef=FirebaseDatabase.getInstance().getReference("visitors");


    }

    //Verify Phone Number is already exist or not
    public  void   verfiedPhone(final String mPhoneNumber){
        refPhoneData.child(mPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this, ""+dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                if(dataSnapshot.getValue()!=null){
                    //Toast.makeText(MainActivity.this, "verifed", Toast.LENGTH_SHORT).show();
                    Log.d("TAG","OldVisitor");
                    getVisitorInfo((String) dataSnapshot.child("mVisitorId").getValue());// Phone Number is Already exist

                }else {

                   Log.d("TAG","New Visitor");
                   sendOtpToVisitor(mPhoneNumber);//New Visitor
                   setSowMessag("WelCome To ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
   //Get Visitor Info TO Up Date visit_count
    public void getVisitorInfo(final String mVisitorId){
        mVisitorDatabaseRef.child(mVisitorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VisitorInfo visitorInfo=dataSnapshot.getValue(VisitorInfo.class);
                int visit_count=visitorInfo.getVisit_count();
                visit_count=visit_count+1;// Up Date By Add +1 visit_count
                //Toast.makeText(MainActivity.this, "Vist_count"+visit_count, Toast.LENGTH_SHORT).show();
                upDateVisitorInfo(mVisitorId,visit_count);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Up Date By Add +1 visit_count to FireBase
    public void upDateVisitorInfo(String mVisitorId,int visit_count){
        mVisitorDatabaseRef.child(mVisitorId).child("visit_count").setValue(visit_count);
        setSowMessag("welcome back for"+ visit_count+"time"+mVisitorName);
        restPhoneNumberFragmentViews();

    }
    private void sendOtpToVisitor(String mPhoneNumbers) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumbers,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    public void startVistorUi(){
        Fragment newFragment = new VisitorPhoneNumber();
        loadFragment(newFragment);
    }
    public static MainActivity getInstance(){
        return mInstance;
    }
    public void loadFragment(Fragment newFragment){
        // Create new fragment and transaction
        if(newFragment instanceof VisitorPhoneNumber){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // and add the transaction to the back stack
            mVisitorPhoneNumberFragment= (VisitorPhoneNumber) newFragment;
            mVisitorPhoneNumberFragment.setOnFragmentInteractionListener(this);
            transaction.replace(R.id.baseFragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            subscreensOnTheStack++;
        }
       else if (newFragment instanceof VerifyOtp){
          VerifyOtp verifyOtp= (VerifyOtp) newFragment;
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          // and add the transaction to the back stack
          verifyOtp.setOnFragmentInteractionListener(this);
          transaction.replace(R.id.baseFragment, newFragment);
          transaction.addToBackStack(null);
          transaction.commit();
          subscreensOnTheStack++;
      }
      else if(newFragment instanceof VisitorImageCapture){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            VisitorImageCapture visitorImageCapture= (VisitorImageCapture) newFragment;
            visitorImageCapture.setOnFragmentInteractionListener(this);
            transaction.replace(R.id.baseFragment, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            subscreensOnTheStack++;

        }

    }
    //Upload VisitorInfo "visitor" under the unique key in   FireBase Store
    private void writeVisitorInfo(String mVisitorPhoneNumber, String mPhoteURL, String mVistorname,int visit_count) {
        ArrayList<VisitorInfo> mVisitorInfoList=new ArrayList<>();
        String mVisitorId = mVisitorDatabaseRef.push().getKey();
        VisitorInfo post = new VisitorInfo(visit_count,mVisitorPhoneNumber,mPhoteURL,mVistorname,mVisitorId);
        mVisitorInfoList.add(post);
        mVisitorDatabaseRef.child(mVisitorId).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               // Toast.makeText(MainActivity.this, "VisitorDataIsUpLoaded ", Toast.LENGTH_SHORT).show();
            }
        });
        writePhoneNumber(mVisitorPhoneNumber,mVisitorId);

    }
    //Upload VisitorID "Visitor_PhoneNumber"   PhoneNumber as a unique key  in   FireBase Store(Purpose to verify Phone Number is already exist or not)
    private void writePhoneNumber(String mVisitorPhonewNumber,String mVistorId){
       refPhoneData.child(mVisitorPhonewNumber).child("mVisitorId").setValue(mVistorId);
        restPhoneNumberFragmentViews();
    }
    //Upload Suspicious VisitorInfo to FireBase Store
    private void writeSuspiciousVisitorInfo(String mVisitorPhoneNumber, String mPhoteURL) {
      DatabaseReference  mDatabase=FirebaseDatabase.getInstance().getReference("suspicious_users");
      ArrayList<VisitorInfo> mVisitorInfoList=new ArrayList<>();
      String  mVisitorId = mDatabase.push().getKey();
      SuspiciousVisitorInfo post = new SuspiciousVisitorInfo(mVisitorPhoneNumber,mPhoteURL,mVisitorId);
      mDatabase.child(mVisitorId).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              // setSowMessag("your Response is Recorded");
            }
        });
      restPhoneNumberFragmentViews();

    }

    private void restPhoneNumberFragmentViews() {
        mVisitorPhoneNumberFragment.setClearViews();
    }

    //Upload Visitor Compressed image to FireBase Store
    public void upLoadImage(final boolean visitorIsVerified){
        final Uri file = Uri.fromFile(mCompressedImage);
        riversRef = mStorageRef.child("images/"+file.getLastPathSegment());
        mUploadTask = riversRef.putFile(file);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
              //  Toast.makeText(MainActivity.this, "uploadImageFailed"+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

               // Toast.makeText(MainActivity.this, "upLoadImageSuccess//", Toast.LENGTH_SHORT).show();


            }
        });
        final StorageReference ref = mStorageRef.child("images/"+file.getLastPathSegment());
        // Get Uri of uploaded Image
        Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mImageUri=task.getResult().toString();
                    if(mImageUri!=null){
                        //Toast.makeText(MainActivity.this, "getUri", Toast.LENGTH_SHORT).show();
                        if(visitorIsVerified){
                            writeVisitorInfo(mVisitorPhoneNumber,mImageUri,mVisitorName,1);
                            //getimageUri(file.getLastPathSegment());
                            mCompressedImage.delete();

                        }
                        else {
                            writeSuspiciousVisitorInfo(mVisitorPhoneNumber,mImageUri);
                            mCompressedImage.delete();
                        }
                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }
    //Compress Captured Image using RxJava in background
    public void compressImage(File mActualImage) {
        if (mActualImage == null) {
            //showError("Please choose an image!");
        } else {
            // Compress image using RxJava in background thread
            new Compressor(this)
                    .compressToFileAsFlowable(mActualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            mCompressedImage = file;
                            fileisCopressed=true;
                            // setCompressedImage();===========
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            //showError(throwable.getMessage());
                        }
                    });
        }
    }

   // Some Messaging function
    public void setSowMessag(String showMasseg){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, showMasseg, Snackbar.LENGTH_LONG);
        View snackView=snackbar.getView();
        snackView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Dark_color_1));
        snackbar.show();

    }
    public void setSowMessagWithActionn(String showMasseg,int actionId){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, showMasseg, Snackbar.LENGTH_LONG);
        snackbar.setAction(actionId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //do action
                sendOtpToVisitor(mVisitorPhoneNumber);
            }
        });
        View snackView=snackbar.getView();
        snackView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Dark_color_1));
        snackbar.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(MY_PERMISSIONS_REQUEST_READ_WRITE==requestCode){
            if( grantResults[0]==PackageManager.PERMISSION_GRANTED){
                startVistorUi();
            }
            else {
                Toast.makeText(this, "Restart App And give Permission for the Process", Toast.LENGTH_SHORT).show();
            }
        }
    }
   //Fragment CallBack Method
    @Override
    public void onFragmentInteraction(Fragment newFragment) {
        loadFragment(newFragment);
       // Toast.makeText(mInstance, "hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserInformation(String mCounterCode,String mPhoneNumbers, File userImage, String userName) {
        compressImage(userImage);
        this.mVisitorName=userName;
        this.mVisitorPhoneNumber=mCounterCode+mPhoneNumbers;
       verfiedPhone(mCounterCode+mPhoneNumbers);

    }

    @Override
    public void onSendMessegToVisitor(String showMessag) {
        setSowMessag(showMessag);
    }

    @Override
    public void onVerifyVerificationCode(String mVerificationCode) {
       // Toast.makeText(mInstance, "Verify Code", Toast.LENGTH_SHORT).show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mVerificationCode);
    }

    @Override
    public void onCheckPhoneNumber(String mCounterCode,String mPhoneNumber) {



    }



    @Override
    public void onBackpressFragment(File userImage) {
       // Toast.makeText(mInstance, "back", Toast.LENGTH_SHORT).show();
        mVisitorPhoneNumberFragment.userCapturedImage(userImage);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
     if(   subscreensOnTheStack==1){
         alrtDialog();

     }else {
         subscreensOnTheStack--;
         super.onBackPressed();
     }

    }
    public void alrtDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Do you Want To Exit")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
       builder.show();
    }

    private void setCheckSelfPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("We need read external storage permission to proceed")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                                        MY_PERMISSIONS_REQUEST_READ_WRITE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                 builder.create();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                        MY_PERMISSIONS_REQUEST_READ_WRITE);
            }
        }else {

            startVistorUi();
        }
    }
}
