package com.example.codetillamgone.takenote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputLayout mEmaill;
    private TextInputLayout mPassword;

    private ProgressDialog mLoginProgressDialog;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        initializeWidgets(); //Helper Method to Initialize widgets and the Toolbar
        mLoginProgressDialog = new ProgressDialog(this);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmaill.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

                    Snackbar.make(findViewById(R.id.login_layout),getString(R.string.empty_field), Snackbar.LENGTH_SHORT).show();

                }
                else if( isOnline()){
                    mLoginProgressDialog.setTitle("Logging in");
                    mLoginProgressDialog.setMessage("Please wait while we check your account details");
                    mLoginProgressDialog.setCanceledOnTouchOutside(false);
                    mLoginProgressDialog.show();
                    signInUser(email, password);
                }



            }
        });


    }

    private void signInUser(String userEmail, String userPassword) {

        mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "signInWithEmail : success");
                    mLoginProgressDialog.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();

                }
                else{
                    try {
                       throw task.getException();
                    } catch( FirebaseAuthInvalidUserException e){
                        mEmaill.setError(getString(R.string.invalid_user));
                        mEmaill.requestFocus();
                    }
                    catch (Exception e){
                       Log.e(LOG_TAG, e.getMessage());
                    }
                    mLoginProgressDialog.hide();

                }
            }
        });
    }

     //Helper Method to Check Network Connectivity
        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.start_menu, menu);

        return true;
    }

    private void initializeWidgets() {

        mEmaill = findViewById(R.id.login_email_et);
        mPassword = findViewById(R.id.login_password_et);

        mLoginBtn = findViewById(R.id.login_btn);
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
