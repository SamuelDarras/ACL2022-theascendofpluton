package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.ul.theascendofpluton.Exceptions.LevelLoadException;
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
    private final PlayerControlListener c;
    private final Joueur joueur;
    private boolean finished = false;
    private MiniMap map;

    public GameView(Pluton game) {
        super();
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

        c = new PlayerControlListener(joueur, map);
        Gdx.input.setInputProcessor(c);

        PlayerContactListener contactListener = new PlayerContactListener();
        levelLoader.getGameWorld().getWorld().setContactListener(contactListener);
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

        levelLoader.getRenderer().setView(camera);

        Pluton.batch.begin();

        if (c.isDebugMode()) {
            levelLoader.getGameWorld().renderDebug();
            debugRenderer.render(levelLoader.getGameWorld().getWorld(), camera.combined);
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
        Pluton.font.draw(Pluton.batch, String.valueOf("Vie :"+joueur.getLife()), camera.position.x-168f, camera.position.y-72f);
        Pluton.font.draw(Pluton.batch, String.valueOf("Force :"+joueur.getDamage()), camera.position.x-168f, camera.position.y-80f);
        Pluton.font.draw(Pluton.batch, String.valueOf("Monnaie :"+joueur.getMoney()), camera.position.x-168f, camera.position.y-88f);

        Pluton.font.getData().setScale(0.4f, 0.4f);
    }
}