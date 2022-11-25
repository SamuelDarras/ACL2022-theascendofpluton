package fr.ul.theascendofpluton.model;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld {
    private Joueur joueur;
    private World world;
    private Set<GameObject> gameObjects;
    private Set<GameObject> toRemove;

    public GameWorld(World world) {
        gameObjects = new HashSet<>();
        toRemove = new HashSet<>();

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
        for(GameObject u : gameObjects) {
            u.render(delta);
        }
    }

    public World getWorld() {
        return world;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void add(GameObject u) {
        gameObjects.add(u);
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public void remove(GameObject object) {
        toRemove.add(object);
    }

    public void dispose() {
        gameObjects.clear();
        world.dispose();
    }
}
