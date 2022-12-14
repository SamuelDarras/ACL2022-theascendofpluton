package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;

public class Joueur extends DamageableObject {
    private float range;
    private float maxLife;
    private boolean isTakingContinuousDamage = false;
    private float continuousDamageValue = 0;
    private boolean invulnerable = false;
    private final float VELOCITY = 30f;
    private float stateTimer = 0;
    private final Sprite playerSprite;
    private TextureRegion playerStand;
    private TextureRegion playerInvulnerable;
    private Animation<TextureRegion> deathAnimation;
    private char directions;
    private boolean shouldAttack;
    private boolean attacking = false;
    private boolean touchPortal = false;
    private float money = 0f;

    private Body attack;
    
    public static final int LEFT  = 0;
    public static final int RIGHT = 1;
    public static final int UP    = 2;
    public static final int DOWN  = 3;

    private float[] vertices;

    public Joueur(Vector2 coords, float[] verticies, float life, float strength, float range, float money) {
        super(coords, life, strength, 0);

        this.money = money;

        this.range = range;
        maxLife = life;

        generateBody(verticies);

        playerSprite = new Sprite();
        playerSprite.setSize(32, 32);
        initTextures();


    }

    /**
     * Rajoute le joueur au nouveau monde lorsque celui-ci passe au prochain niveau
     * @param world
     * @param coords
     * @param verticies
     */
    public void loadInNewWorld( Vector2 coords, float[] verticies){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(coords);
        setBodyDef(bodyDef);
        generateBody(verticies);
    }

    private void generateBody(float[] verticies){
        this.vertices = verticies;
        getBodyDef().type = BodyDef.BodyType.DynamicBody;
        setBody(LevelLoader.getInstance().getGameWorld().getWorld().createBody(getBodyDef()));
        getBody().setFixedRotation(true);

        PolygonShape p = new PolygonShape();
        p.set(verticies);
        getBody().createFixture(createFixture(.5f, .0f, 10f, p)).setUserData("player");
        p.dispose();

        getBody().setUserData(this);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(this.getPosition().cpy());
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        attack = LevelLoader.getInstance().getGameWorld().getWorld().createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = new CircleShape();
        fixtureDef.shape.setRadius(range);
        fixtureDef.isSensor = true;
        attack.createFixture(fixtureDef).setUserData("attack");

        attack.setActive(false);
    }

    private void initTextures() {
        Texture plutonTexture = new Texture(Gdx.files.internal("player.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(plutonTexture, plutonTexture.getWidth() / 10,
                plutonTexture.getHeight() / 24); // max 10 textures/ligne et 24 lignes
        TextureRegion[] deathFrames = new TextureRegion[8];

        // A FAIRE SI L'ON VEUT DES ANIMATIONS

        playerStand = textureRegions[0][0];

        playerInvulnerable = textureRegions[13][1];

        System.arraycopy(textureRegions[15], 0, deathFrames, 0, 8);
        deathAnimation = new Animation<>(0.025f, deathFrames);
    }

    private static FixtureDef createFixture(float density, float resitution, float firction, Shape s) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = resitution;
        fixtureDef.friction = firction;

        fixtureDef.shape = s;

        return fixtureDef;
    }

    /**
     * Change le sprite en joueur en fonction de son ??tat
     * @param delta
     */
    private void updatePlayerSprite(float delta) {
        Sprite playerSprite = LevelLoader.getInstance().getSprite(this.getClass().getSimpleName());
        if (isDead()) {
            playerSprite.setRegion(deathAnimation.getKeyFrame(stateTimer, false));
            stateTimer += delta;
        } else if (invulnerable) {
            playerSprite.setRegion(playerInvulnerable);
        } else {
            playerSprite.setRegion(playerStand);
        }
    }

    public void setShouldAttack(boolean shouldAttack) {
        this.shouldAttack = shouldAttack;
    }

