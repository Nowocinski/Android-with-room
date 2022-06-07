package com.example.architectureexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    public static final String REQUEST_CODE = "requestCode";
    public static final String ADD_NOTE_REQUEST = "1";
    private NoteViewModel viewModel;
    private ActivityResultLauncher<Intent> intentLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = this.findViewById(R.id.button_add_note);
        this.intentLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        switch (intent.getStringExtra(this.REQUEST_CODE)) {
                            case ADD_NOTE_REQUEST:
                                String title = intent.getStringExtra(AddNoteActivity.EXTRA_TITLE);
                                String description = intent.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
                                Integer priority = intent.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1);

                                Note note = new Note(title, description, priority);
                                viewModel.insert(note);

                                Toast.makeText(this, "Note save", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                // ...
                        }
                    } else {
                        Toast.makeText(this, "Note not save", Toast.LENGTH_SHORT).show();
                    }
                });
        buttonAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra(REQUEST_CODE, ADD_NOTE_REQUEST);
            intentLaunch.launch(intent);
        });

        RecyclerView recyclerView = this.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        this.viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        this.viewModel.getAllNotes().observe(this, notes -> {
            adapter.setNotes(notes);
        });
    }
}