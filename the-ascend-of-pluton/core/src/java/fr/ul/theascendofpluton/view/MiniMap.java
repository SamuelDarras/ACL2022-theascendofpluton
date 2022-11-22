package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MiniMap {
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera         camera = new OrthographicCamera();

    private MapObject pluton;

    public MiniMap(TiledMap map, MapObject pluton) {
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.setToOrtho(false, 30, 20);
        camera.zoom = 90;
        this.pluton = pluton;
    }

    public void update(float x, float y, float width, float height) {
        camera.position.x = width - 800;
        camera.position.y = height - 400;
        camera.update();

        renderer.setView(camera);
    }

    public void render() {
        renderer.render(new int[]{1, 2, 3});
        renderer.renderObject(pluton);
    }
}
