package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;

public abstract class DamageableObject extends GameObject{

    private float life;
    private float damage;

    public DamageableObject(Vector2 coords, float life, float damage) {
        super(coords);
        this.life = life;
        this.damage = damage;
    }

    public void inflictDamage(DamageableObject o){
        o.receiveDamage(this.damage);
    };

    public void receiveDamage(float n){
        life -= n;
    };

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = life;
    }

    public float getDamage() {
        return damage;
    }
}
