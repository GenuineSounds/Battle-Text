package com.genuineminecraft.battletext.core;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

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
		setupPos(entity);
		String name = damageSource.damageType;
		setAmount("-", damage, name, false);
		this.amount = damage;
	}

	public Text(EntityLivingBase entity, float healing) {
		setupPos(entity);
		setAmount("+", healing, "heal", false);
		amount = healing;
	}

	public void setAmount(String prefix, float amount, String name, boolean flag) {
		textColor = Colors.getTextColor(name) & 0xFFFFFF;
		backgroundColor = Colors.getBackgroundColor(name) & 0xFFFFFF;
		if (flag)
			display = prefix + (int) amount + " (" + name + ")";
		else
			display = prefix + (int) amount;
	}

	private void setupPos(EntityLivingBase entity) {
		ticks = lifetime;
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
		prevPosX = entity.prevPosX;
		prevPosY = entity.prevPosY;
		prevPosZ = entity.prevPosZ;
		motionX = new Random().nextGaussian() / 24;
		motionY = new Random().nextGaussian() / 32;
		motionZ = new Random().nextGaussian() / 24;
	}

	public boolean onUpdate() {
		if (ticks-- <= 0)
			return false;
		move();
		return true;
	}

	public void move() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionX *= 0.95;
		motionY -= GRAVITY / 100;
		motionZ *= 0.95;
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
	}

	public float getScale() {
		float out = amount / 100;
		return 1 + out;
	}

	public double getPercent() {
		return (double) this.ticks / (double) this.lifetime;
	}

	public double getPreviousPercent() {
		return (double) (this.ticks + 1) / (double) this.lifetime;
	}

	public double getInterpPercent(double delta) {
		return this.getPreviousPercent() + ((this.getPercent() - this.getPreviousPercent()) * delta);
	}

	public double getDistanceTo(Entity entity) {
		return getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public double getDistanceTo(Text text) {
		return getDistanceTo(text.posX, text.posY, text.posZ);
	}

	public double getDistanceTo(double posX, double posY, double posZ) {
		double distanceX = this.posX - posX;
		double distanceY = this.posY - posY;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
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
}
