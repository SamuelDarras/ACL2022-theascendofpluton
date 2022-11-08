package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.physics.box2d.*;

public class Obstacle {
    public BodyDef bodyDef;
    public Body body;

    //x,y postion de l'ennemi dans le monde
    public Obstacle(World world, int x, int y){
        bodyDef  = new BodyDef();
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f,1f);

        fixtureDef.shape = shape;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

}
