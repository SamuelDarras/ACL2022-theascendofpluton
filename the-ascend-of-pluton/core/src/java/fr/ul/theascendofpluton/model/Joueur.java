package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Joueur {


    private boolean shouldGoRight = false;
    private boolean shouldGoLeft = false;
    private boolean shouldGoUp = false;
    private boolean shouldGoDown = false;
    private boolean shouldAttack = false;

    public boolean isShouldAttack() {
        return shouldAttack;
    }

    public void setShouldAttack(boolean shouldAttack) {
        this.shouldAttack = shouldAttack;
    }

    public boolean isShouldGoRight() {
        return shouldGoRight;
    }

    public void setShouldGoRight(boolean shouldGoRight) {
        this.shouldGoRight = shouldGoRight;
    }

    public boolean isShouldGoLeft() {
        return shouldGoLeft;
    }

    public void setShouldGoLeft(boolean shouldGoLeft) {
        this.shouldGoLeft = shouldGoLeft;
    }

    public boolean isShouldGoUp() {
        return shouldGoUp;
    }

    public void setShouldGoUp(boolean shouldGoUp) {
        this.shouldGoUp = shouldGoUp;
    }

    public boolean isShouldGoDown() {
        return shouldGoDown;
    }

    public void setShouldGoDown(boolean shouldGoDown) {
        this.shouldGoDown = shouldGoDown;
    }

    private float strength = 10f;
    private float range = 12f;

    private float l = 5f;
    private float h = 6f;

    private World world;
    private Body body;
    private float life;
    private float money;

    private final float VELOCITY = 20f;

    public final String name = "player";

    public Joueur(World world) {
        this.world = world;
    }

    public void register(float x, float y, float life, float money) {
        this.life = life;
        this.money = money;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setFixedRotation(true);

        PolygonShape p = new PolygonShape();
        p.set(new Vector2[] { new Vector2(-l, h), new Vector2(l, h), new Vector2(l, -h), new Vector2(-l, -h) });
        body.createFixture(createFixture(.5f, .0f, 10f, p)).setUserData("player");
        p.dispose();

        body.setUserData(this);
    }

    public void update() {
        boolean somthingDone = shouldGoLeft || shouldGoDown || shouldGoRight || shouldGoUp || shouldAttack;

        if (shouldGoLeft) {
            body.setLinearVelocity(new Vector2(-VELOCITY, 0f).add(body.getLinearVelocity()).nor().scl(VELOCITY));
        }
        if (shouldGoRight) {
            body.setLinearVelocity(new Vector2(VELOCITY, 0f).add(body.getLinearVelocity()).nor().scl(VELOCITY));
        }
        if (shouldGoUp) {
            body.setLinearVelocity(new Vector2(0f, VELOCITY).add(body.getLinearVelocity()).nor().scl(VELOCITY));
        }
        if (shouldGoDown) {
            body.setLinearVelocity(new Vector2(0f, -VELOCITY).add(body.getLinearVelocity()).nor().scl(VELOCITY));
        }
        if (shouldAttack) {
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);

            for (Body body : bodies) {
                if (body.getUserData() instanceof Zombie) {
                    Zombie z = (Zombie) body.getUserData();
                    if (z.getDistance(getPosition()) < range) {
                        inflictDamage(z);
                    }
                }
            }
            shouldAttack = false;
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

    public void inflictDamage(Zombie target) {
        target.receiveDamage(strength);
    }

    public void receiveDamage(float damage){
        this.life -= damage;
    }
    public void receiveMoney(float monnaie){
        this.money += monnaie;
    }
    public float getLife() {
        return life;
    }

    public float getMoney() {
        return money;
    }
}
