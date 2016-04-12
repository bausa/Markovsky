package org.markovsky;


import javax.sound.midi.InvalidMidiDataException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    @tests.CoverageIgnore
    public static void main(String[] args) throws IOException, InvalidMidiDataException {
        importLoopyBlock();
    }

    private static void importLoopyBlock() throws IOException, InvalidMidiDataException {
        Song song = Song.importMidi("ClassicalCorpus.mid");
        List<Song> songs = new ArrayList<>(1_000);

        while(songs.size() < 100) {
            Song generatedSong = null;
            TransitionMatrix<Block> songMatrix = song.getSteppingMatrix();
            generatedSong = MusicGenerator.generateMusicBlocks(songMatrix.getSeed(), songMatrix);
            songs.add(generatedSong);
        }
        Song[] songsArr = new Song[songs.size()];
        songs.toArray(songsArr);
        songsArr = Song.songTest(songsArr);
        System.out.println(songsArr.length);
        for (int i = 0; i < songsArr.length; i++){
            Song s = songsArr[i];
            s.write("GeneratedSongs" + File.separator + "GeneratedSong " + i + ".mid");
        }
    }

    private static void importBlock() throws IOException, InvalidMidiDataException {
        Song song = Song.importMidi("ClassicalCorpus.mid");
        Song generatedSong = null;
        do {
            TransitionMatrix<Block> songMatrix = song.getSteppingMatrix();
            generatedSong = MusicGenerator.generateMusicBlocks(songMatrix.getSeed(), songMatrix);
        }while(generatedSong.getNumberNotes() < 10);
        generatedSong.write("GeneratedSong.mid");
    }

    private static void importSong() throws IOException, InvalidMidiDataException {
        Song song = Song.importMidi("ClassicalCorpus.mid");
        TransitionMatrix<Note> songMatrix = song.getMatrix();
        Song generatedSong = MusicGenerator.generateMusic(songMatrix.getSeed(), songMatrix);
        generatedSong.write("GeneratedSong.mid");
    }
}
