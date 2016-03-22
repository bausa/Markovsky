import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jack on 3/22/2016.
 */
public class Track {
    private Note[] notes;
    private final int tempo = 0; // tempo

    private Track(){
    }

    // http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static Track importMidi(String filename){
        // TODO: Implement
        return null;
    }

    public static Track importFromArchive(String filename) throws IOException{
        String json = readFile(filename, StandardCharsets.UTF_8);
        return new Gson().fromJson(json, Track.class);
    }

    public String toJson() {
        return new Gson().toJson(this, this.getClass());
    }

    public void archive(String path) throws IOException{
        try(PrintWriter out = new PrintWriter(path)){
            out.println(toJson());
        }
    }
}
