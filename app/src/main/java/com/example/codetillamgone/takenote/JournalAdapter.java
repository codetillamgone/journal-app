package com.example.codetillamgone.takenote;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

public class JournalAdapter extends FirebaseRecyclerAdapter<Journal, JournalAdapter.JournalAdapterViewHolder> {


    public JournalAdapter(Class<Journal> modelClass, int modelLayout, Class<JournalAdapterViewHolder> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(JournalAdapterViewHolder viewHolder, Journal model, int position) {

        viewHolder.setTitle(model.getTitle());
        viewHolder.setNote(model.getNote());
        viewHolder.setDate(model.getDate());

    }

    public static class JournalAdapterViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public JournalAdapterViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            TextView post_title = mView.findViewById(R.id.journal_row_title);
            post_title.setText(title);

        }

        public void setNote(String note){

            TextView post_note = mView.findViewById(R.id.journal_row_preview);

            post_note.setText(note);
        }


        public void setDate(String date) {

            TextView post_date = mView.findViewById(R.id.journal_row_date);

            post_date.setText(date);
        }
    }

}
