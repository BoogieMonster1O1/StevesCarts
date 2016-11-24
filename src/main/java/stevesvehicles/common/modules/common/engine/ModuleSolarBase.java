package stevesvehicles.common.modules.common.engine;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.assembler.SimulationInfo;
import stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.block.LocalizationAssembler;
import stevesvehicles.client.localization.entry.module.LocalizationEngine;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class ModuleSolarBase extends ModuleEngine {
	private int light;
	private boolean maxLight;
	private int panelCoolDown;
	private boolean down = true;
	private boolean upState;
	private DataParameter<Integer> LIGHT;
	private DataParameter<Boolean> UP_STATE;
	private DataParameter<Integer> PRIORITY;

	public ModuleSolarBase(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.add(new SimulationInfoBoolean(LocalizationAssembler.INFO_LIGHT, "light"));
	}

	@Override
	public boolean hasSlots() {
		return false;
	}

	// called to update the module's actions. Called by the cart's update code.
	@Override
	public void update() {
		super.update();
		updateSolarModel();
	}

	@Override
	protected void loadFuel() {
		updateLight();
		updateDataForModel();
		chargeSolar();
	}

	@Override
	public int getTotalFuel() {
		return getFuelLevel();
	}

	@Override
	public float[] getGuiBarColor() {
		return new float[] { 1F, 1F, 0F };
	}

	private void updateLight() {
		light = getVehicle().getWorld().getLightFor(EnumSkyBlock.BLOCK, getVehicle().pos());
		if (light == 15 && !getVehicle().getWorld().canBlockSeeSky(getVehicle().pos().up())) {
			light = 14;
		}
	}

	private void updateDataForModel() {
		if (isPlaceholder()) {
			light = getBooleanSimulationInfo() ? 15 : 14;
		} else {
			if (getVehicle().getWorld().isRemote) {
				light = getDw(LIGHT);
			} else {
				updateDw(LIGHT, light);
			}
		}
		maxLight = light == 15;
		if (!upState && light == 15) {
			light = 14;
		}
	}

	private void chargeSolar() {
		if (light == 15 && getVehicle().getRandom().nextInt(8) < 4) {
			setFuelLevel(getFuelLevel() + getGenSpeed());
			if (getFuelLevel() > getMaxCapacity()) {
				setFuelLevel(getMaxCapacity());
			}
		}
	}

	public int getLight() {
		return light;
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationEngine.SOLAR_TITLE.translate(), 8, 6, 0x404040);
		String str = LocalizationEngine.SOLAR_NO_POWER.translate();
		if (getFuelLevel() > 0) {
			str = LocalizationEngine.SOLAR_POWER.translate(String.valueOf(getFuelLevel()));
		}
		drawString(gui, str, 8, 42, 0x404040);
	}

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/solar.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		super.drawBackground(gui, x, y);
		ResourceHelper.bindResource(TEXTURE);
		int lightWidth = light * 3;
		if (light == 15) {
			lightWidth += 2;
		}
		drawImage(gui, 9, 20, 1, 1, 54, 18);
		drawImage(gui, 9 + 6, 20 + 1, 1, 20, lightWidth, 16);
	}

	@Override
	public int numberOfDataWatchers() {
		return super.numberOfDataWatchers() + 2;
	}

	@Override
	public void initDw() {
		PRIORITY = createDw(DataSerializers.VARINT);
		super.initDw();
		LIGHT = createDw(DataSerializers.VARINT);
		UP_STATE = createDw(DataSerializers.BOOLEAN);
		registerDw(LIGHT, 0);
		registerDw(UP_STATE, false);
	}

	@Override
	protected DataParameter<Integer> getPriorityDw() {
		return PRIORITY;
	}

	protected boolean isGoingDown() {
		return down;
	}

	public void updateSolarModel() {
		if (getVehicle().getWorld().isRemote) {
			updateDataForModel();
		}
		panelCoolDown += maxLight ? 1 : -1;
		if (down && panelCoolDown < 0) {
			panelCoolDown = 0;
		} else if (!down && panelCoolDown > 0) {
			panelCoolDown = 0;
		} else if (Math.abs(panelCoolDown) > 20) {
			panelCoolDown = 0;
			down = !down;
		}
		upState = updatePanels();
		if (!getVehicle().getWorld().isRemote) {
			updateDw(UP_STATE, upState);
		}
	}

	@Override
	public int numberOfGuiData() {
		return 2;
	}

	@Override
	protected void checkGuiData(Object[] info) {
		updateGuiData(info, 0, (short) (getFuelLevel() & 65535));
		updateGuiData(info, 1, (short) ((getFuelLevel() >> 16) & 65535));
	}

	@Override
	public void receiveGuiData(int id, short data) {
		if (id == 0) {
			int dataInt = data;
			if (dataInt < 0) {
				dataInt += 65536;
			}
			setFuelLevel((getFuelLevel() & -65536) | dataInt);
		} else if (id == 1) {
			setFuelLevel((getFuelLevel() & 65535) | (data << 16));
		}
	}

	protected abstract int getMaxCapacity();

	protected abstract int getGenSpeed();

	protected abstract boolean updatePanels();

	@Override
	protected void save(NBTTagCompound tagCompound) {
		super.save(tagCompound);
		tagCompound.setInteger("Fuel", getFuelLevel());
		tagCompound.setBoolean("Up", upState);
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		super.load(tagCompound);
		setFuelLevel(tagCompound.getInteger("Fuel"));
		upState = tagCompound.getBoolean("Up");
	}
}
