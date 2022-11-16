package fr.ul.theascendofpluton.view;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.LevelLoader;

import fr.ul.theascendofpluton.model.Obstacle;

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
    private boolean finished = false;


    public GameView(Pluton game) {
        super();
        this.game = game;
        levelLoader = new LevelLoader(this);
        levelLoader.load("pluton");

        MapObject mapObjectPluton = levelLoader.getPluton();
        world = new World(new Vector2(0f, 0f), true);
        joueur = new Joueur(world);
        joueur.register((float) mapObjectPluton.getProperties().get("x"),
                (float) mapObjectPluton.getProperties().get("y"),
                (float) mapObjectPluton.getProperties().get("vie"));

        levelLoader.addObstacles(world);
        zombies = levelLoader.addZombies(world);

        camera = new OrthographicCamera();
        camera.position.x = joueur.getPosition().x;
        camera.position.y = joueur.getPosition().y;

        vp = new FitViewport(Pluton.CAMERA_WIDTH, Pluton.CAMERA_HEIGHT, camera);
        vp.apply();

        debugRenderer = new Box2DDebugRenderer();



        c = new PlayerControlListener(joueur);
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

        world.step(Gdx.graphics.getDeltaTime(), 2, 2);

        camera.update();
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

            joueur.updatePlayerSprite(delta);

            Sprite playerSprite = joueur.getPlayerSprite();
            playerSprite.setPosition(joueur.getPosition().x - 16 , joueur.getPosition().y - 16);
            playerSprite.draw(game.batch);

            for (Zombie zombie : zombies) {
                Sprite s = levelLoader.spriteHashMap.get("zombie");
                s.setPosition(zombie.getPosition().x - s.getWidth()/2, zombie.getPosition().y - s.getHeight()/2);
                s.draw(game.batch);
            }
        }
        game.batch.end();
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
        zombie.dispose();
        this.zombies.remove(zombie);
    }
}
