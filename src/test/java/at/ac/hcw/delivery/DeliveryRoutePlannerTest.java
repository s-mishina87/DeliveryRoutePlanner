package at.ac.hcw.delivery;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryRoutePlannerTest {
    private static final Path CITY_MAP_PATH = Path.of("data", "city-map.csv");
    private static final Path DISCONNECTED_MAP_PATH = Path.of("data", "disconnected-map.csv");
    private static final Path INVALID_MAP_PATH = Path.of("data", "invalid-map.csv");

    @Test
    void graphCsvReaderLoadsLocations() throws IOException {
        Graph graph = loadCityMap();

        assertTrue(graph.containsLocation("Warehouse"), "Graph should contain Warehouse.");
        assertTrue(graph.containsLocation("Bakery"), "Graph should contain Bakery.");
        assertEquals(9, graph.getLocations().size(), "City map should contain 9 locations.");
    }

    @Test
    void graphCsvReaderLoadsUndirectedRoads() throws IOException {
        Graph graph = loadCityMap();

        assertTrue(hasEdge(graph, "Warehouse", "Bakery", 4), "Warehouse should have a road to Bakery.");
        assertTrue(hasEdge(graph, "Bakery", "Warehouse", 4), "Bakery should have a road back to Warehouse.");
    }

    @Test
    void graphCsvReaderRejectsInvalidDistance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> GraphCsvReader.readFromCsv(INVALID_MAP_PATH),
                "Negative road distance from CSV should not be allowed."
        );
    }

    @Test
    void graphFindLocationIgnoreCase() throws IOException {
        Graph graph = loadCityMap();

        assertEquals("Post Office", graph.findLocationIgnoreCase(" post office "), "Lookup should ignore case and spaces.");
    }

    @Test
    void dijkstraFindsShortestPath() throws IOException {
        Graph graph = loadCityMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Hospital");

        assertEquals(12, result.getDistance(), "Shortest distance should be 12.");
        assertEquals(
                List.of("Warehouse", "Pharmacy", "Hospital"),
                result.getPath(),
                "Shortest path should go through Pharmacy."
        );
    }

    @Test
    void dijkstraHandlesSameStartAndDestination() throws IOException {
        Graph graph = loadCityMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Warehouse");

        assertEquals(0, result.getDistance(), "Distance from a location to itself should be 0.");
        assertEquals(List.of("Warehouse"), result.getPath(), "Path from a location to itself should contain only this location.");
    }

    @Test
    void dijkstraHandlesUnknownLocation() throws IOException {
        Graph graph = loadCityMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Unknown", "Hospital");

        assertFalse(result.isFound(), "Path with an unknown start location should not be found.");
    }

    @Test
    void dijkstraHandlesUnreachableDestination() throws IOException {
        Graph graph = loadDisconnectedMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Museum");

        assertFalse(result.isFound(), "Path to a disconnected destination should not be found.");
    }

    @Test
    void bfsCountsStepsFromStart() throws IOException {
        Graph graph = loadCityMap();
        Map<String, Integer> result = BreadthFirstSearch.findReachableLocations(graph, "Warehouse");

        assertEquals(0, result.get("Warehouse"), "Warehouse should be 0 steps away.");
        assertEquals(1, result.get("Bakery"), "Bakery should be 1 step away.");
        assertEquals(1, result.get("Office"), "Office should be 1 step away.");
        assertEquals(3, result.get("Post Office"), "Post Office should be 3 steps away.");
        assertEquals(9, result.size(), "All 9 locations should be reachable.");
    }

    @Test
    void bfsDoesNotCrossDisconnectedComponents() throws IOException {
        Graph graph = loadDisconnectedMap();
        Map<String, Integer> result = BreadthFirstSearch.findReachableLocations(graph, "Warehouse");

        assertFalse(result.containsKey("Museum"), "BFS should not reach locations in another component.");
    }

    @Test
    void greedyRouteVisitsNearestRemainingStops() throws IOException {
        Graph graph = loadCityMap();
        PathResult result = DeliveryRoutePlanner.planRoute(
                graph,
                "Warehouse",
                List.of("Hospital", "School", "Bookstore")
        );

        assertEquals(24, result.getDistance(), "Greedy route distance should be 24 for this example.");
        assertEquals(
                List.of("Warehouse", "Bakery", "Bookstore", "School", "Post Office", "Hospital"),
                result.getPath(),
                "Greedy route should visit the nearest remaining stop each time."
        );
    }

    @Test
    void greedyRouteHandlesNoStops() throws IOException {
        Graph graph = loadCityMap();
        PathResult result = DeliveryRoutePlanner.planRoute(graph, "Warehouse", List.of());

        assertEquals(0, result.getDistance(), "Route without delivery stops should have distance 0.");
        assertEquals(List.of("Warehouse"), result.getPath(), "Route without delivery stops should stay at the start.");
    }

    @Test
    void greedyRouteHandlesUnreachableStop() throws IOException {
        Graph graph = loadDisconnectedMap();
        PathResult result = DeliveryRoutePlanner.planRoute(graph, "Warehouse", List.of("Museum"));

        assertFalse(result.isFound(), "Route with an unreachable stop should not be found.");
    }

    private static Graph loadCityMap() throws IOException {
        return GraphCsvReader.readFromCsv(CITY_MAP_PATH);
    }

    private static Graph loadDisconnectedMap() throws IOException {
        return GraphCsvReader.readFromCsv(DISCONNECTED_MAP_PATH);
    }

    private static boolean hasEdge(Graph graph, String from, String to, int weight) {
        for (Edge edge : graph.getEdgesFrom(from)) {
            if (edge.getTo().equals(to) && edge.getWeight() == weight) {
                return true;
            }
        }

        return false;
    }
}
