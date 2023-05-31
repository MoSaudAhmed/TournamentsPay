package com.mgdapps.play360.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgdapps.play360.R;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.models.RegisterModel;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Register;

    EditText et_registerFirstName, et_registerLastName, et_registerEmail, et_registerPassword,
            et_registerConfirmPassword;

    TextInputLayout textInput_firstName, textInput_lastName, textInput_email, textInput_Password, textInput_confirmPassword;

    CircularProgressBar circularProgressBar;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initVews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
//        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void initVews() {

        toolbar = findViewById(R.id.toolbar);
        btn_Register = findViewById(R.id.btn_Register);
        et_registerFirstName = findViewById(R.id.et_registerFirstName);
        et_registerLastName = findViewById(R.id.et_registerLastName);
        et_registerEmail = findViewById(R.id.et_registerEmail);
        et_registerPassword = findViewById(R.id.et_registerPassword);
        et_registerConfirmPassword = findViewById(R.id.et_registerConfirmPassword);
        textInput_firstName = findViewById(R.id.textInput_firstName);
        textInput_lastName = findViewById(R.id.textInput_lastName);
        textInput_email = findViewById(R.id.textInput_email);
        textInput_Password = findViewById(R.id.textInput_password);
        textInput_confirmPassword = findViewById(R.id.textInput_confirmPassword);

        circularProgressBar = new CircularProgressBar(this);

        btn_Register.setOnClickListener(this);

        et_registerFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_firstName.setError(null);
                textInput_firstName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_registerLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_lastName.setError(null);
                textInput_lastName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_registerEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_email.setError(null);
                textInput_email.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_registerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_Password.setError(null);
                textInput_Password.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_registerConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_confirmPassword.setError(null);
                textInput_confirmPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Register: {

                validateAndLogin();

                break;
            }
        }
    }

    private void validateAndLogin() {

        if (TextUtils.isEmpty(et_registerFirstName.getText().toString())) {
            textInput_firstName.setErrorEnabled(true);
            textInput_firstName.setError("Enter First Name");
        } else if (TextUtils.isEmpty(et_registerLastName.getText().toString())) {
            textInput_lastName.setErrorEnabled(true);
            textInput_lastName.setError("Enter Last Name");
        } else if (TextUtils.isEmpty(et_registerEmail.getText().toString())) {
            textInput_email.setErrorEnabled(true);
            textInput_email.setError("Enter email");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_registerEmail.getText().toString()).matches()) {
            textInput_email.setErrorEnabled(true);
            textInput_email.setError("Invalid email Address");
        } else if (TextUtils.isEmpty(et_registerPassword.getText().toString())) {
            textInput_Password.setErrorEnabled(true);
            textInput_Password.setError("Enter password");
        } else if (TextUtils.isEmpty(et_registerConfirmPassword.getText().toString())) {
            textInput_confirmPassword.setErrorEnabled(true);
            textInput_confirmPassword.setError("Enter confirm password");
        } else if (!et_registerPassword.getText().toString().equals(et_registerConfirmPassword.getText().toString())) {
            et_registerPassword.setText("");
            et_registerConfirmPassword.setText("");
            Toast.makeText(RegistrationActivity.this, "Password and Confirm Password did not match", Toast.LENGTH_LONG).show();
        } else {
            saveUser();
        }
    }

    private void saveUser() {
        circularProgressBar.showProgressDialog();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                et_registerEmail.getText().toString(),
                et_registerPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        RegisterModel registerModel = new RegisterModel();
                        registerModel.setDisplayName(et_registerFirstName.getText().toString() + " " + et_registerLastName.getText().toString());
                        registerModel.setEmail(firebaseUser.getEmail());
                        registerModel.setUid(firebaseUser.getUid());

                        FirebaseFirestore.getInstance().collection(Constants.UserDB)
                                .document(registerModel.getUid())
                                .set(registerModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        circularProgressBar.dismissProgressDialog();
                                        if (task.isSuccessful()) {
                                           /* FirebaseAuth.getInstance().signOut();
                                            setResult(Activity.RESULT_OK);
                                            finish();*/
                                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Something went Wrong, Please try again later", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                    } else {
                        circularProgressBar.dismissProgressDialog();
                        Toast.makeText(RegistrationActivity.this, "User Already exists, Please Sign In", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    circularProgressBar.dismissProgressDialog();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
