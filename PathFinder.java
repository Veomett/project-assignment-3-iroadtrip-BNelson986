import java.util.*;

/**
 *  Implements BFS Algorithm using an Adjacency List
 *  to find the best (shortest) path.
 */
public class PathFinder {
    PriorityQueue<Node> nodes;
    Set<String> visited;
    Dictionary<String, Integer> distance;
    Dictionary<String, String> parent;
    List<String> jumps;
    final Countries map = Countries.getInstance();
    PathFinder(){
        nodes = new PriorityQueue<>();
        visited = new HashSet<>();
        distance = new Hashtable<>();
        parent = new Hashtable<>();
        jumps = new ArrayList<>();
    }

    List<String> dijkstra(String start, String end){
        nodes.add(new Node(start, 0));
        for(Country temp : map.countries.values()){
            distance.put(temp.getName(), Integer.MAX_VALUE);
        }
        distance.put(start, 0);

        while(!nodes.isEmpty()) {
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
                        String jump = "*\t" + curr.name + " --> " + neighbor.getName() + " (" + neighbor.getDistToCap() + " km.)";
                        distance.put(neighbor.getName(), totalDist);
                        parent.put(neighbor.getName(), curr.name);
                        nodes.add(new Node(neighbor.getName(), totalDist));
                    }
                }
            }
        }
        List<String> bestPath = new ArrayList<>();
        String current = end;

        while(current != null){
            bestPath.add(0, current);
            current = parent.get(current);
        }
        return bestPath;
    }

    private static class Node implements Comparable<Node>{
        int distFromSource;
        String name;

        Node(String source, int distFromSource) {
            name = source;
            this.distFromSource = distFromSource;
        }

        @Override
        public int compareTo (Node o) {
            int compOutcome = Integer.compare(this.distFromSource, o.distFromSource);
            if(compOutcome == 1 || compOutcome == -1){
                return compOutcome;
            }
            return 0;
        }
    }
}
