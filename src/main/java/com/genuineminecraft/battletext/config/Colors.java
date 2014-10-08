package com.genuineminecraft.battletext.config;

import java.util.HashMap;
import java.util.Map;

public class Colors {

	public static int DEFAULT_COLOR = 0xFF5020;
	public static Map<String, Integer> textColors = new HashMap<String, Integer>();
	public static Map<String, Integer> backgroundColors = new HashMap<String, Integer>();

	public static int getBackgroundColor(String name) {
		if (!backgroundColors.containsKey(name))
			backgroundColors.put(name, 0);
		return backgroundColors.get(name);
	}

	public static int getTextColor(String name) {
		if (!textColors.containsKey(name))
			textColors.put(name, DEFAULT_COLOR);
		return textColors.get(name);
	}

	public static void setBackgroundColor(String name, int color) {
		backgroundColors.put(name, color);
	}

	public static void setTextColor(String name, int color) {
		textColors.put(name, color);
	}

	static {
		textColors.put("arrow", 0xFE2712);
		textColors.put("cactus", DEFAULT_COLOR);
		textColors.put("drown", DEFAULT_COLOR);
		textColors.put("explosion", 0xFE2712);
		textColors.put("explosion.player", 0xFE2712);
		textColors.put("fall", DEFAULT_COLOR);
		textColors.put("generic", DEFAULT_COLOR);
		textColors.put("heal", 0x00A550);
		textColors.put("inFire", 0xFF7F00);
		textColors.put("inWall", DEFAULT_COLOR);
		textColors.put("indirectMagic", 0xA020F0);
		textColors.put("lava", 0x4F0000);
		textColors.put("magic", 0xA020F0);
		textColors.put("mob", DEFAULT_COLOR);
		textColors.put("onFire", 0xFF7F00);
		textColors.put("outOfWorld", 0);
		textColors.put("player", DEFAULT_COLOR);
		textColors.put("thrown", DEFAULT_COLOR);
		textColors.put("wither", 0x505050);
		backgroundColors.put("ofOfWorld", -1);
	}
}
