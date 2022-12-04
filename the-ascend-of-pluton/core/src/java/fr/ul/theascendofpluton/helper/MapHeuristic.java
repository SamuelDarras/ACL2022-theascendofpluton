package fr.ul.theascendofpluton.helper;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import fr.ul.theascendofpluton.mapElement.Node;

public class MapHeuristic  implements Heuristic<Node> {

    @Override
    public float estimate(Node node, Node endNode) {
        return Vector2.dst(node.getX(), node.getY(), endNode.getX(), endNode.getY());
    }
}