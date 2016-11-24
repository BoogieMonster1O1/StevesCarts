package stevesvehicles.common.modules.common.attachment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.client.ResourceHelper;
import stevesvehicles.client.gui.screen.GuiVehicle;
import stevesvehicles.client.localization.entry.module.LocalizationCake;
import stevesvehicles.common.container.slots.SlotBase;
import stevesvehicles.common.container.slots.SlotCake;
import stevesvehicles.common.modules.ISuppliesModule;
import stevesvehicles.common.modules.cart.attachment.ModuleAttachment;
import stevesvehicles.common.vehicles.VehicleBase;

public class ModuleCakeServer extends ModuleAttachment implements ISuppliesModule {
	private DataParameter<Integer> BUFFER;

	public ModuleCakeServer(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	private int cooldown = 0;
	private static final int MAX_CAKES = 10;
	private static final int SLICES_PER_CAKE = 6;
	private static final int MAX_TOTAL_SLICES = ((MAX_CAKES + 1) * SLICES_PER_CAKE);
	private static final int REFILL_CHEAT_COOLDOWN = 20;

	@Override
	public void update() {
		super.update();
		if (!getVehicle().getWorld().isRemote) {
			if (getVehicle().hasCreativeSupplies()) {
				if (cooldown >= REFILL_CHEAT_COOLDOWN) {
					if (getCakeBuffer() < MAX_TOTAL_SLICES) {
						setCakeBuffer(getCakeBuffer() + 1);
					}
					cooldown = 0;
				} else {
					++cooldown;
				}
			}
			ItemStack item = getStack(0);
			if (item != null && item.getItem().equals(Items.CAKE) && getCakeBuffer() + SLICES_PER_CAKE <= MAX_TOTAL_SLICES) {
				setCakeBuffer(getCakeBuffer() + SLICES_PER_CAKE);
				setStack(0, null);
			}
		}
	}

	private void setCakeBuffer(int i) {
		updateDw(BUFFER, i);
	}

	private int getCakeBuffer() {
		if (isPlaceholder()) {
			return 6;
		}
		return getDw(BUFFER);
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public void initDw() {
		BUFFER = createDw(DataSerializers.VARINT);
		registerDw(BUFFER, 0);
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	protected int getInventoryWidth() {
		return 1;
	}

	@Override
	protected SlotBase getSlot(int slotId, int x, int y) {
		return new SlotCake(getVehicle().getVehicleEntity(), slotId, 8 + x * 18, 38 + y * 18);
	}

	@Override
	public void drawForeground(GuiVehicle gui) {
		drawString(gui, LocalizationCake.TITLE.translate(), 8, 6, 0x404040);
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		tagCompound.setShort("Cake", (short) getCakeBuffer());
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		setCakeBuffer(tagCompound.getShort("Cake"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		drawStringOnMouseOver(gui, LocalizationCake.CAKES_LABEL.translate(String.valueOf(getCakes()), String.valueOf(MAX_CAKES)) + "\n" + LocalizationCake.SLICES_LABEL.translate(String.valueOf(getSlices()), String.valueOf(SLICES_PER_CAKE)), x, y, RECT);
	}

	private int getCakes() {
		if (getCakeBuffer() == MAX_TOTAL_SLICES) {
			return MAX_CAKES;
		}
		return getCakeBuffer() / SLICES_PER_CAKE;
	}

	private int getSlices() {
		if (getCakeBuffer() == MAX_TOTAL_SLICES) {
			return SLICES_PER_CAKE;
		}
		return getCakeBuffer() % SLICES_PER_CAKE;
	}

	private static final int TEXTURE_SPACING = 1;
	private static final int[] RECT = { 40, 20, 13, 36 };
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/cake.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		ResourceHelper.bindResource(TEXTURE);
		drawImage(gui, RECT, TEXTURE_SPACING, TEXTURE_SPACING + (inRect(x, y, RECT) ? RECT[3] + TEXTURE_SPACING : 0));
		int maxHeight = RECT[3] - 2;
		int height = (int) ((getCakes() / (float) MAX_CAKES) * maxHeight);
		if (height > 0) {
			drawImage(gui, RECT[0] + 1, RECT[1] + 1 + maxHeight - height, TEXTURE_SPACING * 2 + RECT[2], TEXTURE_SPACING + maxHeight - height, 7, height);
		}
		height = (int) ((getSlices() / (float) SLICES_PER_CAKE) * maxHeight);
		if (height > 0) {
			drawImage(gui, RECT[0] + 9, RECT[1] + 1 + maxHeight - height, TEXTURE_SPACING * 3 + RECT[2] + 7, TEXTURE_SPACING + maxHeight - height, 3, height);
		}
	}

	@Override
	public int guiWidth() {
		return 75;
	}

	@Override
	public int guiHeight() {
		return 60;
	}

	@Override
	public boolean onInteractFirst(EntityPlayer entityplayer) {
		if (getCakeBuffer() > 0) {
			if (!getVehicle().getWorld().isRemote && entityplayer.canEat(false)) {
				setCakeBuffer(getCakeBuffer() - 1);
				entityplayer.getFoodStats().addStats(2, 0.1F);
			}
			return true;
		} else {
			return false;
		}
	}

	public int getRenderSliceCount() {
		int count = getSlices();
		if (count == 0 && getCakes() > 0) {
			count = 6;
		}
		return count;
	}

	@Override
	public boolean haveSupplies() {
		return getCakeBuffer() > 0;
	}
}
