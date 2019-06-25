package com.example.simpletodo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //ArrayAdapter helps wire to ListView
    ArrayList<String> tasks;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  getting regerence to ListView created in layout
        lvItems = (ListView) findViewById(R.id.lvItems);
        //  initalize task list
        readItems();
        //  initialize adapter using the items list
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, tasks);
        //  wire adapter to the view
        lvItems.setAdapter(itemsAdapter);

        //  setup the listener on creation
        setupListViewListener();
    }

    private void setupListViewListener() {
        //  set the ListView's itemLongClickListener
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //  remove the itemin the list at the index given by position
                tasks.remove(position);
                //  notify the adapter that the underlying dataset changed
                itemsAdapter.notifyDataSetChanged();
                //store updated list
                writeItems();
                //Logging
                // Log.i("MainActivity", "Removed item " + position);
                //  return true to tell the framework that the long click was consumed
                return true;
            }
        });
    }

    public void onAddItem(View v){
        //  get reference to the EditText created with the layout (you named yours "getNewItem")
        EditText getNewItem = (EditText) findViewById(R.id.getNewItem);
        //  grab the EditText's content as a String
        String itemText = getNewItem.getText().toString();
        //  add the item to the list via the adapter
        itemsAdapter.add(itemText);
        //  store the updated list
        writeItems();
        //clear the EditText by setting it to an empty String
        getNewItem.setText("");
        //  display a notification to the user
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();

    }

    //  return the file in which the data is stored
    private File getDataFile() {
        return new File(getFilesDir(), "todo.txt");
    }

    //  read the items from the file
    private void readItems() {
        try {
            //create array using contents from file
            tasks = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            //  print the error to the console
            e.printStackTrace();
            //  load an empty list
            tasks = new ArrayList<>();
        }

    }

    //  write the items to the filesystems
    private void writeItems() {
        try {
            //  save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), tasks);
        }   catch (IOException e) {
            //  print the error to the console
            e.printStackTrace();
        }
    }
}
