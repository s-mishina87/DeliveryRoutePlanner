# DeliveryRoutePlanner

DeliveryRoutePlanner is a console-based Java application for planning delivery routes on a small city map.

The city is represented as a weighted graph. Locations are vertices, roads are edges, and road distances are weights.

## Project Context

The application helps a delivery driver to:

- show all available locations;
- show the road network;
- find the shortest path between two locations;
- show all locations reachable from a start location;
- plan an approximate delivery route through several stops.

## Data Structure

The main data structure is a weighted graph with an adjacency list.

In Java, the graph is stored as:

```java
Map<String, List<Edge>>
```

Where:

- `String` is the location name;
- `List<Edge>` is the list of roads from this location;
- `Edge` stores the target location and the road weight.

This structure is useful because each location directly stores its neighboring locations.

## Algorithms

### 1. Dijkstra's Algorithm

Used to find the shortest path between two locations.

Example:

```text
Start: Warehouse
Destination: Hospital
```

Result:

```text
Warehouse -> Pharmacy -> Hospital
Total distance: 12
```

The implementation uses a simple loop instead of a priority queue. This makes the code easier to understand for a small educational project.

Runtime:

```text
O(V^2 + E)
```

### 2. Breadth-First Search (BFS)

Used to show all locations reachable from a start location.

BFS counts the number of steps, not the weighted distance.

Example:

```text
0 step(s): Warehouse
1 step(s): Bakery
1 step(s): Pharmacy
2 step(s): Supermarket
```

Runtime:

```text
O(V + E)
```

### 3. Greedy Nearest Neighbor

Used to build an approximate delivery route through several stops.

The algorithm starts at the selected location and repeatedly chooses the nearest unvisited delivery stop. It uses Dijkstra to calculate the shortest path to each possible next stop.

This algorithm is fast and easy to understand, but it does not always guarantee the globally optimal route.

Runtime for `k` delivery stops:

```text
O(k^2 * (V^2 + E))
```

At each step, the algorithm compares all remaining delivery stops. For each possible next stop, it calls Dijkstra to find the shortest path from the current location.

## Project Structure

```text
src/at/ac/hcw/delivery
|-- Main.java
|-- DeliveryApp.java
|-- Graph.java
|-- Edge.java
|-- PathResult.java
|-- Dijkstra.java
|-- BreadthFirstSearch.java
|-- DeliveryRoutePlanner.java
`-- DeliveryRoutePlannerTests.java
```

## How To Run

Open the project in IntelliJ IDEA and run:

```text
at.ac.hcw.delivery.Main
```

The application starts a console menu:

```text
1. Show all locations
2. Show all roads
3. Find shortest path
4. Show reachable locations
5. Plan delivery route
6. Exit
```

## How To Run Tests

The project uses a simple Java test class without JUnit.

Run:

```text
at.ac.hcw.delivery.DeliveryRoutePlannerTests
```

Expected output:

```text
Testing graph...
Testing Dijkstra...
Testing BFS...
Testing greedy route...
All tests passed.
```

## Example City Map

```text
Warehouse -- Bakery: 4
Warehouse -- Pharmacy: 7
Warehouse -- Office: 10
Bakery -- Supermarket: 3
Bakery -- Bookstore: 6
Pharmacy -- Hospital: 5
Pharmacy -- Supermarket: 4
Supermarket -- Hospital: 5
Supermarket -- School: 4
Bookstore -- Office: 3
Office -- School: 6
Hospital -- Post Office: 4
School -- Post Office: 2
Bookstore -- School: 8
```

All roads are undirected and have positive weights.

## Course Requirements

This project fulfills the main requirements:

- one data structure: weighted graph with adjacency list;
- three algorithms: Dijkstra, BFS, Greedy Nearest Neighbor;
- meaningful application context: delivery route planning;
- Java implementation;
- console-based user interaction.
