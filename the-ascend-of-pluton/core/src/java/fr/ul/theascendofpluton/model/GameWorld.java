package fr.ul.theascendofpluton.model;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;

public class GameWorld {
    private World world;
    private Set<GameObject> gameObjects;
    private Set<GameObject> toRemove;
    private int bossCount;
    private Boss lastBossKilled = null;

    public GameWorld(World world) {
        gameObjects = new HashSet<>();
        toRemove = new HashSet<>();
        bossCount = 0;

        this.world = world;
    }

    public void update() {
        world.step(Gdx.graphics.getDeltaTime(), 2, 2);
        for(GameObject u : gameObjects) {
            u.update(this);
        }

        for (GameObject b : toRemove) {
            gameObjects.remove(b);
            world.destroyBody(b.getBody());
        }
        toRemove.clear();
    }

    public void render(float delta) {
        Bat.stateTimer += delta;
        for(GameObject u : gameObjects) {
            u.render(delta);
        }
        if(bossCount == -1){
            LevelLoader.getInstance().getSprite("Portal").draw(Pluton.batch);
        }
    }
    public void renderDebug(){
        for(GameObject u : gameObjects){
            u.renderDebug();
        }
    }
    public World getWorld() {
        return world;
    }


    public void add(GameObject u) {
        gameObjects.add(u);
    }
    public void addBoss(Boss b) {
        add(b);
        bossCount += 1;
    }

    public void remove(GameObject object) {
        toRemove.add(object);
    }
    public void removeBoss(Boss b){
        lastBossKilled = b;
        remove(b);
        bossCount -= 1;

    }
    public void checkAllBossesDead(){
        if(bossCount == 0){
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(lastBossKilled.getPosition());
            Body body = world.createBody(bodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.isSensor = true;
            PolygonShape shape = new PolygonShape();
            shape.set(new float[]{0,0, 10,0, 10,20, 0,20});

            fixtureDef.shape = shape;

            body.setFixedRotation(true);
            body.createFixture(fixtureDef).setUserData("portal");
            body.setUserData(this);
            shape.dispose();
            Sprite s = LevelLoader.getInstance().getSprite("Portal");
            s.setPosition(lastBossKilled.getPosition().x - s.getWidth()/2.5f, lastBossKilled.getPosition().y - 5);
            bossCount = -1;
        }
    }
    public void dispose() {
        gameObjects.clear();
        world.dispose();
    }
}
