package edu.uw.firebasedemo

/**
 * A simple wrapper to represent a word
 */
data class Word(var word:String = "", var frequency:Int = 0) {
    override fun toString(): String {
        return "${this.word} (${this.frequency})"
    }
}
