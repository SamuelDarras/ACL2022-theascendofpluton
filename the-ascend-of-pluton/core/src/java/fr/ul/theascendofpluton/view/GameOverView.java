package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.ul.theascendofpluton.Pluton;


public class GameOverView extends ScreenAdapter {
    private final Pluton game;
    private final Viewport vp;
    private final Stage stage;

    private final Music gameOverMusic = Pluton.manager.get("sounds/game_over.wav", Music.class);

    public GameOverView(Pluton game){
        this.game = game;
        vp = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(vp, Pluton.batch);

        Label.LabelStyle labelStyle = new Label.LabelStyle(Pluton.finalScreenFont, Color.GOLD);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("Perdu !", labelStyle);
        Label playAgainLabel = new Label("Appuyez pour rejouer.", labelStyle);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);

        gameOverMusic.play();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        vp.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()){
            gameOverMusic.stop();
            game.resetLevel();
            game.setScreen(new GameView(game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }
}
