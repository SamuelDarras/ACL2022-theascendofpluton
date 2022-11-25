package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {
    private Body body;
    private BodyDef bodyDef;

    public GameObject(Vector2 coords, World world) {
        bodyDef = new BodyDef();
        bodyDef.position.set(coords);
    }

    public abstract void update(GameWorld w);

    public abstract void render(float delta);

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
