package com.genuineminecraft.battletext.core;

import java.util.HashMap;
import java.util.Map;

public class Colors {

	public static Map<String, Integer> textColors = new HashMap<String, Integer>();
	public static Map<String, Integer> backgroundColors = new HashMap<String, Integer>();

	public static int getTextColor(String name) {
		if (textColors.containsKey(name))
			return textColors.get(name);
		else {
			textColors.put(name, 0xFFFFFF);
			return 0xFFFFFF;
		}
	}

	public static int getBackgroundColor(String name) {
		if (backgroundColors.containsKey(name))
			return backgroundColors.get(name);
		else {
			backgroundColors.put(name, 0);
			return 0;
		}
	}

	public static void setTextColor(String name, int color) {
		textColors.put(name, color);
	}

	public static void setBackgroundColor(String name, int color) {
		backgroundColors.put(name, color);
	}

	static {
		textColors.put("arrow", 0xFE2712);
		textColors.put("cactus", 0xBEBEBE);
		textColors.put("drown", 0xBEBEBE);
		textColors.put("explosion.player", 0xFE2712);
		textColors.put("fall", 0xBEBEBE);
		textColors.put("generic", 0xBEBEBE);
		textColors.put("healing", 0x00A550);
		textColors.put("inFire", 0xFF7F00);
		textColors.put("lava", 0x4F0000);
		textColors.put("magic", 0xA020F0);
		textColors.put("onFire", 0xFF7F00);
		textColors.put("outOfWorld", 0);
		textColors.put("wither", 0x505050);
		backgroundColors.put("ofOfWorld", -1);
	}
}
