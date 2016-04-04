import org.markovsky.TransitionMatrix;

import javax.sound.midi.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
	    TransitionMatrix<String> matrix = new TransitionMatrix<>();
//        System.setIn(new BufferedInputStream(new FileInputStream("bible.txt"))); // test
        Scanner scanner = new Scanner(System.in);
        StringBuilder input = new StringBuilder();

        String data = scanner.nextLine();
        while (scanner.hasNext()) {
            input.append(data + " ");
            data = scanner.nextLine();
        }

        String currentWord = matrix.importData(input.toString());

        while (currentWord != null) {
            System.out.println(currentWord);
        }
    }
}
