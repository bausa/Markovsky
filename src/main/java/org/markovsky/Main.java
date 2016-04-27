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
        Song[] melodies = song.split();
        List<Song> songs = new ArrayList<>(900);

        while(songs.size() < 100) {
            Song generatedSong = null;
            TransitionMatrix<Block> songMatrix = song.getSteppingMatrix();
            generatedSong = MusicGenerator.generateMusicBlocks(songMatrix.getSeed(), songMatrix);
            songs.add(generatedSong);
        }
        Song[] songsArr = new Song[songs.size()];
        songs.toArray(songsArr);
//        songsArr = Song.songTest(songsArr, melodies);
        songsArr = new Song[] {Song.songTest(songsArr, melodies)};
        File folder = new File("GeneratedSongs" + File.separator);
        if(!folder.exists()){
            folder.mkdir();
        }
        for (int i = 0; i < songsArr.length; i++){
            Song s = songsArr[i];
            s.write("GeneratedSongs" + File.separator + "GeneratedSong " + i + ".mid");
        }

        System.out.println(songsArr[0].toString());
        System.out.println("-----------------");
        for(Song s : melodies){
            System.out.println(s.toString());
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
