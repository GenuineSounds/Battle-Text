package com.genuineminecraft.battletext.asm;

import java.util.Arrays;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.7.10")
public class LoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { Transformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return BTDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> paramMap) {}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	public static class BTDummyContainer extends DummyModContainer {

		public BTDummyContainer() {
			super(new ModMetadata());
			ModMetadata md = getMetadata();
			md.autogenerated = true;
			md.authorList = Arrays.asList("Genuine");
			md.modId = "<BattleText ASM>";
			md.name = md.description = "BattleText HealingCore";
			md.version = "1";
		}

		@Override
		public boolean registerBus(EventBus bus, LoadController controller) {
			bus.register(this);
			return true;
		}

		@Subscribe
		public void construction(FMLConstructionEvent evt) {}
	}
}