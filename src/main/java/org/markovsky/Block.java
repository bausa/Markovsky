package org.markovsky;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return Objects.equals(previous, block.previous) &&
                Objects.equals(current, block.current);
    }

    @Override
    public int hashCode() {
        int result = previous != null ? previous.hashCode() : 0;
        result = 31 * result + (current != null ? current.hashCode() : 0);
        return result;
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
}
