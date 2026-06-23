package at.ac.hcw.delivery;

import java.util.List;

public class PathResult {
    private final List<String> path;
    private final int distance;

    public PathResult(List<String> path, int distance) {
        this.path = path;
        this.distance = distance;
    }

    public List<String> getPath() {
        return path;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isFound() {
        return !path.isEmpty();
    }

    @Override
    public String toString() {
        if (!isFound()) {
            return "No path found.";
        }

        return String.join(" -> ", path) + "\nTotal distance: " + distance;
    }
}
