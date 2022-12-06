package fr.ul.theascendofpluton.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import fr.ul.theascendofpluton.Pluton;

public class TitleView extends ScreenAdapter {
    private Stage stage;

    private Button btnValider;
    private TextField textField;

    Pluton game;

    public TitleView(Pluton game) {
        this.game = game;

        Skin skin = new Skin(Gdx.files.internal("skins/skin/uiskin.json"));
        stage = new Stage(new ScreenViewport());
        btnValider = new TextButton("Valider", skin);
        btnValider.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        btnValider.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // TODO Auto-generated method stub
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setPlayerName(textField.getText());
                game.setScreen(new GameView(game));
                dispose();
                return true;
            }
        });
        stage.addActor(btnValider);

        textField = new TextField("", skin);
        textField.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2+textField.getHeight());
        stage.addActor(textField);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        super.render(delta);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        super.resize(width, height);

        stage.getViewport().update(width, height);
    }
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        super.dispose();
    }
}
