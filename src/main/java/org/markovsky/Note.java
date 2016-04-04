package org.markovsky;

/**
 * Created by jack on 3/22/2016.
 */
public class Note {
    private static final int REST_NUMBER = -1;
    private double duration; // in beats
    private int pitch; // in MIDI keyboard

    public Note(double duration, int pitch) {
        this.duration = duration;
        this.pitch = pitch;
    }

    public double getDuration() {
        return duration;
    }

    public int getPitch() {
        return pitch;
    }

    public boolean isRest(){
        return pitch == REST_NUMBER;
    }

    public static String getNotation(int midiNumber) {
        // http://stackoverflow.com/questions/712679/convert-midi-note-numbers-to-name-and-octave
        if(midiNumber == REST_NUMBER) return "REST";
        final String notes = "C C#D D#E F F#G G#A A#B ";
        final int octave = midiNumber / 12 - 1;
        if(octave <= 0) return "UNK"; // fault, replace with assertion later
        return notes.substring((midiNumber % 12) * 2, (midiNumber % 12) * 2 + 2);
    }

    public String toString(){
        return duration + " of " + getNotation(pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (Double.compare(note.duration, duration) != 0) return false;
        return pitch == note.pitch;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(duration);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + pitch;
        return result;
    }
}
