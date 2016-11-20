package vswe.stevesvehicles.module.common.addon;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.localization.entry.module.LocalizationVisual;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.vehicle.VehicleBase;
import vswe.stevesvehicles.vehicle.VehicleDataSerializers;

public class ModuleColorRandomizer extends ModuleAddon {
	private static final int[] BUTTON = new int[] { 10, 26, 16, 16 };
	private int cooldown;
	private DataParameter<int[]> COLORS;

	public ModuleColorRandomizer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, getModuleName(), 8, 6, 0x404040);
	}

	@Override
	public int guiWidth() {
		return 100;
	}

	@Override
	public int guiHeight() {
		return 50;
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/color_randomizer.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		float[] color = getColor();
		GL11.glColor4f(color[0], color[1], color[2], 1.0F);
		drawImage(gui, 50, 20, 1, 18, 28, 28);
		GL11.glColor4f(1, 1, 1, 1);
		if (inRect(x, y, BUTTON)) {
			drawImage(gui, 10, 26, 35, 1, 16, 16);
		} else {
			drawImage(gui, 10, 26, 18, 1, 16, 16);
		}
		drawImage(gui, 10, 26, 1, 1, 16, 16);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		if (inRect(x, y, BUTTON)) {
			String randomizeString = LocalizationVisual.RANDOMIZE.translate();
			drawStringOnMouseOver(gui, randomizeString, x, y, BUTTON);
		}
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (button == 0) {
			if (inRect(x, y, BUTTON)) {
				sendPacketToServer(getDataWriter());
			}
		}
	}

	@Override
	public void activatedByRail(int x, int y, int z, boolean active) {
		if (active && cooldown == 0) {
			randomizeColor();
			cooldown = 5;
		}
	}

	@Override
	public void update() {
		if (cooldown > 0) {
			cooldown--;
		}
	}

	private void randomizeColor() {
		int red = getVehicle().getRandom().nextInt(256);
		int green = getVehicle().getRandom().nextInt(256);
		int blue = getVehicle().getRandom().nextInt(256);
		setColorVal(0, (byte) red);
		setColorVal(1, (byte) green);
		setColorVal(2, (byte) blue);
	}

	@Override
	public int numberOfDataWatchers() {
		return 3;
	}

	@Override
	public void initDw() {
		COLORS = createDw(VehicleDataSerializers.VARINT);
		registerDw(COLORS, new int[] { 255, 255, 255 });
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) {
		randomizeColor();
	}

	public int getColorVal(int i) {
		if (isPlaceholder()) {
			return 255;
		}
		int tempVal = this.getDw(COLORS)[i];
		if (tempVal < 0) {
			tempVal += 256;
		}
		return tempVal;
	}

	public void setColorVal(int id, int val) {
		int[] colors = getDw(COLORS);
		colors[id] = val;
		updateDw(COLORS, colors);
	}

	private float getColorComponent(int i) {
		return getColorVal(i) / 255F;
	}

	@Override
	public float[] getColor() {
		return new float[] { getColorComponent(0), getColorComponent(1), getColorComponent(2) };
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setByte("Red", (byte) getColorVal(0));
		tagCompound.setByte("Green", (byte) getColorVal(1));
		tagCompound.setByte("Blue", (byte) getColorVal(2));
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setColorVal(0, tagCompound.getByte("Red"));
		setColorVal(1, tagCompound.getByte("Green"));
		setColorVal(2, tagCompound.getByte("Blue"));
	}
}
