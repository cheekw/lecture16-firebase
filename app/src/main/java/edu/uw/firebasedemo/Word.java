package edu.uw.firebasedemo;

/**
 * A simple wrapper to represent a word
 */
public class Word {

    public String word;
    public int frequency;

    public Word() {
        //required default constructor
    }

    public Word(String word, int frequency){
        this.word = word;
        this.frequency = frequency;
    }

    public String toString() {
        return this.word+" ("+this.frequency+")";
    }
}
