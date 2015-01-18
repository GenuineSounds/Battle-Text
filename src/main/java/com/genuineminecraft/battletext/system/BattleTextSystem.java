package com.genuineminecraft.battletext.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BattleTextSystem {

	public static String cap(String in) {
		return in.substring(0, 1).toUpperCase() + in.substring(1);
	}

	public static boolean ccIsLoaded() {
		return Loader.isModLoaded(BattleTextSystem.CC_MOD_NAME);
	}

	public static BattleTextSystem getInstance() {
		if (BattleTextSystem.instance == null)
			BattleTextSystem.instance = new BattleTextSystem();
		return BattleTextSystem.instance;
	}

	public static final String CC_MOD_NAME = "ClosedCaptions";
	private static BattleTextSystem instance;
	public long time = 0L;
	public List<Text> textList = Collections.synchronizedList(new ArrayList<Text>());

	public BattleTextSystem() {}

	public synchronized void addText(Text txt) {
		if (txt.amount >= 0)
			textList.add(txt);
	}

	@SubscribeEvent
	public void entityHeal(LivingHealEvent event) {
		if (BattleTextSystem.ccIsLoaded() && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			String amount = Integer.toString((int) event.amount);
			StringBuilder message = new StringBuilder();
			message.append("Healing: ");
			message.append(ChatFormatting.GREEN.toString());
			message.append(amount);
			message.append(ChatFormatting.RESET.toString());
			FMLInterModComms.sendMessage(BattleTextSystem.CC_MOD_NAME, "message", message.toString());
			return;
		}
		addText(new Text(event.entityLiving, event.amount));
	}

	@SubscribeEvent
	public void entityHurt(LivingHurtEvent event) {
		if (BattleTextSystem.ccIsLoaded() && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			String name = "";
			if (event.source instanceof EntityDamageSource) {
				EntityDamageSource nds = (EntityDamageSource) event.source;
				Entity src = null;
				if (nds instanceof EntityDamageSourceIndirect)
					src = ((EntityDamageSourceIndirect) nds).getEntity();
				else
					src = nds.getSourceOfDamage();
				if (src instanceof EntityPlayer)
					name = ((EntityPlayer) src).getDisplayName();
				else
					name = src.getCommandSenderName();
			}
			String[] tmps = event.source.getDamageType().split("\\.");
			String out = "";
			for (String string : tmps)
				out += BattleTextSystem.cap(string);
			out = I18n.format(out.replaceAll("[A-Z]", " $0").trim());
			if (name.isEmpty())
				name = out;
			else
				name = ChatFormatting.BLUE + name + ChatFormatting.RESET + " -> " + out;
			StringBuilder message = new StringBuilder();
			message.append(name);
			message.append(": ");
			message.append(ChatFormatting.DARK_RED);
			message.append((int) event.ammount);
			message.append(ChatFormatting.RESET);
			FMLInterModComms.sendMessage(BattleTextSystem.CC_MOD_NAME, "message", message.toString());
			return;
		}
		addText(new Text(event.entityLiving, event.source, event.ammount));
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		tick(event.partialTicks);
	}

	public void renderText(float delta) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (Text txt : textList) {
			if (txt.getDistanceTo(Minecraft.getMinecraft().thePlayer) > 32)
				continue;
			double x = RenderManager.renderPosX - (txt.prevPosX + (txt.posX - txt.prevPosX) * delta);
			double y = RenderManager.renderPosY - (txt.prevPosY + (txt.posY - txt.prevPosY) * delta) - 2;
			double z = RenderManager.renderPosZ - (txt.prevPosZ + (txt.posZ - txt.prevPosZ) * delta);
			GL11.glTranslated(-x, -y, -z);
			GL11.glRotatef(RenderManager.instance.playerViewY + 180, 0F, -1F, 0F);
			GL11.glRotatef(RenderManager.instance.playerViewX, -1F, 0F, 0F);
			int alpha = (int) (txt.getPercent() * 0xFF) & 0xFF;
			if (alpha < 5)
				alpha = 5;
			int color1 = txt.textColor | alpha << 24;
			int color2 = txt.backgroundColor | alpha << 24;
			int offX = -fr.getStringWidth(txt.display);
			int offY = -4;
			double scale = 0.0175;
			scale *= txt.getScale();
			GL11.glScaled(scale, -scale, scale);
			// Shadows
			fr.drawString(txt.display, offX + 1, offY, color2);
			fr.drawString(txt.display, offX, offY + 1, color2);
			fr.drawString(txt.display, offX, offY - 1, color2);
			fr.drawString(txt.display, offX - 1, offY, color2);
			// Main
			fr.drawString(txt.display, offX, offY, color1);
			GL11.glScaled(1.0 / scale, -1.0 / scale, 1.0 / scale);
			GL11.glRotatef(RenderManager.instance.playerViewX, 1F, 0F, 0F);
			GL11.glRotatef(RenderManager.instance.playerViewY - 180, 0F, 1F, 0F);
			GL11.glTranslated(x, y, z);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	public synchronized void tick(float deltaTime) {
		if (RenderManager.instance == null || RenderManager.instance.worldObj == null)
			return;
		long tick = RenderManager.instance.worldObj.getTotalWorldTime();
		if (time != tick) {
			List<Text> removalQueue = new ArrayList<Text>();
			for (Text caption : textList)
				if (!caption.onUpdate())
					removalQueue.add(caption);
			time = tick;
			textList.removeAll(removalQueue);
			removalQueue.clear();
			Collections.sort(textList);
		}
		renderText(deltaTime);
	}
}
