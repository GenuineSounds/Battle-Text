package com.genuineflix.battle.render;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.genuineflix.battle.text.Text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

public class Renderer {

	public Renderer() {}

	public void render(final List<Text> renderList, final double partialTicks) {
		if (renderList == null || renderList.isEmpty())
			return;
		final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (final Text txt : renderList) {
			if (txt.getDistanceTo(Minecraft.getMinecraft().thePlayer) > 24)
				continue;
			RenderManager rm = Minecraft.getMinecraft().getRenderManager();
			final double x = rm.viewerPosX - (txt.prevPosX + (txt.posX - txt.prevPosX) * partialTicks);
			final double y = rm.viewerPosY - (txt.prevPosY + (txt.posY - txt.prevPosY) * partialTicks) - 2;
			final double z = rm.viewerPosZ - (txt.prevPosZ + (txt.posZ - txt.prevPosZ) * partialTicks);
			GL11.glTranslated(-x, -y, -z);
			GL11.glRotatef(rm.playerViewY + 180, 0F, -1F, 0F);
			GL11.glRotatef(rm.playerViewX, -1F, 0F, 0F);
			int alpha = (int) (txt.getPercent() * 0xFF) & 0xFF;
			if (alpha < 5)
				alpha = 5;
			final int color1 = txt.textColor | alpha << 24;
			final int color2 = txt.backgroundColor | alpha << 24;
			final int offX = -fr.getStringWidth(txt.display);
			final int offY = -4;
			double scale = 0.02;
			scale *= txt.getScale();
			GL11.glScaled(scale, -scale, scale);
			// Shadows
			fr.drawString(txt.display, offX + 1, offY, color2);
			fr.drawString(txt.display, offX, offY + 1, color2);
			fr.drawString(txt.display, offX, offY - 1, color2);
			fr.drawString(txt.display, offX - 1, offY, color2);
			// Main
			fr.drawString(txt.display, offX, offY, color1);
			GL11.glScaled(1.0 / scale, -1.0 / scale, 1.0 / scale);
			GL11.glRotatef(rm.playerViewX, 1F, 0F, 0F);
			GL11.glRotatef(rm.playerViewY - 180, 0F, 1F, 0F);
			GL11.glTranslated(x, y, z);
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
