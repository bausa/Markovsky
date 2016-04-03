package tests;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by sambaumgarten on 4/3/16
 */
public class MidiFileGenerator {
    public static void writeMidiFile(File location) throws InvalidMidiDataException, IOException {
        Sequence sequence = new Sequence(javax.sound.midi.Sequence.PPQ, 24);

        Track track = sequence.createTrack();

        MidiEvent midiEvent;

        // Set tempo
        MetaMessage metaMessage = new MetaMessage();
        byte[] tempoData = {0x02, (byte) 0x00, 0x00};
        metaMessage.setMessage(0x51, tempoData, 3);
        midiEvent = new MidiEvent(metaMessage, (long) 0);
        track.add(midiEvent);

        ShortMessage shortMessage;

        // Note on (C at tick 1)
        shortMessage = new ShortMessage();
        shortMessage.setMessage(0x90, 0x3C, 0x60);
        midiEvent = new MidiEvent(shortMessage, (long) 0);
        track.add(midiEvent);

        // Note off (C at tick 2880 or 1 second)
        shortMessage = new ShortMessage();
        shortMessage.setMessage(0x80, 0x3C, 0x40);
        midiEvent = new MidiEvent(shortMessage, (long) 120);
        track.add(midiEvent);

        // Set end of track
        metaMessage = new MetaMessage();
        byte[] endOfTrackData = {}; // empty array
        metaMessage.setMessage(0x2F, endOfTrackData, 0);
        midiEvent = new MidiEvent(metaMessage, (long) 140);
        track.add(midiEvent);

        // Write to file
        MidiSystem.write(sequence, 1, location);
    }
}
