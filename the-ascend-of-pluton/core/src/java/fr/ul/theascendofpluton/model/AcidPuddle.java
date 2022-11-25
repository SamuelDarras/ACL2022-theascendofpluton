package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class AcidPuddle {
    public BodyDef bodyDef;
    public Body body;
    public  float damage;

    public  float getDamage() {
        return damage;
    }

    //x,y postion de l'acide dans le monde avec son nombre de dégâts infligé au entité
    public AcidPuddle(World world, Vector2 coords, float[] verticies, float damage){
        bodyDef = new BodyDef();
        bodyDef.position.set(coords);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        PolygonShape shape = new PolygonShape();
        shape.set(verticies);

        fixtureDef.shape = shape;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef).setUserData("acid");
        body.setUserData(this);
        shape.dispose();

        this.damage = damage;
    }
}