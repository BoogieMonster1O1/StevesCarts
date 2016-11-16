package vswe.stevesvehicles.client.gui.screen;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.container.ContainerUpgrade;
import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.upgrade.effect.util.InterfaceEffect;
import vswe.stevesvehicles.upgrade.effect.util.InventoryEffect;

@SideOnly(Side.CLIENT)
public class GuiUpgrade extends GuiBase {
	public GuiUpgrade(InventoryPlayer invPlayer, TileEntityUpgrade upgrade) {
		super(new ContainerUpgrade(invPlayer, upgrade));
		this.upgrade = upgrade;
		setXSize(256);
		setYSize(190);
	}

	@Override
	public void drawGuiForeground(int x, int y) {
		GL11.glDisable(GL11.GL_LIGHTING);
		if (upgrade.getUpgrade() != null) {
			getFontRenderer().drawString(upgrade.getUpgrade().getTranslatedName(), 8, 6, 0x404040);
			InterfaceEffect gui = upgrade.getInterfaceEffect();
			if (gui != null) {
				gui.drawForeground(this);
				gui.drawMouseOver(this, x, y);
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private static final int SLOT_SRC_X = 1;
	private static final int SLOT_SRC_Y = 191;
	private static final int SLOT_SIZE = 18;
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/upgrade.png");

	@Override
	public void drawGuiBackground(float f, int x, int y) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		int left = getGuiLeft();
		int top = getGuiTop();
		ResourceHelper.bindResource(TEXTURE);
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		if (upgrade.getUpgrade() != null) {
			InventoryEffect inventory = upgrade.getInventoryEffect();
			if (inventory != null) {
				for (int i = 0; i < inventory.getInventorySize(); i++) {
					drawTexturedModalRect(left + inventory.getSlotX(i) - 1, top + inventory.getSlotY(i) - 1, SLOT_SRC_X, SLOT_SRC_Y, SLOT_SIZE, SLOT_SIZE);
				}
			}
			InterfaceEffect gui = upgrade.getInterfaceEffect();
			if (gui != null) {
				gui.drawBackground(this, x, y);
			}
		}
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}

	private TileEntityUpgrade upgrade;
}
