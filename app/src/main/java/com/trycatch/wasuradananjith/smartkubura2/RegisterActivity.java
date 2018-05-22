package com.trycatch.wasuradananjith.smartkubura2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trycatch.wasuradananjith.smartkubura2.Model.User;

import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity {

    TextView backToLogin;
    EditText etName,etPhone,etEmail,etPassword,etConfirmPassword;
    Button btnRegister;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText)findViewById(R.id.edtNewUserName);
        etPhone = (EditText)findViewById(R.id.edtTelephone);
        etEmail = (EditText)findViewById(R.id.edtNewEmail);
        etPassword = (EditText)findViewById(R.id.edtNewPassword);
        etConfirmPassword = (EditText)findViewById(R.id.edtConfirmPassword);
        btnRegister = (Button)findViewById(R.id.btn_sign_up);
        backToLogin = (TextView)findViewById(R.id.link_login);

        // get the database reference "users" in firebase realtime database
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // when the back to login button is clicked
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        // when the submit/register button is clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                // if the any of the required text fields are empty
                if (name.isEmpty() &&
                        phone.isEmpty() &&
                        password.isEmpty() &&
                        confirmPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "සියලු ක්ෂේත්ර පුරවන්න",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    // checks whether password matches with the re-entered password
                    if(password.equals(confirmPassword)){

                        // create a new instance of User class inorder to add to the database
                        final User user = new User(name,phone,email,password);

                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(user.getPhone()).exists()){
                                    Toast.makeText(RegisterActivity.this,"ඔබ මීට පෙර ලියාපදිංචි වී ඇත!",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    mDatabase.child(user.getPhone()).setValue(user);

                                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                                            R.style.AppTheme_Dark_Dialog);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("ගිණුම සැකසෙමින් පවතී ...");
                                    progressDialog.show();

                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    Toast.makeText(RegisterActivity.this,"ලියාපදිංචි වීම සාර්ථකයි!",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                                    progressDialog.dismiss();
                                                }
                                            }, 3000);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                "මුරපදය තහවුරු කිරීමට නැවත යෙදූ මුරපදය නිවැරදි බවට වග බලා ගන්න.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
