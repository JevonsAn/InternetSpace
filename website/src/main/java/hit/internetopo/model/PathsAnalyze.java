package hit.internetopo.model;

import hit.internetopo.domain.IPNode;
import hit.internetopo.domain.IPPath;

import java.lang.reflect.Array;
import java.util.*;

public class PathsAnalyze {
    private Collection<IPPath> paths;
    private Collection<List<Integer>> abstractPaths;
    private Collection<List<Integer>> asPaths;
    private Map<String, Integer> nodeMap;
    private Collection<IPNode> nodes;

    public PathsAnalyze(Collection<IPPath> paths) {
        this.paths = paths;
        nodeMap = new HashMap<>();
        nodes = new ArrayList<>();
        abstractPaths = new ArrayList<>();
        asPaths = new ArrayList<>();
        Set<Collection<Integer>> asPathSet = new HashSet<>();
        for (IPPath path: paths){
            List<Integer> atp = new ArrayList<>();
            List<Integer> asp = new ArrayList<>();
            for (IPNode node: path.getNodes()){
                int isExist = nodeMap.getOrDefault(node.getIp(), -1);
                if (isExist == -1){ // 不存在
                    nodeMap.put(node.getIp(), nodes.size());
                    atp.add(nodes.size());
                    nodes.add(node);
                }else {
                    atp.add(nodeMap.get(node.getIp()));
                }
                asp.add(node.getAsn());
            }
            abstractPaths.add(atp);
            if (!asPathSet.contains(asp)){
                asPathSet.add(asp);
                asPaths.add(asp);
            }
        }
    }

    public Collection<IPPath> getPaths() {
        return paths;
    }

    public Collection<List<Integer>> getAbstractPaths() {
        return abstractPaths;
    }

    public Collection<List<Integer>> getAsPaths() {
        return asPaths;
    }

    public Map<String, Integer> getNodeMap() {
        return nodeMap;
    }

    public Collection<IPNode> getNodes() {
        return nodes;
    }
}
