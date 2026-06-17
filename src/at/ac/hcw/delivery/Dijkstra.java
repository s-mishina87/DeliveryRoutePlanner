package at.ac.hcw.delivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijkstra {
    private static final int INFINITY = Integer.MAX_VALUE;

    public static PathResult findShortestPath(Graph graph, String start, String destination) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previousLocations = new HashMap<>();
        Set<String> visited = new HashSet<>();

        // В начале все расстояния неизвестны, поэтому ставим "бесконечность".
        for (String location : graph.getLocations()) {
            distances.put(location, INFINITY);
        }
        distances.put(start, 0);

        while (visited.size() < graph.getLocations().size()) {
            // В простой версии Dijkstra мы каждый раз ищем ближайшую локацию обычным циклом.
            String current = findNearestUnvisitedLocation(distances, visited);

            if (current == null) {
                break;
            }

            if (current.equals(destination)) {
                break;
            }

            visited.add(current);

            for (Edge edge : graph.getEdgesFrom(current)) {
                String neighbor = edge.getTo();

                if (visited.contains(neighbor)) {
                    continue;
                }

                int newDistance = distances.get(current) + edge.getWeight();

                // Если через current путь короче, запоминаем новую дистанцию и предыдущую локацию.
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousLocations.put(neighbor, current);
                }
            }
        }

        if (distances.get(destination) == INFINITY) {
            return new PathResult(Collections.emptyList(), 0);
        }

        return new PathResult(buildPath(previousLocations, start, destination), distances.get(destination));
    }

    private static String findNearestUnvisitedLocation(Map<String, Integer> distances, Set<String> visited) {
        String nearestLocation = null;
        int nearestDistance = INFINITY;

        for (String location : distances.keySet()) {
            int distance = distances.get(location);

            if (!visited.contains(location) && distance < nearestDistance) {
                nearestLocation = location;
                nearestDistance = distance;
            }
        }

        return nearestLocation;
    }

    private static List<String> buildPath(Map<String, String> previousLocations, String start, String destination) {
        List<String> path = new ArrayList<>();
        String current = destination;

        // Идем назад от destination к start, потом разворачиваем список.
        while (current != null) {
            path.add(current);

            if (current.equals(start)) {
                break;
            }

            current = previousLocations.get(current);
        }

        Collections.reverse(path);
        return path;
    }
}