    public void update(GameWorld gameWorld) {
        if (isDead()) {
            return;
        }

        if (isTakingContinuousDamage) {
            receiveDamage(continuousDamageValue);
        }

        Vector2 newVelocity;
        // directions: 0 0 0 0
        //             D U R L
        switch (directions) {
            case 1:
            case 13: // Left
                newVelocity = new Vector2(-1, 0);
                break;
            case 2: 
            case 14: // Right
                newVelocity = new Vector2(1, 0);
                break;
            case 4: 
            case 7: // Up
                newVelocity = new Vector2(0, 1);
                break;
            case 8: 
            case 11: // Down
                newVelocity = new Vector2(0, -1);
                break;
            case 5: // Up Left
                newVelocity = new Vector2(-1, 1);
                break;
            case 6: // Up Right
                newVelocity = new Vector2(1, 1);
                break;
            case 9: // Down Left
                newVelocity = new Vector2(-1, -1);
                break;
            case 10: // Down Right
                newVelocity = new Vector2(1, -1);
                break;
            default:
                newVelocity = new Vector2();
        }
        getBody().setLinearVelocity(newVelocity.nor().scl(VELOCITY));
        if (!isDead() || !touchPortal()) attack.setTransform(this.getPosition().cpy(), 0);

        if (shouldAttack && !attacking) {
            attack.setActive(true);
            attacking = true;
            Timer.schedule(new Timer.Task() {

                @Override
                public void run() {
                    attack.setActive(false);
                    attacking = false;
                    shouldAttack = false;
                }
            }, .2f);
        }
    }

    @Override
    public void receiveDamage(float n) {
        if (!isDead() && !invulnerable) {
            super.receiveDamage(n);

            if (!isDead()) {
                Pluton.manager.get("sounds/hurt.ogg", Music.class).play();
                invulnerable = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        invulnerable = false;
                    }
                }, .5f);
            }
        }
    }

    public void receiveContinuousDamage(float n) {
        isTakingContinuousDamage = true;
        continuousDamageValue = n;
    }

    public void receiveLife(float n) {
        setLife(Math.min(getLife() + n, maxLife));
        Pluton.manager.get("sounds/heal.wav", Music.class).play();
    }

    public void stopContinuousDamage() {
        isTakingContinuousDamage = false;
        continuousDamageValue = 0;
    }

    public boolean isDead() {
        return getLife() <= 0f;
    }

    public void receiveMoney(float money) {
        this.money += money;
    }
    public void spendMoney(float money) {
        this.money -= money;
    }

    public float getMaxLife() {
        return maxLife;
    }
    public void setMaxLife(float maxLife) {
        this.maxLife = maxLife;
    }

    @Override
    public float getMoney() {
        return money;
    }

    @Override
    public void render(float delta) {
        updatePlayerSprite(delta);
        super.render();
    }

    public void addDirection(int direction) {
        directions = (char) (0b1<<direction | directions);
    }
    
    public void removeDirection(int direction) {
        directions = (char) (~(0b1<<direction) & directions);
    }

    /**
     * Le joueur s'ach??te des statistiques
     * @param achat
     * @param prix
     */
    public void buyShop(String achat, float prix){
        if (this.money >= prix){
            switch(achat){
                case "vie":
                    //System.out.println(achat + " richesse : " + this.money + " Prix : " + prix);
                    Pluton.manager.get("sounds/heal.wav", Music.class).play();
                    setMaxLife(this.maxLife+10);
                    setLife(this.getLife()+10);
                    break;
                case "strength":
                    //System.out.println(achat + " richesse : " + this.money + " Prix : " + prix);
                    Pluton.manager.get("sounds/heal.wav", Music.class).play();
                    setDamage(this.getDamage()+1);
                    break;
            }
            spendMoney(prix);
        } else {
            Pluton.manager.get("sounds/wrong.wav", Music.class).play();
            System.out.println("Vous ??tes trop pauvre...");

        }
    }

    public float getRange() {
        return range;
    }

    public void setTouchPortal(boolean touchPortal) {
        this.touchPortal = touchPortal;
    }
    public boolean touchPortal(){
        return touchPortal;
    }

    public float[] getVertices() {
        return vertices;
    }
}
