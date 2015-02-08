package com.genuineflix.battle.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class Text implements Comparable<Text> {

	public static class Colors {

		public static int getBackgroundColor(final String name) {
			if (!Colors.backgroundColors.containsKey(name))
				Colors.backgroundColors.put(name, 0);
			return Colors.backgroundColors.get(name);
		}

		public static int getTextColor(final String name) {
			if (!Colors.textColors.containsKey(name))
				Colors.textColors.put(name, Colors.DEFAULT_COLOR);
			return Colors.textColors.get(name);
		}

		public static void setBackgroundColor(final String name, final int color) {
			Colors.backgroundColors.put(name, color);
		}

		public static void setTextColor(final String name, final int color) {
			Colors.textColors.put(name, color);
		}

		public static int DEFAULT_COLOR = 0xFF5020;
		public static Map<String, Integer> textColors = new HashMap<String, Integer>();
		public static Map<String, Integer> backgroundColors = new HashMap<String, Integer>();
		static {
			Colors.textColors.put("arrow", 0xFE2712);
			Colors.textColors.put("cactus", Colors.DEFAULT_COLOR);
			Colors.textColors.put("drown", Colors.DEFAULT_COLOR);
			Colors.textColors.put("explosion", 0xFE2712);
			Colors.textColors.put("explosion.player", 0xFE2712);
			Colors.textColors.put("fall", Colors.DEFAULT_COLOR);
			Colors.textColors.put("generic", Colors.DEFAULT_COLOR);
			Colors.textColors.put("heal", 0x00A550);
			Colors.textColors.put("inFire", 0xFF7F00);
			Colors.textColors.put("inWall", Colors.DEFAULT_COLOR);
			Colors.textColors.put("indirectMagic", 0xA020F0);
			Colors.textColors.put("lava", 0x4F0000);
			Colors.textColors.put("magic", 0xA020F0);
			Colors.textColors.put("mob", Colors.DEFAULT_COLOR);
			Colors.textColors.put("onFire", 0xFF7F00);
			Colors.textColors.put("outOfWorld", 0);
			Colors.textColors.put("player", Colors.DEFAULT_COLOR);
			Colors.textColors.put("thrown", Colors.DEFAULT_COLOR);
			Colors.textColors.put("wither", 0x505050);
			Colors.backgroundColors.put("ofOfWorld", -1);
		}
	}

	public static float gravity = 0.5F;
	public static float fontScale = 1.0F;
	public static boolean sideVariance = true;
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
	private final Random random = new Random();

	public Text(final EntityLivingBase entity, final DamageSource damageSource, final float damage) {
		setupPos(entity);
		final String name = damageSource.damageType;
		setAmount("-", damage, name, false);
		amount = damage;
	}

	public Text(final EntityLivingBase entity, final float healing) {
		setupPos(entity);
		setAmount("+", healing, "heal", false);
		amount = healing;
	}

	@Override
	public int compareTo(final Text text) {
		if (Minecraft.getMinecraft().thePlayer == null)
			return 0;
		final double distance1 = this.getDistanceTo(Minecraft.getMinecraft().thePlayer);
		final double distance2 = text.getDistanceTo(Minecraft.getMinecraft().thePlayer);
		if (distance1 == distance2)
			return 0;
		else if (distance2 > distance1)
			return 1;
		else
			return -1;
	}

	public double getDistanceTo(final double posX, final double posY, final double posZ) {
		final double distanceX = this.posX - posX;
		final double distanceY = this.posY - posY;
		final double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}

	public double getDistanceTo(final Entity entity) {
		return this.getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public double getDistanceTo(final Text text) {
		return this.getDistanceTo(text.posX, text.posY, text.posZ);
	}

	public double getInterpPercent(final double delta) {
		return getPreviousPercent() + (getPercent() - getPreviousPercent()) * delta;
	}

	public double getPercent() {
		return (double) ticks / (double) lifetime;
	}

	public double getPreviousPercent() {
		return (double) (ticks + 1) / (double) lifetime;
	}

	public float getScale() {
		final float out = amount / 100;
		return (1 + out) * fontScale;
	}

	public void move() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionX *= 0.95;
		motionY -= Text.gravity / 100;
		motionZ *= 0.95;
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
	}

	public boolean onUpdate() {
		if (ticks-- <= 0)
			return false;
		move();
		return true;
	}

	public void setAmount(final String prefix, final float amount, final String name, final boolean flag) {
		textColor = Colors.getTextColor(name) & 0xFFFFFF;
		backgroundColor = Colors.getBackgroundColor(name) & 0xFFFFFF;
		if (flag)
			display = prefix + (int) amount + " (" + name + ")";
		else
			display = prefix + (int) amount;
	}

	private void setupPos(final EntityLivingBase entity) {
		ticks = lifetime;
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
		prevPosX = entity.prevPosX;
		prevPosY = entity.prevPosY;
		prevPosZ = entity.prevPosZ;
		motionX = random.nextGaussian() / 24;
		motionY = random.nextGaussian() / 32;
		motionZ = random.nextGaussian() / 24;
	}
}
