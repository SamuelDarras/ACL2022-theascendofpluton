package fr.ul.theascendofpluton.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import fr.ul.theascendofpluton.view.GameView;

/**
 * Cette classe permet de connaitre les éléments essentiels du monde
 */
public class GameWorld {
    private World world;
    private Set<GameObject> gameObjects;
    private Set<GameObject> toRemove;
    private int bossCount;
    private Vector2 lastBossKilled = new Vector2();
    private Joueur joueur;

    public Map<String, Vector2> spriteOffsets;

    public GameWorld(World world) {
        spriteOffsets = new HashMap<>();
        gameObjects = new HashSet<>();
        toRemove = new HashSet<>();
        bossCount = 0;

        this.world = world;

        GameView.kryo.register(GameWorld.class, new Serializer<GameWorld>() {

            @Override
            public void write(Kryo kryo, Output output, GameWorld object) {
                System.out.println(LevelLoader.getInstance().getGameWorld().spriteOffsets);

                output.writeInt(Pluton.getLevelIdx());

                output.writeFloat(lastBossKilled.x);
                output.writeFloat(lastBossKilled.y);

                kryo.writeObject(output, LevelLoader.getInstance().getGameWorld().spriteOffsets);

                output.writeInt(gameObjects.size());

                for (GameObject o : gameObjects) {
                    if (o instanceof Zombie) {
                        kryo.writeClassAndObject(output, (Zombie) o);
                    }
                    if (o instanceof Boss) {
                        kryo.writeClassAndObject(output, (Boss) o);
                    }
                    if (o instanceof Bat) {
                        kryo.writeClassAndObject(output, (Bat) o);
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
                Pluton.setLevelIdx(input.readInt());
                GameWorld gameWorld = new GameWorld(world);
                float x = input.readFloat();
                float y = input.readFloat();
                gameWorld.lastBossKilled = new Vector2(x, y);
                
                gameWorld.spriteOffsets = kryo.readObject(input, HashMap.class);

                int size = input.readInt();

                Joueur joueur = null;
                
                System.out.println(size);
                for (int i = 0; i < size; i++) {
                    Object o = kryo.readClassAndObject(input);
                    if (o instanceof Zombie) {
                        System.out.println("Miam");
                    }
                    if (o instanceof Boss) {
                        System.out.println("Grrrr");
                        gameWorld.addBoss((Boss) o);
                        continue;
                    }
                    if (o instanceof Bat) {
                        System.out.println("Foup Floup");
                    }
                    if (o instanceof Apple) {
                        System.out.println("Scrunch");
                    }
                    if (o instanceof Joueur) {
                        System.out.println("Oof");
                        joueur = (Joueur) o;
                    }
                    gameWorld.add((GameObject) o);
                }
                gameWorld.joueur = joueur;
                return gameWorld;
            }
            
        });
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
        lastBossKilled = b.getBody().getPosition().cpy();
        remove(b);
        bossCount -= 1;

    }

    /**
     * Vérifie si tous les bosses sont mort, si c'est le cas génère un portail vers le niveau suivant.
     */
    public void checkAllBossesDead(){
        if(bossCount == 0){
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(lastBossKilled);
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
            s.setPosition(lastBossKilled.x - s.getWidth()/2.5f, lastBossKilled.y - 5);
            bossCount = -1;
        }
    }
    public void dispose() {
        gameObjects.clear();
        world.dispose();
    }

    public void save() {
        try (Output output = new Output(new FileOutputStream("partie-"+Pluton.getPlayerName()+".bin"))) {
            GameView.kryo.writeObjectOrNull(output, this, getClass());
        } catch (FileNotFoundException | KryoException e) {
            e.printStackTrace();
        }
    }

    public Joueur getJoueur() {
        return joueur;
    }
}
