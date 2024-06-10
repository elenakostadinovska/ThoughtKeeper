package com.example.thoughtkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //variables
    FloatingActionButton addThoughtBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    ThoughtAdapter thoughtAdapter;
    Spinner moodFilterSpinner; // Renamed to moodFilterSpinner

    //sets content view to activity_main.xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing UI elements
        addThoughtBtn = findViewById(R.id.add_thought_btn);
        recyclerView = findViewById(R.id.recycler_view);
        menuBtn = findViewById(R.id.menu_btn);
        moodFilterSpinner = findViewById(R.id.mood_filter_spinner); // Updated to mood_filter_spinner

        //click listeners
        addThoughtBtn.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, ThoughtDetailsActivity.class)));
        menuBtn.setOnClickListener((v) -> showMenu());
        setupRecyclerView();
        setupMoodFilterSpinner(); // Updated method name
    }

    //Logout menu, the user is redirected to LoginActivity
    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //if logout is being clicked
                if (menuItem.getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    // sets up the RecyclerView with a Firestore query to fetch Thought objects, ordered by timestamp in descending order
    //also, it initializes and sets the ThoughtAdapter to the RecyclerView
    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForThoughts().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Thought> options = new FirestoreRecyclerOptions.Builder<Thought>()
                .setQuery(query, Thought.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        thoughtAdapter = new ThoughtAdapter(options, this);
        recyclerView.setAdapter(thoughtAdapter);
    }

    //sets up the Spinner with mood options from a string array resource
    //also, it adds "None" as the first item and sets an OnItemSelectedListener to filter thoughts based on the selected mood
    void setupMoodFilterSpinner() {
        // Populating the Spinner with mood options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mood_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Add "None" as the first item in the Spinner
        String[] moods = getResources().getStringArray(R.array.mood_array);
        List<String> moodList = new ArrayList<>(Arrays.asList(moods));
        moodList.add(0, "None");
        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moodList);
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the mood filter spinner
        moodFilterSpinner.setAdapter(moodAdapter);

        // Set a listener for Spinner item selection
        moodFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMood = parent.getItemAtPosition(position).toString();
                filterThoughtsByMood(selectedMood);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    //this method filters the thoughts based on the selected mood from the Spinner
    //if "None" is selected, it shows all thoughts
    void filterThoughtsByMood(String selectedMood) {
        Query query;
        if (selectedMood.equals("None")) { // Updated condition to check for "None"
            query = Utility.getCollectionReferenceForThoughts().orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            query = Utility.getCollectionReferenceForThoughts().whereEqualTo("mood", selectedMood)
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        }
        FirestoreRecyclerOptions<Thought> options = new FirestoreRecyclerOptions.Builder<Thought>()
                .setQuery(query, Thought.class).build();
        thoughtAdapter.updateOptions(options);
    }

    //starts listening for Firestore updates
    @Override
    protected void onStart() {
        super.onStart();
        thoughtAdapter.startListening();
    }

    //stops listening for Firestore updates
    @Override
    protected void onStop() {
        super.onStop();
        thoughtAdapter.stopListening();
    }

    //notifies the adapter to refresh its data when the activity resumes
    //when we add a thought and come back, the thought will be updated
    @Override
    protected void onResume() {
        super.onResume();
        thoughtAdapter.notifyDataSetChanged();
    }
}
