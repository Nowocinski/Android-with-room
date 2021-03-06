package com.example.architectureexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    public static final String REQUEST_CODE = "requestCode";
    public static final String ADD_NOTE_REQUEST = "1";
    public static final String EDIT_NOTE_REQUEST = "2";
    private NoteViewModel viewModel;
    private ActivityResultLauncher<Intent> intentLaunch;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = this.findViewById(R.id.button_add_note);

        buttonAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(REQUEST_CODE, ADD_NOTE_REQUEST);
            intentLaunch.launch(intent);
        });

        RecyclerView recyclerView = this.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        this.adapter = new NoteAdapter();
        recyclerView.setAdapter(this.adapter);

        this.viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        this.viewModel.getAllNotes().observe(this, notes -> {
            adapter.setNotes(notes);
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.delete(adapter.getNoteAt(viewHolder.getBindingAdapterPosition()));
                adapter.remove(viewHolder.getBindingAdapterPosition());
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        this.intentLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        switch (intent.getStringExtra(this.REQUEST_CODE)) {
                            case ADD_NOTE_REQUEST:
                                String title = intent.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                                String description = intent.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                                Integer priority = intent.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);

                                Note note = new Note(title, description, priority);
                                viewModel.insert(note);
                                this.adapter.add(note);

                                Toast.makeText(this, "Note save", Toast.LENGTH_SHORT).show();
                                break;
                            case EDIT_NOTE_REQUEST:
                                int id = intent.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
                                if (id == -1) {
                                    Toast.makeText(this, "Note can't be update", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                title = intent.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
                                description = intent.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
                                priority = intent.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
                                note = new Note(title, description, priority);
                                note.setId(id);
                                viewModel.update(note);
                                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();

                            default:
                                // ...
                        }
                    } else {
                        Toast.makeText(this, "Note not save", Toast.LENGTH_SHORT).show();
                    }
                });

        this.adapter.setOnItemClickedListener(new NoteAdapter.IOnItemClickedListener() {
            @Override
            public void onItemClicked(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                intent.putExtra(MainActivity.REQUEST_CODE, MainActivity.EDIT_NOTE_REQUEST);
                intentLaunch.launch(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                viewModel.deleteAllNotes();
                this.adapter.clear();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}