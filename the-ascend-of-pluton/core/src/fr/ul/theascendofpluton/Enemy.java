package fr.ul.theascendofpluton;

import com.badlogic.gdx.physics.box2d.*;

public class Enemy {
    public BodyDef bodyDef;
    public Body body;

    //x,y postion de l'ennemi dans le monde
    public Enemy(World world, int x, int y){
        bodyDef  = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(.5f,.5f);

        fixtureDef.shape = shape;
        fixtureDef.density = .5f;
        fixtureDef.restitution = .1f;
        fixtureDef.friction = .5f;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    //x,y coordonnées à atteindre
    public void update(float x, float y){
        float target_x = body.getPosition().x - x;
        float target_y = body.getPosition().y - y;
        float force_x = 0;
        float force_y = 0;

        if (target_x < 0) force_x = 1f;
        if (target_x > 0) force_x = -1f;
        if (target_y < 0) force_y = 1f;
        if (target_y > 0) force_y = -1f;

        body.setLinearVelocity(force_x, force_y);
    }

}
