package fr.ul.theascendofpluton.model;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;

public abstract class GameObject {
    private Body body;
    private final BodyDef bodyDef;

    public GameObject(Vector2 coords) {
        bodyDef = new BodyDef();
        bodyDef.position.set(coords);
    }

    public abstract void update(GameWorld w);

    public abstract void render(float delta);

    void render(){
        Sprite s = LevelLoader.getInstance().spriteHashMap.get(this.getClass().getSimpleName());
        Vector2 offsetVec = LevelLoader.getInstance().spriteOffsets.get(this.getClass().getSimpleName());
        s.setPosition(getPosition().x - offsetVec.x, getPosition().y - offsetVec.y);
        s.draw(Pluton.batch);
    }
    public void renderDebug(){
        Pluton.font.draw(Pluton.batch, this.getClass().getSimpleName()+": "+getPosition(), getPosition().x, getPosition().y);
        Pluton.batch.draw(Pluton.positionPoint, getPosition().x, getPosition().y);

    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }
    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
