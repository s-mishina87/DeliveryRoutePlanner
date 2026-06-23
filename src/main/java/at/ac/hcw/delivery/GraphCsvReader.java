package at.ac.hcw.delivery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class GraphCsvReader {
    private GraphCsvReader() {
    }

    public static Graph readFromCsv(Path path) throws IOException {
        Graph graph = new Graph();
        List<String> lines = Files.readAllLines(path);

        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber).trim();

            if (line.isEmpty() || line.startsWith("#") || isHeader(lineNumber, line)) {
                continue;
            }

            String[] parts = line.split(",", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid CSV row at line " + (lineNumber + 1) + ": " + line);
            }

            String from = parts[0].trim();
            String to = parts[1].trim();
            String distanceText = parts[2].trim();

            if (from.isEmpty() || to.isEmpty() || distanceText.isEmpty()) {
                throw new IllegalArgumentException("CSV row has empty values at line " + (lineNumber + 1) + ": " + line);
            }

            int distance = parseDistance(distanceText, lineNumber + 1);
            graph.addUndirectedRoad(from, to, distance);
        }

        return graph;
    }

    private static boolean isHeader(int lineNumber, String line) {
        return lineNumber == 0 && line.equalsIgnoreCase("from,to,distance");
    }

    private static int parseDistance(String distanceText, int lineNumber) {
        try {
            return Integer.parseInt(distanceText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid distance at line " + lineNumber + ": " + distanceText);
        }
    }
}
