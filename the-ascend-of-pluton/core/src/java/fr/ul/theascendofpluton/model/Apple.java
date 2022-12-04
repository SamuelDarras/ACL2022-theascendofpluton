package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import fr.ul.theascendofpluton.LevelLoader;


public class Apple extends GameObject  {
    private final float heal;
    private boolean used = false;

    private float[] verticies;

    public Apple(Vector2 coords, Vector2 offsetVector, float[] verticies, float heal){
        super(coords, offsetVector);

        this.verticies = verticies;

        setBody(LevelLoader.getInstance().getWorld().createBody(getBodyDef()));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        PolygonShape shape = new PolygonShape();
        shape.set(verticies);

        fixtureDef.shape = shape;

        getBody().setFixedRotation(true);
        getBody().createFixture(fixtureDef).setUserData("apple");
        getBody().setUserData(this);
        shape.dispose();

        this.heal = heal;
    }

    public float use() {
        used = true;
        return heal;
    }

    @Override
    public void update(GameWorld world) {
        if(used) {
            world.remove(this);
        }
    }

    @Override
    public void render(float delta) {
        super.render();
    }

    public float[] getVerticies() {
        return verticies;
    }

    public float getHeal() {
        return heal;
    }
}
