package com.firebase.assignmentapp.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.assignmentapp.databinding.ActivitySigninBinding;
import com.firebase.assignmentapp.view.home.MyProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    ActivitySigninBinding activitySigninBinding;
    FirebaseAuth auth;  //FirebaseAuth Instance
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySigninBinding = ActivitySigninBinding.inflate(getLayoutInflater());

        setContentView(activitySigninBinding.mainLoginView);
        auth= FirebaseAuth.getInstance();
        activitySigninBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v);
            }
        });
        activitySigninBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp(v);
            }
        });
    }
    public void doSignUp(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
        activitySigninBinding.progress.setVisibility(View.GONE);
    }
    //Login button click
    public void doLogin(View view) {
        String Email = activitySigninBinding.textUserEmail.getText().toString();
        final String pass = activitySigninBinding.textUserPass.getText().toString();

        //Validation section
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        activitySigninBinding.progress.setVisibility(View.VISIBLE);
        if (pass.length() < 6) {
            activitySigninBinding.textUserPass.setError("Should be greater than 6");
        }
        auth.signInWithEmailAndPassword(Email, pass)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("MyTag", task.getException().toString());

                        } else {
                            Intent intent = new Intent(SignInActivity.this, MyProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

}