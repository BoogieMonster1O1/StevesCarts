package stevesvehicles.client.gui.screen;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.localization.entry.block.LocalizationDistributor;
import stevesvehicles.common.blocks.tileentitys.TileEntityDistributor;
import stevesvehicles.common.blocks.tileentitys.TileEntityManager;
import stevesvehicles.common.blocks.tileentitys.distributor.DistributorSetting;
import stevesvehicles.common.blocks.tileentitys.distributor.DistributorSide;
import stevesvehicles.common.container.ContainerDistributor;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.network.PacketType;

@SideOnly(Side.CLIENT)
public class GuiDistributor extends GuiBase {
	public GuiDistributor(TileEntityDistributor distributor) {
		super(new ContainerDistributor(distributor));
		setXSize(256);
		setYSize(186);
		this.distributor = distributor;
	}

	@Override
	public void drawGuiForeground(int x, int y) {
		GL11.glDisable(GL11.GL_LIGHTING);
		getFontRenderer().drawString(LocalizationDistributor.TITLE.translate(), 8, 6, 0x404040);
		TileEntityManager[] inventories = distributor.getInventories();
		if (inventories.length == 0) {
			getFontRenderer().drawString(LocalizationDistributor.NOT_CONNECTED.translate(), 30, 40, 0xFF4040);
		}
		if (mouseOverText != null && !mouseOverText.isEmpty()) {
			drawMouseOver(mouseOverText, x - getGuiLeft(), y - getGuiTop());
		}
		mouseOverText = null;
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private String mouseOverText;

	private void drawMouseMover(String str, int x, int y, int[] rect) {
		if (inRect(x, y, rect)) {
			mouseOverText = str;
		}
	}

	private static final int TEXTURE_SPACING = 1;
	private static final int SIDE_BORDER_SRC_X = 1;
	private static final int SIDE_BORDER_SRC_Y = 187;
	private static final int SIDE_SRC_X = 47;
	private static final int SIDE_SRC_Y = 187;
	private static final int SIDE_SIZE = 18;
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/distributor.png");

	@Override
	public void drawGuiBackground(float f, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = getGuiLeft();
		int top = getGuiTop();
		ResourceHelper.bindResource(TEXTURE);
		drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
		x -= getGuiLeft();
		y -= getGuiTop();
		TileEntityManager[] inventories = distributor.getInventories();
		ArrayList<DistributorSide> sides = distributor.getSides();
		int id = 0;
		for (DistributorSide side : sides) {
			if (side.isEnabled(distributor)) {
				int[] box = getSideBoxRect(id);
				int srcX = 0;
				if (inRect(x, y, box)) {
					srcX = box[2] + TEXTURE_SPACING;
				}
				drawTexturedModalRect(left + box[0], top + box[1], SIDE_BORDER_SRC_X + srcX, SIDE_BORDER_SRC_Y, box[2], box[3]);
				drawTexturedModalRect(left + box[0] + 2, top + box[1] + 2, SIDE_SRC_X + (SIDE_SIZE + TEXTURE_SPACING) * side.getId(), SIDE_SRC_Y, SIDE_SIZE, SIDE_SIZE);
				drawMouseMover(LocalizationDistributor.SIDE_NAME.translate(side.getName()) + (activeId != -1 ? "\n[" + LocalizationDistributor.DROP_INSTRUCTION.translate() + "]" : ""), x, y, box);
				int settingCount = 0;
				for (DistributorSetting setting : DistributorSetting.settings) {
					if (setting.isEnabled(distributor)) {
						if (side.isSet(setting.getId())) {
							int[] settingsBox = getActiveSettingBoxRect(id, settingCount++);
							drawSetting(setting, settingsBox, inRect(x, y, settingsBox));
							drawMouseMover(setting.getName(inventories) + "\n[" + LocalizationDistributor.REMOVE_INSTRUCTION.translate() + "]", x, y, settingsBox);
						}
					}
				}
				id++;
			}
		}
		for (DistributorSetting setting : DistributorSetting.settings) {
			if (setting.isEnabled(distributor)) {
				int[] box = getSettingBoxRect(setting.getImageId(), setting.getIsTop());
				drawSetting(setting, box, inRect(x, y, box));
				drawMouseMover(setting.getName(inventories), x, y, box);
			}
		}
		if (activeId != -1) {
			DistributorSetting setting = DistributorSetting.settings.get(activeId);
			drawSetting(setting, new int[] { x - 8, y - 8, 16, 16 }, true);
		}
	}

	private static final int SETTING_SRC_X = 1;
	private static final int SETTING_SRC_Y = 210;
	private static final int SIDE_TYPE_SIZE = 12;
	private static final int SIDE_TYPE_SRC_X = 69;
	private static final int SIDE_TYPE_SRC_Y = 210;

	private void drawSetting(DistributorSetting setting, int[] box, boolean hover) {
		int j = getGuiLeft();
		int k = getGuiTop();
		int srcX = 0;
		if (!setting.getIsTop()) {
			srcX += (box[2] + TEXTURE_SPACING) * 2;
		}
		if (hover) {
			srcX += box[2] + TEXTURE_SPACING;
		}
		drawTexturedModalRect(j + box[0], k + box[1], SETTING_SRC_X + srcX, SETTING_SRC_Y, box[2], box[3]);
		drawTexturedModalRect(j + box[0] + 2, k + box[1] + 2, SIDE_TYPE_SRC_X + (SIDE_TYPE_SIZE + TEXTURE_SPACING) * setting.getImageId(), SIDE_TYPE_SRC_Y, SIDE_TYPE_SIZE, SIDE_TYPE_SIZE);
	}

	private int[] getSideBoxRect(int i) {
		return new int[] { 20, 18 + i * 24, 22, 22 };
	}

	private int[] getSettingBoxRect(int i, boolean topRow) {
		return new int[] { 20 + i * 18, 143 + (topRow ? 0 : 18), 16, 16 };
	}

	private int[] getActiveSettingBoxRect(int side, int setting) {
		int[] coordinate = getSideBoxRect(side);
		return new int[] { coordinate[0] + coordinate[2] + 5 + setting * 18, coordinate[1] + (coordinate[3] - 16) / 2, 16, 16 };
	}

	private int activeId = -1;

	@Override
	public void mouseClick(int x, int y, int button) {
		super.mouseClick(x, y, button);
		x -= getGuiLeft();
		y -= getGuiTop();
		if (button == 0) {
			for (DistributorSetting setting : DistributorSetting.settings) {
				if (setting.isEnabled(distributor)) {
					int[] box = getSettingBoxRect(setting.getImageId(), setting.getIsTop());
					if (inRect(x, y, box)) {
						activeId = setting.getId();
					}
				}
			}
		}
	}

	@Override
	public void mouseMoved(int x, int y, int button) {
		super.mouseMoved(x, y, button);
		x -= getGuiLeft();
		y -= getGuiTop();
		if (button == 0 && activeId != -1) {
			int id = 0;
			for (DistributorSide side : distributor.getSides()) {
				if (side.isEnabled(distributor)) {
					int[] box = getSideBoxRect(id++);
					if (inRect(x, y, box)) {
						DataWriter dw = PacketHandler.getDataWriter(PacketType.BLOCK);
						dw.writeByte(activeId);
						dw.writeByte(side.getId());
						dw.writeBoolean(true);
						PacketHandler.sendPacketToServer(dw);
						break;
					}
				}
			}
			activeId = -1;
		} else if (button == 1) {
			int id = 0;
			for (DistributorSide side : distributor.getSides()) {
				if (side.isEnabled(distributor)) {
					int settingCount = 0;
					for (DistributorSetting setting : DistributorSetting.settings) {
						if (setting.isEnabled(distributor)) {
							if (side.isSet(setting.getId())) {
								int[] settingsBox = getActiveSettingBoxRect(id, settingCount++);
								if (inRect(x, y, settingsBox)) {
									DataWriter dw = PacketHandler.getDataWriter(PacketType.BLOCK);
									dw.writeByte(activeId);
									dw.writeByte(side.getId());
									dw.writeBoolean(false);
									PacketHandler.sendPacketToServer(dw);
									break;
								}
							}
						}
					}
					id++;
				}
			}
		}
	}

	private TileEntityDistributor distributor;
}
