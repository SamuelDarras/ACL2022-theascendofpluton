package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import fr.ul.theascendofpluton.Pluton;

public class Joueur {
    private float strength = 10f;
    private float range = 12f;
    private float l = 5f;
    private float h = 6f;
    private World world;
    private Body body;
    private float maxLife;
    private float life;
    private boolean isTakingContinuousDamage = false;
    private float continuousDamageValue = 0;
    private boolean invulnerable = false;
    private final float VELOCITY = 20f;
    public final String name = "player";
    private float stateTimer = 0;
    private final Sprite playerSprite;
    private TextureRegion playerStand;
    private TextureRegion playerInvulterable;
    private Animation<TextureRegion> death_anim;
    private boolean shouldGoRight = false;
    private boolean shouldGoLeft = false;
    private boolean shouldGoUp = false;
    private boolean shouldGoDown = false;
    private boolean shouldAttack = false;
    private float monnaie = 0f;


    public Joueur(World world, Vector2 coords, float[] verticies, float life) {
        this.world = world;
        this.life = life;
        maxLife = life;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(coords);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        body.setFixedRotation(true);

        PolygonShape p = new PolygonShape();
        p.set(verticies);
        body.createFixture(createFixture(.5f, .0f, 10f, p)).setUserData("player");
        p.dispose();

        body.setUserData(this);

        playerSprite = new Sprite();
        playerSprite.setSize(32, 32);
        initTextures();
    }

    private void initTextures() {
        Texture plutonTexture = new Texture(Gdx.files.internal("player.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(plutonTexture, plutonTexture.getWidth() / 10, plutonTexture.getHeight() / 24); // max 10 textures/ligne et 24 lignes
        TextureRegion[] death_frames = new TextureRegion[8];

        //A FAIRE SI L'ON VEUT DES ANIMATIONS

        playerStand = textureRegions[0][0];

        playerInvulterable = textureRegions[13][1];

        System.arraycopy(textureRegions[15], 0, death_frames, 0, 8);
        death_anim = new Animation<>(0.025f, death_frames);

    }


    static FixtureDef createFixture(float density, float resitution, float firction, Shape s) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = resitution;
        fixtureDef.friction = firction;

        fixtureDef.shape = s;

        return fixtureDef;
    }

    public void updatePlayerSprite(float delta){
        if(isDead()){
            playerSprite.setRegion(death_anim.getKeyFrame(stateTimer, false));
            stateTimer += delta;
        }
        else if(invulnerable){
            playerSprite.setRegion(playerInvulterable);
        }
        else{
            playerSprite.setRegion(playerStand);
        }
    }
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

    public void update() {
        if(isTakingContinuousDamage){
            receiveDamage(continuousDamageValue);
        }
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



    public void inflictDamage(Zombie target) {
        target.receiveDamage(strength);
    }

    public void receiveDamage(float n){
        if(!isDead() && !invulnerable){
            this.life -= n;

            if(!isDead()){
                Pluton.manager.get("sounds/hurt.ogg", Music.class).play();
                invulnerable = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        invulnerable = false;
                    }
                },.5f);
            }
        }
    }

    public void receiveContinuousDamage(float n){
        isTakingContinuousDamage = true;
        continuousDamageValue = n;
    }
    public void receiveLife(float n){
        life = Math.min(life + n, maxLife);
        Pluton.manager.get("sounds/heal.wav", Music.class).play();
    }

    public void stopContinuousDamage(){
        isTakingContinuousDamage = false;
        continuousDamageValue = 0;
    }

    public boolean isDead(){
        return life <= 0f;
    }
    public float getLife() {
        return life;
    }

    public Sprite getPlayerSprite(){
        return playerSprite;
    }

    public void receiveMoney(float money) {
        this.monnaie += money;
    }

    public float getMoney() {
        return this.monnaie;
    }

    public float getStrength() {
        return strength;
    }
}
