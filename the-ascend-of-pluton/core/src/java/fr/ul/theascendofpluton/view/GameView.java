package fr.ul.theascendofpluton.view;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;
import fr.ul.theascendofpluton.Exceptions.LevelLoadException;
import fr.ul.theascendofpluton.listener.PlayerContactListener;
import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Apple;
import fr.ul.theascendofpluton.model.Bat;
import fr.ul.theascendofpluton.model.Boss;
import fr.ul.theascendofpluton.model.DamageableObject;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.model.Zombie;

public class GameView extends ScreenAdapter {
    private final Pluton game;
    private final LevelLoader levelLoader;
    private final Viewport vp;
    private final OrthographicCamera camera;
    private final Box2DDebugRenderer debugRenderer;
    private final PlayerControlListener c;
    private Joueur joueur;
    private boolean finished = false;
    private MiniMap map;

    private ShapeRenderer shapeRenderer;

    private OrthographicCamera HUDCamera;
    private SpriteBatch HUDBatch;

    public static final Kryo kryo = new Kryo();

    public <T> GameView(Pluton game) {
        super();
        kryo.register(Vector2.class);
        kryo.register(Joueur.class, new Serializer<Joueur>() {
            @Override
            public void write(Kryo kryo, Output output, Joueur joueur) {
                output.writeFloat(joueur.getPosition().x);
                output.writeFloat(joueur.getPosition().y);

                output.writeFloat(joueur.getLife());
                output.writeFloat(joueur.getDamage());
                output.writeFloat(joueur.getRange());

                output.writeInt(joueur.getVertices().length);
                output.writeFloats(joueur.getVertices(), 0, joueur.getVertices().length);
            }

            @Override
            public Joueur read(Kryo kryo, Input input, Class<? extends Joueur> type) {
                float x = input.readFloat();
                float y = input.readFloat();

                float life = input.readFloat();
                float strength = input.readFloat();
                float range = input.readFloat();
                
                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                return new Joueur(LevelLoader.getInstance().getGameWorld().getWorld(), new Vector2(x, y), vertices, life, strength, range);
            }
        });
        kryo.register(Zombie.class, new Serializer<Zombie>() {

            @Override
            public void write(Kryo kryo, Output output, Zombie zombie) {
                output.writeFloat(zombie.getPosition().x);
                output.writeFloat(zombie.getPosition().y);

                output.writeFloat(zombie.getLife());

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

                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                float damage = input.readFloat();
                float money = input.readFloat();

                return new Zombie(new Vector2(x, y), vertices, life, damage, money);
            }
            
        });
        kryo.register(Bat.class, new Serializer<Bat>() {

            @Override
            public void write(Kryo kryo, Output output, Bat bat) {
                output.writeFloat(bat.getPosition().x);
                output.writeFloat(bat.getPosition().y);

                output.writeFloat(bat.getLife());

                output.writeInt(bat.getVertices().length);
                output.writeFloats(bat.getVertices(), 0, bat.getVertices().length);

                output.writeFloat(bat.getDamage());
                output.writeFloat(bat.getMoney());
            }

            @Override
            public Bat read(Kryo kryo, Input input, Class<? extends Bat> type) {
                float x = input.readFloat();
                float y = input.readFloat();

                float life = input.readFloat();

                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                float damage = input.readFloat();
                float money = input.readFloat();

                return new Bat(new Vector2(x, y), vertices, life, damage, money);
            }
            
        });
        kryo.register(Apple.class, new Serializer<Apple>() {

            @Override
            public void write(Kryo kryo, Output output, Apple apple) {
                output.writeFloat(apple.getPosition().x);
                output.writeFloat(apple.getPosition().y);

                output.writeInt(apple.getVerticies().length);
                output.writeFloats(apple.getVerticies(), 0, apple.getVerticies().length);

                output.writeFloat(apple.getHeal());
            }

            @Override
            public Apple read(Kryo kryo, Input input, Class<? extends Apple> type) {
                float x = input.readFloat();
                float y = input.readFloat();

                int verticesLength = input.readInt();
                float[] vertices = input.readFloats(verticesLength);

                float heal = input.readFloat();

                return new Apple(new Vector2(x, y), vertices, heal);
            }
            
        });
        kryo.register(Boss.class, new Serializer<Boss>() {

            @Override
            public void write(Kryo kryo, Output output, Boss boss) {
                kryo.writeClassAndObject(output, boss.bossObject);
            }

            @Override
            public Boss read(Kryo kryo, Input input, Class<? extends Boss> type) {
                DamageableObject o = (DamageableObject) kryo.readClassAndObject(input);
                return new Boss(o);
            }
            
        });

        MapSerializer serializer = new MapSerializer();
        serializer.setKeyClass(String.class, kryo.getSerializer(String.class));
        
        kryo.register(HashMap.class, serializer);
        kryo.setReferences(true);

        this.game = game;
        levelLoader = LevelLoader.getInstance();
        try {
            levelLoader.load(game.getCurrentLevel());
        } catch (LevelLoadException e) {
            throw new RuntimeException(e);
        }
        joueur = levelLoader.getPluton();

        map = new MiniMap(levelLoader.getMap());


        camera = new OrthographicCamera();

        vp = new FitViewport(Pluton.CAMERA_WIDTH, Pluton.CAMERA_HEIGHT, camera);

        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        c = new PlayerControlListener(levelLoader.getPluton(), map, game);
        Gdx.input.setInputProcessor(c);

        PlayerContactListener contactListener = new PlayerContactListener();
        levelLoader.getGameWorld().getWorld().setContactListener(contactListener);

        HUDBatch = new SpriteBatch();
        HUDCamera = new OrthographicCamera();
        HUDCamera.position.set(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 0);
        HUDCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        HUDBatch.setProjectionMatrix(HUDCamera.combined);
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
            if(joueur.touchPortal()){
                finished = true;

                gameWin();
            }
        }

