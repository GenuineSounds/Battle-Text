package com.genuineflix.battle.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import org.lwjgl.opengl.GL11;

import com.genuineflix.battle.text.Text;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class System {

	public static boolean ccIsLoaded() {
		return Loader.isModLoaded(System.CC_MOD_NAME);
	}

	public static final String CC_MOD_NAME = "ClosedCaption";
	public static final String CC_DIRECT_MESSAGE_KEY = "[Direct]";
	public static final System instance = new System();
	private List<Text> textList = Collections.synchronizedList(new ArrayList<Text>());

	public System() {}

	@SubscribeEvent
	public void entityHeal(final LivingHealEvent event) {
		if (event.amount <= 0)
			return;
		if (System.ccIsLoaded() && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("type", "healing");
			tag.setFloat("amount", event.amount);
			FMLInterModComms.sendMessage(System.CC_MOD_NAME, System.CC_DIRECT_MESSAGE_KEY, tag);
			return;
		}
		synchronized (textList) {
			textList.add(new Text(event.entityLiving, event.amount));
		}
	}

	@SubscribeEvent
	public void entityHurt(final LivingHurtEvent event) {
		if (event.ammount <= 0)
			return;
		if (System.ccIsLoaded() && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("type", "damage");
			tag.setFloat("amount", event.ammount);
			tag.setString("message", constructDamageMessage(event.source));
			FMLInterModComms.sendMessage(System.CC_MOD_NAME, System.CC_DIRECT_MESSAGE_KEY, tag);
			return;
		}
		synchronized (textList) {
			textList.add(new Text(event.entityLiving, event.source, event.ammount));
		}
	}

	private String constructDamageMessage(DamageSource source) {
		StringBuilder message = beginDamageMessage(source);
		message.append(constructDamageTypes(source));
		message.append(": ");
		message.append(ChatFormatting.DARK_RED);
		return message.toString();
	}

	private String constructDamageTypes(DamageSource source) {
		final String[] tmps = source.getDamageType().split("\\.");
		StringBuilder sourceTypes = new StringBuilder();
		for (final String string : tmps)
			sourceTypes.append(string.substring(0, 1).toUpperCase() + string.substring(1));
		return sourceTypes.toString().replaceAll("[A-Z]", " $0").trim();
	}

	private StringBuilder beginDamageMessage(DamageSource source) {
		StringBuilder message = new StringBuilder();
		if (!(source instanceof EntityDamageSource))
			return message;
		final EntityDamageSource nds = (EntityDamageSource) source;
		Entity src = null;
		if (nds instanceof EntityDamageSourceIndirect)
			src = ((EntityDamageSourceIndirect) nds).getEntity();
		else
			src = nds.getSourceOfDamage();
		if (src instanceof EntityPlayer)
			message.append(damageSourceName(((EntityPlayer) src).getDisplayName()));
		else
			message.append(damageSourceName(src.getCommandSenderName()));
		return message;
	}

	private String damageSourceName(String name) {
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(ChatFormatting.BLUE);
		nameBuilder.append(name);
		nameBuilder.append(ChatFormatting.RESET);
		nameBuilder.append(" -> ");
		return nameBuilder.toString();
	}

	@SubscribeEvent
	public void render(final RenderWorldLastEvent event) {
		final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		synchronized (textList) {
			for (final Text txt : textList) {
				if (txt.getDistanceTo(Minecraft.getMinecraft().thePlayer) > 32)
					continue;
				final double x = RenderManager.renderPosX - (txt.prevPosX + (txt.posX - txt.prevPosX) * event.partialTicks);
				final double y = RenderManager.renderPosY - (txt.prevPosY + (txt.posY - txt.prevPosY) * event.partialTicks) - 2;
				final double z = RenderManager.renderPosZ - (txt.prevPosZ + (txt.posZ - txt.prevPosZ) * event.partialTicks);
				GL11.glTranslated(-x, -y, -z);
				GL11.glRotatef(RenderManager.instance.playerViewY + 180, 0F, -1F, 0F);
				GL11.glRotatef(RenderManager.instance.playerViewX, -1F, 0F, 0F);
				int alpha = (int) (txt.getPercent() * 0xFF) & 0xFF;
				if (alpha < 5)
					alpha = 5;
				final int color1 = txt.textColor | alpha << 24;
				final int color2 = txt.backgroundColor | alpha << 24;
				final int offX = -fr.getStringWidth(txt.display);
				final int offY = -4;
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
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void tick(final ClientTickEvent event) {
		if (event.phase == Phase.START)
			return;
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame())
			return;
		final List<Text> removalQueue = new ArrayList<Text>();
		synchronized (textList) {
			for (final Text text : textList)
				if (!text.onUpdate())
					removalQueue.add(text);
			textList.removeAll(removalQueue);
			Collections.sort(textList);
		}
		removalQueue.clear();
	}
}
