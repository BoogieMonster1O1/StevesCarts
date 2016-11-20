package vswe.stevesvehicles.module.cart.addon;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.gui.ColorHelper;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.localization.entry.module.cart.LocalizationCartRails;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.network.DataWriter;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleHeightControl extends ModuleAddon {
	private DataParameter<Integer> Y_TARGET;

	public ModuleHeightControl(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public int guiWidth() {
		return Math.max(100, ORE_MAP_X + 5 + HeightControlOre.ores.size() * 4);
	}

	@Override
	public int guiHeight() {
		return 65;
	}

	private static final int LEVEL_NUMBER_BOX_X = 8;
	private static final int LEVEL_NUMBER_BOX_Y = 18;
	private static final int[] ARROW_UP = new int[] { 9, 36, 17, 9 };
	private static final int[] ARROW_MIDDLE = new int[] { 9, 46, 17, 6 };
	private static final int[] ARROW_DOWN = new int[] { 9, 53, 17, 9 };
	private static final int ORE_MAP_X = 40;
	private static final int ORE_MAP_Y = 18;

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
		String s = String.valueOf(getYTarget());
		int x = LEVEL_NUMBER_BOX_X + 6;
		int color = 0xFFFFFF;
		if (getYTarget() >= 100) {
			x -= 4;
		} else if (getYTarget() < 10) {
			x += 3;
			if (getYTarget() < 5) {
				color = 0xFF0000;
			}
		}
		drawString(gui, s, x, LEVEL_NUMBER_BOX_Y + 5, color);
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/height_control.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		// draw the box for the numbers
		drawImage(gui, LEVEL_NUMBER_BOX_X, LEVEL_NUMBER_BOX_Y, 6, 42, 21, 15);
		// draw the controls
		drawHoverImage(gui, ARROW_UP, 6, 15, x, y);
		drawHoverImage(gui, ARROW_MIDDLE, 6, 25, x, y);
		drawHoverImage(gui, ARROW_DOWN, 6, 32, x, y);
		// draw the ores map
		for (int i = 0; i < HeightControlOre.ores.size(); i++) {
			HeightControlOre ore = HeightControlOre.ores.get(i);
			for (int j = 0; j < 11; j++) {
				int altitude = getYTarget() - j + 5;
				boolean empty = !(ore.spanLowest <= altitude && altitude <= ore.spanHighest);
				boolean high = ore.bestLowest <= altitude && altitude <= ore.bestHighest;
				int srcY;
				int srcX;
				if (empty) {
					srcY = 1;
					srcX = 1;
				} else {
					if (!ore.useDefaultTexture) {
						ResourceHelper.bindResource(ore.specialTexture);
					}
					srcX = ore.srcX;
					srcY = ore.srcY;
					if (high) {
						srcY += 5;
					}
				}
				drawImage(gui, ORE_MAP_X + i * 4, ORE_MAP_Y + j * 4, srcX, srcY, 4, 4);
				ResourceHelper.bindResource(TEXTURE);
			}
		}
		// draw the markers
		if (getYTarget() != getVehicle().y()) {
			drawMarker(gui, 5, false);
		}
		int pos = getYTarget() + 5 - getVehicle().y();
		if (pos >= 0 && pos < 11) {
			drawMarker(gui, pos, true);
		}
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		String change = LocalizationCartRails.MOVE_TARGET.translate() + "\n" + ColorHelper.GRAY + LocalizationCartRails.MOVE_TARGET_TEN.translate();
		drawStringOnMouseOver(gui, change, x, y, ARROW_UP);
		drawStringOnMouseOver(gui, change, x, y, ARROW_DOWN);
		drawStringOnMouseOver(gui, LocalizationCartRails.RESET_TARGET.translate(), x, y, ARROW_MIDDLE);
	}

	private void drawHoverImage(GuiVehicle gui, int[] bounds, int u, int v, int mX, int mY) {
		if (inRect(mX, mY, bounds)) {
			u += bounds[2] + 1;
		}
		drawImage(gui, bounds, u, v);
	}

	private void drawMarker(GuiVehicle gui, int pos, boolean isTargetLevel) {
		int srcX = 6;
		int srcY = isTargetLevel ? 8 : 1;
		drawImage(gui, ORE_MAP_X - 1, ORE_MAP_Y + pos * 4 - 1, srcX, srcY, 1, 6);
		for (int i = 0; i < HeightControlOre.ores.size(); i++) {
			drawImage(gui, ORE_MAP_X + i * 4, ORE_MAP_Y + pos * 4 - 1, srcX + 1, srcY, 4, 6);
		}
		drawImage(gui, ORE_MAP_X + HeightControlOre.ores.size() * 4, ORE_MAP_Y + pos * 4 - 1, srcX + 5, srcY, 1, 6);
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (button == 0) {
			int arrow = -1;
			if (inRect(x, y, ARROW_MIDDLE)) {
				arrow = 0;
			} else if (inRect(x, y, ARROW_UP)) {
				arrow = 1;
			} else if (inRect(x, y, ARROW_DOWN)) {
				arrow = 2;
			}
			if (arrow != -1) {
				DataWriter dw = getDataWriter();
				dw.writeByte(arrow);
				dw.writeBoolean(GuiScreen.isShiftKeyDown());
				sendPacketToServer(dw);
			}
		}
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) {
		int arrow = dr.readByte();
		boolean isShift = dr.readBoolean();
		if (arrow == 0) {
			setYTarget(getVehicle().y());
		} else {
			int multiplier;
			int dif;
			if (arrow == 1) {
				multiplier = 1;
			} else {
				multiplier = -1;
			}
			if (isShift) {
				dif = 10;
			} else {
				dif = 1;
			}
			int targetY = getYTarget();
			targetY += multiplier * dif;
			if (targetY < 0) {
				targetY = 0;
			} else if (targetY > 255) {
				targetY = 255;
			}
			setYTarget(targetY);
		}
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		Y_TARGET = createDw(DataSerializers.VARINT);
		registerDw(Y_TARGET, getVehicle().y());
	}

	public void setYTarget(int val) {
		updateDw(Y_TARGET, val);
	}

	@Override
	public int getYTarget() {
		if (isPlaceholder()) {
			return 64;
		}
		int data = getDw(Y_TARGET);
		if (data < 0) {
			data += 256;
		}
		return data;
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setShort("Height", (short) getYTarget());
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setYTarget(tagCompound.getShort("Height"));
	}
}
