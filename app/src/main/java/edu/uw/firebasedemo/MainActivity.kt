package edu.uw.firebasedemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*






class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

//    private lateinit var adapter: ArrayAdapter<Word>
    private lateinit var adapter: FirebaseListAdapter<Word>

    //firebase!
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mDatabase = FirebaseDatabase.getInstance().reference;

        val query = mDatabase.child("words").limitToLast(50) //set up the "query"
        val options = FirebaseListOptions.Builder<Word>()
                .setQuery(query, Word::class.java)
                .setLayout(R.layout.list_item_layout)
                .build() //build a list options

        adapter = object : FirebaseListAdapter<Word>(options) {
            override fun populateView(v: View, model: Word, position: Int) {
                val txtWord = v.findViewById<TextView>(R.id.txt_item_word)
                val txtFreq = v.findViewById<TextView>(R.id.txt_item_freq)
                txtWord.text = model.word
                txtFreq.text = model.frequency.toString()
            }
        }


        //list-model
        val data = mutableListOf(Word("Dog", 10), Word("Cat", 10), Word("Android", 10), Word("Inconceivable", 10))

        //list-view
        val listView = findViewById<AdapterView<FirebaseListAdapter<Word>>>(R.id.word_list_view)

        //list-controller
//        adapter = ArrayAdapter(this, R.layout.list_item_layout, R.id.txt_item_word, data)
        listView.setAdapter(adapter)


        //handle button input
        val inputText = findViewById<TextView>(R.id.txt_add_word)
        val addButton = findViewById<Button>(R.id.btn_add_word)
        addButton.setOnClickListener {
            val inputWord = inputText.text.toString().toLowerCase()
            Log.v(TAG, "To add: $inputWord")

            val newWord = Word(inputWord, 0)

            mDatabase.child("words").push().setValue(newWord) //add to the list
        }

        //listen for database changes!
//        val valueListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                adapter.clear()
//                for (wordSnapshop in dataSnapshot.children) {
//                    adapter.add(wordSnapshop.getValue(Word::class.java))
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(TAG, "loadWords:onCancelled", databaseError.toException())
//            }
//        }
//        mDatabase.child("words").addValueEventListener(valueListener)


        //handle item clicking
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val word = parent.getItemAtPosition(position) as Word //item we clicked on
            Log.v(TAG, "Clicked on '" + word.word + "'")

            val wordRef = adapter.getRef(position) //get reference to element
            wordRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val word = mutableData.getValue(Word::class.java)
                    if (word != null) {
                        mutableData.value = Word(word.word, word.frequency + 1) //set value to have increased frequency
                    }
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, b: Boolean, dataSnapshot: DataSnapshot?) {
                    if (databaseError != null)
                        Log.d(TAG, "wordTransaction:onComplete:$databaseError")
                }
            })

        }

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private val SIGN_IN_RESPONSE_CODE = 100

    fun loginUser() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            Log.v(TAG, "Logged in")
            //already signed in... as whom?
            val user = auth.currentUser
            Toast.makeText(this, "Logged in as " + user!!.email!!, Toast.LENGTH_SHORT).show()
        } else {
            Log.v(TAG, "Not logged in")
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_RESPONSE_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_test -> {
                //experiment with database changes
                mDatabase.child("test").setValue(Word("Test", 1000));

                true
            }
            R.id.menu_login -> {
                loginUser()
                true
            }
            R.id.menu_logout -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener { Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
