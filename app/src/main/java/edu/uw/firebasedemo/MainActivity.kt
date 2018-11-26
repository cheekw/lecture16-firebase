package edu.uw.firebasedemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var adapter: ArrayAdapter<Word>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //list-model
        val data = mutableListOf(Word("Dog", 10), Word("Cat", 10), Word("Android", 10), Word("Inconceivable", 10))

        //list-view
        val listView = findViewById<AdapterView<ArrayAdapter<Word>>>(R.id.word_list_view)

        //list-controller
        adapter = ArrayAdapter(this, R.layout.list_item_layout, R.id.txt_item_word, data)
        listView.setAdapter(adapter)


        //handle button input
        val inputText = findViewById<TextView>(R.id.txt_add_word)
        val addButton = findViewById<Button>(R.id.btn_add_word)
        addButton.setOnClickListener {
            val inputWord = inputText.text.toString().toLowerCase()
            Log.v(TAG, "To add: $inputWord")
        }

        //handle item clicking
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val word = parent.getItemAtPosition(position) as Word //item we clicked on
            Log.v(TAG, "Clicked on '" + word.word + "'")
        }

    }


    fun loginUser() {
        //TODO
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_test ->
                //experiment with database changes
                true
            R.id.menu_login -> {
                loginUser()
                true
            }
            R.id.menu_logout -> {
                //logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
