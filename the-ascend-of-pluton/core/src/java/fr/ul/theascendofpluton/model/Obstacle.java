package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Elements du monde empêchant le joueur de sortir du niveau.
 */
public class Obstacle {
    public BodyDef bodyDef;
    public Body body;


    public Obstacle(World world, Vector2 coords, float[] vertices){
        bodyDef  = new BodyDef();
        bodyDef.position.set(coords);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);

        fixtureDef.shape = shape;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef).setUserData("obstacle");
        body.setUserData(this);
        shape.dispose();
    }

}