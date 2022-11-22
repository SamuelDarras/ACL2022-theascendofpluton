package fr.ul.theascendofpluton.view;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;

import fr.ul.theascendofpluton.Pluton;
import fr.ul.theascendofpluton.listener.PlayerContactListener;
import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Apple;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.LevelLoader;

public class GameView extends ScreenAdapter {
    private final Pluton game;
    private final LevelLoader levelLoader;
    private final Viewport vp;
    private final OrthographicCamera camera;
    private final Box2DDebugRenderer debugRenderer;
    private final World world;
    private final PlayerControlListener c;
    private final Joueur joueur;
    private final Set<Zombie> zombies;
    private final Set<Apple> apples;
    private boolean finished = false;

    private MiniMap map;
    private BitmapFont font;

    public GameView(Pluton game) {
        super();
        this.game = game;
        levelLoader = new LevelLoader(this);
        levelLoader.load("plutonV2");

        map = new MiniMap(levelLoader.getMap());

        world = new World(new Vector2(0f, 0f), true);

        levelLoader.addObstacles(world);
        levelLoader.addObjects(world);
        zombies = levelLoader.getZombies();
        apples = levelLoader.getApples();
        joueur = levelLoader.getPluton();

        camera = new OrthographicCamera();
        camera.position.x = joueur.getPosition().x;
        camera.position.y = joueur.getPosition().y;

        vp = new FitViewport(Pluton.CAMERA_WIDTH, Pluton.CAMERA_HEIGHT, camera);
        vp.apply();

        debugRenderer = new Box2DDebugRenderer();

        font = new BitmapFont();

        c = new PlayerControlListener(joueur, map);
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
                game_over();
            }
            else{
                joueur.update();
            }
        }

        for(Zombie zombie : zombies){
            zombie.update(joueur.getPosition().x, joueur.getPosition().y);
        }

        for(Apple apple : apples){
            apple.update();
        }

        world.step(Gdx.graphics.getDeltaTime(), 2, 2);

        camera.update();
        map.update(joueur.getPosition().x, joueur.getPosition().y, camera.viewportWidth, camera.viewportHeight);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();


        game.batch.setProjectionMatrix(camera.combined);

        levelLoader.getRenderer().setView(camera);

        game.batch.begin();

        if(c.isDebugMode()){
            debugRenderer.render(world, camera.combined);
        }
        else{
            levelLoader.getRenderer().render();

        showStats();
        joueur.updatePlayerSprite(delta);
            Sprite playerSprite = joueur.getPlayerSprite();
            playerSprite.setPosition(joueur.getPosition().x  , joueur.getPosition().y );
            playerSprite.draw(game.batch);
            for(Apple apple : apples){
                Sprite s = levelLoader.spriteHashMap.get("apple");
                s.setPosition(apple.getPosition().x, apple.getPosition().y - s.getHeight()/2 *.25f);
                s.draw(game.batch);
            }
            for (Zombie zombie : zombies) {
                Sprite s = levelLoader.spriteHashMap.get("zombie");
                s.setPosition(zombie.getPosition().x + 6*.25f,  zombie.getPosition().y - 40*.25f);
                s.draw(game.batch);
            }
        }
        game.batch.end();
        
        map.render();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, levelLoader.getLevelWidth(), levelLoader.getLevelHeight());
        camera.position.set(joueur.getPosition().x,joueur.getPosition().y, 0);
        camera.update();

        levelLoader.getRenderer().setView(camera);

        vp.update(width, height, true);
    }

    @Override
    public void dispose() {
        levelLoader.dispose();
        debugRenderer.dispose();
        world.dispose();
    }


    private void game_over() {
        Music gameOverMusic = Pluton.manager.get("sounds/death.ogg", Music.class);
        gameOverMusic.setOnCompletionListener(music -> {
            game.setScreen(new GameOverView(game));
            dispose();
        });
        gameOverMusic.play();


    }

    public void setToDestroy(Zombie zombie) {
        //À la mort du zombie, le joueur gagne de l'argent
        joueur.receiveMoney(zombie.getMoney());
        System.out.println(joueur.getMoney());
        this.zombies.remove(zombie);
    }
    public void setToDestroy2(Apple apple){
        this.apples.remove(apple);
    }

    public void showStats(){

        font.draw(game.batch, String.valueOf("Vie :"+joueur.getLife()), camera.position.x-168, camera.position.y-72);
        font.draw(game.batch, String.valueOf("Force :"+joueur.getStrength()), camera.position.x-168, camera.position.y-80);
        font.draw(game.batch, String.valueOf("Monnaie :"+joueur.getMoney()), camera.position.x-168, camera.position.y-88);

        //batch = new SpriteBatch();
        //
        font.getData().setScale(0.4f, 0.4f);
    }
}