package com.genuineflix.battle.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.genuineflix.battle.render.Renderer;
import com.genuineflix.battle.text.Text;
import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class System {

	private static boolean ccIsLoaded() {
		return Loader.isModLoaded(System.CC_MOD_NAME);
	}

	private static final String CC_MOD_NAME = "ClosedCaption";
	private static final String CC_DIRECT_MESSAGE_KEY = "[Direct]";
	public static final System instance = new System();
	private final Renderer renderer = new Renderer();
	private final List<Text> textList = new ArrayList<Text>();
	private ImmutableList<Text> renderList;
	private long tick;

	private System() {}

	private StringBuilder beginDamageMessage(final DamageSource source) {
		final StringBuilder message = new StringBuilder();
		if (!(source instanceof EntityDamageSource))
			return message;
		final EntityDamageSource nds = (EntityDamageSource) source;
		Entity src = null;
		if (nds instanceof EntityDamageSourceIndirect)
			src = ((EntityDamageSourceIndirect) nds).getEntity();
		else
			src = nds.getSourceOfDamage();
		if (src instanceof EntityPlayer)
			message.append(damageSourceName(((EntityPlayer) src).getDisplayName().getFormattedText()));
		else
			message.append(damageSourceName(src.getCommandSenderEntity().getName()));
		return message;
	}

	private String constructDamageMessage(final DamageSource source) {
		final StringBuilder message = beginDamageMessage(source);
		message.append(constructDamageTypes(source));
		message.append(": ");
		message.append(ChatFormatting.DARK_RED);
		return message.toString();
	}

	private String constructDamageTypes(final DamageSource source) {
		final String[] tmps = source.getDamageType().split("\\.");
		final StringBuilder sourceTypes = new StringBuilder();
		for (final String string : tmps)
			sourceTypes.append(string.substring(0, 1).toUpperCase() + string.substring(1));
		return sourceTypes.toString().replaceAll("[A-Z]", " $0").trim();
	}

	private String damageSourceName(final String name) {
		final StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(ChatFormatting.BLUE);
		nameBuilder.append(name);
		nameBuilder.append(ChatFormatting.RESET);
		nameBuilder.append(" -> ");
		return nameBuilder.toString();
	}

	@SubscribeEvent
	public void entityHeal(final LivingHealEvent event) {
		if (event.amount <= 0)
			return;
		if (System.ccIsLoaded() && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			final NBTTagCompound tag = new NBTTagCompound();
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
			final NBTTagCompound tag = new NBTTagCompound();
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

	@SubscribeEvent
	public void renderWorldEvent(final RenderWorldLastEvent event) {
		tick();
		renderer.render(renderList, event.partialTicks);
	}

	public void tick() {
		final long tick = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
		if (this.tick == tick)
			return;
		this.tick = tick;
		synchronized (textList) {
			final List<Text> removalQueue = new ArrayList<Text>();
			for (final Text text : textList)
				if (!text.onUpdate())
					removalQueue.add(text);
			textList.removeAll(removalQueue);
			Collections.sort(textList);
			renderList = ImmutableList.copyOf(textList);
		}
	}
}
