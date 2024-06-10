package com.example.thoughtkeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ThoughtAdapter extends FirestoreRecyclerAdapter<Thought, ThoughtAdapter.ThoughtViewHolder> {
    private Context context;

    //initializes the adapter with the given FirestoreRecyclerOptions and a Context
    //assigns the context to a member variable for later use
    public ThoughtAdapter(@NonNull FirestoreRecyclerOptions<Thought> options, Context context) {
        super(options);
        this.context = context;
    }

    //is called by RecyclerView to display data at a specific position
    //binds the Thought data to the views (TextViews) within a ThoughtViewHolder
    //sets an OnClickListener on the item view, which starts a new ThoughtDetailsActivity and passes data related to the clicked Thought
    @Override
    protected void onBindViewHolder(@NonNull ThoughtViewHolder holder, int position, @NonNull Thought thought) {
        holder.titleTextView.setText(thought.title);
        holder.contentTextView.setText(thought.content);
        holder.timestampTextView.setText(Utility.timestampToString(thought.timestamp));
        holder.moodTextView.setText("Mood: " + thought.mood);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, ThoughtDetailsActivity.class);
            intent.putExtra("title", thought.title);
            intent.putExtra("content", thought.content);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("mood", thought.getMood());
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    //is called when RecyclerView needs a new ViewHolder
    //inflates the layout for the individual items (recycler_thought_item.xml) and returns a new instance of ThoughtViewHolder
    @NonNull
    @Override
    public ThoughtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_thought_item, parent, false);
        return new ThoughtViewHolder(view);
    }

    static class ThoughtViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, timestampTextView,moodTextView;

        //represents a single item view within the RecyclerView
        //holds references to the individual views within the item layout.
        public ThoughtViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.thought_title_text_view);
            contentTextView = itemView.findViewById(R.id.thought_content_text_view);
            timestampTextView = itemView.findViewById(R.id.thought_timestamp_text_view);
            moodTextView = itemView.findViewById(R.id.thought_mood_text_view);
        }
    }
}
