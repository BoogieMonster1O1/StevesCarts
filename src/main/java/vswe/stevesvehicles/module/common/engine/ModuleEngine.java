package vswe.stevesvehicles.module.common.engine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.localization.entry.module.LocalizationEngine;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.network.DataWriter;
import vswe.stevesvehicles.vehicle.VehicleBase;

public abstract class ModuleEngine extends ModuleBase {
	private int fuel;
	protected int[] priorityButton;

	public ModuleEngine(VehicleBase vehicleBase) {
		super(vehicleBase);
		initPriorityButton();
	}

	protected void initPriorityButton() {
		priorityButton = new int[] {78,7,16,16};	
	}

	//called to update the module's actions. Called by the cart's update code.
	@Override
	public void update() {
		super.update();
		loadFuel();
	}

	//returns if this cart supplies the cart with fuel
	@Override
	public boolean hasFuel(int consumption) {
		return getFuelLevel() >= consumption && !isDisabled();
	}

	public int getFuelLevel() {
		return fuel;
	}

	public void setFuelLevel(int val) {
		fuel = val;
	}

	protected boolean isDisabled() {
		return getPriority()  >= 3 || getPriority() < 0;
	}

	public int getPriority() {
		if (isPlaceholder()) {
			return 0;
		}

		int temp = getDw(0);
		if (temp < 0 || temp > 3) {
			temp = 3;
		}
		return temp;
	}

	private void setPriority(int data) {	
		if (data < 0) {
			data = 0;
		}else if (data > 3) {
			data = 3;
		}

		updateDw(0, data);
	}

	public void consumeFuel(int consumption) {
		setFuelLevel(getFuelLevel() - consumption);
	}

	protected abstract void loadFuel();

	public void smoke(){}


	public abstract int getTotalFuel();
	public abstract float[] getGuiBarColor();


	@Override
	public boolean hasGui(){
		return true;
	}

	@Override
	public int guiWidth() {
		return 100;
	}

	@Override
	public int guiHeight() {
		return 50;
	}

	private static final int TEXTURE_SPACING = 1;
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/engine.png");

	@Override
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);

		int sourceX = TEXTURE_SPACING + (16 + TEXTURE_SPACING) * getPriority();
		int sourceY = TEXTURE_SPACING;
		if (inRect(x,y, priorityButton)) {
			sourceY += 16 + TEXTURE_SPACING;
		}
		drawImage(gui, priorityButton, sourceX, sourceY);
	}

	@Override
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, getPriorityText() , x, y , priorityButton);
	}

	private String getPriorityText() {
		if (isDisabled()) {
			return LocalizationEngine.DISABLED.translate();
		}else{
			return LocalizationEngine.PRIORITY.translate(String.valueOf(getPriority()));
		}
	}

	@Override
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (inRect(x,y, priorityButton)) {
			if (button == 0 || button == 1) {
				DataWriter dw = getDataWriter();
				dw.writeBoolean(button == 0);
				sendPacketToServer(dw);
			}
		}
	}
	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) {
		int priority = getPriority();
		priority += dr.readBoolean() ? 1 : -1;
		priority %= 4;
		if (priority < 0) {
			priority += 4;
		}
		setPriority(priority);
	}


	@Override
	public void initDw() {
		addDw(0,0);
	}	
	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setByte("Priority",(byte)getPriority());
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setPriority(tagCompound.getByte("Priority"));
	}

}