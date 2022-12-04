package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import fr.ul.theascendofpluton.LevelLoader;

public class Bat extends DamageableObject{
    public float money;

    public Bat(World world, Vector2 coords, float[] polygonVerticies, float life, float damage, float monnaie) {
        super(coords, life, damage);
        this.money = monnaie;

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(polygonVerticies);

        fixtureDef.shape = shape;
        fixtureDef.density = .5f;
        fixtureDef.restitution = .1f;
        fixtureDef.friction = .5f;

        getBodyDef().type = BodyDef.BodyType.KinematicBody;
        getBodyDef().position.set(coords);
        setBody(world.createBody(getBodyDef()));

        getBody().setFixedRotation(true);
        getBody().createFixture(fixtureDef).setUserData("bat");
        getBody().setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(GameWorld w) {
        float target_x = getBody().getPosition().x - w.getJoueur().getPosition().x;
        float target_y = getBody().getPosition().y - w.getJoueur().getPosition().y;
        float force_x = 0;
        float force_y = 0;

        if (target_x < 0) force_x = 10f;
        if (target_x > 0) force_x = -10f;
        if (target_y < 0) force_y = 10f;
        if (target_y > 0) force_y = -10f;

        getBody().setLinearVelocity(force_x, force_y);

        if (getLife() <= 0f) {
            LevelLoader.getInstance().getPluton().receiveMoney(money);
            w.remove(this);
        }
    }

    @Override
    public void render(float delta) {
        super.render();
    }

    public float getDistance(Vector2 position) {
        return position.dst(getBody().getPosition());
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

}
