package com.genuineminecraft.battletext.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class LivingHealEvent extends LivingEvent {

	public float amount;

	public LivingHealEvent(EntityLivingBase entity, float amount) {
		super(entity);
		this.amount = amount;
	}
}