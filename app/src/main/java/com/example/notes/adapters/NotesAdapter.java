package com.example.notes.adapters;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.entities.Note;
import com.example.notes.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final NotesListener notesListener;
    private Timer timer;
    private final List<Note> noteSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        noteSource=notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(v -> notesListener.onNoteClicked(notes.get(position), position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutNote;
        RoundedImageView roundImageNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle= itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote =itemView.findViewById(R.id.layoutNote);
            roundImageNote = itemView.findViewById(R.id.imageNoteInContainer);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if(note.getTitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();

            if(note.getColor()!= null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null) {
                roundImageNote.setImageBitmap((BitmapFactory.decodeFile(note.getImagePath())));
                roundImageNote.setVisibility(View.VISIBLE);
            }else{
                roundImageNote.setVisibility(View.GONE);
            }
        }
    }

    public void searchNotes(final String keyword){
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                if(keyword.trim().isEmpty()){
                    notes=noteSource;
                }else{
                    ArrayList<Note> list = new ArrayList<>();
                    for (Note note: noteSource){
                        if(note.getTitle().toLowerCase().contains(keyword.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(keyword.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(keyword.toLowerCase())){
                            list.add(note);
                        }
                    }
                    notes=list;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        },500);
    }

    public void cancelTimer(){
        if (timer!=null){
            timer.cancel();
        }
    }
}
