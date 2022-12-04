package fr.ul.theascendofpluton.helper;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import fr.ul.theascendofpluton.mapElement.Edge;
import fr.ul.theascendofpluton.mapElement.Node;

import java.util.HashMap;
import java.util.Map;

public class MapGraph implements IndexedGraph<Node> {

    private MapHeuristic mapHeuristic = new MapHeuristic();
    public Map<String, Node> nodes = new HashMap<>();
    public Array<Edge> edges = new Array<>();

    /** Map of Cities to Streets starting in that Node. */
    ObjectMap<Node, Array<Connection<Node>>> edgesMap = new ObjectMap<>();

    private int lastNodeIndex = 0;

    public void addNode(String name, Node node){
        node.index = lastNodeIndex;
        lastNodeIndex++;

        nodes.put(name, node);
    }

    public void connectNodes(Node fromNode, Node toNode){
        Edge edge = new Edge(fromNode, toNode);
        if(!edgesMap.containsKey(fromNode)){
            edgesMap.put(fromNode, new Array<Connection<Node>>());
        }
        edgesMap.get(fromNode).add(edge);
        edges.add(edge);
    }

    public GraphPath<Node> findPath(Node startNode, Node goalNode){
        GraphPath<Node> nodePath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startNode, goalNode, mapHeuristic, nodePath);
        return nodePath;
    }

    @Override
    public int getIndex(Node node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return lastNodeIndex;
    }

    @Override
    public Array<Connection<Node>> getConnections(Node fromNode) {
        if(edgesMap.containsKey(fromNode)){
            return edgesMap.get(fromNode);
        }

        return new Array<>(0);
    }
}

