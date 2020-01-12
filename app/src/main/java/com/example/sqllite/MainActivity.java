package com.example.sqllite;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private static final  int MENU_ITEM_VIEW = 111;
    private static final  int MENU_ITEM_CREATE = 222;
    private static final  int MENU_ITEM_EDIT = 333;
    private static final  int MENU_ITEM_DELETE = 444;

    private static final int MY_REQUEST_CODE = 1000;

    private final List<Note> noteList = new ArrayList<Note>();
    private ArrayAdapter<Note>  listViewAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.createDefaultNotesIfNeed();

        List<Note> list = db.getAllNotes();
        this.noteList.addAll(list);

        this.listViewAdapter = new ArrayAdapter<Note>(this,android.R.layout.simple_list_item_1,
                android.R.id.text1, noteList);


        this.listView.setAdapter(this.listViewAdapter);
        registerForContextMenu(this.listView);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");

        menu.add(0, MENU_ITEM_VIEW, 0, "VIEW NOTE");
        menu.add(0, MENU_ITEM_CREATE, 1, "CREATE NOTE");
        menu.add(0, MENU_ITEM_EDIT, 2, "EDIT NOTE");
        menu.add(0, MENU_ITEM_DELETE, 4, "DELETE NOTE");

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Note selectedNote = (Note) this.listView.getItemAtPosition(info.position);

        if(item.getItemId() == MENU_ITEM_VIEW) {
            Toast.makeText(getApplicationContext(), selectedNote.getNoteContent(), Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == MENU_ITEM_CREATE) {
            Intent intent = new Intent(this, activity_add_edit_note.class);
            // start activity_add_edit_note co phan hoi
            this.startActivityForResult(intent, MY_REQUEST_CODE);

        } else if (item.getItemId() == MENU_ITEM_EDIT) {
            Intent intent = new Intent(this, activity_add_edit_note.class);
            intent.putExtra("note", selectedNote);

            // Start AddEditNoteActivity, có phản hồi.
            this.startActivityForResult(intent, MY_REQUEST_CODE);
        } else if (item.getItemId() == MENU_ITEM_DELETE) {

            new AlertDialog.Builder(this).setMessage(selectedNote.getNoteTitle() + "Are you sure want to delete ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteNote(selectedNote);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            return false;
        }
        return true;
    }

    public void deleteNote(Note selectedNote) {
            MyDatabaseHelper db = new MyDatabaseHelper(this);
            db.deleteNote(selectedNote);
            this.noteList.remove(selectedNote);
             // Refresh listView
            this.listViewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE){
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);

            //refresh listview
            if(needRefresh){
                this.noteList.clear();
                MyDatabaseHelper db = new MyDatabaseHelper(this);
                List<Note> list = db.getAllNotes();
                this.noteList.addAll(list);
                // thong bao du lieu thay doi (de refresh listview
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }
}


