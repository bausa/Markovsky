package org.markovsky;

/**
 * Created by piete on 4/7/2016.
 */

/*
  Usage:
  In recording:
  Keep track of three things as you are parsing the file: the previous note, the current note, and the next note.
  Add the previous note and the current note to a Block. Add that block to the matrix and have it point to a block containing
  the current note and the next note.

  In generation:
  Simply add a block's current note to your generated notes and look up in the matrix for another block.
  Add that next block's current note. Repeat until finished.
 */


public class Block{

    private Note previous;
    private Note current;

    public Block(Note previous, Note current){
        this.previous = previous;
        this.current = current;
    }

    public boolean equals(Object o){
        if(o instanceof Block) return equals((Block) o);
        return false;
    }


    //Just checks that the contents are the same.
    public boolean equals(Block b){
        if(b.current != current) return false;
        if(b.previous != previous) return false;
        return true;
    }

    public Note getPrevious(){
        return previous;
    }

    public Note getCurrent(){
        return current;
    }

    //Poorly implemented hashCode, but it will get the job done.
    public int hashCode(){
        int val = Math.abs(previous.hashCode() + current.hashCode());
        if(val < 0) val = 0;
        return val;
    }
}
