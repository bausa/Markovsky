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
        Note rest = new Note(1.0, -1);
        Note notRest = new Note(1.0, 50);

        assertEquals(rest.isRest(), true);
        assertEquals(notRest.isRest(), false);
        assertEquals(Note.getNotation(-1), "REST");
    }

    @Test
    public void testToString() throws Exception {
        Note note = new Note(1.0, 50);
        Note fault = new Note(1.0, 5);

        // See https://github.com/samuelb2/Markovsky/commit/97d7406cab41e9b02d065d5258f21e5e5bebac43#commitcomment-16946352
        assertEquals(note.toString(), "1.0 of D ");
        assertEquals(fault.toString(), "1.0 of UNK");
    }

    @Test
    public void testEquals() throws Exception {
        Note note = new Note(1.0, 50);
        Note note2 = new Note(2.0, 50);
        Note note3 = new Note(1.0, 75);
        Note note4 = new Note(1.0, 50);

        assertTrue(note.equals(note));
        assertFalse(note.equals(null));
        assertFalse(note.equals(""));
        assertFalse(note.equals(note2));
        assertFalse(note.equals(note3));
        assertTrue(note.equals(note4));
    }
}