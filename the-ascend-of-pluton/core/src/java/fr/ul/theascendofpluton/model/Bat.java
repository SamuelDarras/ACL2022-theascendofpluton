package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import fr.ul.theascendofpluton.LevelLoader;

public class Bat extends DamageableObject{
    public static float stateTimer = 0;

    private float[] vertices;

    public Bat(Vector2 coords, float[] polygonVerticies, float life, float damage, float monnaie) {
        super(coords, life, damage, monnaie);

        this.vertices = polygonVerticies;

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(polygonVerticies);

        fixtureDef.shape = shape;
        fixtureDef.density = .5f;
        fixtureDef.restitution = .1f;
        fixtureDef.friction = .5f;

        fixtureDef.isSensor = true;

        getBodyDef().type = BodyDef.BodyType.DynamicBody;
        getBodyDef().position.set(coords);
        setBody(LevelLoader.getInstance().getGameWorld().getWorld().createBody(getBodyDef()));

        getBody().setFixedRotation(true);
        getBody().createFixture(fixtureDef).setUserData("bat");
        getBody().setUserData(this);
        shape.dispose();

    }

    @Override
    public void update(GameWorld w) {
        if (isTargetRange()) {
            float target_x = getBody().getPosition().x - LevelLoader.getInstance().getPluton().getPosition().x;
            float target_y = getBody().getPosition().y - LevelLoader.getInstance().getPluton().getPosition().y;
            float force_x = 0;
            float force_y = 0;

            if (target_x < 0) force_x = 10f;
            if (target_x > 0) force_x = -10f;
            if (target_y < 0) force_y = 10f;
            if (target_y > 0) force_y = -10f;

            getBody().setLinearVelocity(force_x, force_y);
        }

        if (getLife() <= 0f) {
            LevelLoader.getInstance().getPluton().receiveMoney(getMoney());
            w.remove(this);
        }
    }

    private void updateBatSprite() {
        Sprite batSprite = LevelLoader.getInstance().getSprite(this.getClass().getSimpleName());
        batSprite.setRegion(LevelLoader.getInstance().flyAnimation.getKeyFrame(stateTimer, true));
    }

    /**
     *
     * @return Vrai si le joueur est assez proche de la chauve-souris pour qu'il le pourchasse, faux sinon.
     */
    private boolean isTargetRange() {
        return LevelLoader.getInstance().getPluton().getPosition().dst(getPosition()) <= 160;
    }

    @Override
    public void render(float delta) {
        updateBatSprite();
        super.render();
    }

    public float[] getVertices() {
        return vertices;
    }
}
