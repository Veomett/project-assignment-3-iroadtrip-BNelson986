import javax.sound.midi.Soundbank;
import java.util.*;

/**
 *  Implements BFS Algorithm using an Adjacency List
 *  to find the best (shortest) path.
 */
public class PathFinder {
    private Dictionary<String, Integer> dist;
    private PriorityQueue<Node> pq;
    private List<List<Node>> adj;
    private static final Countries map = Countries.getInstance();

    public PathFinder(int numVertices){
        dist = new Hashtable<>();
        pq = new PriorityQueue<>();
    }

    public void dijkstra(List<List<Node>> adj, String src){
        //  Use keys from main program, set all values to Infinity
        for(String c : map.countries.keySet()){
            dist.put(c, Integer.MAX_VALUE);
        }
        dist.put(src, 0);

        pq.add(new Node(src, 0));

        while(!pq.isEmpty()){
            Node V = pq.remove();

            List<Country.Neighbor> neighbors = map.countries.get(V.name).getNeighbors();

            for(Country.Neighbor neighbor : neighbors){
                int newDist = dist.get(V) + neighbor.getDistToCap();

                if(newDist < dist.get(neighbor)){
                    dist.put(neighbor.getName(), newDist);
                    pq.add(new Node(neighbor.getName(), newDist));
                }
            }

        }
        System.out.println(dist);
    }

    static class Node implements Comparator<Node>{
        public String name;
        public int dist;

        public Node(){
        }
        public Node(String origin, int dist){
            //  Store lowercase to use as key for dictionary
            this.name = origin.toLowerCase();
            this.dist = dist;
        }
        @Override
        public int compare (Node o1, Node o2) {
            return Integer.compare(o1.dist, o2.dist);
        }
    }
}
