package com.genuineflix.bt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.genuineflix.bt.system.BattleTextSystem;
import com.genuineflix.bt.system.Text;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = BattleText.MODID, name = BattleText.NAME, version = BattleText.VERSION)
public class BattleText {

	@Instance(BattleText.MODID)
	public static BattleText instance;
	public static final String MODID = "BattleText";
	public static final String NAME = "Battle Text";
	public static final String VERSION = "1.0.8";
	public static File dir;
	public static Configuration config;

	@EventHandler
	public void pre(final FMLPreInitializationEvent event) {
		BattleText.dir = new File(event.getModConfigurationDirectory(), "BattleText");
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(BattleTextSystem.getInstance());
		MinecraftForge.EVENT_BUS.register(BattleTextSystem.getInstance());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void post(final FMLPostInitializationEvent event) {
		loadColors();
		saveColors();
		loadColors();
	}

	public void saveColors() {
		try {
			if (!BattleText.dir.exists())
				BattleText.dir.mkdirs();
			final File file = new File(BattleText.dir, "Colors.cfg");
			if (!file.exists())
				file.createNewFile();
			final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			final List<String> newList = new ArrayList<String>();
			for (final Entry<String, Integer> entry : Text.Colors.textColors.entrySet())
				newList.add(entry.getKey() + "=0x" + Integer.toHexString(entry.getValue()).toUpperCase());
			Collections.sort(newList);
			for (final String line : newList)
				((BufferedWriter) bw.append(line)).newLine();
			bw.close();
		}
		catch (final Exception e) {}
	}

	public void loadColors() {
		if (!BattleText.dir.exists())
			BattleText.dir.mkdirs();
		try {
			final File file = new File(BattleText.dir, "Colors.cfg");
			final BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#"))
					continue;
				final String[] values = line.split("=");
				if (values.length > 1)
					try {
						Text.Colors.setTextColor(values[0], Integer.decode(values[1]));
					}
				catch (final Exception e) {
					Text.Colors.setTextColor(values[0], Text.Colors.DEFAULT_COLOR);
				}
				else if (values.length > 0)
					Text.Colors.setTextColor(values[0], Text.Colors.DEFAULT_COLOR);
			}
			br.close();
		}
		catch (final Exception e) {}
	}
}
