import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    TransitionMatrix<String> matrix = new TransitionMatrix<>();
        Scanner scanner = new Scanner(System.in);

        StringBuilder input = new StringBuilder();

        String data = scanner.nextLine();
        while (!data.equals("done")) {
            input.append(data + " ");
            data = scanner.nextLine();
        }

        int level = 2;

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
        }
    }
}
