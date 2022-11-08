package fr.ul.theascendofpluton.view;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;

import fr.ul.theascendofpluton.listener.PlayerContactListener;
import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.LevelLoader;

public class GameView extends ScreenAdapter {
    private final float CAMERA_HEIGHT = (32*9)/1.5f;
    private final float CAMERA_WIDTH = (32*16)/1.5f;

    Viewport vp;
    OrthographicCamera camera;

    Box2DDebugRenderer renderer;
    World world;

    LevelLoader levelLoader;

    Joueur joueur;
    MapObject pluton;

    PlayerControlListener c;
    PlayerContactListener contactListener;

    MapObjects zombiesMo;
    Set<Zombie> zombies;

    public GameView() {
        super();
        levelLoader = new LevelLoader();
        levelLoader.load("pluton");

        MapLayer mapLayerEntities = levelLoader.getEntities();
        pluton = mapLayerEntities.getObjects().get("Pluton");
        world = new World(new Vector2(0f, 0f), true);

        joueur = new Joueur(world);
        joueur.register((float) pluton.getProperties().get("x"),
                (float) pluton.getProperties().get("y"),
                (float) pluton.getProperties().get("vie"));

        camera = new OrthographicCamera();
        camera.position.x = joueur.getPosition().x;
        camera.position.y = joueur.getPosition().y;

        vp = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, camera);
        vp.apply();

        renderer = new Box2DDebugRenderer();

        c = new PlayerControlListener(joueur);
        Gdx.input.setInputProcessor(c);

        contactListener = new PlayerContactListener();
        world.setContactListener(contactListener);

        zombies = new HashSet<>();
        zombiesMo = mapLayerEntities.getObjects();
        for (MapObject zombie : zombiesMo) {
            if (zombie.getName().equals("Zombie")) {
                zombies.add(new Zombie(world, (float) zombie.getProperties().get("x"),
                        (float) zombie.getProperties().get("y"),
                        (float) zombie.getProperties().get("vie"),
                        (float) zombie.getProperties().get("damage")));
            }
        }

        // System.out.println(pluton.getProperties().get("x") + " " +
        // pluton.getProperties().get("y"));
        System.out.println(pluton.getProperties().get("x") + " " + pluton.getProperties().get("y"));
        System.out.println(camera.position);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update();

        levelLoader.getRenderer().setView(camera);
        levelLoader.getRenderer().render();
        renderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, levelLoader.getLevelWidth(), levelLoader.getLevelHeight());
        camera.position.set((float) pluton.getProperties().get("x"), (float) pluton.getProperties().get("y"), 0);
        camera.update();

        levelLoader.getRenderer().setView(camera);

        vp.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        world.dispose();
    }

    private void update() {
        if (!(joueur.getPosition().x + CAMERA_WIDTH/2 > levelLoader.getLevelWidth() * 32 || joueur.getPosition().x - CAMERA_WIDTH/2 < 0))
            camera.position.x = joueur.getPosition().x;

        if (!(joueur.getPosition().y + CAMERA_HEIGHT/2 > levelLoader.getLevelHeight() * 32 || joueur.getPosition().y - CAMERA_HEIGHT/2 < 0))
            camera.position.y = joueur.getPosition().y;

        world.step(Gdx.graphics.getDeltaTime(), 2, 2);
        joueur.update();
        for (Zombie zombie : zombies) {
            zombie.update(joueur.getPosition().x, joueur.getPosition().y);
        }

        camera.update();
    }
}
