import com.google.gson.Gson;
import com.sun.media.sound.StandardMidiFileReader;

import javax.sound.midi.*;
import javax.sound.midi.spi.MidiFileReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 3/22/2016.
 */
public class Song {
    private Note[] notes;
    private static final int tempo = 0; // tempo

    private Song(Note[] notes){
        this.notes = notes;
    }

    public enum StatusCodes{
        NOTE_ON,
        NOTE_OFF,
        UNRECOGNIZED;

        // https://www.midi.org/specifications/item/table-2-expanded-messages-list-status-bytes
        public static StatusCodes intToStatus(int number){
            number = number ^ 0b00001111;
            if(number == 128) return NOTE_OFF;
            number = number ^ 0b00011111;
            if(number == 128) return NOTE_ON;
            return UNRECOGNIZED;
        }
    }

    // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static Song importMidi(String filename) throws IOException, InvalidMidiDataException {
        MidiFileReader reader = new StandardMidiFileReader();
        Sequence sequence = reader.getSequence(new BufferedInputStream(new FileInputStream(filename)));
        List<Note> notes = new ArrayList<>();
        //final float divisionType = sequence.getDivisionType(); It's PPQ in the corpus

        final int ticksPerQuarter = sequence.getResolution();

        // can get tempo here if needed, but doesn't appear to be needed
        final Track[] tracks = sequence.getTracks();
        // get rid of empty tracks
        for(Track t : tracks){
            // What do we do for multiple tracks?
            int prevNoteNumber = -1;
            long lastTick = 0;
            for(int i = 0; i < t.size(); i++){
                final MidiEvent currentEvent = t.get(i);
                final MidiMessage message = currentEvent.getMessage();
                final long currentTick = currentEvent.getTick();
                final long ticks = currentTick-lastTick;
                final double quarters = (double)ticks/(double)ticksPerQuarter;
                if(message instanceof ShortMessage) { // just wanna look at shortmessage
                    ShortMessage noteMessage = (ShortMessage) message;
                    final StatusCodes status = StatusCodes.intToStatus(message.getStatus());
                    final int noteNumber = noteMessage.getData1(); // 0-127
                    final int velocityNumber = noteMessage.getData2(); // 0-127, can ignore
                    final Note currentNote = new Note(quarters, prevNoteNumber); // make last note
                    notes.add(currentNote);
                    lastTick = currentTick;
                    switch (status) {
                        case NOTE_ON:
                            prevNoteNumber = noteNumber;
                            break;
                        case NOTE_OFF:
                            prevNoteNumber = -1;  // rests
                            break;
                        default:
                            System.err.println("unrecognized");
                            break;
                    }
                }
            }
        }
        Note[] notesArr = new Note[notes.size()];
        notesArr = notes.toArray(notesArr);
        Song retSong = new Song(notesArr);
        return retSong;
    }

    private static String getNotation(int midiNumber) {
        // http://stackoverflow.com/questions/712679/convert-midi-note-numbers-to-name-and-octave
        final String notes = "C C#D D#E F F#G G#A A#B ";
        final int octave = midiNumber / 12 - 1;
        return notes.substring((midiNumber % 12) * 2, (midiNumber % 12) * 2 + 2);
    }

    public static Song importFromArchive(String filename) throws IOException{
        String json = readFile(filename, StandardCharsets.UTF_8);
        return new Gson().fromJson(json, Song.class);
    }

    public String toJson() {
        return new Gson().toJson(this, this.getClass());
    }

    public void archive(String path) throws IOException{
        try(PrintWriter out = new PrintWriter(path)){
            out.println(toJson());
        }
    }

    public static void main(String[] args) throws IOException, InvalidMidiDataException {
        Song song = importMidi("magic flute.mid");
    }
}
