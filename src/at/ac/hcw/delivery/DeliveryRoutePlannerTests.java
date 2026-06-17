package at.ac.hcw.delivery;

import java.util.List;
import java.util.Map;

public class DeliveryRoutePlannerTests {
    public static void main(String[] args) {
        testGraph();
        testDijkstra();
        testBfs();
        testGreedy();
        testInvalidInputCases();

        System.out.println("All tests passed.");
    }

    private static void testGraph() {
        System.out.println("Testing graph...");
        Graph graph = createTestGraph();

        assertTrue(graph.containsLocation("Warehouse"), "Graph should contain Warehouse.");
        assertTrue(graph.containsLocation("Bakery"), "Graph should contain Bakery.");

        // Проверяем, что дороги добавились в обе стороны.
        assertEquals(3, graph.getEdgesFrom("Warehouse").size(), "Warehouse should have 3 roads.");
        assertEquals(3, graph.getEdgesFrom("Hospital").size(), "Hospital should have 3 roads.");
    }

    private static void testDijkstra() {
        System.out.println("Testing Dijkstra...");
        Graph graph = createTestGraph();

        PathResult result = Dijkstra.findShortestPath(graph, "Warehouse", "Hospital");

        assertEquals(12, result.getDistance(), "Shortest distance should be 12.");
        assertEquals(
                List.of("Warehouse", "Pharmacy", "Hospital"),
                result.getPath(),
                "Shortest path should go through Pharmacy."
        );
    }

    private static void testBfs() {
        System.out.println("Testing BFS...");
        Graph graph = createTestGraph();

        Map<String, Integer> result = BreadthFirstSearch.findReachableLocations(graph, "Warehouse");

        assertEquals(0, result.get("Warehouse"), "Warehouse should be 0 steps away.");
        assertEquals(1, result.get("Bakery"), "Bakery should be 1 step away.");
        assertEquals(1, result.get("Office"), "Office should be 1 step away.");
        assertEquals(3, result.get("Post Office"), "Post Office should be 3 steps away.");
        assertEquals(9, result.size(), "All 9 locations should be reachable.");
    }

    private static void testGreedy() {
        System.out.println("Testing greedy route...");
        Graph graph = createTestGraph();

        PathResult result = DeliveryRoutePlanner.planRoute(
                graph,
                "Warehouse",
                List.of("Hospital", "School", "Bookstore")
        );

        // Greedy выбирает ближайшую следующую остановку, но не гарантирует идеальный маршрут.
        assertEquals(24, result.getDistance(), "Greedy route distance should be 24 for this example.");
        assertEquals(
                List.of("Warehouse", "Bakery", "Bookstore", "School", "Post Office", "Hospital"),
                result.getPath(),
                "Greedy route should visit the nearest remaining stop each time."
        );
    }

    private static void testInvalidInputCases() {
        System.out.println("Testing invalid input cases...");
        Graph graph = createTestGraph();

        PathResult sameLocation = Dijkstra.findShortestPath(graph, "Warehouse", "Warehouse");
        assertEquals(0, sameLocation.getDistance(), "Distance from a location to itself should be 0.");
        assertEquals(List.of("Warehouse"), sameLocation.getPath(), "Path from a location to itself should contain only this location.");

        PathResult unknownLocation = Dijkstra.findShortestPath(graph, "Unknown", "Hospital");
        assertTrue(!unknownLocation.isFound(), "Path with an unknown start location should not be found.");

        PathResult emptyRoute = DeliveryRoutePlanner.planRoute(graph, "Warehouse", List.of());
        assertEquals(0, emptyRoute.getDistance(), "Route without delivery stops should have distance 0.");
        assertEquals(List.of("Warehouse"), emptyRoute.getPath(), "Route without delivery stops should stay at the start.");

        assertThrows(
                () -> graph.addUndirectedRoad("A", "B", -1),
                "Negative road distance should not be allowed."
        );
    }

    private static Graph createTestGraph() {
        Graph graph = new Graph();

        graph.addUndirectedRoad("Warehouse", "Bakery", 4);
        graph.addUndirectedRoad("Warehouse", "Pharmacy", 7);
        graph.addUndirectedRoad("Warehouse", "Office", 10);
        graph.addUndirectedRoad("Bakery", "Supermarket", 3);
        graph.addUndirectedRoad("Bakery", "Bookstore", 6);
        graph.addUndirectedRoad("Pharmacy", "Hospital", 5);
        graph.addUndirectedRoad("Pharmacy", "Supermarket", 4);
        graph.addUndirectedRoad("Supermarket", "Hospital", 5);
        graph.addUndirectedRoad("Supermarket", "School", 4);
        graph.addUndirectedRoad("Bookstore", "Office", 3);
        graph.addUndirectedRoad("Office", "School", 6);
        graph.addUndirectedRoad("Hospital", "Post Office", 4);
        graph.addUndirectedRoad("School", "Post Office", 2);
        graph.addUndirectedRoad("Bookstore", "School", 8);

        return graph;
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

    private static void assertThrows(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            return;
        }

        throw new AssertionError(message);
    }
}
