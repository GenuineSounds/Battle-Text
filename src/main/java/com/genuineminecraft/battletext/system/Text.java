package com.genuineminecraft.battletext.system;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import com.genuineminecraft.battletext.config.Colors;

public class Text implements Comparable<Text> {

	public static final double GRAVITY = 0.5;
	public String display;
	public final float amount;
	public int textColor;
	public int backgroundColor;
	protected int ticks;
	protected int lifetime = 40;
	public double posX;
	public double posY;
	public double posZ;
	public double prevPosX;
	public double prevPosY;
	public double prevPosZ;
	private double motionX;
	private double motionY;
	private double motionZ;

	public Text(EntityLivingBase entity, DamageSource damageSource, float damage) {
		this.setupPos(entity);
		String name = damageSource.damageType;
		this.setAmount("-", damage, name, false);
		this.amount = damage;
	}

	public Text(EntityLivingBase entity, float healing) {
		this.setupPos(entity);
		this.setAmount("+", healing, "heal", false);
		this.amount = healing;
	}

	@Override
	public int compareTo(Text text) {
		if (Minecraft.getMinecraft().thePlayer == null)
			return 0;
		double distance1 = this.getDistanceTo(Minecraft.getMinecraft().thePlayer);
		double distance2 = text.getDistanceTo(Minecraft.getMinecraft().thePlayer);
		if (distance1 == distance2)
			return 0;
		else if (distance2 > distance1)
			return 1;
		else
			return -1;
	}

	public double getDistanceTo(double posX, double posY, double posZ) {
		double distanceX = this.posX - posX;
		double distanceY = this.posY - posY;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}

	public double getDistanceTo(Entity entity) {
		return this.getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public double getDistanceTo(Text text) {
		return this.getDistanceTo(text.posX, text.posY, text.posZ);
	}

	public double getInterpPercent(double delta) {
		return this.getPreviousPercent() + ((this.getPercent() - this.getPreviousPercent()) * delta);
	}

	public double getPercent() {
		return (double) this.ticks / (double) this.lifetime;
	}

	public double getPreviousPercent() {
		return (double) (this.ticks + 1) / (double) this.lifetime;
	}

	public float getScale() {
		float out = this.amount / 100;
		return 1 + out;
	}

	public void move() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionX *= 0.95;
		this.motionY -= GRAVITY / 100;
		this.motionZ *= 0.95;
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
	}

	public boolean onUpdate() {
		if (this.ticks-- <= 0)
			return false;
		this.move();
		return true;
	}

	public void setAmount(String prefix, float amount, String name, boolean flag) {
		this.textColor = Colors.getTextColor(name) & 0xFFFFFF;
		this.backgroundColor = Colors.getBackgroundColor(name) & 0xFFFFFF;
		if (flag)
			this.display = prefix + (int) amount + " (" + name + ")";
		else
			this.display = prefix + (int) amount;
	}

	private void setupPos(EntityLivingBase entity) {
		this.ticks = this.lifetime;
		this.posX = entity.posX;
		this.posY = entity.posY;
		this.posZ = entity.posZ;
		this.prevPosX = entity.prevPosX;
		this.prevPosY = entity.prevPosY;
		this.prevPosZ = entity.prevPosZ;
		this.motionX = new Random().nextGaussian() / 24;
		this.motionY = new Random().nextGaussian() / 32;
		this.motionZ = new Random().nextGaussian() / 24;
	}
}
