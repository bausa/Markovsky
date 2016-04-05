package org.markovsky;


import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;

public class Main {

    @tests.CoverageIgnore
    public static void main(String[] args) throws IOException, InvalidMidiDataException {
        Song song = Song.importMidi("ClassicalCorpus.mid");
        TransitionMatrix<Note> songMatrix = song.getMatrix();
        Song generatedSong = MusicGenerator.generateMusic(song.getNote(0), songMatrix);
        generatedSong.write("GeneratedSong.mid");
    }
}
