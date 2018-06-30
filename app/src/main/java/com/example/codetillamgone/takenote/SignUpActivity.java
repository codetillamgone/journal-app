package com.example.codetillamgone.takenote;

import android.app.LoaderManager;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity{
    private TextInputLayout mEmail, mPassword, mConfirmPassword;
    private Button mSignUpBtn;
    private FirebaseAuth mAuth;

    private final String LOG_TAG = this.getClass().getSimpleName();
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressDialog mRegProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeWidgets(); //Helper Method to initialize all the needed widgets in the layout including Toolbar
        mAuth = FirebaseAuth.getInstance();
        mRegProgressDialog = new ProgressDialog(this);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = mEmail.getEditText().getText().toString();
                String userPassword = mPassword.getEditText().getText().toString();
                String confirmUserPassword = mConfirmPassword.getEditText().getText().toString();
                if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)  || TextUtils.isEmpty(confirmUserPassword)){
                    Snackbar.make(findViewById(R.id.signup_layout),getString(R.string.empty_field), Snackbar.LENGTH_SHORT ).show();


                }
                else if(!isOnline()){

                    Snackbar.make(findViewById(R.id.signup_layout), getString(R.string.check_network_connection), Snackbar.LENGTH_SHORT).show();

                }
                else if( !userPassword.contentEquals(confirmUserPassword)){
                    mConfirmPassword.setError("Passwords don't match");
                    mConfirmPassword.requestFocus();
                }
                else if(isOnline()){

                    mRegProgressDialog.setTitle("Registering User");
                    mRegProgressDialog.setMessage("Please wait while we create your account");
                    mRegProgressDialog.setCanceledOnTouchOutside(false);
                    mRegProgressDialog.show();
                    registerUser(userEmail, userPassword); // Helper method that contains the Firebase Sign Up with Email and Password Method

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


    private void registerUser(String email, String password) {

    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            if(task.isSuccessful()){

                Log.d(LOG_TAG, "createUserWithEmail : Success");
                mRegProgressDialog.dismiss();
                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();

            }
            else {
                try{
                    throw task.getException();
                } catch  (FirebaseAuthWeakPasswordException e){
                   mPassword.setError(getString(R.string.weak_password));
                   mPassword.requestFocus();
                }
                catch( FirebaseAuthInvalidCredentialsException e){
                    mEmail.setError(getString(R.string.invalid_email));
                    mEmail.requestFocus();

                }
                catch (FirebaseAuthUserCollisionException e){
                    mEmail.setError(getString(R.string.error_user_exist));
                }
                catch (Exception e){
                    Log.e(LOG_TAG, e.getMessage());
                }
                mRegProgressDialog.hide();
                Toast.makeText(SignUpActivity.this, "You got some Error", Toast.LENGTH_SHORT).show();
            }
        }
    });

    }

    private void initializeWidgets() {
        mEmail = findViewById(R.id.sign_up_email_et);
        mPassword = findViewById(R.id.sign_up_pass_et);
        mConfirmPassword = findViewById(R.id.sign_up_confirm_pass_et);

        mSignUpBtn = findViewById(R.id.sign_up_btn);
        mToolbar = findViewById(R.id.sign_up_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.create_account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
}
