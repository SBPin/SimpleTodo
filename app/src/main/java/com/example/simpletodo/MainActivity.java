package com.example.simpletodo;

import android.content.Intent;
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

    //  numeric code to identify the edit activity
    public static final int EDIT_REQUEST_CODE = 20;
    //  passes data between activities
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";


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
                //  Logging
                // Log.i("MainActivity", "Removed item " + position);
                //  return true to tell the framework that the long click was consumed
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //  first parameter is the context, second is the class of the activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                // put "extras" into the bundle for access in the edit activity
                i.putExtra(ITEM_TEXT, tasks.get(position));
                i.putExtra(ITEM_POSITION, position);
                //  bring up the edit activity w/ expectation of a results
                startActivityForResult(i, EDIT_REQUEST_CODE);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)   {
        super.onActivityResult(requestCode, resultCode, data);
        //EDIT_REQUEST_CODE defined with constants
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            //  extract updated item value from result extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            //  get pos. of item which was edited
            int position = data.getExtras().getInt(ITEM_POSITION, 0);
            //  update the model w/ the new item text at the edited position
            tasks.set(position, updatedItem);
            //  notify the adapter the model changed
            itemsAdapter.notifyDataSetChanged();
            //  Store the updated items back to disk
            writeItems();
            //  notify the user the operation completed successfully
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }
}
