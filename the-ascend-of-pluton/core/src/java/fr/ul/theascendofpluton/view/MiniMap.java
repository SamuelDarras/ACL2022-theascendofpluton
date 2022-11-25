package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MiniMap {
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;

    public Viewport getViewport() {
        return viewport;
    }

    private boolean show = false;

    public MiniMap(TiledMap map) {
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.zoom = 10;

        viewport = new FitViewport(30, 30, camera);
        camera.setToOrtho(false, viewport.getScreenWidth(), viewport.getScreenHeight());
    }

    public void update(float x, float y, float width, float height) {
        camera.position.x = x;
        camera.position.y = y;
        // camera.position.x = width + map.getProperties().get("width", Integer.class)*32;
        // camera.position.y = height + map.getProperties().get("height", Integer.class)*32;
        camera.update();

        renderer.setView(camera);
    }

    public void toggle() {
        show = !show;
    }

    public void render() {
        viewport.apply();
        if (show) {
            renderer.render(new int[]{1, 2, 3});
        }
    }

    public void resize(int width, int height) {
        int s = Math.min(height/4, width/4);
        viewport.setScreenBounds(0, height-s, s, s);
    }
}
