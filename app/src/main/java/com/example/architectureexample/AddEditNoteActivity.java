package com.example.architectureexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.architectureexample.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.architectureexample.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.architectureexample.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.architectureexample.EXTRA_PRIORITY";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        this.editTextTitle = this.findViewById(R.id.edit_text_title);
        this.editTextDescription = this.findViewById(R.id.edit_text_description);
        this.numberPickerPriority = this.findViewById(R.id.number_picker_priority);

        this.numberPickerPriority.setMinValue(1);
        this.numberPickerPriority.setMaxValue(10);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(this.EXTRA_ID)) {
            this.setTitle("Edit note");
            this.editTextTitle.setText(intent.getStringExtra(AddEditNoteActivity.EXTRA_TITLE));
            this.editTextDescription.setText(intent.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION));
            this.numberPickerPriority.setValue(intent.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1));
        } else {
            this.setTitle("Add note");
        }
    }

    private void saveNote() {
        String title = this.editTextTitle.getText().toString();
        String description = this.editTextDescription.getText().toString();
        int priority = this.numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(this.EXTRA_TITLE, title);
        data.putExtra(this.EXTRA_DESCRIPTION, description);
        data.putExtra(this.EXTRA_PRIORITY, priority);
        if (this.getIntent().getStringExtra(MainActivity.REQUEST_CODE).equals(MainActivity.EDIT_NOTE_REQUEST)) {
            data.putExtra(MainActivity.REQUEST_CODE, MainActivity.EDIT_NOTE_REQUEST);
        } else {
            data.putExtra(MainActivity.REQUEST_CODE, MainActivity.ADD_NOTE_REQUEST);
        }
        int id = getIntent().getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(AddEditNoteActivity.EXTRA_ID, id);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}