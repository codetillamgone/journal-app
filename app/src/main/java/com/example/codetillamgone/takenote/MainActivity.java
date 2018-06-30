package com.example.codetillamgone.takenote;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private android.support.v7.widget.Toolbar mToolbar;
    private RecyclerView mJournalList;
    private JournalAdapter mJournalAdapter;
    private FloatingActionButton mAddNote;
    private DatabaseReference mDatabase;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initializeWidgets(); //Helper Method to Initialize Widgets including Toolbar
        mJournalList.setLayoutManager(new LinearLayoutManager(this));
        mJournalList.setHasFixedSize(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User_Notes");
        mDatabase.keepSynced(true);
        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
        });




    }

    private void initializeWidgets() {
        mToolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TakeNote");
        mJournalList = findViewById(R.id.recyclerview_journal);
        mAddNote = findViewById(R.id.main_add_note_ftbtn);



    }



    private void sendToStart() {

        Intent signUpIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(signUpIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int itemId = item.getItemId();
        if(itemId == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToStart();
        }
        mJournalAdapter = new JournalAdapter(Journal.class,R.layout.journal_row, JournalAdapter.JournalAdapterViewHolder.class, mDatabase);
        mJournalList.setAdapter(mJournalAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
