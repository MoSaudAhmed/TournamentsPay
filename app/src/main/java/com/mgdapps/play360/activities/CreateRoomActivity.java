package com.mgdapps.play360.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgdapps.play360.R;
import com.mgdapps.play360.adapters.RulesAdapter;
import com.mgdapps.play360.adapters.SpinnerAdapter;
import com.mgdapps.play360.helper.CircularProgressBar;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.models.CreateMatchModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class CreateRoomActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatSpinner spinner_type, spinner_entryFee, spinner_mode, spinner_map, spinner_server, spinner_aa, spinner_passwordTime;
    AppCompatCheckBox cb_CreateMatch_termsAndPolicy, cb_CreateMatch_private;
    TextView tv_CreateMatchtermsConditions, tv_createMatch_Create, tv_createMatch_title, tv_CreateDate, tv_CreateTime;
    EditText et_createMatchTitle, et_createMatchPassword, et_createMatchRoomID;

    RecyclerView rv_rules;

    Toolbar toolbar;
    Calendar calendar;
    CircularProgressBar circularProgressBar;

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        initViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Room");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        circularProgressBar = new CircularProgressBar(this);

        spinner_type.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.typeItems))));
        spinner_mode.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.modeItems))));
        spinner_map.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.mapItems))));
        spinner_server.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.serverItems))));
        spinner_passwordTime.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.passwordTimeItems))));
        spinner_aa.setAdapter(new SpinnerAdapter(Arrays.asList(getResources().getStringArray(R.array.aaItems))));

        rv_rules.setLayoutManager(new LinearLayoutManager(this));
        rv_rules.setHasFixedSize(true);
        rv_rules.setAdapter(new RulesAdapter(Arrays.asList(getResources().getStringArray(R.array.rulesItems))));


        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 5);
        setDateAndTimeToText();
    }

    private void setDateAndTimeToText() {
        try {
            tv_CreateDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR));
            String hours = "";
            if (calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.HOUR_OF_DAY) != 00) {
                if (calendar.get(Calendar.HOUR_OF_DAY) > 12) {
                    hours = String.format("%02d", (calendar.get(Calendar.HOUR_OF_DAY) - 12));
                } else {
                    hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
                }

            } else {
                hours = "12";
            }
            String Mins = String.format("%02d", calendar.get(Calendar.MINUTE));
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
                tv_CreateTime.setText(hours + ":" + Mins + " PM");
            } else {
                tv_CreateTime.setText(hours + ":" + Mins + " AM");
            }

        } catch (Exception e) {
            Log.e("ErrorinsettingDateTime", e.getLocalizedMessage());
        }

    }

    private void initViews() {
        spinner_type = findViewById(R.id.spinner_type);
        /*        spinner_entryFee = findViewById(R.id.spinner_entryFee);*/
        spinner_mode = findViewById(R.id.spinner_mode);
        spinner_map = findViewById(R.id.spinner_map);
        spinner_server = findViewById(R.id.spinner_server);
        spinner_aa = findViewById(R.id.spinner_aa);
        spinner_passwordTime = findViewById(R.id.spinner_passwordTime);
        rv_rules = findViewById(R.id.rv_rules);
        tv_CreateDate = findViewById(R.id.tv_CreateDate);
        tv_CreateTime = findViewById(R.id.tv_CreateTime);
        et_createMatchPassword = findViewById(R.id.et_createMatchPassword);
        et_createMatchRoomID = findViewById(R.id.et_createMatchRoomID);

        toolbar = findViewById(R.id.toolbar);

        tv_createMatch_Create = findViewById(R.id.tv_createMatch_Create);
        tv_createMatch_title = findViewById(R.id.tv_createMatch_title);
        et_createMatchTitle = findViewById(R.id.et_createMatchTitle);

        cb_CreateMatch_termsAndPolicy = findViewById(R.id.cb_CreateMatch_termsAndPolicy);
        cb_CreateMatch_private = findViewById(R.id.cb_CreateMatch_private);
        tv_CreateMatchtermsConditions = findViewById(R.id.tv_CreateMatchtermsConditions);

        tv_CreateMatchtermsConditions.setOnClickListener(this);
        tv_createMatch_Create.setOnClickListener(this);
        tv_CreateDate.setOnClickListener(this);
        tv_CreateTime.setOnClickListener(this);

        et_createMatchTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_createMatch_title.setText(s);
            }
        });

        spinner_aa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spinner_map.setSelection(0);
                    spinner_type.setSelection(0);
                    spinner_mode.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_passwordTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    et_createMatchPassword.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    Calendar date;

    public void showDateTimePicker() {

        final Calendar currentDate = calendar;
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(CreateRoomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        date.set(Calendar.SECOND, 0);
                        calendar = date;
                        setDateAndTimeToText();
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_CreateMatchtermsConditions: {
                startActivity(new Intent(CreateRoomActivity.this, TermsActivity.class));
                break;
            }
            case R.id.tv_createMatch_Create: {
                if (spinner_aa.getSelectedItemPosition() != 0) {
                    if (!TextUtils.isEmpty(et_createMatchTitle.getText().toString().trim())) {
                        if (spinner_server.getSelectedItemPosition() != 0) {
                            if (spinner_mode.getSelectedItemPosition() != 0) {
                               /* if ((spinner_passwordTime.getSelectedItemPosition() != 1 && !TextUtils.isEmpty(et_createMatchPassword.getText().toString().trim())) ||
                                        (spinner_passwordTime.getSelectedItemPosition() == 1 && TextUtils.isEmpty(et_createMatchPassword.getText().toString().trim()))) {
                               */
                                if (spinner_passwordTime.getSelectedItemPosition() != 0) {
                                    // if (!TextUtils.isEmpty(et_createMatchRoomID.getText().toString().trim())) {
                                    if (cb_CreateMatch_termsAndPolicy.isChecked()) {
                                        createMatchService();
                                    } else {
                                        showToast("Please read and accept our terms");
                                    }
                                        /*} else {
                                            showToast("Please enter Room Id, It is shown with password");
                                        }*/
                                } else {
                                    showToast("Please select when password should be visible to players");
                                }
                                /*} else {
                                    showToast("Please enter room password");
                                }*/
                            } else {
                                showToast("Please select match mode");
                            }
                        } else {
                            showToast("Please select match server");
                        }
                    } else {
                        showToast("Please enter match title");
                    }
                } else {
                    if (!TextUtils.isEmpty(et_createMatchTitle.getText().toString().trim())) {
                        if (spinner_type.getSelectedItemPosition() != 0) {
                            if (spinner_mode.getSelectedItemPosition() != 0) {
                                if (spinner_map.getSelectedItemPosition() != 0) {
                                    if (spinner_server.getSelectedItemPosition() != 0) {
                                      /*  if ((spinner_passwordTime.getSelectedItemPosition() != 1 && !TextUtils.isEmpty(et_createMatchPassword.getText().toString().trim())) ||
                                                (spinner_passwordTime.getSelectedItemPosition() == 1 && TextUtils.isEmpty(et_createMatchPassword.getText().toString().trim()))) {
                                      */
                                        if (spinner_passwordTime.getSelectedItemPosition() != 0) {
                                            //   if (!TextUtils.isEmpty(et_createMatchRoomID.getText().toString().trim())) {
                                            if (cb_CreateMatch_termsAndPolicy.isChecked()) {
                                                createMatchService();
                                            } else {
                                                showToast("Please read and accept our terms");
                                            }
                                               /* } else {
                                                    showToast("Please enter Room Id, It is shown with password");
                                                }*/
                                        } else {
                                            showToast("Please select when password should be visible to players");
                                        }
                                        /*} else {
                                            showToast("Please enter room password");
                                        }*/
                                    } else {
                                        showToast("Please select match server");
                                    }
                                } else {
                                    showToast("Please select match map");
                                }
                            } else {
                                showToast("Please select match mode");
                            }
                        } else {
                            showToast("Please select match type");
                        }
                    } else {
                        showToast("Please enter match title");
                    }
                }
                break;
            }
            case R.id.tv_CreateDate: {
                showDateTimePicker();
                break;
            }
            case R.id.tv_CreateTime: {
                showDateTimePicker();
                break;
            }
        }
    }

    private void createMatchService() {

        final String id = firebaseFirestore.collection(Constants.Matches).document().getId();


        circularProgressBar.showProgressDialog();

        CreateMatchModel createMatchModel = new CreateMatchModel();
        createMatchModel.setMatchTitle(et_createMatchTitle.getText().toString());
        createMatchModel.setMatchType(spinner_type.getSelectedItemPosition());
        createMatchModel.setMatchMode(spinner_mode.getSelectedItemPosition());
        createMatchModel.setMatchMap(spinner_map.getSelectedItemPosition());
        createMatchModel.setMatchServer(spinner_server.getSelectedItemPosition());
        createMatchModel.setMatchAA(spinner_aa.getSelectedItemPosition());
        createMatchModel.setMatchShowPassword(spinner_passwordTime.getSelectedItemPosition());
        createMatchModel.setUID(firebaseUser.getUid());
        createMatchModel.setPassword(et_createMatchPassword.getText().toString());
        createMatchModel.setRoomId(et_createMatchRoomID.getText().toString());

        if (cb_CreateMatch_private.isChecked()) {
            createMatchModel.setMatchprivate(true);
        } else {
            createMatchModel.setMatchprivate(false);
        }
        createMatchModel.setMatchTime(new Date(calendar.getTimeInMillis()));

        createMatchModel.setMatchId(id);
        //Generate Random Code
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        createMatchModel.setJoinCode(number);

        firebaseFirestore.collection(Constants.Matches).document(id).set(createMatchModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                circularProgressBar.dismissProgressDialog();
                if (task.isSuccessful()) {
                    showToast("Room '" + et_createMatchTitle.getText().toString() + "' Created");
                    updateJoinedMatchToUser(id);
                } else {
                    showToast("Oops, There was an error in creating room, Please try again later");
                    finish();
                }
            }
        });
    }

    private void updateJoinedMatchToUser(String matchId) {
        final CircularProgressBar circularProgressBar = new CircularProgressBar(this);
        circularProgressBar.showProgressDialog();

        HashMap<String, Boolean> hashMap = new HashMap<>();
        hashMap.put(matchId, true);
        firebaseFirestore.collection(Constants.UserDB).document(firebaseUser.getUid())
                .collection(Constants.Joined).document(matchId).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                circularProgressBar.dismissProgressDialog();
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(CreateRoomActivity.this, message, Toast.LENGTH_LONG).show();
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
