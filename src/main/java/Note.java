/**
 * Created by jack on 3/22/2016.
 */
public class Note {
    private double duration;
    private int pitch;

    public Note(float duration, int pitch) {
        this.duration = duration;
        this.pitch = pitch;
    }

    public double getDuration() {
        return duration;
    }

    public int getPitch() {
        return pitch;
    }

    public boolean isRest(){
        return pitch == -1;
    }
}
