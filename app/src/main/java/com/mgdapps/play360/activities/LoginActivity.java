package com.mgdapps.play360.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgdapps.play360.R;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.helper.Preferences;
import com.mgdapps.play360.models.RegisterModel;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "LoginPage";
    CallbackManager mCallbackManager;
    LoginButton btn_login_fb;

    SignInButton btn_signin;
    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 546;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    Button btn_facebookCircle, btn_googleCircle;

    AppCompatCheckBox cb_termsAndPolicy;

    EditText et_loginEmail, et_loginPassword;
    TextInputLayout textInput_loginEmail, textInput_loginPassword;

    TextView tvTermsAndPolicy, tv_forgotPassword;

    Button btn_GoToRegister, btn_normalLogin;

    int registrationRequestCode = 111;

    CircularProgressBar circularProgressBar;

    FirebaseAnalytics mFirebaseAnalytics = null;

    Preferences preferences;

    String gotoMatch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gotoMatch = getIntent().getStringExtra(Constants.MatchId);

        mCallbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Failed Connecting, Please Try again Later", Toast.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        preferences = new Preferences();
        preferences.loadPreferences(this);

        initViews();
        termsAndPrivacySpannableString();

        circularProgressBar = new CircularProgressBar(LoginActivity.this);

        btn_login_fb.setReadPermissions(Arrays.asList("email"));
        btn_login_fb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

