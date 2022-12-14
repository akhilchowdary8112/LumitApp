package com.example.lumit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity12<PhoneAuthOptions> extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText edtPhone, edtOTP;
    private Button verifyOTPBtn, generateOTPBtn;
    private String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);
        mAuth = FirebaseAuth.getInstance();
        //initializing variables for button and Edittext.
        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        edtOTP = findViewById(R.id.idEdtOtp);
        verifyOTPBtn = findViewById(R.id.idBtnVerify);
        generateOTPBtn = findViewById(R.id.idBtnGetOtp);

        //setting onclick listner for generate OTP button.
        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //below line is for checking weather the user has entered his mobile number or not.
                if (TextUtils.isEmpty(edtPhone.getText().toString()) ){
                    //when mobile number text field is empty displaying a toast message.
                    Toast.makeText(MainActivity12.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();

                }else{
                    //if the text field is not empty we are calling our send OTP method for gettig OTP from Firebase.
                    String phone ="+91"+edtPhone.getText().toString();
                    sendVerificationCode(phone);
                }
            }
        });
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(edtOTP.getText().toString())){
                    //if the OTP text field is empty display a message to user to enter OTP
                    Toast.makeText(MainActivity12.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                }else{
                    //if OTP field is not empty calling method to verify the OTP.
                    verifyCode(edtOTP.getText().toString());
                }

            }
        });

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        //inside this method we are checking if the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if the code is correct and the task is succesful we are sending our user to new activity.

                            Intent i =new Intent(MainActivity12.this,MainActivity14.class);
                            startActivity(i);
                            finish();

                        } else {
                            //if the code is not correct then we are displaying an error message to the user.
                            Toast.makeText(MainActivity12.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        //this method is used for getting OTP on user phone number.

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,//first parameter is user's mobile number
                60,//second parameter is time limit for OTP verification which is 60 seconds in our case.
                TimeUnit.SECONDS,// third parameter is for initializing units for time period which is in seconds in our case.
                TaskExecutors.MAIN_THREAD,//this task will be excuted on Main thread.
                mCallBack//we are calling callback method when we recieve OTP for auto verification of user.
        );

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //below method is used when OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //when we recieve the OTP it contains a unique id wich we are storing in our string which we have already created.
            verificationId = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //below line is used for getting OTP code which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();
            //checking if the code is null or not.
            if (code != null) {
                //if the code is not null then we are setting that code to our OTP edittext field.
                edtOTP.setText(code);
                //after setting this code to OTP edittext field we are calling our verifycode method.
                verifyCode(code);

            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //displaying error message with firebase exception.
            Toast.makeText(MainActivity12.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
    private void verifyCode(String code) {
        //below line is used for getting getting credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        //after getting credential we are calling sign in method.
        signInWithCredential(credential);
    }
}
