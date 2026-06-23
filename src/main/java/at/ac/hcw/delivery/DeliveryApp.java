package at.ac.hcw.delivery;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DeliveryApp {
    private static final Path DEFAULT_GRAPH_PATH = Path.of("data", "city-map.csv");

    private final Graph graph;
    private final Scanner scanner;

    public DeliveryApp() {
        graph = loadCityMap();
        scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showLocations();
                    waitForEnter();
                    break;
                case "2":
                    showRoads();
                    waitForEnter();
                    break;
                case "3":
                    findShortestPath();
                    waitForEnter();
                    break;
                case "4":
                    showReachableLocations();
                    waitForEnter();
                    break;
                case "5":
                    planDeliveryRoute();
                    waitForEnter();
                    break;
                case "6":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Please choose a number from 1 to 6.");
                    waitForEnter();
            }

            System.out.println();
        }
    }

    private void waitForEnter() {
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private void printMenu() {
        System.out.println("=== Delivery Route Planner ===");
        System.out.println("1. Show all locations");
        System.out.println("2. Show all roads");
        System.out.println("3. Find shortest path");
        System.out.println("4. Show reachable locations");
        System.out.println("5. Plan delivery route");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private void showLocations() {
        System.out.println("Locations:");
        for (String location : graph.getLocations()) {
            System.out.println("- " + location);
        }
    }

    private void showRoads() {
        System.out.println("Roads:");
        for (String location : graph.getLocations()) {
            System.out.println(location + " -> " + graph.getEdgesFrom(location));
        }
    }

    private void findShortestPath() {
        String start = askForLocation("Start: ");
        String destination = askForLocation("Destination: ");

        PathResult result = Dijkstra.findShortestPath(graph, start, destination);

        System.out.println("Shortest path:");
        System.out.println(result);
    }

    private void showReachableLocations() {
        String start = askForLocation("Start: ");
        Map<String, Integer> reachableLocations = BreadthFirstSearch.findReachableLocations(graph, start);

        System.out.println("Reachable locations:");
        for (Map.Entry<String, Integer> entry : reachableLocations.entrySet()) {
            System.out.println(entry.getValue() + " step(s): " + entry.getKey());
        }
    }

    private void planDeliveryRoute() {
        String start = askForLocation("Start: ");
        List<String> stops = askForStops();

        if (removeStartFromStops(stops, start)) {
            System.out.println("Start location removed from delivery stops: " + start);
        }

        if (stops.isEmpty()) {
            System.out.println("No valid delivery stops entered.");
            return;
        }

        PathResult result = DeliveryRoutePlanner.planRoute(graph, start, stops);

        System.out.println("Planned delivery route:");
        System.out.println(result);
        System.out.println("This is a greedy route, so it is not always globally optimal.");
    }

    private String askForLocation(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            String location = graph.findLocationIgnoreCase(input);

            if (location != null) {
                return location;
            }

            System.out.println("Unknown location. Please try again.");
            showLocations();
        }
    }

    private List<String> askForStops() {
        List<String> stops = new ArrayList<>();

        System.out.println("Enter delivery stops separated by commas.");
        System.out.print("Stops: ");
        String input = scanner.nextLine();

        for (String part : input.split(",")) {
            String location = graph.findLocationIgnoreCase(part);

            if (location != null && !stops.contains(location)) {
                stops.add(location);
            } else if (location == null && !part.trim().isEmpty()) {
                System.out.println("Unknown delivery stop ignored: " + part.trim());
            }
        }

        return stops;
    }

    static boolean removeStartFromStops(List<String> stops, String start) {
        return stops.remove(start);
    }

    private Graph loadCityMap() {
        try {
            return GraphCsvReader.readFromCsv(DEFAULT_GRAPH_PATH);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read city map from " + DEFAULT_GRAPH_PATH, e);
        }
    }
}
