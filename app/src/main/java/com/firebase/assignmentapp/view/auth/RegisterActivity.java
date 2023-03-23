package com.firebase.assignmentapp.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.assignmentapp.data.UserData;
import com.firebase.assignmentapp.databinding.ActivityRegisterBinding;
import com.firebase.assignmentapp.view.home.MyProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding registerBinding;
    FirebaseAuth auth;
    UserData userData;
    Bitmap photo;//FirebaseAuth Instance
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        auth=FirebaseAuth.getInstance();
        setContentView(registerBinding.mainProfileView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserInfo");
        userData = new UserData();
        registerBinding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignupClick(v);
            }
        });
        registerBinding.textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onsigninClicl(v);
            }
        });

        registerBinding.selectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });
    }
    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(RegisterActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerBinding.loginProgress.setVisibility(View.GONE);
    }
    // Register button click
    public void onSignupClick(View view) {

        //Fetching data
        String emailInput = registerBinding.edtUserEmail.getText().toString();
        String password = registerBinding.edtPassword.getText().toString().trim();
        String confirmPwd = registerBinding.edtConfirmPassword.getText().toString().trim();
        String userName = registerBinding.edtUName.getText().toString().trim();
        String userBio = registerBinding.edtUserBio.getText().toString().trim();

        //Validation check
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPwd)) {
            Toast.makeText(getApplicationContext(), "Enter password confirm password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userBio)) {
            Toast.makeText(getApplicationContext(), "Enter password userbio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        registerBinding.loginProgress.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(emailInput, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        registerBinding.loginProgress.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            doAddFirebaseData(userName, emailInput, userBio,"test");
                            startActivity(new Intent(RegisterActivity.this, MyProfileActivity.class));
                            // finish();
                        }
                    }
                });
    }
    //Login button click
    public void onsigninClicl(View view) {
        startActivity(new Intent(this, MyProfileActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RegisterActivity.this.RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            registerBinding.imgProfile.setImageBitmap(photo);
            fetchFirebaseDataStorage();
        }
    }
    public void fetchFirebaseDataStorage(){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] b = stream.toByteArray();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("documentImages").child("noplateImg");
        storageReference.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                Log.e("Snapshot", String.valueOf(downloadUrl));
                Toast.makeText(RegisterActivity.this, "uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"failed",Toast.LENGTH_LONG).show();


            }
        });

    }

    private void doAddFirebaseData(String name, String email, String bio, String image) {
        userData.setUserName(name);
        userData.setEmail(email);
        userData.setUserBio(bio);
        userData.setImage(image);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}