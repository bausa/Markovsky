package org.markovsky;

import com.google.gson.Gson;
import com.sun.media.sound.StandardMidiFileReader;

import javax.sound.midi.*;
import javax.sound.midi.spi.MidiFileReader;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jack on 3/22/2016.
 */
public class Song {
    public static final int WRITE_RESOLUTION = 24;
    private Note[] notes;
    private static final int tempo = 10; // tempo

    public Song(Note[] notes){
        this.notes = notes;
    }

    public enum StatusCodes{
        NOTE_ON,
        NOTE_OFF,
        UNRECOGNIZED;

        // https://www.midi.org/specifications/item/table-2-expanded-messages-list-status-bytes
        public static StatusCodes intToStatus(int number){
            if((number & 144) == 144) return NOTE_ON;
            if((number & 128) == 128) return NOTE_OFF;
            return UNRECOGNIZED;
        }
    }

    public Song[] split(){
        List<Song> songs = new ArrayList<>();
        int currentMarker = 0;
        for (int i = 0; i < notes.length; i++){
            if(notes[i].isRest() && notes[i].getDuration() >= 2){
                Note[] noteDest = new Note[i - currentMarker];
                System.arraycopy(this.notes, currentMarker, noteDest, 0, i-currentMarker);
                songs.add(new Song(noteDest));
                currentMarker = i + 1; // skip the long rest
            }
        }
        Song[] songArr = new Song[songs.size()];
        songArr = songs.toArray(songArr);
        return songArr;
    }

    // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    // all of these made from refinements suggested by MT
    public static Song[] songTest(Song[] songs, Song[] sources){
        List<Song> songList = new ArrayList<>();
        for(Song s : songs){
            if(s.getNumberNotes() < 10){
                continue;
            }

            boolean signal = false;

            int minSum = Integer.MAX_VALUE;
            Song maxSong = null;

            for(Song source : sources){
                if(s.equals(source)){
                    signal = true;
                    break;
                }
                // if it equals more than a subset of, say, 3 notes, cancel
                int topSum = 0;
                for(int i = 0; i < source.notes.length; i++){
                    int sum = 0;
                    int midSum = 0;
                    for(int j = 0; j < s.notes.length; j++){
                        if(source.notes[i].equals(s.notes[j])){
                            sum++;
                        }
                        else{
                            if(sum > midSum){
                                midSum = sum;
                            }
                            sum = 0;
                        }
                    }
                    if(midSum > topSum){
                        topSum = midSum;
                    }
                }
                if(topSum >= 3){
                    signal = true;
                    break;
                }
            }

            if(signal) continue;

            songList.add(s);
        }
        Song[] retArr = new Song[songList.size()];
        songList.toArray(retArr);
        return retArr;
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
                            System.err.println("unrecognized: " + message.getStatus());
                            break;
                    }
                }
            }
        }
        // cleanup
        for(int i = 0; i < notes.size(); i++){
            Note n = notes.get(i);
            if(n.getDuration() == 0) notes.remove(i--);
        }
        Note[] notesArr = new Note[notes.size()];
        notesArr = notes.toArray(notesArr);
        Song retSong = new Song(notesArr);
        return retSong;
    }

    public void write(String filename) throws InvalidMidiDataException, IOException {
        Sequence sequence = new Sequence(Sequence.PPQ, WRITE_RESOLUTION); // 24 ticks per quarter
        Track track = sequence.createTrack();

        // Set tempo
        MetaMessage metaMessage = new MetaMessage();
        byte[] tempoData = {(byte)tempo, (byte) 0x00, 0x00};
        metaMessage.setMessage(0x51, tempoData, 3);
        MidiEvent midiEvent = new MidiEvent(metaMessage, (long) 0);
        track.add(midiEvent);

        long currentTick = 0;
        for(int i = 0; i < notes.length; i++){
            final Note currentNote = notes[i];
            if(currentNote == Note.END) continue;
            final long currentNoteLength = Math.round(currentNote.getDuration() * WRITE_RESOLUTION);
            if(!currentNote.isRest()) { // unsure if this is how to handle rests
                final ShortMessage onMessage = new ShortMessage(144, currentNote.getPitch(), 64);// velocity is set at 64 in the range of 0-127
                final MidiEvent onEvent = new MidiEvent(onMessage, currentTick);
                track.add(onEvent);

                final ShortMessage offMessage = new ShortMessage(128, currentNote.getPitch(), 64);
                final MidiEvent offEvent = new MidiEvent(offMessage, currentTick + currentNoteLength);
                track.add(offEvent);
            }
            currentTick += currentNoteLength;
        }

        // set end of track
        MetaMessage mt = new MetaMessage();
        byte[] bet = {}; // empty array
        mt.setMessage(0x2F,bet,0);
        MidiEvent me = new MidiEvent(mt, (long)140);
        track.add(me);

        File destinationFile = new File(filename);
        MidiSystem.write(sequence, 1, destinationFile);
    }

    public Note getNote(int index) {
        return notes[index];
    }

    public int getNumberNotes(){
        return notes.length;
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

    public TransitionMatrix<Note> getMatrix(){
        TransitionMatrix<Note> matrix = new TransitionMatrix<>();
        for(int i = 0; i < notes.length-1; i++) {
            if(notes[i].isRest() && notes[i].getDuration() >= 2){
                matrix.recordTransition(notes[i], Note.END);
            }
            else{
                matrix.recordTransition(notes[i], notes[i+1]);
            }
        }
        return matrix;
    }

    //Only issue with my generation is that the first note of the file is left out.
    //Otherwise it is VERY similar to above.
    public TransitionMatrix<Block> getBlockMatrix(){
        TransitionMatrix<Block> matrix = new TransitionMatrix<>();
        for(int i = 1; i < notes.length-1; i++){
            Block current = new Block(notes[i-1], notes[1]);
            if(notes[i].isRest() && notes[i].getDuration() >= 2){
                matrix.recordTransition(current, Block.END);
            } else {
                Block next = new Block(notes[i], notes[i+1]);
                matrix.recordTransition(current, next);
            }
        }
        return matrix;
    }

    //To get data with two notes and singular notes
    public TransitionMatrix<Block> getSteppingMatrix(){
        TransitionMatrix<Block> matrix = new TransitionMatrix<>();
        Note prev, curr, next;
        //Record a single transition if needed:
        prev = notes[0];
        curr = notes[1];
        if(!(prev.isRest() && prev.getDuration() >= 2)){
            Block current = new Block(null, prev);
            matrix.recordTransition(current, new Block(null, curr));
        }

        for(int i = 0; i < notes.length-2; i++){

            prev = notes[i];
            curr = notes[i + 1];
            next = notes[i + 2];

            Block current = new Block(prev, curr);
            Block follow = new Block(curr, next);

            //Record the double transition of current to follow:
            if(curr.isRest() && curr.getDuration() >= 2){
                matrix.recordTransition(current, Block.END);
            } else {
                matrix.recordTransition(current, follow);
            }

            current = new Block(null, curr);
            follow = new Block(curr, next);

            //Record the single note pointing to a double transition:
            //As well as record the single pointing to a single:
            if(curr.isRest() && curr.getDuration() >= 2){
                matrix.recordTransition(current, Block.END);
            } else {
                matrix.recordTransition(current, follow);

                follow = new Block(null, next);

                matrix.recordTransition(current, follow);
            }
        }
        return matrix;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Note n : notes) {
            stringBuilder.append(n.toString() + "\n");
        }

        return stringBuilder.toString();
        //return Arrays.toString(notes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(notes, song.notes);

    }

    @Override
    public int hashCode() {
        return notes != null ? Arrays.hashCode(notes) : 0;
    }
}
