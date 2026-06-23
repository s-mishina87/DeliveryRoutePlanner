package at.ac.hcw.delivery;

public class Edge {
    private final String to; //final значит: после создания объекта эти значения уже не меняются
    private final int weight;

    public Edge(String to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    public String getTo() {
        return to;
    } //куда ведёт дорога, нужен другим классам, например Dijkstra и BFS, чтобы узнать соседнюю локацию.

    public int getWeight() {
        return weight;
    }//расстояние дороги Дейкстра считает общий вес пути

    @Override
    public String toString() {
        return to + " (" + weight + ")";
    } //красивый вывод дороги в консоль
}
