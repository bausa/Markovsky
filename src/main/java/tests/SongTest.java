package tests;

import org.markovsky.Song;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sound.midi.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Created by sambaumgarten on 4/3/16
 */
public class SongTest {
    @BeforeMethod
    public void createTempDir() {
        TemporaryDirectoryManager.createTempDir();
    }

    @BeforeMethod
    public void writeMidiFile() throws InvalidMidiDataException, IOException {
        MidiFileGenerator.writeMidiFile(new File("tmp/test_midi_file.mid"));
    }

    @AfterMethod
    public void deleteTempDir() {
        TemporaryDirectoryManager.deleteTempDir();
    }

    @Test
    public void testStatusCodes() {
        assertEquals(Song.StatusCodes.intToStatus(128), Song.StatusCodes.NOTE_OFF);
        assertEquals(Song.StatusCodes.intToStatus(144), Song.StatusCodes.NOTE_ON);
        assertEquals(Song.StatusCodes.intToStatus(5), Song.StatusCodes.UNRECOGNIZED);
    }

    @Test
    public void testMidiImport() throws Exception {
        Song song = Song.importMidi("tmp/test_midi_file.mid");

        assertEquals(song.toString(), "[5.0 of C ]");
    }

    @Test
    public void testSongIO() throws Exception {
        Song song = Song.importMidi("tmp/test_midi_file.mid");

        // Write to file
        File file = new File("tmp/test_json.txt");
        song.archive(file.getPath());

        // Read from file
        Song jsonSong = Song.importFromArchive(file.getAbsolutePath());

        assertEquals(song, jsonSong);
    }
}