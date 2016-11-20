package vswe.stevesvehicles.client.gui.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiContainerSpecial {
	public GuiBase(Container container) {
		super(container);
	}

	public void drawMouseOver(String str, int x, int y) {
		List text = new ArrayList();
		String[] split = str.split("\n");
		for (int i = 0; i < split.length; i++) {
			text.add(split[i]);
		}
		drawMouseOver(text, x, y);
	}

	public boolean inRect(int x, int y, int[] coords) {
		return coords != null && x >= coords[0] && x < coords[0] + coords[2] && y >= coords[1] && y < coords[1] + coords[3];
	}

	// TODO due to the scaling the mouse over may go outside the screen(since it
	// doesn't realize the mouse over should go to the left rather than to the
	// right when close to the right hand edge)
	public void drawMouseOver(List text, int x, int y) {
		GL11.glDisable(GL11.GL_LIGHTING);
		int var5 = 0;
		Iterator var6 = text.iterator();
		while (var6.hasNext()) {
			String var7 = (String) var6.next();
			int var8 = this.getFontRenderer().getStringWidth(var7);
			if (var8 > var5) {
				var5 = var8;
			}
		}
		int var15 = x + 10;
		int var16 = y;
		int var9 = 8;
		if (text.size() > 1) {
			var9 += 2 + (text.size() - 1) * 10;
		}
		this.zLevel = 300.0F;
		itemRender.zLevel = 300.0F;
		int var10 = -267386864;
		this.drawGradientRect(var15 - 3, var16 - 4, var15 + var5 + 3, var16 - 3, var10, var10);
		this.drawGradientRect(var15 - 3, var16 + var9 + 3, var15 + var5 + 3, var16 + var9 + 4, var10, var10);
		this.drawGradientRect(var15 - 3, var16 - 3, var15 + var5 + 3, var16 + var9 + 3, var10, var10);
		this.drawGradientRect(var15 - 4, var16 - 3, var15 - 3, var16 + var9 + 3, var10, var10);
		this.drawGradientRect(var15 + var5 + 3, var16 - 3, var15 + var5 + 4, var16 + var9 + 3, var10, var10);
		int var11 = 1347420415;
		int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
		this.drawGradientRect(var15 - 3, var16 - 3 + 1, var15 - 3 + 1, var16 + var9 + 3 - 1, var11, var12);
		this.drawGradientRect(var15 + var5 + 2, var16 - 3 + 1, var15 + var5 + 3, var16 + var9 + 3 - 1, var11, var12);
		this.drawGradientRect(var15 - 3, var16 - 3, var15 + var5 + 3, var16 - 3 + 1, var11, var11);
		this.drawGradientRect(var15 - 3, var16 + var9 + 2, var15 + var5 + 3, var16 + var9 + 3, var12, var12);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (int var13 = 0; var13 < text.size(); ++var13) {
			String var14 = (String) text.get(var13);
			this.getFontRenderer().drawStringWithShadow(var14, var15, var16, -1);
			if (var13 == 0) {
				var16 += 2;
			}
			var16 += 10;
		}
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public Minecraft getMinecraft() {
		return mc;
	}

	public FontRenderer getFontRenderer() {
		return fontRendererObj;
	}

	public void setXSize(int val) {
		xSize = val;
		this.guiLeft = (this.width - this.xSize) / 2;
	}

	public void setYSize(int val) {
		ySize = val;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}

	public int getGuiLeft() {
		return guiLeft;
	}

	public int getGuiTop() {
		return guiTop;
	}

	@Override
	protected final void drawGuiContainerForegroundLayer(int x, int y) {
		this.drawGuiForeground(x, y);
	}

	public void drawGuiForeground(int x, int y) {
	}

	public void applyColor(int color, float multiplier) {
		float a = (color >> 24 & 255) == 0 ? 1 : (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GL11.glColor4f(r * multiplier, g * multiplier, b * multiplier, a);
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		startScaling();
	}

	private int scaleX(float x) {
		float scale = getScale();
		x /= scale;
		x += getGuiLeft();
		x -= (this.width - this.xSize * scale) / (2 * scale);
		return (int) x;
	}

	private int scaleY(float y) {
		float scale = getScale();
		y /= scale;
		y += getGuiTop();
		y -= (this.height - this.ySize * scale) / (2 * scale);
		return (int) y;
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		super.drawScreen(scaleX(x), scaleY(y), f);
		// stop scale
		stopScaling();
	}

	protected float getScale() {
		net.minecraft.client.gui.ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		float w = scaledresolution.getScaledWidth() * 0.9F;
		float h = scaledresolution.getScaledHeight() * 0.9F;
		float multX = w / getXSize();
		float multY = h / getYSize();
		float mult = Math.min(multX, multY);
		if (mult > 1F) {
			mult = 1F;
		}
		return mult;
	}

	private void startScaling() {
		// start scale
		GL11.glPushMatrix();
		float scale = getScale();
		GL11.glScalef(scale, scale, 1);
		GL11.glTranslatef(-guiLeft, -guiTop, 0.0F);
		GL11.glTranslatef((this.width - this.xSize * scale) / (2 * scale), (this.height - this.ySize * scale) / (2 * scale), 0.0F);
	}

	private void stopScaling() {
		// stop scale
		GL11.glPopMatrix();
	}

	public void drawLine(int x1, int y1, int x2, int y2, int thickness, int color) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		applyColor(color, 1);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(1 + thickness * this.width / 500F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(x1, y1, 0);
		GL11.glVertex3f(x2, y2, 0);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	protected final void drawGuiContainerBackgroundLayer(float f, int x, int y) {
		this.drawGuiBackground(f, x, y);
	}

	public void drawGuiBackground(float f, int x, int y) {
	}

	@Override
	protected final void mouseClicked(int x, int y, int button) throws IOException {
		x = scaleX(x);
		y = scaleY(y);
		super.mouseClicked(x, y, button);
		this.mouseClick(x, y, button);
	}

	public void mouseClick(int x, int y, int button) {
	}

	protected void mouseMovedOrUp(int x, int y, final int button) {
		x = this.scaleX(x);
		y = this.scaleY(y);
		//super.mouseMovedOrUp(x, y, button);
		this.mouseMoved(x, y, button);
		this.mouseDragged(x, y, button);
	}

	private int myOwnEventButton = 0;
	private long myOwnTimeyWhineyThingy = 0L;
	private int myOwnTouchpadTimeWhineyThingy = 0;

	/**
	 * Handles mouse input.
	 * 
	 * @throws IOException
	 */
	@Override
	public void handleMouseInput() throws IOException {
		int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		if (Mouse.getEventButtonState()) {
			if (this.mc.gameSettings.touchscreen && this.myOwnTouchpadTimeWhineyThingy++ > 0) {
				return;
			}
			this.myOwnEventButton = Mouse.getEventButton();
			this.myOwnTimeyWhineyThingy = Minecraft.getSystemTime();
			this.mouseClicked(i, j, this.myOwnEventButton);
		} else if (Mouse.getEventButton() != -1) {
			if (this.mc.gameSettings.touchscreen && --this.myOwnTouchpadTimeWhineyThingy > 0) {
				return;
			}
			this.myOwnEventButton = -1;
			this.mouseMovedOrUp(i, j, Mouse.getEventButton());
		} else if (this.myOwnEventButton != -1 && this.myOwnTimeyWhineyThingy > 0L) {
			long k = Minecraft.getSystemTime() - this.myOwnTimeyWhineyThingy;
			this.mouseClickMove(i, j, this.myOwnEventButton, k);
		} else {
			this.mouseMovedOrUp(i, j, -1);
		}
	}

	@Override
	protected final void mouseClickMove(int x, int y, int button, long timeSinceClick) {
		x = scaleX(x);
		y = scaleY(y);
		super.mouseClickMove(x, y, button, timeSinceClick);
		this.mouseMoved(x, y, button);
		this.mouseDragged(x, y, button);
	}

	public void mouseMoved(int x, int y, int button) {
	}

	public void mouseDragged(int x, int y, int button) {
	}

	@Override
	protected final void keyTyped(char character, int extraInformation) throws IOException {
		if (extraInformation == 1 || !disableStandardKeyFunctionality()) {
			super.keyTyped(character, extraInformation);
		}
		this.keyPress(character, extraInformation);
	}

	public boolean disableStandardKeyFunctionality() {
		return false;
	}

	public void keyPress(char character, int extraInformation) {
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	public void enableKeyRepeat(boolean val) {
		Keyboard.enableRepeatEvents(val);
	}

	public float getZLevel() {
		return zLevel;
	}
	/*
	 * public void drawIcon(IIcon icon, int targetX, int targetY, float sizeX,
	 * float sizeY, float offsetX, float offsetY) { Tessellator tessellator =
	 * Tessellator.instance; tessellator.startDrawingQuads(); float x =
	 * icon.getMinU() + offsetX * (icon.getMaxU() - icon.getMinU()); float y =
	 * icon.getMinV() + offsetY * (icon.getMaxV() - icon.getMinV()); float width
	 * = (icon.getMaxU() - icon.getMinU()) * sizeX; float height =
	 * (icon.getMaxV() - icon.getMinV()) * sizeY;
	 * tessellator.addVertexWithUV(targetX + 0, targetY + 16 * sizeY,
	 * this.getZLevel(), x + 0, y + height); tessellator.addVertexWithUV(targetX
	 * + 16 * sizeX, targetY + 16 * sizeY, this.getZLevel(), x + width, y +
	 * height); tessellator.addVertexWithUV(targetX + 16 * sizeX, targetY + 0,
	 * this.getZLevel(), x + width, y + 0); tessellator.addVertexWithUV(targetX
	 * + 0, targetY + 0, this.getZLevel(), x + 0, y + 0); tessellator.draw(); }
	 */

	public void drawRectWithSourceOffset(int x, int y, int u, int v, RenderRotation rotation, int offsetX, int offsetY, int fullWidth, int fullHeight) {
		int w = fullWidth - offsetX;
		int h = fullHeight - offsetY;
		int srcW = w;
		int srcH = h;
		boolean rotateSize = false;
		switch (rotation) {
			case NORMAL:
				x += offsetX;
				y += offsetY;
				u += offsetX;
				v += offsetY;
				break;
			case ROTATE_90:
				x += 0;
				y += offsetX;
				u += offsetX;
				v += offsetY;
				rotateSize = true;
				break;
			case ROTATE_180:
				x += 0;
				y += 0;
				u += offsetX;
				v += offsetY;
				break;
			case ROTATE_270:
				x += offsetY;
				y += 0;
				u += offsetX;
				v += offsetY;
				rotateSize = true;
				break;
			case FLIP_HORIZONTAL:
				x += 0;
				y += offsetY;
				u += offsetX;
				v += offsetY;
				break;
			case ROTATE_90_FLIP:
				x += 0;
				y += 0;
				u += offsetX;
				v += offsetY;
				rotateSize = true;
				break;
			case FLIP_VERTICAL:
				x += offsetX;
				y += 0;
				u += offsetX;
				v += offsetY;
				break;
			case ROTATE_270_FLIP:
				x += offsetY;
				y += offsetX;
				u += offsetX;
				v += offsetY;
				rotateSize = true;
				break;
		}
		if (rotateSize) {
			int temp = w;
			w = h;
			h = temp;
		}
		drawRect(x, y, u, v, w, h, srcW, srcH, rotation, 0.00390625F);
	}

	public void drawRect(int[] target, int u, int v) {
		drawRect(target[0], target[1], u, v, target[2], target[3], RenderRotation.NORMAL);
	}

	public void drawRect(int x, int y, int u, int v, int w, int h, RenderRotation rotation) {
		drawRect(x, y, u, v, w, h, rotation, 0.00390625F);
	}

	public void drawRect(int x, int y, int u, int v, int w, int h, RenderRotation rotation, int textureSize) {
		drawRect(x, y, u, v, w, h, rotation, 1F / textureSize);
	}

	public void drawRectWithTextureSize(int x, int y, int u, int v, int w, int h, int textureSize) {
		drawRect(x, y, u, v, w, h, RenderRotation.NORMAL, 1F / textureSize);
	}

	private void drawRect(int x, int y, int u, int v, int w, int h, RenderRotation rotation, float multiplier) {
		drawRect(x, y, u, v, w, h, w, h, rotation, multiplier);
	}

	private void drawRect(int x, int y, int u, int v, int w1, int h1, int w2, int h2, RenderRotation rotation, float multiplier) {
		double a = (u + 0) * multiplier;
		double b = (u + w2) * multiplier;
		double c = (v + h2) * multiplier;
		double d = (v + 0) * multiplier;
		double[] ptA = new double[] { a, c };
		double[] ptB = new double[] { b, c };
		double[] ptC = new double[] { b, d };
		double[] ptD = new double[] { a, d };
		double[] pt1, pt2, pt3, pt4;
		switch (rotation) {
			default:
			case NORMAL:
				pt1 = ptA;
				pt2 = ptB;
				pt3 = ptC;
				pt4 = ptD;
				break;
			case ROTATE_90:
				pt1 = ptB;
				pt2 = ptC;
				pt3 = ptD;
				pt4 = ptA;
				break;
			case ROTATE_180:
				pt1 = ptC;
				pt2 = ptD;
				pt3 = ptA;
				pt4 = ptB;
				break;
			case ROTATE_270:
				pt1 = ptD;
				pt2 = ptA;
				pt3 = ptB;
				pt4 = ptC;
				break;
			case FLIP_HORIZONTAL:
				pt1 = ptB;
				pt2 = ptA;
				pt3 = ptD;
				pt4 = ptC;
				break;
			case ROTATE_90_FLIP:
				pt1 = ptA;
				pt2 = ptD;
				pt3 = ptC;
				pt4 = ptB;
				break;
			case FLIP_VERTICAL:
				pt1 = ptD;
				pt2 = ptC;
				pt3 = ptB;
				pt4 = ptA;
				break;
			case ROTATE_270_FLIP:
				pt1 = ptC;
				pt2 = ptB;
				pt3 = ptA;
				pt4 = ptD;
				break;
		}
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((x + 0), y + h1, this.zLevel).tex(pt1[0], pt1[1]).endVertex();
		vertexbuffer.pos((x + w1), y + h1, this.zLevel).tex(pt2[0], pt2[1]).endVertex();
		vertexbuffer.pos((x + w1), y + 0, this.zLevel).tex(pt3[0], pt3[1]).endVertex();
		vertexbuffer.pos((x + 0), y + 0, this.zLevel).tex(pt4[0], pt4[1]).endVertex();
		tessellator.draw();
	}

	public void setZLevel(int ZLevel) {
		this.zLevel = ZLevel;
	}

	public static enum RenderRotation {
		NORMAL, ROTATE_90, ROTATE_180, ROTATE_270, FLIP_HORIZONTAL, ROTATE_90_FLIP, FLIP_VERTICAL, ROTATE_270_FLIP;
		public RenderRotation getNextRotation() {
			switch (this) {
				default:
				case NORMAL:
					return ROTATE_90;
				case ROTATE_90:
					return ROTATE_180;
				case ROTATE_180:
					return ROTATE_270;
				case ROTATE_270:
					return NORMAL;
				case FLIP_HORIZONTAL:
					return ROTATE_90_FLIP;
				case ROTATE_90_FLIP:
					return FLIP_VERTICAL;
				case FLIP_VERTICAL:
					return ROTATE_270_FLIP;
				case ROTATE_270_FLIP:
					return FLIP_HORIZONTAL;
			}
		}

		public RenderRotation getPreviousRotation() {
			return getNextRotation().getNextRotation().getNextRotation();
		}

		public RenderRotation getFlippedRotation() {
			switch (this) {
				default:
				case NORMAL:
					return FLIP_HORIZONTAL;
				case ROTATE_90:
					return ROTATE_90_FLIP;
				case ROTATE_180:
					return FLIP_VERTICAL;
				case ROTATE_270:
					return ROTATE_270_FLIP;
				case FLIP_HORIZONTAL:
					return NORMAL;
				case ROTATE_90_FLIP:
					return ROTATE_90;
				case FLIP_VERTICAL:
					return ROTATE_180;
				case ROTATE_270_FLIP:
					return ROTATE_270;
			}
		}
	}

	protected void setupScissor(int[] target) {
		setupScissor(target[0], target[1], target[2], target[3]);
	}

	protected void setupScissor(int x, int y, int w, int h) {
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		float scale = getScale();
		double scaleX = Minecraft.getMinecraft().displayWidth / scaledresolution.getScaledWidth_double();
		double scaleY = Minecraft.getMinecraft().displayHeight / scaledresolution.getScaledHeight_double();
		int scissorW = (int) (w * scaleX * scale);
		int scissorH = (int) (h * scaleY * scale);
		double scissorX = x * scaleX * scale;
		double scissorY = y * scaleY * scale;
		scissorX += (((width - getXSize() * scale) / (2 * scale)) * scale) * scaleX;
		scissorY += (((height - getYSize() * scale) / (2 * scale)) * scale) * scaleY;
		scissorY = Minecraft.getMinecraft().displayHeight - (scissorY + scissorH);
		GL11.glScissor((int) scissorX, (int) scissorY, scissorW, scissorH);
	}

	protected void stopScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	protected void startScissor() {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
}
