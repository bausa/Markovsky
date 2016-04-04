package org.markovsky;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public @interface CoverageIgnore{}

    @CoverageIgnore
    public static void main(String[] args) throws FileNotFoundException {
	    TransitionMatrix<String> matrix = new TransitionMatrix<>();

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
