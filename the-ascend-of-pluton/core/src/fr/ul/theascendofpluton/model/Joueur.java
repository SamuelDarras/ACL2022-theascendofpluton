package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Joueur {

    public boolean shouldGoRight = false;
    public boolean shouldGoLeft = false;
    public boolean shouldGoUp = false;
    public boolean shouldGoDown = false;

    float l = 1f;
    float h = 1f;

    World world;
    Body body;

    private final float VELOCITY = 20f;

    public Joueur(World world) {
        this.world = world;
    }

    public void register(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setFixedRotation(true);

        PolygonShape p = new PolygonShape();
        p.set(new Vector2[] { new Vector2(-l, h), new Vector2(l, h), new Vector2(l, -h), new Vector2(-l, -h) });
        body.createFixture(createFixture(.5f, .1f, .25f, p));
        p.dispose();

        body.setUserData(this);
    }

    public void update() {
        boolean somthingDone = shouldGoLeft || shouldGoDown || shouldGoRight || shouldGoUp;

        if (shouldGoLeft) {
            body.applyLinearImpulse(-VELOCITY, 0f, getPosition().x, getPosition().y, true);
        }
        if (shouldGoRight) {
            body.applyLinearImpulse(VELOCITY, 0f, getPosition().x, getPosition().y, true);
        }
        if (shouldGoUp) {
            body.applyLinearImpulse(0f, VELOCITY, getPosition().x, getPosition().y, true);
        }
        if (shouldGoDown) {
            body.applyLinearImpulse(0f, -VELOCITY, getPosition().x, getPosition().y, true);
        }

        if (somthingDone) {
            body.setLinearVelocity(body.getLinearVelocity().nor().scl(VELOCITY));
        } else {
            body.setLinearVelocity(0f, 0f);
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    static FixtureDef createFixture(float density, float resitution, float firction, Shape s) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = resitution;
        fixtureDef.friction = firction;

        fixtureDef.shape = s;

        return fixtureDef;
    }
}
