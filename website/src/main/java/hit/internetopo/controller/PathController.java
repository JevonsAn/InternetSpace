package hit.internetopo.controller;

import hit.internetopo.domain.IPPath;
import hit.internetopo.model.PathsAnalyze;
import org.neo4j.driver.Driver;
import org.neo4j.springframework.data.core.Neo4jClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hit.internetopo.model.Neo4jFunc;

import java.util.Collection;

@RestController
public class PathController {

    private final Neo4jClient client;

    public PathController(Driver driver) {
        this.client = Neo4jClient.create(driver);
    }

    @GetMapping(path = "/nodeCount")
    public Integer getNodeCount() {
        return Neo4jFunc.getNodeCount(client, "merge_node_table");
    }

    @GetMapping(path = "/paths")
    public PathsAnalyze getPaths(
            @RequestParam(value="sip") String startIP,
            @RequestParam(value="dip") String endIP,
            @RequestParam(value="label") String label
    ) {
        Collection<IPPath> paths = Neo4jFunc.getShortestPath(client, startIP, endIP,label + "_node_table", label + "_edge_table");
        PathsAnalyze pa = new PathsAnalyze(paths);
        return pa;
    }
}