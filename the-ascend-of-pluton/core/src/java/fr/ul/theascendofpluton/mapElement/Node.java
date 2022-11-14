package fr.ul.theascendofpluton.mapElement;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Node {

    private float x;
    private float y;

    public int index;

    public Node(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setIndex(int i){
        this.index = i;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void render(ShapeRenderer shapeRenderer){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.circle(x, y, 5);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.circle(x, y, 5);
        shapeRenderer.end();

    }
}
