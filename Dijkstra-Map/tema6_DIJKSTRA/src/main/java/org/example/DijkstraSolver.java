package org.example;
import java.util.*;

public class DijkstraSolver {

    public static List<Node> solve(Node start, Node end, GraphMap map) {
        Map<Integer, List<Edge>> adj = map.getAdjacencyList();
        Map<Integer, Node> nodes = map.getNodes();

        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
        Map<Integer, Integer> distances = new HashMap<>(); //pt drum min
        Map<Integer, Integer> parents = new HashMap<>(); //reconstruire drum
        Set<Integer> visited = new HashSet<>();//noduri procesate deja

        distances.put(start.getId(), 0);
        pq.add(new NodeDistance(start.getId(), 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            int u = current.nodeId;

            if (u == end.getId()) break; //ajuns la destinatie
            if (visited.contains(u)) continue; //nod deja vizitat
            visited.add(u);

            //verificare vecini
            if (adj.containsKey(u)) {
                for (Edge e : adj.get(u)) {
                    int v = e.getTargetNodeId();
                    int newDist = distances.get(u) + e.getLength();

                    if (!distances.containsKey(v) || newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        parents.put(v, u);
                        pq.add(new NodeDistance(v, newDist));
                    }
                }
            }
        }

        List<Node> path = new ArrayList<>();
        Integer curr = end.getId();
        if (parents.containsKey(curr) || curr == start.getId()) {
            while (curr != null) {
                path.add(0, nodes.get(curr));
                curr = parents.get(curr);
            }
        }

        if (!path.isEmpty()) {
            int totalDist = distances.getOrDefault(end.getId(), -1);
            System.out.println("Lungime drum: " + totalDist + " unități");
            System.out.println("Număr noduri pe drum: " + path.size());
        } else {
            System.out.println("Nu există drum între nodurile selectate!");
        }

        return path;
    }

    private static class NodeDistance {
        int nodeId;
        int distance; //distanta pana la nod
        public NodeDistance(int nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }
}