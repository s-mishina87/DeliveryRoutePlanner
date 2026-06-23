package at.ac.hcw.delivery;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BreadthFirstSearch {
    public static Map<String, Integer> findReachableLocations(Graph graph, String start) {
        Map<String, Integer> stepsFromStart = new LinkedHashMap<>();
        Queue<String> queue = new LinkedList<>();

        // BFS идет слоями: сначала старт, потом соседи, потом соседи соседей.
        stepsFromStart.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (Edge edge : graph.getEdgesFrom(current)) {
                String neighbor = edge.getTo();

                if (!stepsFromStart.containsKey(neighbor)) {
                    stepsFromStart.put(neighbor, stepsFromStart.get(current) + 1);
                    queue.add(neighbor);
                }
            }
        }

        return stepsFromStart;
    }
}
