package at.ac.hcw.delivery;

public class Edge {
    private final String to;
    private final int weight;

    public Edge(String to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    public String getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return to + " (" + weight + ")";
    }
}
