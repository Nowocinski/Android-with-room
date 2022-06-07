package com.example.architectureexample;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new NoteRepository(application);
        this.allNotes = this.repository.getAllNotes();
    }

    public void insert(Note note) {
        this.repository.insert(note);
    }

    public void update(Note note) {
        this.repository.update(note);
    }

    public void delete(Note note) {
        this.repository.delete(note);
    }

    public void deleteAllNotes() {
        this.repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return this.allNotes;
    }
}
