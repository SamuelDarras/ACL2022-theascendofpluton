package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import fr.ul.theascendofpluton.view.GameView;

import java.util.Map;
import java.util.Set;

public class Apple {
    private final float heal;
    private boolean used = false;
    private final BodyDef bodyDef;
    private final Body body;
    private GameView gv;
    private World world;

    public Apple(World world, float x, float y, float[] verticies, float heal, GameView gv){
        bodyDef  = new BodyDef();
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        PolygonShape shape = new PolygonShape();
        shape.set(verticies);

        fixtureDef.shape = shape;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef).setUserData("apple");
        body.setUserData(this);
        shape.dispose();

        this.world = world;
        this.heal = heal;
        this.gv = gv;
    }

    public void dispose() {
        gv.setToDestroy2(this);
        world.destroyBody(body);
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getHeal() {
        used = true;
        return heal;
    }

    public void update() {
        if(used){
            dispose();
        }
    }
}
