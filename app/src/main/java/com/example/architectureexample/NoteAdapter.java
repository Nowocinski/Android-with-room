package com.example.architectureexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private List<Note> notes = new ArrayList<>();
    private IOnItemClickedListener listener;
    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
    }

    @Override
    public int getItemCount() {
        return this.notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        this.notifyDataSetChanged();
    }

    public Note getNoteAt(int position) {
        return this.notes.get(position);
    }

    public void remove(int position) {
        this.notes.remove(position);
        this.notifyItemRemoved(position);
    }

    public void clear() {
        this.notes.clear();
        this.notifyDataSetChanged();
    }

    public void add(Note note) {
        this.notes.add(note);
        int position = this.notes.size() - 1;
        notifyItemInserted(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.text_view_title);
            this.textViewDescription = itemView.findViewById(R.id.text_view_description);
            this.textViewPriority = itemView.findViewById(R.id.text_view_priority);
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClicked(notes.get(position));
                }
            });
        }
    }

    public interface IOnItemClickedListener {
        void onItemClicked(Note note);
    }

    public void setOnItemClickedListener(IOnItemClickedListener listener) {
        this.listener = listener;
    }
}
