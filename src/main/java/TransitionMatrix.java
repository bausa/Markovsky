import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by sambaumgarten on 3/8/16
 */
public class TransitionMatrix<Node> {
    private final String VERSION = "0.1";

    private HashMap<Node, OccurrencesCount> matrix = new HashMap<Node, OccurrencesCount>();

    public TransitionMatrix() {}

    public static class VersionMatchException extends Exception {
        public VersionMatchException(String version) {
            super("Data imported is of version " + version + ".  Expected version " + new TransitionMatrix<>().VERSION);
        }
    }

    /**
     * Creates a transition matrix from the serialized JSON
     * @param json the json to base the matrix on
     * @return a filled transition matrix
     */
    public static TransitionMatrix importJson(String json) throws VersionMatchException {
        TransitionMatrix matrix = new Gson().fromJson(json, TransitionMatrix.class);
        if (matrix.VERSION != new TransitionMatrix().VERSION) {
            throw new VersionMatchException(matrix.VERSION);
        }
        return matrix;
    }

    public class OccurrencesCount {
        private Node node;
        private HashMap<Node, OccurrenceProbability> occurrenceProbabilityHashMap = new HashMap<Node, OccurrenceProbability>();
        private int totalCount;

        public class OccurrenceProbability {
            private int count;

            public void add() {
                count++;
                totalCount++;
            }

            public double getProbability() {
                if (totalCount == 0) return 0;
                return (double)count / totalCount;
            }

            public String toString() {
                return "" + getProbability();
            }
        }

        public OccurrencesCount(Node node) {
            this.node = node;
        }

        public void add(Node occurringNode) {
            OccurrenceProbability occurrenceProbability;
            if (occurrenceProbabilityHashMap.containsKey(occurringNode)) occurrenceProbability = occurrenceProbabilityHashMap.get(occurringNode);
            else occurrenceProbability = new OccurrenceProbability();

            occurrenceProbability.add();

            occurrenceProbabilityHashMap.put(occurringNode, occurrenceProbability);
        }
    }

    public class ProbabilityMap {
        private HashMap<Node, OccurrencesCount.OccurrenceProbability> map;

        public ProbabilityMap(HashMap<Node, OccurrencesCount.OccurrenceProbability> map) {
            this.map = map;
        }

        public HashMap<Node, OccurrencesCount.OccurrenceProbability> getMap() {
            return map;
        }

        public Node randomNode() {
            Random random = new Random();

            double randomNumber = random.nextDouble();
            double currentNumber = 0;

            HashMap<Node, OccurrencesCount.OccurrenceProbability> probabilityHashMap = getMap();
            if (probabilityHashMap == null) return null;

            for (int i = 0; i < probabilityHashMap.size(); i++) {
                Node key = (Node) probabilityHashMap.keySet().toArray()[i];
                TransitionMatrix.OccurrencesCount.OccurrenceProbability occurrenceProbability = (TransitionMatrix.OccurrencesCount.OccurrenceProbability) probabilityHashMap.values().toArray()[i];
                currentNumber += occurrenceProbability.getProbability();

                if (currentNumber >= randomNumber) return key;
            }

            return null;
        }
    }

    public String importData(String data, int level) {
        String[] words = data.split(" ");

        String firstFrom = words[0];
        String firstTo = segmentForString(words, level, 1);

        recordTransition((Node) firstFrom, (Node) firstTo);

        for (int i = 0; i < words.length - level; i++) {
            String from = segmentForString(words, level, i);
            String to = segmentForString(words, level, i + level);

            recordTransition((Node) from, (Node) to);
        }

        return firstFrom;
    }

    private String segmentForString(String words[], int level, int word) {
        String wordsAtPoint[] = new String[level + 1];

        for (int j = 0; j <= level; j++) {
            if (words.length <= word + j) break;
            wordsAtPoint[j] = words[word + j];
        }

        return String.join(" ", wordsAtPoint);
    }

    public void recordTransition(Node from, Node to) {
        OccurrencesCount occurrencesCountFrom;

        if (matrix.containsKey(from)) occurrencesCountFrom = matrix.get(from);
        else occurrencesCountFrom = new OccurrencesCount(from);

        occurrencesCountFrom.add(to);

        matrix.put(from, occurrencesCountFrom);
    }

    public ProbabilityMap probabilities(Node node) {
        if (!matrix.containsKey(node)) return null;
        return new ProbabilityMap(matrix.get(node).occurrenceProbabilityHashMap);
    }

    public String toJson() {
        return new Gson().toJson(this, this.getClass());
    }
}
