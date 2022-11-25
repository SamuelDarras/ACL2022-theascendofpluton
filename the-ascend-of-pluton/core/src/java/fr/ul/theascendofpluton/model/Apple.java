package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;

public class Apple extends GameObject  {
    private final float heal;
    private boolean used = false;

    public Apple(World world, Vector2 coords, float[] verticies, float heal){
        super(coords, world);

        setBody(world.createBody(getBodyDef()));

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

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public float getHeal() {
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
        Sprite s = LevelLoader.getInstance().spriteHashMap.get("apple");
        s.setPosition(getPosition().x, getPosition().y - s.getHeight()/2 *.25f);
        s.draw(Pluton.batch);
        
    }
}
