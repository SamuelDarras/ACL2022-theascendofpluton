package fr.ul.theascendofpluton.model;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.view.GameView;

public class GameWorld {
    private Joueur joueur;
    private World world;
    private Set<GameObject> gameObjects;
    private Set<GameObject> toRemove;

    public GameWorld(World world) {
        gameObjects = new HashSet<>();
        toRemove = new HashSet<>();

        this.world = world;

        GameView.kryo.register(GameWorld.class, new Serializer<GameWorld>() {

            @Override
            public void write(Kryo kryo, Output output, GameWorld object) {
                output.writeInt(gameObjects.size());

                for (GameObject o : gameObjects) {
                    if (o instanceof Zombie) {
                        kryo.writeClassAndObject(output, (Zombie) o);
                    }
                    if (o instanceof Joueur) {
                        kryo.writeClassAndObject(output, (Joueur) o);
                    }
                    if (o instanceof Apple) {
                        kryo.writeClassAndObject(output, (Apple) o);
                    }
                }
            }

            @Override
            public GameWorld read(Kryo kryo, Input input, Class<? extends GameWorld> type) {
                int size = input.readInt();
                Set<GameObject> set = new HashSet<>();

                Joueur joueur = null;
                
                System.out.println(size);
                for (int i = 0; i < size; i++) {
                    Object o = kryo.readClassAndObject(input);
                    if (o instanceof Zombie) {
                        System.out.println("Miam");
                    }
                    if (o instanceof Apple) {
                        System.out.println("Scrunch");
                    }
                    if (o instanceof Joueur) {
                        System.out.println("Oof");
                        joueur = (Joueur) o;
                    }
                    set.add((GameObject) o);
                }
                GameWorld gameWorld = new GameWorld(world);
                gameWorld.gameObjects = set;
                gameWorld.joueur = joueur;
                return gameWorld;
            }
            
        });
    }

    public void update() {
        LevelLoader.getInstance().getWorld().step(Gdx.graphics.getDeltaTime(), 2, 2);
        for(GameObject u : gameObjects) {
            u.update(this);
        }

        for (GameObject b : toRemove) {
            gameObjects.remove(b);
            LevelLoader.getInstance().getWorld().destroyBody(b.getBody());
        }
        toRemove.clear();
    }

    public void render(float delta) {
        for(GameObject u : gameObjects) {
            u.render(delta);
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

    public void save(Output output) {
        GameView.kryo.writeObjectOrNull(output, this, getClass());
    }
}
