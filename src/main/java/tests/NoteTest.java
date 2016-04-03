package tests;

import org.markovsky.Note;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by sambaumgarten on 4/3/16
 */
public class NoteTest {

    @Test
    public void testParameters() throws Exception {
        Note note = new Note(1.0, 50);

        assertEquals(note.getDuration(), 1.0);
        assertEquals(note.getPitch(), 50);

        // This has a space at the end (See https://github.com/samuelb2/Markovsky/commit/97d7406cab41e9b02d065d5258f21e5e5bebac43#commitcomment-16946352)
        assertEquals(Note.getNotation(50), "D ");
    }

    @Test
    public void testRest() throws Exception {
        Note note = new Note(1.0, -1);

        assertEquals(note.isRest(), true);
        assertEquals(Note.getNotation(-1), "REST");
    }

    @Test
    public void testToString() throws Exception {
        Note note = new Note(1.0, 50);

        // See https://github.com/samuelb2/Markovsky/commit/97d7406cab41e9b02d065d5258f21e5e5bebac43#commitcomment-16946352
        assertEquals(note.toString(), "1.0 of D ");

    }
}