        levelLoader.getGameWorld().update();
        levelLoader.getGameWorld().checkAllBossesDead();


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
        shapeRenderer.setProjectionMatrix(camera.combined);

        levelLoader.getRenderer().setView(camera);

        Pluton.batch.begin();

        if (c.isDebugMode()) {
            levelLoader.getGameWorld().renderDebug();
            debugRenderer.render(LevelLoader.getInstance().getGameWorld().getWorld(), camera.combined);

            // shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            // shapeRenderer.setColor(Color.CYAN);
            // shapeRenderer.circle(joueur.getPosition().x, joueur.getPosition().y, joueur.getRange());
            // shapeRenderer.end();
        } else {
            levelLoader.getRenderer().render();

            levelLoader.getGameWorld().render(delta);
        }
        Pluton.batch.end();

        showStats();

        map.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, levelLoader.getLevelWidth(), levelLoader.getLevelHeight());
        System.out.println(joueur.getPosition().toString());
        camera.position.set(joueur.getPosition().x, joueur.getPosition().y, 0);
        camera.update();

        levelLoader.getRenderer().setView(camera);

        vp.update(width, height, false);
        map.resize(vp.getScreenWidth(), vp.getBottomGutterHeight()+vp.getScreenHeight());
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        levelLoader.getGameWorld().dispose();
    }


    private void gameOver() {
        Music deathMusic = Pluton.manager.get("sounds/death.ogg", Music.class);
        deathMusic.setOnCompletionListener(music -> {
            game.setScreen(new GameOverView(game));
            dispose();
        });
        deathMusic.play();
    }

    private void gameWin() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new GameWinView(game));
                dispose();
            }
        }, .5f);
    }
    public void showStats(){
        // TODO: ajouter une autre caméra
        HUDBatch.begin();

            Pluton.font.draw(HUDBatch, String.valueOf("Vie :"+joueur.getLife()), camera.viewportWidth/20f, camera.viewportHeight/2f);
            Pluton.font.draw(HUDBatch, String.valueOf("Force :"+joueur.getDamage()), camera.viewportWidth/20f, camera.viewportHeight/2f - (Pluton.font.getCapHeight()*2));
            Pluton.font.draw(HUDBatch, String.valueOf("Monnaie :"+joueur.getMoney()), camera.viewportWidth/20f, camera.viewportHeight/2f - (Pluton.font.getCapHeight()*4));

        HUDBatch.end();
    }
}
