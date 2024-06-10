package com.example.thoughtkeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class ThoughtDetailsActivity extends AppCompatActivity {

    //variables
    EditText titleEditText, contentEditText;
    ImageButton saveThoughtBtn;
    TextView pageTitleTextView;
    String title, content, docId, mood;
    boolean isEditMode = false;
    TextView deleteThoughtTextViewBtn;
    Spinner moodSpinner;

    //it sets the layout of the activity to activity_thought_details
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thought_details);

        //initialize UI components
        titleEditText = findViewById(R.id.thoughts_title_text);
        contentEditText = findViewById(R.id.thoughts_content_text);
        saveThoughtBtn = findViewById(R.id.save_thought_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteThoughtTextViewBtn = findViewById(R.id.delete_thought_text_view_btn);
        moodSpinner = findViewById(R.id.mood_spinner);

        //sets up the moodSpinner with an array of mood options from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        mood = getIntent().getStringExtra("mood"); // Retrieve saved mood

        //checks if docId is present and not empty to determine if the activity should be in "edit mode"
        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        //edits title and content in EditMode
        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode) {
            pageTitleTextView.setText("Edit your thought");
            deleteThoughtTextViewBtn.setVisibility(View.VISIBLE);
        }

        //sets saved mood in the spinner
        if (mood != null) {
            int index = adapter.getPosition(mood);
            if (index != -1) {
                moodSpinner.setSelection(index);
            }
        }

        //set click listeners for the save and delete buttons
        saveThoughtBtn.setOnClickListener((v) -> saveThought());
        deleteThoughtTextViewBtn.setOnClickListener((v) -> deleteThoughtFromFirebase());

        //sets a listener for mood selection
        moodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //handle the selected mood here
                mood = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    //validates input, creates a Thought object, and saves it to Firebase
    void saveThought() {
        String thoughtTitle = titleEditText.getText().toString();
        String thoughtContent = contentEditText.getText().toString();

        if (thoughtTitle.isEmpty()) {
            titleEditText.setError("Thought Title is required");
            return;
        }

        Thought thought = new Thought();
        thought.setTitle(thoughtTitle);
        thought.setContent(thoughtContent);
        thought.setMood(mood); //set selected mood
        thought.setTimestamp(Timestamp.now());

        saveThoughtToFirebase(thought);
    }

    //saves the thought to Firebase, either updating an existing document or creating a new one
    void saveThoughtToFirebase(Thought thought) {
        DocumentReference documentReference;
        if (isEditMode) {
            //updates the thought
            documentReference = Utility.getCollectionReferenceForThoughts().document(docId);
        } else {
            //creates new thought
            documentReference = Utility.getCollectionReferenceForThoughts().document();
        }

        documentReference.set(thought).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Thought is added
                    Utility.showToast(ThoughtDetailsActivity.this, "Thought added successfully");
                    finish();
                } else {
                    Utility.showToast(ThoughtDetailsActivity.this, "Failed while adding a thought");
                }
            }
        });
    }

    //deletes the thought from Firebase
    void deleteThoughtFromFirebase() {
        DocumentReference documentReference = Utility.getCollectionReferenceForThoughts().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //thought is deleted
                    Utility.showToast(ThoughtDetailsActivity.this, "Thought deleted successfully");
                    finish();
                } else {
                    Utility.showToast(ThoughtDetailsActivity.this, "Failed while deleting the thought");
                }
            }
        });
    }
}
