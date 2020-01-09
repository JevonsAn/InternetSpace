package hit.internetopo.model;

import hit.internetopo.domain.Edge;
import hit.internetopo.domain.IPNode;
import hit.internetopo.domain.IPPath;
import org.jetbrains.annotations.NotNull;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.TypeSystem;
import org.neo4j.springframework.data.core.Neo4jClient;

import java.util.*;

public class Neo4jFunc {
    private static final String nodeCountTemplate = "MATCH (n: %s) RETURN count(n) as c";
    private static final String shortestPathTemplate = "MATCH (n: %s {ip: $sip}), (m: %s {ip: $dip}), " +
            "p=ALLSHORTESTPATHS((n)-[e:%s *..20]-(m)) return nodes(p) as ns, relationships(p) as rs";

    static public int getNodeCount(@NotNull Neo4jClient client, String nodeLabel){
        Map<String, Object> defaultNum = new HashMap<>();
        defaultNum.put("c", 0);
        Map<String, Object> result = client.query(String.format(nodeCountTemplate, nodeLabel))
                .fetch().one().orElse(defaultNum);
        int count = Integer.parseInt(result.get("c").toString());
        return count;
    }

    static public Collection<IPPath> getShortestPath(@NotNull Neo4jClient client, String startIP, String endIP, String nodeLabel, String edgeLabel){
        Collection<IPPath> result = client.query(
                String.format(shortestPathTemplate, nodeLabel, nodeLabel, edgeLabel))
                .bind(startIP).to("sip")
                .bind(endIP).to("dip")
                .fetchAs(IPPath.class).mappedBy((TypeSystem t, Record record) -> {
                    List<IPNode> nodes = record.get("ns")
                            .asList(v -> new IPNode
                                    (v.get("ip").asString(), v.get("asn").asInt(), v.get("is_dest").asString()));
                    List<Edge> edges = record.get("rs")
                            .asList(v -> new Edge(v.get("is_dest").asString(), v.get("monitor").asString(),
                                    v.get("delay").asDouble(), v.get("star").asInt()));
//                    List<IPNode> nodes = record.get("ns")
//                            .asList(v -> new IPNode
//                                    (v.get("ip").asString(), 0, ""));
//                    List<Edge> edges = record.get("rs")
//                            .asList(v -> new Edge("", "", 0.1, 0));
                    return new IPPath(nodes, edges);
                })
                .all();
        return result;
    }
}
