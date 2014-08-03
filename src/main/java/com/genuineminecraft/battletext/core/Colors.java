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
		//		textColors.put("cactus", 0xBEBEBE);
		//		textColors.put("fire", 0xFF7F00);
		//		textColors.put("fall", 0xBEBEBE);
		//		textColors.put("drown", 0xBEBEBE);
		//		textColors.put("healing", 0x00A550);
		//		textColors.put("explosion", 0xFE2712);
		//		textColors.put("magic", 0xA020F0);
		//		textColors.put("mob", 0xFE2712);
		//		textColors.put("starve", 0xBEBEBE);
		//		textColors.put("arrow", 0xFE2712);
		//		textColors.put("melee", 0xFE2712);
		//		textColors.put("generic", 0xBEBEBE);
		//		backgroundColors.put("cactus", 0x0);
		//		backgroundColors.put("fire", 0x0);
		//		backgroundColors.put("fall", 0x0);
		//		backgroundColors.put("drown", 0x0);
		//		backgroundColors.put("healing", 0x0);
		//		backgroundColors.put("explosion", 0x0);
		//		backgroundColors.put("magic", 0x0);
		//		backgroundColors.put("mob", 0x0);
		//		backgroundColors.put("starve", 0x0);
		//		backgroundColors.put("arrow", 0x0);
		//		backgroundColors.put("melee", 0x0);
		//		backgroundColors.put("generic", 0x0);
	}
}
