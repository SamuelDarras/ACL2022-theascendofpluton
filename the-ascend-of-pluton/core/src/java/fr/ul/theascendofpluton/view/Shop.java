package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.ul.theascendofpluton.Pluton;
import jdk.jfr.FlightRecorder;

import javax.swing.text.Style;

public class Shop {
    public static SpriteBatch batch;
    public static BitmapFont font;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;

    public Viewport getViewport() {
        return viewport;
    }

    private boolean show = false;
    private Texture textureVie;
    private Texture textureCoin;
    private Texture textureStrength;
    private Texture textureShop;

    public Shop(TiledMap map) {
        renderer = new OrthogonalTiledMapRenderer(map);
        camera.zoom = 10;

        viewport = new FitViewport(30, 30, camera);
        camera.setToOrtho(false, viewport.getScreenWidth(), viewport.getScreenHeight());
    }
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        textureVie = new Texture(Gdx.files.internal("vie.png"));
        textureCoin = new Texture(Gdx.files.internal("coin.png"));
        textureStrength = new Texture(Gdx.files.internal("sword.png"));
        textureShop = new Texture(Gdx.files.internal("shop.png"));
    }
    public void update(float x, float y, float width, float height) {
        camera.position.x = x;
        camera.position.y = y;
        camera.update();

        renderer.setView(camera);
    }

    public void toggle() {
        show = !show;
    }

    public void render() {
        create();
        viewport.apply();
        if (show) {
            Pluton.batch.begin();
            Pluton.batch.draw(textureShop, camera.position.x-96, camera.position.y-24,1280/5,720/8);
            Pluton.batch.draw(textureVie, camera.position.x-72, camera.position.y+8,1280/20,720/20);
            Pluton.batch.draw(textureCoin, camera.position.x-48, camera.position.y-12,1280/96,720/96);
            Pluton.batch.draw(textureCoin, camera.position.x+96, camera.position.y-12,1280/96,720/96);
            Pluton.batch.draw(textureStrength, camera.position.x+72, camera.position.y+8,1280/20,720/20);
            Pluton.font.draw(Pluton.batch, "Magasin", camera.position.x+20, camera.position.y+48);
            Pluton.font.draw(Pluton.batch, "+ 10 Vies max", camera.position.x-72, camera.position.y+2);
            Pluton.font.draw(Pluton.batch, "[ I ]", camera.position.x-48, camera.position.y-14);
            Pluton.font.draw(Pluton.batch, "[ O ]", camera.position.x+88, camera.position.y-14);
            Pluton.font.draw(Pluton.batch, "Prix : 5", camera.position.x-72, camera.position.y-6);
            Pluton.font.draw(Pluton.batch, "Prix : 10", camera.position.x+72, camera.position.y-6);
            Pluton.font.draw(Pluton.batch, "+ 1 Strengh", camera.position.x+72, camera.position.y+2);
            Pluton.batch.end();
        }

    }

    public void resize(int width, int height) {
        int s = Math.min(width, height);
        viewport.setScreenBounds(width/8, height-s, s, s);
    }

}
