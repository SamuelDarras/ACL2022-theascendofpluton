package fr.ul.theascendofpluton.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import fr.ul.theascendofpluton.Pluton;
import fr.ul.theascendofpluton.listener.PlayerContactListener;
import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Apple;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.LevelLoader;

public class GameView extends ScreenAdapter {
    private final Pluton game;
    private final LevelLoader levelLoader;
    private final Viewport vp;
    private final OrthographicCamera camera;
    private final Box2DDebugRenderer debugRenderer;
    private final World world;
    private final PlayerControlListener c;
    private Joueur joueur;
    private boolean finished = false;
    private MiniMap map;

    public static final Kryo kryo = new Kryo();

    public GameView(Pluton game) {
        super();
        kryo.register(Vector2.class);
        kryo.register(Joueur.class, new Serializer<Joueur>() {
            @Override
            public void write(Kryo kryo, Output output, Joueur joueur) {
                output.writeFloat(joueur.getPosition().x);
                output.writeFloat(joueur.getPosition().y);

                output.writeFloat(joueur.getLife());

                output.writeFloat(joueur.getOffsetVector().x);
                output.writeFloat(joueur.getOffsetVector().y);
                
                output.writeInt(joueur.getVertices().length);
                output.writeFloats(joueur.getVertices(), 0, joueur.getVertices().length);
            }

            @Override
            public Joueur read(Kryo kryo, Input input, Class<? extends Joueur> type) {
                float x = input.readFloat();
                float y = input.readFloat();

                float life = input.readFloat();

                float offsetX = input.readFloat();
                float offsetY = input.readFloat();

                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                return new Joueur(new Vector2(x, y), new Vector2(offsetX, offsetY), vertices, life);
            }
        });
        kryo.register(Zombie.class, new Serializer<Zombie>() {

            @Override
            public void write(Kryo kryo, Output output, Zombie zombie) {
                output.writeFloat(zombie.getPosition().x);
                output.writeFloat(zombie.getPosition().y);

                output.writeFloat(zombie.getLife());

                output.writeFloat(zombie.getOffsetVector().x);
                output.writeFloat(zombie.getOffsetVector().y);

                output.writeInt(zombie.getVertices().length);
                output.writeFloats(zombie.getVertices(), 0, zombie.getVertices().length);

                output.writeFloat(zombie.getDamage());
                output.writeFloat(zombie.getMoney());
            }

            @Override
            public Zombie read(Kryo kryo, Input input, Class<? extends Zombie> type) {
                float x = input.readFloat();
                float y = input.readFloat();

                float life = input.readFloat();

                float offsetX = input.readFloat();
                float offsetY = input.readFloat();

                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                float damage = input.readFloat();
                float money = input.readFloat();

                return new Zombie(new Vector2(x, y), new Vector2(offsetX, offsetY), vertices, life, damage, money);
            }
            
        });
        kryo.register(Apple.class, new Serializer<Apple>() {

            @Override
            public void write(Kryo kryo, Output output, Apple apple) {
                output.writeFloat(apple.getPosition().x);
                output.writeFloat(apple.getPosition().y);

                output.writeFloat(apple.getOffsetVector().x);
                output.writeFloat(apple.getOffsetVector().x);

                output.writeInt(apple.getVerticies().length);
                output.writeFloats(apple.getVerticies(), 0, apple.getVerticies().length);

                output.writeFloat(apple.getHeal());
            }

            @Override
            public Apple read(Kryo kryo, Input input, Class<? extends Apple> type) {
                float x = input.readFloat();
                float y = input.readFloat();

                float offsetX = input.readFloat();
                float offsetY = input.readFloat();

                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                float heal = input.readFloat();

                return new Apple(new Vector2(x, y), new Vector2(offsetX, offsetY), vertices, heal);
            }
            
        });
        kryo.setReferences(true);

        this.game = game;
        levelLoader = LevelLoader.getInstance();
        levelLoader.load("plutonV2");

        // try (Output output = new Output(new FileOutputStream("joueur.bin"))) {
        //     kryo.writeObject(output, joueur);
        //     output.close();
        // } catch (FileNotFoundException | KryoException e) {
        //     e.printStackTrace();
        // }

        // try {
        //     Input input = new Input(new FileInputStream("joueur.bin"));
        //     joueur = kryo.readObject(input, Joueur.class);
        //     input.close();
        // } catch (FileNotFoundException e) {
        //     joueur = LevelLoader.getInstance().getPluton();
        // }

        joueur = LevelLoader.getInstance().getPluton();

        map = new MiniMap(levelLoader.getMap());

        world = levelLoader.getGameWorld().getWorld();

        camera = new OrthographicCamera();

        vp = new FitViewport(Pluton.CAMERA_WIDTH, Pluton.CAMERA_HEIGHT, camera);

        debugRenderer = new Box2DDebugRenderer();

        c = new PlayerControlListener(levelLoader.getGameWorld().getJoueur(), map);
        Gdx.input.setInputProcessor(c);

        PlayerContactListener contactListener = new PlayerContactListener();
        world.setContactListener(contactListener);
    }
    private void update() {
        if (!(joueur.getPosition().x + Pluton.CAMERA_WIDTH/2 > levelLoader.getLevelWidth() * 32 || joueur.getPosition().x - Pluton.CAMERA_WIDTH/2 < 0))
            camera.position.x = joueur.getPosition().x;

        if (!(joueur.getPosition().y + Pluton.CAMERA_HEIGHT/2 > levelLoader.getLevelHeight() * 32 || joueur.getPosition().y - Pluton.CAMERA_HEIGHT/2 < 0))
            camera.position.y = joueur.getPosition().y;

        if(!finished){
            if(joueur.isDead()){
                finished = true;
                //world.setContactListener(null); au cas ou ça sigsegv plus tard
                gameOver();
            }
        }

        levelLoader.getGameWorld().update();

        camera.update();
        map.update(joueur.getPosition().x, joueur.getPosition().y, camera.viewportWidth, camera.viewportHeight);
    }
    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        vp.apply();

