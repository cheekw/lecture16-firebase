package edu.uw.firebasedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayAdapter<Word> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //list-model
        Word[] dataArray = {new Word("Dog",10), new Word("Cat",10), new Word("Android",10), new Word("Inconceivable",10)};
        ArrayList<Word> data = new ArrayList<Word>(Arrays.asList(dataArray));

        //list-view
        AdapterView listView = (AdapterView) findViewById(R.id.word_list_view);

        //list-controller
        adapter = new ArrayAdapter<Word>(this, R.layout.list_item_layout, R.id.txt_item_word, data);
        listView.setAdapter(adapter);


        //handle button input
        final TextView inputText = (TextView) findViewById(R.id.txt_add_word);
        Button addButton = (Button) findViewById(R.id.btn_add_word);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputWord = inputText.getText().toString().toLowerCase();
                Log.v(TAG, "To add: " + inputWord);


            }
        });

        //handle item clicking
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = (Word) parent.getItemAtPosition(position); //item we clicked on
                Log.v(TAG, "Clicked on '" + word.word + "'");

            }
        });

    }



    public void loginUser() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_test:
                //experiment with database changes

                return true;
            case R.id.menu_login:
                loginUser();
                return true;
            case R.id.menu_logout:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
