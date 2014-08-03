package com.genuineminecraft.battletext.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class Text {

	public static int LIFETIME = 40;
	public static double gravity = 1;
	public String display;
	public float damage;
	public int textColor;
	public int backgroundColor;
	protected int lifespan;
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;
	public double posX;
	public double posY;
	public double posZ;
	private double motionX;
	private double motionY;
	private double motionZ;

	public Text(EntityLivingBase entity, DamageSource damagesource, float amount) {
		setupPos(entity);
		constructDamage(damagesource, amount);
		damage = amount;
	}

	public Text(EntityLivingBase entity, float healing) {
		setupPos(entity);
		setDamage("+", healing, "heal", false);
	}

	public void constructDamage(DamageSource ds, float damage) {
		String name = ds.getDamageType();
		setDamage("", damage, name, false);
	}

	public void setDamage(String prefix, float damage, String name, boolean flag) {
		textColor = Colors.getTextColor(name) & 0xFFFFFF;
		backgroundColor = Colors.getBackgroundColor(name) & 0xFFFFFF;
		if (flag) {
			display = prefix + (int) damage + " (" + name + ")";
		} else {
			display = prefix + (int) damage;
		}
	}

	private void setupPos(EntityLivingBase entity) {
		lifespan = LIFETIME;
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
		prevPosX = entity.prevPosX;
		prevPosY = entity.prevPosY;
		prevPosZ = entity.prevPosZ;
		motionX = Math.random() * 0.2 - 0.1;
		motionZ = Math.random() * 0.2 - 0.1;
	}

	public boolean onUpdate() {
		if (lifespan-- <= 0)
			return false;
		move();
		return true;
	}

	public void move() {
		motionX *= 0.98;
		motionY -= gravity * 0.01;
		motionZ *= 0.98;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
	}

	public double getPercent() {
		return (double) this.lifespan / (double) this.LIFETIME;
	}

	public double getPreviousPercent() {
		return (double) (this.lifespan + 1) / (double) this.LIFETIME;
	}

	public double getPercent(double delta) {
		return this.getPreviousPercent() + ((this.getPercent() - this.getPreviousPercent()) * delta);
	}

	public double getDistanceToEntity(Entity entity) {
		return getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public double getDistanceTo(double posX, double posY, double posZ) {
		double distanceX = this.posX - posX;
		double distanceY = this.posY - posY;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}
}
