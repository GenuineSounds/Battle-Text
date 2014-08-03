package com.genuineminecraft.battletext;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

import com.genuineminecraft.battletext.events.LivingHealEvent;

public class Hooks {

	public static float onLivingHeal(EntityLivingBase entity, float amount) {
		LivingHealEvent event = new LivingHealEvent(entity, amount);
		return MinecraftForge.EVENT_BUS.post(event) ? 0 : event.amount;
	}
}
