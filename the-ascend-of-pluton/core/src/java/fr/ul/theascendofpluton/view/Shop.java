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
    private OrthographicCamera camera;
    private Viewport viewport;

    private boolean show = false;
    private final Texture textureVie;
    private final Texture textureCoin;
    private final Texture textureStrength;
    private final Texture textureShop;

    public Shop(float screenWidth, float screenHeight) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.position.set(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 0);

        batch = new SpriteBatch();
        font = new BitmapFont();
        textureVie = new Texture(Gdx.files.internal("vie.png"));
        textureCoin = new Texture(Gdx.files.internal("coin.png"));
        textureStrength = new Texture(Gdx.files.internal("sword.png"));
        textureShop = new Texture(Gdx.files.internal("shop.png"));

        // camera.position.set(200, 200, 0);
    }

    public void toggle() {
        show = !show;
    }

    public void render() {
        if (show) {
            batch.begin();
                batch.draw(textureShop, camera.position.x-96, camera.position.y-24,1280/5,720/8);
                batch.draw(textureVie, camera.position.x-72, camera.position.y+8,1280/20,720/20);
                batch.draw(textureCoin, camera.position.x-48, camera.position.y-12,1280/96,720/96);
                batch.draw(textureCoin, camera.position.x+96, camera.position.y-12,1280/96,720/96);
                batch.draw(textureStrength, camera.position.x+72, camera.position.y+8,1280/20,720/20);

                Pluton.font.draw(batch, "Magasin", camera.position.x+20, camera.position.y+48);
                Pluton.font.draw(batch, "+ 10 Vies max", camera.position.x-72, camera.position.y+2);
                Pluton.font.draw(batch, "[ I ]", camera.position.x-48, camera.position.y-14);
                Pluton.font.draw(batch, "[ O ]", camera.position.x+88, camera.position.y-14);
                Pluton.font.draw(batch, "Prix : 5", camera.position.x-72, camera.position.y-6);
                Pluton.font.draw(batch, "Prix : 10", camera.position.x+72, camera.position.y-6);
                Pluton.font.draw(batch, "+ 1 Strengh", camera.position.x+72, camera.position.y+2);
            batch.end();
        }

    }

    public void resize(int width, int height) {
        // camera.viewportWidth = width;
        // camera.viewportHeight = height;
        // camera.update();
    }

}
