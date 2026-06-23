package at.ac.hcw.delivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    // Adjacency list: для каждой локации хранится список дорог к соседям.
    private final Map<String, List<Edge>> adjacencyList = new LinkedHashMap<>();

    public void addLocation(String location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    public void addUndirectedRoad(String firstLocation, String secondLocation, int distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Road distance must not be negative.");
        }

        // дорога добавляется два раза - по ней можно ехать в обе стороны
        addRoad(firstLocation, secondLocation, distance);
        addRoad(secondLocation, firstLocation, distance);
    }

    public Set<String> getLocations() {
        return adjacencyList.keySet();
    }

    public List<Edge> getEdgesFrom(String location) {
        return adjacencyList.getOrDefault(location, Collections.emptyList());
    }

    public boolean containsLocation(String location) {
        return adjacencyList.containsKey(location);
    }

    public String findLocationIgnoreCase(String input) {
        for (String location : adjacencyList.keySet()) {
            if (location.equalsIgnoreCase(input.trim())) {
                return location;
            }
        }
        return null;
    }

    private void addRoad(String from, String to, int distance) {
        addLocation(from);
        addLocation(to);
        adjacencyList.get(from).add(new Edge(to, distance));
    }
}
