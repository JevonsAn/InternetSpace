package hit.internetopo.domain;

import java.util.List;

public class IPPath {
    private List<IPNode> nodes;
    private List<Edge> edges;

    public IPPath(List<IPNode> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<IPNode> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
