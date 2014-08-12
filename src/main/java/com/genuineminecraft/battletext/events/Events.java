package com.genuineminecraft.battletext.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.genuineminecraft.battletext.core.Text;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Events {

	@SubscribeEvent
	public void entityHurt(LivingHurtEvent event) {
		if (Loader.isModLoaded("ClosedCaptions") && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			String name = "World";
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
			StringBuilder message = new StringBuilder();
			message.append("Damage: ");
			message.append(ChatFormatting.DARK_RED);
			message.append("-");
			message.append((int) event.ammount);
			message.append(" ");
			message.append(ChatFormatting.RESET);
			message.append("from ");
			message.append(name);
			FMLInterModComms.sendMessage("ClosedCaptions", "message", message.toString());
			return;
		}
		Text fx = new Text(event.entityLiving, event.source, event.ammount);
		BattleTextContainer.getInstance().addText(fx);
	}

	@SubscribeEvent
	public void entityHeal(LivingHealEvent event) {
		if (Loader.isModLoaded("ClosedCaptions") && event.entityLiving.equals(Minecraft.getMinecraft().thePlayer)) {
			String amount = Integer.toString((int) event.amount);
			StringBuilder message = new StringBuilder();
			message.append("Heal: ");
			message.append(ChatFormatting.GREEN.toString());
			message.append(amount);
			message.append(ChatFormatting.RESET.toString());
			message.append(" hp");
			FMLInterModComms.sendMessage("ClosedCaptions", "message", message.toString());
			return;
		}
		Text fx = new Text(event.entityLiving, event.amount);
		BattleTextContainer.getInstance().addText(fx);
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		BattleTextContainer.getInstance().tick(event.partialTicks);
	}
}
