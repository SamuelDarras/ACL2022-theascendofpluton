package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MiniMap {
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera         camera = new OrthographicCamera();

    private boolean show = false;

    public MiniMap(TiledMap map) {
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.setToOrtho(false, 30, 20);
        camera.zoom = 40;
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
        if (show) {
            renderer.render(new int[]{1, 2, 3});
        }
    }
}
