package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import fr.ul.theascendofpluton.listener.PlayerControlListener;
import fr.ul.theascendofpluton.model.Joueur;

public class GameView extends ScreenAdapter {
    Viewport vp;
    Camera camera;

    Box2DDebugRenderer renderer;
    World world;

    Joueur joueur;
    PlayerControlListener c;

    public GameView() {
        super();
        world = new World(new Vector2(0f, 0f), true);

        joueur = new Joueur(world);
        joueur.register(10f, 10f);

        renderer = new Box2DDebugRenderer();

        c = new PlayerControlListener(joueur);
        Gdx.input.setInputProcessor(c);
    }

    @Override
    public void render(float delta) {
        update();

        ScreenUtils.clear(Color.DARK_GRAY);
        renderer.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
        camera = new OrthographicCamera(16, 16);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        vp = new FitViewport(camera.viewportWidth, camera.viewportHeight, camera);
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
        joueur.update();
        camera.update();
    }
}
