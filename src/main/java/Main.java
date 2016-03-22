import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
	    TransitionMatrix<String> matrix = new TransitionMatrix<>();
        System.setIn(new BufferedInputStream(new FileInputStream("bible.txt")));
        Scanner scanner = new Scanner(System.in);
        StringBuilder input = new StringBuilder();

        String data = scanner.nextLine();
        while (scanner.hasNext()) {
            input.append(data + " ");
            data = scanner.nextLine();
        }

        int level = 2; // amount of frame

        String currentWord = matrix.importData(input.toString(), level);
        
        while (currentWord != null) {
            String word = "";
            for (int i = 0; i < level; i++) {
                if (currentWord.split(" ").length > i) word += currentWord.split(" ")[i] + " ";
            }

            System.out.print(word);

            TransitionMatrix<String>.ProbabilityMap probabilityMap = matrix.probabilities(currentWord);

            if (probabilityMap == null) break;

            currentWord = probabilityMap.randomNode();
            System.out.println(currentWord);
        }
    }
}
