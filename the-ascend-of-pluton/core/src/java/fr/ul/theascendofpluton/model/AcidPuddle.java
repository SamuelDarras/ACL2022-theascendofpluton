package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.physics.box2d.*;

public class AcidPuddle {
    public BodyDef bodyDef;
    public Body body;
    public static float damage;

    public static float getDamage() {
        return damage;
    }

    //x,y postion de l'acide dans le monde avec son nombre de dégâts infligé au entité
    public AcidPuddle(World world, float x, float y,float damage){
        bodyDef  = new BodyDef();
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4f,4f);

        fixtureDef.shape = shape;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef).setUserData("acid");
        body.setUserData(this);
        shape.dispose();

        this.damage=damage;
    }
}