/*        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_termsAndPolicy.isChecked()) {
                    signIn();
                } else {
                    cb_termsAndPolicy.setError("Please check our Terms");
                    Toast.makeText(LoginActivity.this, "Please check our terms to continue", Toast.LENGTH_LONG).show();
                }

            }
        });*/

        circularProgressBar = new CircularProgressBar(LoginActivity.this);

        cb_termsAndPolicy.setOnCheckedChangeListener(this);

    }

    private void initViews() {
        btn_facebookCircle = findViewById(R.id.btn_facebookCircle);
        btn_login_fb = findViewById(R.id.btn_fb_login);

        btn_googleCircle = findViewById(R.id.btn_googleCircle);
        btn_signin = findViewById(R.id.btn_signin);

        cb_termsAndPolicy = findViewById(R.id.cb_termsAndPolicy);
        tvTermsAndPolicy = findViewById(R.id.tvTermsAndPolicy);
        btn_GoToRegister = findViewById(R.id.btn_GoToRegister);
        btn_normalLogin = findViewById(R.id.btn_normalLogin);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        et_loginEmail = findViewById(R.id.et_loginemail);
        et_loginPassword = findViewById(R.id.et_loginPassword);
        textInput_loginEmail = findViewById(R.id.textInput_loginEmail);
        textInput_loginPassword = findViewById(R.id.textInput_loginPassword);

        btn_GoToRegister.setOnClickListener(this);
        btn_facebookCircle.setOnClickListener(this);
        btn_normalLogin.setOnClickListener(this);
        tv_forgotPassword.setOnClickListener(this);
        btn_googleCircle.setOnClickListener(this);

        et_loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_loginEmail.setError(null);
                textInput_loginEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_loginPassword.setError(null);
                textInput_loginPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        circularProgressBar.showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseFirestore.getInstance().collection(Constants.UserDB).document(user.getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                                user = mAuth.getCurrentUser();
                                                updateUI(user);
                                                circularProgressBar.dismissProgressDialog();
                                            } else {
                                                RegisterModel registerModel = new RegisterModel();
                                                if (user.getDisplayName() != null) {
                                                    registerModel.setDisplayName(user.getDisplayName());
                                                } else {
                                                    registerModel.setDisplayName("Anonymous");
                                                }
                                                registerModel.setEmail(user.getEmail());
                                                registerModel.setUid(user.getUid());
                                                if (user.getPhotoUrl() != null) {
                                                    registerModel.setPhotoURL(user.getPhotoUrl().toString());
                                                }

                                                FirebaseFirestore.getInstance().collection(Constants.UserDB)
                                                        .document(registerModel.getUid())
                                                        .set(registerModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        circularProgressBar.dismissProgressDialog();
                                                        if (task.isSuccessful()) {
                                                            updateUI(user);
                                                        } else {
                                                            Toast.makeText(LoginActivity.this, "Something went wrong, Please try another method of signIn", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    circularProgressBar.dismissProgressDialog();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            LoginManager.getInstance().logOut();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                            alertDialog.setCancelable(false);
                            alertDialog.setTitle("Authentication failed");
                            alertDialog.setMessage(task.getException().getLocalizedMessage());
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    circularProgressBar.dismissProgressDialog();
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                        }
                    }
                });
    }

    Snackbar bar;

    CircularProgressBar progressDialog;

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        Snackbar.make(findViewById(R.id.main_layout), "Signing In", Snackbar.LENGTH_LONG).show();

        bar = Snackbar.make(findViewById(R.id.main_layout), "Loading", Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(LoginActivity.this);
        contentLay.addView(item, 0);
        bar.show();

        if (progressDialog != null) {
            progressDialog = new CircularProgressBar(LoginActivity.this);
        }
    }


    private void updateUI(FirebaseUser user) {

        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(Constants.MatchId, gotoMatch);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == registrationRequestCode) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(LoginActivity.this, "Registration Successful, Please Login", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = result.getSignInAccount();

                user = mAuth.getCurrentUser();

                Log.e("ResultCalled", "True");
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Log.e("ResultCalled", "Failed " + result.getStatus().getStatusMessage());
                bar.dismiss();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            user = mAuth.getCurrentUser();

                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                                RegisterModel registerModel = new RegisterModel();
                                if (user.getDisplayName() != null) {
                                    registerModel.setDisplayName(user.getDisplayName());
                                }
                                registerModel.setEmail(user.getEmail());
                                registerModel.setUid(user.getUid());
                                if (user.getPhotoUrl() != null) {
                                    registerModel.setPhotoURL(user.getPhotoUrl().toString());
                                }
                                FirebaseFirestore.getInstance().collection(Constants.UserDB)
                                        .document(registerModel.getUid())
                                        .set(registerModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                circularProgressBar.dismissProgressDialog();
                                                if (task.isSuccessful()) {
                                                    updateUI(user);
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Something went Wrong in saving user", Toast.LENGTH_LONG).show();
                                                    updateUI(user);
                                                }
                                            }
                                        });

                            } else {
                                circularProgressBar.dismissProgressDialog();
                                updateUI(user);
                            }

                        } else {
                            // If sign in fails, display a message to the user.

                            circularProgressBar.dismissProgressDialog();

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        bar.dismiss();
                        // ...
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismissProgressDialog();
        circularProgressBar.dismissProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_GoToRegister: {
                startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), registrationRequestCode);
                break;
            }
            case R.id.btn_facebookCircle: {
                if (cb_termsAndPolicy.isChecked()) {
                    btn_login_fb.performClick();
                } else {
                    cb_termsAndPolicy.setError("Please check our Terms");
                    Toast.makeText(LoginActivity.this, "Please check our terms to continue", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.tv_forgotPassword: {
                if (!TextUtils.isEmpty(et_loginEmail.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(et_loginEmail.getText()).matches()) {
                    showForgotPasswordDialog();
                } else {
                    textInput_loginEmail.setErrorEnabled(true);
                    textInput_loginEmail.setError("Please enter a valid email Address");
                }
                break;
            }
            case R.id.btn_normalLogin: {

                if (TextUtils.isEmpty(et_loginEmail.getText().toString())) {
                    textInput_loginEmail.setErrorEnabled(true);
                    textInput_loginEmail.setError("Email address can't be empty");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_loginEmail.getText()).matches()) {
                    textInput_loginEmail.setErrorEnabled(true);
                    textInput_loginEmail.setError("Invalid email address");
                } else if (TextUtils.isEmpty(et_loginPassword.getText().toString())) {
                    textInput_loginPassword.setErrorEnabled(true);
                    textInput_loginPassword.setError("Please enter a password");
                } else {
                    if (cb_termsAndPolicy.isChecked()) {
                        normalSignIn();
                    } else {
                        cb_termsAndPolicy.setError("Please check our Terms");
                        Toast.makeText(LoginActivity.this, "Please check our terms to continue", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case R.id.btn_googleCircle: {
                if (cb_termsAndPolicy.isChecked()) {
                    signIn();
                } else {
                    cb_termsAndPolicy.setError("Please check our Terms");
                    Toast.makeText(LoginActivity.this, "Please check our terms to continue", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void normalSignIn() {
        circularProgressBar.showProgressDialog();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(et_loginEmail.getText().toString(), et_loginPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI(mAuth.getCurrentUser());
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        circularProgressBar.dismissProgressDialog();
                    }
                });
    }

    private void showForgotPasswordDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Reset Password");
        alertDialog.setMessage("A mail will be sent to " + et_loginEmail.getText().toString() + ". Once sent, Open up the mail and click on the link to reset your password.");
        alertDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                circularProgressBar.showProgressDialog();

                mAuth.sendPasswordResetEmail(et_loginEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Sent Successful... Please check your email", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                        circularProgressBar.dismissProgressDialog();
                    }
                });
            }
        });
        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    //Terms and conditions click and color to text
    private void termsAndPrivacySpannableString() {

        SpannableString spannableString = new SpannableString(getString(R.string.terms_and_conditions_string));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(LoginActivity.this, TermsActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.colorAccent));
            }
        };
        spannableString.setSpan(clickableSpan, 13, getString(R.string.terms_and_conditions_string).length(), 0);
        tvTermsAndPolicy.setText(spannableString, TextView.BufferType.SPANNABLE);
        tvTermsAndPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //to remove error in Terms Checkbox listner
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        cb_termsAndPolicy.setError(null);
    }

}
