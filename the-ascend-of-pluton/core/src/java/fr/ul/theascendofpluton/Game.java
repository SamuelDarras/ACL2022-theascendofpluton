package fr.ul.theascendofpluton;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;
import java.util.Map;

public class Game extends ApplicationAdapter {

	private OrthographicCamera camera;
	private LevelLoader levelLoader;


	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, (32 * 16) / 1.5f, (32 * 9) / 1.5f);
		levelLoader = new LevelLoader();
		levelLoader.load("pluton");

		MapLayer mapLayerEntities = levelLoader.getEntities();
		MapObject pluton = mapLayerEntities.getObjects().get("Pluton");

		camera.position.set((float) pluton.getProperties().get("x"), (float) pluton.getProperties().get("y"), 0);
	}

	public void update() {
		camera.update();
		levelLoader.getRenderer().setView(camera);
		levelLoader.getRenderer().render();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update();

	}

	@Override
	public void dispose() {
	}
}
