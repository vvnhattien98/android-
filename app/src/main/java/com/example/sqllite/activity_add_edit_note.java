package com.example.sqllite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class activity_add_edit_note extends AppCompatActivity {

    Note note;

    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    private int mode;
    private EditText textTitle;
    private EditText textContent;

    private boolean needRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        this.textTitle = (EditText)this.findViewById(R.id.text_note_title);
        this.textContent = (EditText)this.findViewById(R.id.text_note_content);

        Intent intent = this.getIntent();
        this.note = (Note)intent.getSerializableExtra("note");
        if(note == null){
            this.mode = MODE_CREATE;
        }else{
            this.mode = MODE_EDIT;
            this.textTitle.setText(note.getNoteTitle());
            this.textContent.setText(note.getNoteContent());
        }
    }

    public void buttonSaveClicked(View view){
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        String title = this.textTitle.getText().toString();
        String content = this.textContent.getText().toString();

        if(title.equals("") || content.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter title or content", Toast.LENGTH_LONG).show();
            return;
        }

        if(mode == MODE_CREATE){
            this.note = new Note(title, content);
            db.addNote(note);
        }else{
            this.note.setNoteTitle(title);
            this.note.setNoteContent(content);
            db.updateNote(note);
        }

        this.needRefresh = true;
        // tro lai MainActivity
        this.onBackPressed();
    }

    public void buttonCancelClicked(View view){
        this.onBackPressed();
    }

    @Override
    public void finish() {

        //chuan bi du lieu Intent
        Intent data = new Intent();
        // Yeu cau MainActivity refresh lai listView hoac khong
        data.putExtra("needRefresh", needRefresh);

        // Activity da hoan thanh ok, tra ve du lieu
        this.setResult(Activity.RESULT_OK, data);
        super.finish();

    }
}
