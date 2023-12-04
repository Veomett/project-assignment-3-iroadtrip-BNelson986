import java.util.*;

/**
 * Implements BFS Algorithm using an Adjacency List
 * to find the best (shortest) path.
 */
public class PathFinder {
    private final Countries map = Countries.getInstance();
    private final Set<String> visited;
    private final Dictionary<String, Integer> distance;
    private final Dictionary<String, String> parent;
    private final PriorityQueue<Node> nodes;

    /**
     *  Initializes necessary structures to calculate and store the best path
     */
    PathFinder () {
        nodes = new PriorityQueue<>();
        visited = new HashSet<>();
        distance = new Hashtable<>();
        parent = new Hashtable<>();
    }

    /**
     *  Uses Dijkstra's Algorithm to find the shortest path between start and end
     * @param start Originating country
     * @param end   Destination Country
     * @return  List of all countries that were jumped to, in order
     */
    public List<String> dijkstra (String start, String end) {
        nodes.add(new Node(start, 0));
        for (Country temp : map.countries.values()) {
            distance.put(temp.getName(), Integer.MAX_VALUE);
        }
        distance.put(start, 0);

        while (!nodes.isEmpty()) {
            Node curr = nodes.remove();

            visited.add(curr.name);

            //  Path found
            if (curr.name.equalsIgnoreCase(end)) {
                break;
            }

            List<Country.Neighbor> neighborList = map.findCountry(curr.name.toLowerCase()).getNeighbors();

            for (Country.Neighbor neighbor : neighborList) {
                if (!visited.contains(neighbor.getName())) {
                    int totalDist = distance.get(curr.name) + neighbor.getDistToCap();

                    if (totalDist < distance.get(neighbor.getName())) {
                        distance.put(neighbor.getName(), totalDist);
                        parent.put(neighbor.getName(), curr.name);
                        nodes.add(new Node(neighbor.getName(), totalDist));
                    }
                }
            }
        }
        List<String> bestPath = new ArrayList<>();
        String current = end;

        while (current != null) {
            bestPath.add(0, current);
            current = parent.get(current);
        }
        return bestPath;
    }

    /**
     *  Node object to store information about each jump made and its cost
     */
    private static class Node implements Comparable<Node> {
        private final int distFromSource;
        private final String name;

        /**
         *  Creates new Node object with source and total dist from source
         * @param source Country Name
         * @param distFromSource Distance in km from start to source
         */
        Node (String source, int distFromSource) {
            name = source;
            this.distFromSource = distFromSource;
        }

        /**
         *  Custom comparator to order Priority Queue by distFromSource
         * @param o the object to be compared.
         * @return -1, 0, 1 corresponding to this < o, this == o, and this > o (Respectively)
         */
        @Override
        public int compareTo (Node o) {
            int compOutcome = Integer.compare(this.distFromSource, o.distFromSource);
            if (compOutcome == 1 || compOutcome == -1) {
                return compOutcome;
            }
            return 0;
        }
    }
}
