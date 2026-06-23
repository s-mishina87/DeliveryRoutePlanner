package at.ac.hcw.delivery;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DeliveryRoutePlannerTests {
    private static final Path CITY_MAP_PATH = Path.of("data", "city-map.csv");
    private static final Path DISCONNECTED_MAP_PATH = Path.of("data", "disconnected-map.csv");
    private static final Path INVALID_MAP_PATH = Path.of("data", "invalid-map.csv");

    public static void main(String[] args) throws IOException {
        testGraphCsvReaderLoadsLocations();
        testGraphCsvReaderLoadsUndirectedRoads();
        testGraphCsvReaderRejectsInvalidDistance();
        testGraphFindLocationIgnoreCase();
        testDijkstraFindsShortestPath();
        testDijkstraHandlesSameStartAndDestination();
        testDijkstraHandlesUnknownLocation();
        testDijkstraHandlesUnreachableDestination();
        testBfsCountsStepsFromStart();
        testBfsDoesNotCrossDisconnectedComponents();
        testGreedyRouteVisitsNearestRemainingStops();
        testGreedyRouteHandlesNoStops();
        testGreedyRouteHandlesUnreachableStop();

        System.out.println("All tests passed.");
    }

    private static void testGraphCsvReaderLoadsLocations() throws IOException {
        System.out.println("Testing CSV reader loads locations...");
        Graph graph = loadCityMap();
        assertTrue(graph.containsLocation("Warehouse"), "Graph should contain Warehouse.");
        assertTrue(graph.containsLocation("Bakery"), "Graph should contain Bakery.");
        assertEquals(9, graph.getLocations().size(), "City map should contain 9 locations.");
    }

    private static void testGraphCsvReaderLoadsUndirectedRoads() throws IOException {
        System.out.println("Testing CSV reader loads undirected roads...");
        Graph graph = loadCityMap();

        assertTrue(hasEdge(graph, "Warehouse", "Bakery", 4), "Warehouse should have a road to Bakery.");
        assertTrue(hasEdge(graph, "Bakery", "Warehouse", 4), "Bakery should have a road back to Warehouse.");
    }

    private static void testGraphCsvReaderRejectsInvalidDistance() {
        System.out.println("Testing CSV reader rejects invalid distances...");
        assertThrows(
                () -> GraphCsvReader.readFromCsv(INVALID_MAP_PATH),
                "Negative road distance from CSV should not be allowed."
        );
    }

    private static void testGraphFindLocationIgnoreCase() throws IOException {
        System.out.println("Testing graph location lookup...");
        Graph graph = loadCityMap();
        assertEquals("Post Office", graph.findLocationIgnoreCase(" post office "), "Lookup should ignore case and spaces.");
    }

    private static void testDijkstraFindsShortestPath() throws IOException {
        System.out.println("Testing Dijkstra shortest path...");
        Graph graph = loadCityMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Hospital");

        assertEquals(12, result.getDistance(), "Shortest distance should be 12.");
        assertEquals(
                List.of("Warehouse", "Pharmacy", "Hospital"),
                result.getPath(),
                "Shortest path should go through Pharmacy."
        );
    }

    private static void testDijkstraHandlesSameStartAndDestination() throws IOException {
        System.out.println("Testing Dijkstra same start and destination...");
        Graph graph = loadCityMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Warehouse");

        assertEquals(0, result.getDistance(), "Distance from a location to itself should be 0.");
        assertEquals(List.of("Warehouse"), result.getPath(), "Path from a location to itself should contain only this location.");
    }

    private static void testDijkstraHandlesUnknownLocation() throws IOException {
        System.out.println("Testing Dijkstra unknown location...");
        Graph graph = loadCityMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Unknown", "Hospital");

        assertTrue(!result.isFound(), "Path with an unknown start location should not be found.");
    }

    private static void testDijkstraHandlesUnreachableDestination() throws IOException {
        System.out.println("Testing Dijkstra unreachable destination...");
        Graph graph = loadDisconnectedMap();
        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Museum");

        assertTrue(!result.isFound(), "Path to a disconnected destination should not be found.");
    }

    private static void testBfsCountsStepsFromStart() throws IOException {
        System.out.println("Testing BFS step counting...");
        Graph graph = loadCityMap();
        Map<String, Integer> result = BreadthFirstSearch.findReachableLocations(graph, "Warehouse");

        assertEquals(0, result.get("Warehouse"), "Warehouse should be 0 steps away.");
        assertEquals(1, result.get("Bakery"), "Bakery should be 1 step away.");
        assertEquals(1, result.get("Office"), "Office should be 1 step away.");
        assertEquals(3, result.get("Post Office"), "Post Office should be 3 steps away.");
        assertEquals(9, result.size(), "All 9 locations should be reachable.");
    }

    private static void testBfsDoesNotCrossDisconnectedComponents() throws IOException {
        System.out.println("Testing BFS disconnected components...");
        Graph graph = loadDisconnectedMap();
        Map<String, Integer> result = BreadthFirstSearch.findReachableLocations(graph, "Warehouse");

        assertTrue(!result.containsKey("Museum"), "BFS should not reach locations in another component.");
    }

    private static void testGreedyRouteVisitsNearestRemainingStops() throws IOException {
        System.out.println("Testing greedy route...");
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

    private static void testGreedyRouteHandlesNoStops() throws IOException {
        System.out.println("Testing greedy route without stops...");
        Graph graph = loadCityMap();
        PathResult result = DeliveryRoutePlanner.planRoute(graph, "Warehouse", List.of());

        assertEquals(0, result.getDistance(), "Route without delivery stops should have distance 0.");
        assertEquals(List.of("Warehouse"), result.getPath(), "Route without delivery stops should stay at the start.");
    }

    private static void testGreedyRouteHandlesUnreachableStop() throws IOException {
        System.out.println("Testing greedy route with unreachable stop...");
        Graph graph = loadDisconnectedMap();
        PathResult result = DeliveryRoutePlanner.planRoute(graph, "Warehouse", List.of("Museum"));

        assertTrue(!result.isFound(), "Route with an unreachable stop should not be found.");
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

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    private static void assertThrows(ThrowingRunnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            return;
        } catch (Exception e) {
            throw new AssertionError(message + " Unexpected exception: " + e);
        }

        throw new AssertionError(message);
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
