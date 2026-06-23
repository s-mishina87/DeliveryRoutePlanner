package at.ac.hcw.delivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeliveryRoutePlanner {
    public static PathResult planRoute(Graph graph, String start, List<String> stops) {
        List<String> unvisitedStops = new ArrayList<>(stops);
        List<String> fullPath = new ArrayList<>();
        String current = start;
        int totalDistance = 0;

        fullPath.add(start);

        while (!unvisitedStops.isEmpty()) {
            String nearestStop = null;
            PathResult nearestPath = new PathResult(Collections.emptyList(), 0);

            // Greedy выбирает ближайшую доставку из тех, где мы еще не были.
            for (String stop : unvisitedStops) {
                PathResult pathToStop = Dijkstra.findShortestPath(graph, current, stop);

                if (pathToStop.isFound() && (nearestStop == null || pathToStop.getDistance() < nearestPath.getDistance())) {
                    nearestStop = stop;
                    nearestPath = pathToStop;
                }
            }

            if (nearestStop == null) {
                return new PathResult(Collections.emptyList(), 0);
            }

            List<String> pathPart = nearestPath.getPath();
            fullPath.addAll(pathPart.subList(1, pathPart.size()));
            totalDistance += nearestPath.getDistance();

            current = nearestStop;
            unvisitedStops.remove(nearestStop);
        }

        return new PathResult(fullPath, totalDistance);
    }
}
