package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.ul.theascendofpluton.Pluton;
import fr.ul.theascendofpluton.listener.PlayerContactListener;
import fr.ul.theascendofpluton.listener.PlayerControlListener;
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
    private boolean finished = false;
    private MiniMap map;
    private final Shop shop;

    public GameView(Pluton game) {
        super();
        this.game = game;
        levelLoader = LevelLoader.getInstance();
        levelLoader.load("plutonV2");
        joueur = levelLoader.getGameWorld().getJoueur();

        map = new MiniMap(levelLoader.getMap());
        shop = new Shop(1,1);

        world = levelLoader.getGameWorld().getWorld();

        camera = new OrthographicCamera();

        vp = new FitViewport(Pluton.CAMERA_WIDTH, Pluton.CAMERA_HEIGHT, camera);

        debugRenderer = new Box2DDebugRenderer();

        c = new PlayerControlListener(levelLoader.getGameWorld().getJoueur(), map, shop);
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
            shop.render();

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
        shop.resize(width, height);
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
        Pluton.font.draw(Pluton.batch, String.valueOf("Vie :"+joueur.getLife()+"/"+joueur.getMaxLife()), camera.position.x-168f, camera.position.y-72f);
        Pluton.font.draw(Pluton.batch, String.valueOf("Force :"+joueur.getStrength()), camera.position.x-168f, camera.position.y-80f);
        Pluton.font.draw(Pluton.batch, String.valueOf("Monnaie :"+joueur.getMoney()), camera.position.x-168f, camera.position.y-88f);
        Pluton.font.getData().setScale(0.4f, 0.4f);
    }
}