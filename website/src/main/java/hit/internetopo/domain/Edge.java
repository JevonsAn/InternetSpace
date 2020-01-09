package hit.internetopo.domain;

public class Edge {
    private String isDest;
    private String monitor;
    private float delay;
    private int Star;

    public Edge(String isDest, String monitor, double delay, int star) {
        this.isDest = isDest;
        this.monitor = monitor;
        this.delay = (float) delay;
        this.Star = star;
    }

    public String getIsDest() {
        return isDest;
    }

    public String getMonitor() {
        return monitor;
    }

    public float getDelay() {
        return delay;
    }

    public int getStar() {
        return Star;
    }
}
