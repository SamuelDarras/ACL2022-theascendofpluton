package fr.ul.theascendofpluton.view;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Enemy;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.LevelLoader;

public class GameView extends ScreenAdapter {
    Viewport vp;
    OrthographicCamera camera;

    Box2DDebugRenderer renderer;
    World world;

    LevelLoader levelLoader;

    Joueur joueur;
    MapObject pluton;
    PlayerControlListener c;

    Enemy e;

    public GameView() {
        super();
        levelLoader = new LevelLoader();
        levelLoader.load("pluton");

        MapLayer mapLayerEntities = levelLoader.getEntities();
        pluton = mapLayerEntities.getObjects().get("Pluton");

        world = new World(new Vector2(0f, 0f), true);

        e = new Enemy(world, 5, 5);

        joueur = new Joueur(world);
        joueur.register((float) pluton.getProperties().get("x") / levelLoader.getLevelWidth(),
                (float) pluton.getProperties().get("y") / levelLoader.getLevelHeight());
        Gdx.app.log("la", joueur.getPosition().toString());

        renderer = new Box2DDebugRenderer();

        c = new PlayerControlListener(joueur);
        Gdx.input.setInputProcessor(c);
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        levelLoader.getRenderer().render();
        renderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, (32 * 16) / 1.5f, (32 * 9) / 1.5f);
        camera.position.set((float) pluton.getProperties().get("x"), (float) pluton.getProperties().get("y"), 0);
        camera.update();

        levelLoader.getRenderer().setView(camera);

        vp = new ScalingViewport(Scaling.stretch, levelLoader.getLevelWidth(), levelLoader.getLevelHeight(), camera);
        vp.update(width, height, true);
        vp.apply();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        world.dispose();
    }

    private void update() {
        world.step(Gdx.graphics.getDeltaTime(), 2, 2);
        levelLoader.getRenderer().render();
        joueur.update();
        e.update(joueur.getPosition().x, joueur.getPosition().y);
        camera.update();
    }
}
