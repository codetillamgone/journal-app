package com.example.codetillamgone.takenote;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private TextInputLayout mTitle, mNotes;
    private DatabaseReference mUserDatabase, newNote;
    private Toolbar mToolbar;
    private String currentUid;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initializeWidgets(); //Helper Method to initialize the widgets and toolbar;
        if(currentUser != null){


            currentUid = currentUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User_Notes").child(currentUid);
        }








    }

    private void initializeWidgets() {

        mTitle = findViewById(R.id.post_title);
        mNotes = findViewById(R.id.post_notes);
        mToolbar = findViewById(R.id.post_app_bar);
        mProgressBar = findViewById(R.id.post_prog_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if( currentUser == null ){
            sendToSignUp();
        }

    }

    private void sendToSignUp() {

        Intent signUpIntent = new Intent(PostActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_save){
            mProgressBar.setVisibility(View.VISIBLE);
            startPosting();
        }
        else if( itemId == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return true;
    }

    private void startPosting() {

        String title = mTitle.getEditText().getText().toString().trim();
        String notes = mNotes.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(title) && TextUtils.isEmpty(notes)){
            Snackbar.make(findViewById(R.id.post_layout), getString(R.string.empty_field), Snackbar.LENGTH_SHORT).show();
        }
        else {
            String currentDate = DateFormat.getDateInstance().format(new Date());
            if( currentUser != null){

                newNote = mUserDatabase.push();

            }
            HashMap noteMap = new HashMap<>();
            noteMap.put("title", title);
            noteMap.put("note", notes);
            noteMap.put("date", currentDate);



            newNote.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Snackbar.make(findViewById(R.id.post_layout), getString(R.string.note_uploaded), Snackbar.LENGTH_SHORT).show();

                    }
                    else {
                        Snackbar.make(findViewById(R.id.post_layout), getString(R.string.note_received), Snackbar.LENGTH_SHORT).show();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
            });




        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);

        return true;
    }
}
