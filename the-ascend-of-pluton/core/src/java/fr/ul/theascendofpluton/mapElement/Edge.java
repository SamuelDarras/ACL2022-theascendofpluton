package fr.ul.theascendofpluton.mapElement;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Edge implements Connection<Node> {

    public Node begin;
    public Node end;

    public Edge(Node begin, Node end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public float getCost() {
        return 1;
    }

    @Override
    public Node getFromNode() {
        return begin;
    }

    @Override
    public Node getToNode() {
        return end;
    }

    public void render(ShapeRenderer shapeRenderer){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rectLine(begin.getX(), begin.getY(), end.getX(), end.getY(), 4);
    }
}
