package edu.uw.firebasedemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //private ArrayAdapter<Word> adapter;
    private FirebaseListAdapter<Word> adapter;

    //firebase!
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        Query query = mDatabase.child("words").limitToLast(50); //set up the "query"
        FirebaseListOptions<Word> options = new FirebaseListOptions.Builder<Word>()
                .setQuery(query, Word.class)
                .setLayout(R.layout.list_item_layout)
                .build(); //build a list options

        adapter = new FirebaseListAdapter<Word>(options) {
            @Override
            protected void populateView(View v, Word model, int position) {
                TextView txtWord = v.findViewById(R.id.txt_item_word);
                TextView txtFreq = v.findViewById(R.id.txt_item_freq);
                txtWord.setText(model.word);
                txtFreq.setText(model.frequency+"");
            }
        };


        //list-model
        Word[] dataArray = {new Word("Dog",10), new Word("Cat",10), new Word("Android",10), new Word("Inconceivable",10)};
        ArrayList<Word> data = new ArrayList<Word>(Arrays.asList(dataArray));

        //list-view
        AdapterView listView = (AdapterView) findViewById(R.id.word_list_view);

        //list-controller
        //adapter = new ArrayAdapter<Word>(this, R.layout.list_item_layout, R.id.txt_item_word, data);
        listView.setAdapter(adapter);


        //handle button input
        final TextView inputText = (TextView) findViewById(R.id.txt_add_word);
        Button addButton = (Button) findViewById(R.id.btn_add_word);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputWord = inputText.getText().toString().toLowerCase();
                Log.v(TAG, "To add: " + inputWord);

                Word newWord = new Word(inputWord, 0);

                mDatabase.child("words").push().setValue(newWord); //add to the list

            }
        });

        //handle item clicking
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = (Word) parent.getItemAtPosition(position); //item we clicked on
                Log.v(TAG, "Clicked on '" + word.word + "'");

                DatabaseReference wordRef = adapter.getRef(position); //get reference to element
                wordRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Word word = mutableData.getValue(Word.class);
                        if (word != null) {
                            mutableData.setValue(new Word(word.word, word.frequency+1)); //set value to have increased frequency
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if(databaseError != null)
                            Log.d(TAG, "wordTransaction:onComplete:" + databaseError);
                    }
                });
            }
        });

        //listen for database changes!
//        ValueEventListener valueListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                adapter.clear();
//                for(DataSnapshot wordSnapshop : dataSnapshot.getChildren()){
//                    Word word = (Word)wordSnapshop.getValue(Word.class); //what type it should be!
//                    adapter.add(word);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "loadWords:onCancelled", databaseError.toException());
//            }
//        };
//        mDatabase.child("words").addValueEventListener(valueListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private static final int SIGN_IN_RESPONSE_CODE = 100;

    public void loginUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Log.v(TAG, "Logged in");
            //already signed in... as whom?
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(this, "Logged in as "+user.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            Log.v(TAG, "Not logged in");
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_RESPONSE_CODE);
        }
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
                mDatabase.child("test").setValue(new Word("Test", 1000));

                return true;
            case R.id.menu_login:
                loginUser();
                return true;
            case R.id.menu_logout:
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                        }
                    });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
