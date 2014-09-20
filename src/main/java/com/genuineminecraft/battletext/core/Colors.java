package com.genuineminecraft.battletext.core;

import java.util.HashMap;
import java.util.Map;

public class Colors {

	public static Map<String, Integer> textColors = new HashMap<String, Integer>();
	public static Map<String, Integer> backgroundColors = new HashMap<String, Integer>();

	public static int getTextColor(String name) {
		if (!textColors.containsKey(name))
			textColors.put(name, 0xFF5020);
		return textColors.get(name);
	}

	public static int getBackgroundColor(String name) {
		if (!backgroundColors.containsKey(name))
			backgroundColors.put(name, 0);
		return backgroundColors.get(name);
	}

	public static void setTextColor(String name, int color) {
		textColors.put(name, color);
	}

	public static void setBackgroundColor(String name, int color) {
		backgroundColors.put(name, color);
	}

	static {
		textColors.put("arrow", 0xFE2712);
		textColors.put("cactus", 0xFF5020);
		textColors.put("drown", 0xFF5020);
		textColors.put("explosion", 0xFE2712);
		textColors.put("explosion.player", 0xFE2712);
		textColors.put("fall", 0xFF5020);
		textColors.put("generic", 0xFF5020);
		textColors.put("heal", 0x00A550);
		textColors.put("inFire", 0xFF7F00);
		textColors.put("inWall", 0xFF5020);
		textColors.put("indirectMagic", 0xA020F0);
		textColors.put("lava", 0x4F0000);
		textColors.put("magic", 0xA020F0);
		textColors.put("mob", 0xFF5020);
		textColors.put("onFire", 0xFF7F00);
		textColors.put("outOfWorld", 0);
		textColors.put("player", 0xFF5020);
		textColors.put("thrown", 0xFF5020);
		textColors.put("wither", 0x505050);
		backgroundColors.put("ofOfWorld", -1);
	}
}
