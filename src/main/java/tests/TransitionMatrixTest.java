package tests;

import static org.testng.Assert.*;
import org.markovsky.TransitionMatrix;
import org.testng.annotations.Test;

/**
 * Created by sambaumgarten on 4/3/16
 */
public class TransitionMatrixTest {
    @org.testng.annotations.Test
    public void testRecordTransition() throws Exception {
        TransitionMatrix<String> transitionMatrix = new TransitionMatrix<>();

        // Add a transition
        transitionMatrix.recordTransition("Hello", "My");

        // Check that 2 nodes were added
        assertEquals(transitionMatrix.numberOfNodes(), 2);

        // Add another transition
        transitionMatrix.recordTransition("Hello", "Your");

        // Check that 1 node was added
        assertEquals(transitionMatrix.numberOfNodes(), 3);

        // Add another transition
        transitionMatrix.recordTransition("Your", "Name");

        // Check that 1 node was added
        assertEquals(transitionMatrix.numberOfNodes(), 4);
    }

    @org.testng.annotations.Test
    public void testProbabilities() throws Exception {
        TransitionMatrix<String> transitionMatrix = new TransitionMatrix<>();

        // Add a transition
        transitionMatrix.recordTransition("Hello", "My");

        // Check that the probability from "Hello" to "My" is 100%
        assertEquals(transitionMatrix.probabilities("Hello").getMap().get("My").getProbability(), 1.0);

        // Add another transition
        transitionMatrix.recordTransition("Hello", "Your");

        // Check that the probability from "Hello" to "Your" and "My" is 50%
        assertEquals(transitionMatrix.probabilities("Hello").getMap().get("My").getProbability(), 0.5);
        assertEquals(transitionMatrix.probabilities("Hello").getMap().get("Your").getProbability(), 0.5);
    }

    @org.testng.annotations.Test
    public void testJson() throws Exception {
        TransitionMatrix<String> transitionMatrix = new TransitionMatrix<>();

        // Add a transition
        transitionMatrix.recordTransition("Hello", "My");

        String json = transitionMatrix.toJson();
        System.out.println(json);

        TransitionMatrix<String> matrixFromJson = TransitionMatrix.importJson(json);

        assertEquals(matrixFromJson, transitionMatrix);
    }

    @Test
    public void testNumberOfNodes() throws Exception {
        TransitionMatrix<String> transitionMatrix = new TransitionMatrix<>();

        // Store the number of nodes
        int nodes = 0;

        // Ensure there are no transitions
        assertEquals(transitionMatrix.numberOfNodes(), nodes);

        // Add a transition
        transitionMatrix.recordTransition("Hello", "My");
        nodes += 2;

        // Ensure the transition was added successfully
        assertEquals(transitionMatrix.numberOfNodes(), nodes);

        // Add another transition
        transitionMatrix.recordTransition("Hello", "Your");
        nodes++;

        // Ensure the transition was added successfully
        assertEquals(transitionMatrix.numberOfNodes(), nodes);
    }

    @Test
    public void testGetTable() throws Exception {
        TransitionMatrix<String> transitionMatrix = new TransitionMatrix<>();

        // Add transitions
        transitionMatrix.importData("Hello My Name Is Hello Your Name Is");

        String table = transitionMatrix.getTable();

        assertEquals(table, "{\n" +
                "\t\"Hello\" : {\n" +
                "\t\t\"occurrences\" : 2,\n" +
                "\t\t\"probabilities\" : {\n" +
                "\t\t\"Your\" : 0.5\n" +
                "\t\t\"My\" : 0.5\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"Your\" : {\n" +
                "\t\t\"occurrences\" : 1,\n" +
                "\t\t\"probabilities\" : {\n" +
                "\t\t\"Name\" : 1.0\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"Is\" : {\n" +
                "\t\t\"occurrences\" : 1,\n" +
                "\t\t\"probabilities\" : {\n" +
                "\t\t\"Hello\" : 1.0\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"My\" : {\n" +
                "\t\t\"occurrences\" : 1,\n" +
                "\t\t\"probabilities\" : {\n" +
                "\t\t\"Name\" : 1.0\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"Name\" : {\n" +
                "\t\t\"occurrences\" : 2,\n" +
                "\t\t\"probabilities\" : {\n" +
                "\t\t\"Is\" : 1.0\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}");
    }

    @Test
    public void testEquals() throws Exception {
        TransitionMatrix<String> transitionMatrixA = new TransitionMatrix<>();
        TransitionMatrix<String> transitionMatrixB = new TransitionMatrix<>();

        // Ensure equal to itself
        assertEquals(transitionMatrixA, transitionMatrixA);

        // Ensure empty matricies are equal
        assertEquals(transitionMatrixA, transitionMatrixB);

        // Record same transition
        transitionMatrixA.recordTransition("Hello", "My");
        transitionMatrixB.recordTransition("Hello", "My");

        // Ensure still equal
        assertEquals(transitionMatrixA, transitionMatrixB);

        transitionMatrixA.recordTransition("My", "Name");

        // Ensure not equal
        assertNotEquals(transitionMatrixA, transitionMatrixB);

        // Ensure different classes aren't equal
        assertNotEquals(transitionMatrixA, "");
    }

    @Test(expectedExceptions = TransitionMatrix.VersionMatchException.class)
    public void testVesionMismatch() throws TransitionMatrix.VersionMatchException {
        // Should throw version mismatch
        TransitionMatrix.importJson("{\"VERSION\":\"0.0\",\"matrix\":{\"Hello\":{\"node\":\"Hello\",\"occurrenceProbabilityHashMap\":{\"My\":{\"count\":1}},\"totalCount\":1}}}");
    }

    @Test
    public void testRandomNode() throws Exception {
        TransitionMatrix<String> transitionMatrix = new TransitionMatrix<>();

        // Add a transition
        transitionMatrix.recordTransition("Hello", "My");

        assertEquals(transitionMatrix.probabilities("Hello").randomNode(), "My");

        // Add a transition
        transitionMatrix.recordTransition("Hello", "Your");

        int tries = 10;

        while (tries >= 0) {
            if (transitionMatrix.probabilities("Hello").randomNode().equals("Your")) break;

            tries--;
        }

        if (tries < 0) {
            fail("randomNode() never produced Node \"Your\"");
        }
    }
}