import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

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
        if (matrix.VERSION != new TransitionMatrix().VERSION) throw new VersionMatchException(matrix.VERSION);
        return matrix;
    }

    /**
     * Stores the number of occurrences that a Node has and the probability it will goto any other Node
     */
    public class OccurrencesCount {
        // The node itself
        private Node node;

        // The map from one of the node's neighbors to the probability it comes up (stored as occurrences)
        private HashMap<Node, OccurrenceProbability> occurrenceProbabilityHashMap = new HashMap<Node, OccurrenceProbability>();

        // The total number of occurrences of the node
        private int totalCount;

        /**
         * The occurrence that a node will come up (from another node)
         */
        public class OccurrenceProbability {
            private int count;

            /**
             * Adds another occurrence
             */
            public void add() {
                count++;
                totalCount++;
            }

            /**
             * Calculates the probability that this node will come up (from the node that this class is nested in)
             * @return The probability as a double
             */
            public double getProbability() {
                if (totalCount == 0) return 0;
                return (double)count / totalCount;
            }

            public String toString() {
                return "" + getProbability();
            }
        }

        /**
         * Creates a new occurrence count from a Node
         * @param node
         */
        public OccurrencesCount(Node node) {
            this.node = node;
        }

        /**
         * Records an occurrence of a node and increments the neighbor node count
         * @param occurringNode The node that is to be added (neighbor)
         */
        public void add(Node occurringNode) {
            OccurrenceProbability occurrenceProbability;
            if (occurrenceProbabilityHashMap.containsKey(occurringNode)) occurrenceProbability = occurrenceProbabilityHashMap.get(occurringNode);
            else occurrenceProbability = new OccurrenceProbability();

            occurrenceProbability.add();

            occurrenceProbabilityHashMap.put(occurringNode, occurrenceProbability);
        }

        public String print(){
            StringBuilder builder = new StringBuilder();
            Set<Node> nodes = occurrenceProbabilityHashMap.keySet();
            int i = 0;
            for(Node n : nodes){
                if(i != 0) builder.append(",");
                builder.append("\n\t\t\"" + n.toString() + "\" : " + occurrenceProbabilityHashMap.get(n));
            }
            return builder.toString();
        }
    }

    /**
     * Stores the map from a Node to the probability of its neighbors occurring
     */
    public class ProbabilityMap {
        // The map itself
        private HashMap<Node, OccurrencesCount.OccurrenceProbability> map;

        /**
         * Creates a new Map from an existing HashMap
         * @param map
         */
        public ProbabilityMap(HashMap<Node, OccurrencesCount.OccurrenceProbability> map) {
            this.map = map;
        }

        /**
         * Gets the map from the ProbabilityMap wrapper
         * @return
         */
        public HashMap<Node, OccurrencesCount.OccurrenceProbability> getMap() {
            return map;
        }

        /**
         * Picks a random node (weighted by probability)
         * @return The randomly picked node
         */
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

    /**
     * Records a transition from one node to another
     * @param from the first node
     * @param to the node that the first node points to
     */
    public void recordTransition(Node from, Node to) {
        OccurrencesCount occurrencesCountFrom;

        if (matrix.containsKey(from)) occurrencesCountFrom = matrix.get(from);
        else occurrencesCountFrom = new OccurrencesCount(from);

        occurrencesCountFrom.add(to);

        matrix.put(from, occurrencesCountFrom);
    }

    /**
     * Returns a probability map from a specific node
     * @param node the node to get the map from
     * @return the probability map from a specific node
     */
    public ProbabilityMap probabilities(Node node) {
        if (!matrix.containsKey(node)) return null;
        return new ProbabilityMap(matrix.get(node).occurrenceProbabilityHashMap);
    }

    /**
     * Converts the matrix to a human readable format (importable with importJson method)
     * @return the json string
     */
    public String toJson() {
        return new Gson().toJson(this, this.getClass());
    }


    //returns a table with all the Nodes, their occurences, and the probability of other Nodes in a JSON format.
    public String getTable(){
        StringBuilder table = new StringBuilder();
        table.append("{");
        Set<Node> nodes = matrix.keySet();
        int i = 0;
        for(Node n : nodes){
            if(i != 0) table.append(",");
            i++;
            table.append("\n\t\"" + n.toString() + "\" : {\n");
            table.append("\t\t\"occurrences\" : " + matrix.get(n).totalCount);
            table.append(",\n\t\t\"probabilities\" : {");
            table.append(matrix.get(n).print());
            table.append("\n\t\t}");
            table.append("\n\t}");
        }
        table.append("\n}");
        return table.toString();
    }
}
