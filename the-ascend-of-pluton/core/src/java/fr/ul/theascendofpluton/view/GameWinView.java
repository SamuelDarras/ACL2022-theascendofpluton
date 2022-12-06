package fr.ul.theascendofpluton.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.ul.theascendofpluton.Pluton;

public class GameWinView extends ScreenAdapter {
    private final Pluton game;
    private final Viewport vp;
    private final Stage stage;
    private final Music gameWinMusic = Pluton.manager.get("sounds/game_win.wav", Music.class);

    public GameWinView(Pluton game){
        this.game = game;
        vp = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
        stage = new Stage(vp, Pluton.batch);


        Label.LabelStyle labelStyle = new Label.LabelStyle(Pluton.finalScreenFont, Color.GOLD);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameWinLabel = new Label("Gagner !", labelStyle);
        Label continueLabel = new Label("Appuyez pour continuer.", labelStyle);

        table.add(gameWinLabel).expandX();
        table.row();
        table.add(continueLabel).expandX().padTop(10f);

        stage.addActor(table);

        gameWinMusic.play();
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
            gameWinMusic.stop();
            game.nextLevel();
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