        Pluton.batch.setProjectionMatrix(camera.combined);

        levelLoader.getRenderer().setView(camera);

        Pluton.batch.begin();

        if (c.isDebugMode()) {
            levelLoader.getGameWorld().renderDebug();
            debugRenderer.render(world, camera.combined);
        } else {
            levelLoader.getRenderer().render();

            levelLoader.getGameWorld().render(delta);

            showStats();

        }
        Pluton.batch.end();
        
        map.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, levelLoader.getLevelWidth(), levelLoader.getLevelHeight());
        camera.position.set(joueur.getPosition().x,joueur.getPosition().y, 0);
        camera.update();

        levelLoader.getRenderer().setView(camera);

        vp.update(width, height, true);
        map.resize(vp.getScreenWidth(), vp.getBottomGutterHeight()+vp.getScreenHeight());
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        levelLoader.getGameWorld().dispose();
    }


    private void gameOver() {
        Music gameOverMusic = Pluton.manager.get("sounds/death.ogg", Music.class);
        gameOverMusic.setOnCompletionListener(music -> {
            game.setScreen(new GameOverView(game));
            dispose();
        });
        gameOverMusic.play();
    }
    public void showStats(){
        // TODO: ajouter une autre caméra
        Pluton.font.draw(Pluton.batch, String.valueOf("Vie :"+joueur.getLife()), camera.position.x-168f, camera.position.y-72f);
        Pluton.font.draw(Pluton.batch, String.valueOf("Force :"+joueur.getStrength()), camera.position.x-168f, camera.position.y-80f);
        Pluton.font.draw(Pluton.batch, String.valueOf("Monnaie :"+joueur.getMoney()), camera.position.x-168f, camera.position.y-88f);

        //batch = new SpriteBatch();
        //
        Pluton.font.getData().setScale(0.4f, 0.4f);
    }
}