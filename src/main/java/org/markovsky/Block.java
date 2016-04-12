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

    public static final Block END = new Block(null, null);

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

    public String toString(){
        String s = "";
        if(previous != null) s += previous.toString() + " and ";
        if(current != null) s +=  current.toString();
        return s;
    }

    public Note getPrevious(){
        return previous;
    }

    public Note getCurrent(){
        return current;
    }

    //Poorly implemented hashCode, but it will get the job done.
    public int hashCode(){
        int curr = (current != null)? getCurrent().hashCode() : 0;
        int prev = (previous != null)? getPrevious().hashCode(): 0;
        int val = Math.abs(curr + prev);
        if(val < 0) val = 0;
        return val;
    }
}
