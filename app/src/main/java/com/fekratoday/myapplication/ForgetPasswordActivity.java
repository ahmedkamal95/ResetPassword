package com.fekratoday.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.Group;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private static final String EMAIL = "ahmed95kamal@gmail.com";
    private static final String PIN = "1234";
    private static final long START_TIME_IN_MILLIS = 30000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean mIsTimerRunning;
    private long mEndTime;

    private TextInputEditText mEdtEmail, mEdtPassword, mEdtConfirmPassword;
    private Button mBtnNext, mBtnResendEmail, mBtnDone;
    private Pinview mPinview;
    private TextView mTxtTimer;
    private Group mGroupEmail, mGroupPinview, mGroupPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initView();
        addListener();

    }

    private void initView() {
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        mEdtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        mBtnNext = findViewById(R.id.btnNext);
        mBtnResendEmail = findViewById(R.id.btnResendEmail);
        mBtnDone = findViewById(R.id.btnDone);
        mPinview = findViewById(R.id.pinview);
        mTxtTimer = findViewById(R.id.txtTimer);
        mGroupEmail = findViewById(R.id.groupEmail);
        mGroupPinview = findViewById(R.id.groupPinview);
        mGroupPassword = findViewById(R.id.groupPassword);
    }

    private void addListener() {
        mBtnNext.setOnClickListener(view -> {
            String mEmail = Objects.requireNonNull(mEdtEmail.getText()).toString().trim();
            if (mEmail.equals("")) {
                mEdtEmail.setError("Please enter your Email");
            } else if (!validateRegex(VALID_EMAIL_ADDRESS_REGEX, mEmail)) {
                mEdtEmail.setError("Please enter correct Email");
            } else if (mEmail.equals(EMAIL)) {
                mEdtEmail.setText("");
                hideGroup(mGroupEmail);
                showGroup(mGroupPinview);
                startTimer();
                Toast.makeText(this, "Pin was sent to your Email", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            }
        });

        mPinview.setPinViewEventListener((pinview, fromUser) -> {
            if (pinview.getValue().equals(PIN)) {
                hideGroup(mGroupPinview);
                showGroup(mGroupPassword);
            } else {
                Toast.makeText(this, "Wrong pin", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnResendEmail.setOnClickListener(view -> {
            Toast.makeText(this, "Pin was sent to your Email", Toast.LENGTH_SHORT).show();
            resetTimer();
            startTimer();
            mBtnResendEmail.setEnabled(false);
            mBtnResendEmail.setTextColor(getResources().getColor(R.color.dark_gray));
        });

        mBtnDone.setOnClickListener(view -> {
            String mPassword = Objects.requireNonNull(mEdtPassword.getText()).toString().trim();
            String mConfirmPassword = Objects.requireNonNull(mEdtConfirmPassword.getText()).toString().trim();

            if (mPassword.equals("")) {
                mEdtPassword.setError("Please enter password");
            } else if (mPassword.length() < 6) {
                mEdtPassword.setError("Password must be more than 6 character");
            } else if (mConfirmPassword.equals("")) {
                mEdtConfirmPassword.setError("Please enter confirm password");
            } else if (!mPassword.equals(mConfirmPassword)) {
                mEdtConfirmPassword.setError("Password and confirm password not same");
            } else {
                Toast.makeText(this, "Password change successful", Toast.LENGTH_SHORT).show();
                hideGroup(mGroupPassword);
                showGroup(mGroupEmail);
            }
        });

    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mIsTimerRunning = false;
                mBtnResendEmail.setEnabled(true);
                mBtnResendEmail.setTextColor(getResources().getColor(R.color.blue));
            }
        }.start();

        mIsTimerRunning = true;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTxtTimer.setText(timeLeftFormatted);
    }

    private void showGroup(Group group) {
        if (group.getVisibility() == View.GONE) {
            group.setVisibility(View.VISIBLE);
        }
    }

    private void hideGroup(Group group) {
        if (group.getVisibility() == View.VISIBLE) {
            group.setVisibility(View.GONE);
        }
    }

    public boolean validateRegex(String regex, String regexString) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(regexString);
        return matcher.find();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("groupEmailStatus", mGroupEmail.getVisibility());
        outState.putInt("groupPinviewStatus", mGroupPinview.getVisibility());
        outState.putInt("groupPasswordStatus", mGroupPassword.getVisibility());
        outState.putBoolean("btnResendEmailStatus", mBtnResendEmail.isEnabled());
        outState.putInt("btnResendEmailColor", mBtnResendEmail.getCurrentTextColor());

        outState.putLong("millisLeft", mTimeLeftInMillis);
        outState.putBoolean("timerRunning", mIsTimerRunning);
        outState.putLong("endTime", mEndTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGroupEmail.setVisibility(savedInstanceState.getInt("groupEmailStatus"));
        mGroupPinview.setVisibility(savedInstanceState.getInt("groupPinviewStatus"));
        mGroupPassword.setVisibility(savedInstanceState.getInt("groupPasswordStatus"));
        mBtnResendEmail.setEnabled(savedInstanceState.getBoolean("btnResendEmailStatus"));
        mBtnResendEmail.setTextColor(savedInstanceState.getInt("btnResendEmailColor"));


        mTimeLeftInMillis = savedInstanceState.getLong("millisLeft");
        mIsTimerRunning = savedInstanceState.getBoolean("timerRunning");
        updateCountDownText();

        if (mIsTimerRunning) {
            mEndTime = savedInstanceState.getLong("endTime");
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            startTimer();
        }
    }
}
