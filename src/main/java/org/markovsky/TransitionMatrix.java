package org.markovsky;

import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sambaumgarten on 3/8/16
 */
public class TransitionMatrix<Node> {
    private String VERSION = "0.1";

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
        if (!matrix.VERSION.equals(new TransitionMatrix().VERSION)) {
            throw new VersionMatchException(matrix.VERSION);
        }
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

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                OccurrenceProbability that = (OccurrenceProbability) o;

                return count == that.count;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OccurrencesCount that = (OccurrencesCount) o;

            if (totalCount != that.totalCount) return false;
            if (!node.equals(that.node)) return false;
            return occurrenceProbabilityHashMap.equals(that.occurrenceProbabilityHashMap);
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

    public String importData(String data) {
        String[] words = data.split(" ");

        for (int i = 0; i < words.length - 1; i++) {
            recordTransition((Node) words[i], (Node) words[i + 1]);
        }

        return words[0];
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

    /**
     * Returns the number of nodes stored
     * @return the number of nodes stored
     */
    public int numberOfNodes() {
        int count = 0;
        LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
        ArrayList<Node> visited = new ArrayList<>();

        for (Node node : matrix.keySet()) {
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            Node node = queue.remove();
            visited.add(node);

            count++;

            if (!matrix.containsKey(node)) continue;

            for (Node child : matrix.get(node).occurrenceProbabilityHashMap.keySet()) {
                if (!visited.contains(child) && !queue.contains(child)) queue.add(child);
            }
        }

        return count;
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

    @Override
    public boolean equals(Object object) {
        if (object instanceof TransitionMatrix) return equals((TransitionMatrix) object);
        else return false;
    }

    private boolean equals(TransitionMatrix matrix) {
        return matrix.matrix.equals(this.matrix);
    }
}
