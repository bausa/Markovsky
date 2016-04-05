package org.markovsky;

import java.util.LinkedList;

/**
 * Created by sambaumgarten on 4/4/16
 */
public class MusicGenerator {
    public static Song generateMusic(Note startNote, TransitionMatrix<Note> transitionMatrix) {
        LinkedList<Note> notes = new LinkedList<>();

        Note currentNote = startNote;
        int i = 0;
        while (currentNote != null) {
            notes.add(currentNote);
            if (transitionMatrix.probabilities(currentNote) != null) {
                currentNote = transitionMatrix.probabilities(currentNote).randomNode();
            } else {
                currentNote = null;
            }
        }

        return new Song((Note [])notes.toArray(new Note[notes.size()]));
    }
}
