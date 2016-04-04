package tests;

import org.markovsky.MusicGenerator;
import org.markovsky.Note;
import org.markovsky.Song;
import org.markovsky.TransitionMatrix;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by sambaumgarten on 4/4/16
 */
public class MusicGeneratorTest {
    @Test
    public void testGenerateMusic() throws Exception {
        TransitionMatrix<Note> noteTransitionMatrix = new TransitionMatrix<>();

        Note note1 = new Note(1.0, 30);
        Note note2 = new Note(1.0, 40);

        noteTransitionMatrix.recordTransition(note1, note2);

        Song song = MusicGenerator.generateMusic(note1, noteTransitionMatrix);

        assertEquals(song, new Song(new Note[] {note1, note2}));
    }

    @Test
    public void testCreation() throws Exception {
        MusicGenerator musicGenerator = new MusicGenerator();
    }
}