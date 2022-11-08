package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.MapObject;

import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Zombie;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.LevelLoader;

import java.util.Set;

public class GameView extends ScreenAdapter {
    Viewport vp;
    OrthographicCamera camera;

    Box2DDebugRenderer renderer;
    World world;

    LevelLoader levelLoader;

    Joueur joueur;
    MapObject mapObjectPluton;
    PlayerControlListener c;

    Set<Zombie> zombies;

    public GameView() {
        super();
        levelLoader = new LevelLoader();
        levelLoader.load("pluton");

        mapObjectPluton = levelLoader.getPluton();
        System.out.println(mapObjectPluton);
        world = new World(new Vector2(0f, 0f), true);
        levelLoader.addObstacles(world);
        zombies = levelLoader.addZombies(world);

        joueur = new Joueur(world);
        joueur.register((float) mapObjectPluton.getProperties().get("x") ,
                (float) mapObjectPluton.getProperties().get("y"));

        camera = new OrthographicCamera();
        vp = new FitViewport( (32*16)/1.5f, (32*9)/1.5f, camera);
        vp.apply();

        renderer = new Box2DDebugRenderer();

        c = new PlayerControlListener(joueur);
        Gdx.input.setInputProcessor(c);

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
        if(joueur == null){
            camera.position.set((float) mapObjectPluton.getProperties().get("x"), (float) mapObjectPluton.getProperties().get("y"), 0);
        }
        else{
            camera.position.set(joueur.getPosition().x,joueur.getPosition().y, 0);
        }
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
        camera.position.x = joueur.getPosition().x;
        camera.position.y = joueur.getPosition().y;
        world.step(Gdx.graphics.getDeltaTime(), 2, 2);
        joueur.update();
        for (Zombie z : zombies){
            z.update(joueur.getPosition().x, joueur.getPosition().y);
        }
        camera.update();
    }
}
