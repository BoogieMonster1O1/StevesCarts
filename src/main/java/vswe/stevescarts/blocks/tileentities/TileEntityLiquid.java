package vswe.stevescarts.blocks.tileentities;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.network.NetworkManager;
import reborncore.common.util.FluidUtils;
import vswe.stevescarts.containers.ContainerBase;
import vswe.stevescarts.containers.ContainerLiquid;
import vswe.stevescarts.containers.ContainerManager;
import vswe.stevescarts.containers.slots.SlotLiquidFilter;
import vswe.stevescarts.containers.slots.SlotLiquidManagerInput;
import vswe.stevescarts.containers.slots.SlotLiquidOutput;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.guis.GuiBase;
import vswe.stevescarts.guis.GuiLiquid;
import vswe.stevescarts.helpers.storages.ITankHolder;
import vswe.stevescarts.helpers.storages.SCTank;
import vswe.stevescarts.helpers.storages.TransferHandler;
import vswe.stevescarts.helpers.storages.TransferManager;
import vswe.stevescarts.modules.storages.tanks.ModuleTank;
import vswe.stevescarts.packet.PacketFluidSync;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class TileEntityLiquid extends TileEntityManager implements ITankHolder {
	public SCTank[] tanks;
	private int tick;
	private static final int[] topSlots;
	private static final int[] botSlots;
	private static final int[] sideSlots;

	@Override
	@SideOnly(Side.CLIENT)
	public GuiBase getGui(final InventoryPlayer inv) {
		return new GuiLiquid(inv, this);
	}

	@Override
	public ContainerBase getContainer(final InventoryPlayer inv) {
		return new ContainerLiquid(inv, this);
	}

	public TileEntityLiquid() {
		tanks = new SCTank[4];
		for (int i = 0; i < 4; ++i) {
			tanks[i] = new SCTank(this, 32000, i);
		}
	}

	public SCTank[] getTanks() {
		return tanks;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (tick-- <= 0) {
			tick = 5;
			if (!world.isRemote) {
				for (int i = 0; i < 4; ++i) {
					tanks[i].containerTransfer();
				}
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return 12;
	}

	@Override
	public String getName() {
		return "tile.SC2:BlockLiquidManager.name";
	}

	@Override
	@Nonnull
	public ItemStack getInputContainer(final int tankid) {
		return getStackInSlot(tankid * 3);
	}

	@Override
	public void setInputContainer(final int tankid, ItemStack stack) {
		setInventorySlotContents(tankid * 3, stack);
	}

	@Override
	public void addToOutputContainer(final int tankid, @Nonnull ItemStack item) {
		TransferHandler.TransferItem(item, this, tankid * 3 + 1, tankid * 3 + 1, new ContainerLiquid(null, this), Slot.class, null, -1);
	}

	@Override
	public void onFluidUpdated(final int tankid) {
		markDirty();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawImage(int tankid, GuiBase gui, TextureAtlasSprite sprite, int targetX, int targetY, int srcX, int srcY, int width, int height) {
		gui.drawIcon(sprite, gui.getGuiLeft() + targetX, gui.getGuiTop() + targetY, width / 16F, height / 16F, srcX / 16F, srcY / 16F);
	}

	@Override
	protected boolean isTargetValid(final TransferManager transfer) {
		return true;
	}

	@Override
	protected boolean doTransfer(final TransferManager transfer) {
		final int maximumToTransfer = hasMaxAmount(transfer.getSetting()) ? Math.min(getMaxAmount(transfer.getSetting()) - transfer.getWorkload(), Fluid.BUCKET_VOLUME) : Fluid.BUCKET_VOLUME;
		boolean sucess = false;

		if (toCart[transfer.getSetting()]) {
			boolean allFull = true;
			for (int i = 0; i < tanks.length; i++) {
				final int fill = fillTank(transfer.getCart(), i, transfer.getSetting(), maximumToTransfer, false);
				if (fill > 0) {
					fillTank(transfer.getCart(), i, transfer.getSetting(), fill, true);
					sucess = true;
					if (fill >= maximumToTransfer)
						allFull = false;
					if (hasMaxAmount(transfer.getSetting())) {
						transfer.setWorkload(transfer.getWorkload() + fill);
					}
					break;
				}
			}
			if (allFull) {
				return false;
			}
		} else {
			final ArrayList<ModuleTank> cartTanks = transfer.getCart().getTanks();
			for (final IFluidTank cartTank : cartTanks) {
				final int drain = drainTank(cartTank, transfer.getSetting(), maximumToTransfer, false);
				if (drain > 0) {
					drainTank(cartTank, transfer.getSetting(), drain, true);
					sucess = true;
					if (hasMaxAmount(transfer.getSetting())) {
						transfer.setWorkload(transfer.getWorkload() + drain);
					}
					break;
				}
			}
		}
		if (sucess && hasMaxAmount(transfer.getSetting()) && transfer.getWorkload() == getMaxAmount(transfer.getSetting())) {
			transfer.setLowestSetting(transfer.getSetting() + 1);
		}
		return sucess;
	}

	private int fillTank(final EntityMinecartModular cart, final int tankId, final int sideId, int fillAmount, final boolean doFill) {
		if (isTankValid(tankId, sideId)) {
			final FluidStack fluidToFill = tanks[tankId].drain(fillAmount, doFill);
			if (fluidToFill == null) {
				return 0;
			}
			fillAmount = fluidToFill.amount;
			if (isFluidValid(sideId, fluidToFill)) {
				final ArrayList<ModuleTank> cartTanks = cart.getTanks();
				for (final IFluidTank cartTank : cartTanks) {
					fluidToFill.amount -= cartTank.fill(fluidToFill, doFill);
					if (fluidToFill.amount <= 0) {
						return fillAmount;
					}
				}
				return fillAmount - fluidToFill.amount;
			}
		}
		return 0;
	}

	private int drainTank(final IFluidTank cartTank, final int sideId, int drainAmount, final boolean doDrain) {
		final FluidStack drainedFluid = cartTank.drain(drainAmount, doDrain);
		if (drainedFluid == null) {
			return 0;
		}
		drainAmount = drainedFluid.amount;
		if (isFluidValid(sideId, drainedFluid)) {
			for (int i = 0; i < tanks.length; ++i) {
				final SCTank tank = tanks[i];
				if (isTankValid(i, sideId)) {
					final FluidStack fluidStack = drainedFluid;
					fluidStack.amount -= tank.fill(drainedFluid, doDrain);
					if (drainedFluid.amount <= 0) {
						return drainAmount;
					}
				}
			}
			return drainAmount - drainedFluid.amount;
		}
		return 0;
	}

	private boolean isTankValid(final int tankId, int sideId) {
		return (layoutType != 1 || tankId == sideId) && (layoutType != 2 || color[sideId] == color[tankId]);
	}

	private boolean isTankValid(final int tankId, EnumFacing facing) {
		if(facing == null){
			return false;
		}
		switch (layoutType) {
			case 0: return true;
			case 1: return tankId == facingToTankId(facing);
			case 2: return color[tankId] == facingToColorId(facing);
			default: return false;
		}
	}

	private boolean isFluidValid(final int sideId, final FluidStack fluid) {
		@Nonnull
		ItemStack filter = getStackInSlot(sideId * 3 + 2);
		final FluidStack filterFluid = FluidUtils.getFluidStackInContainer(filter);
		return filterFluid == null || filterFluid.isFluidEqual(fluid);
	}

	public int getMaxAmount(final int id) {
		return (int) (getMaxAmountBuckets(id) * Fluid.BUCKET_VOLUME);
	}

	public float getMaxAmountBuckets(final int id) {
		switch (getAmountId(id)) {
			case 1:
				return 0.25f;
			case 2:
				return 0.5f;
			case 3:
				return 0.75f;
			case 4:
				return 1.0f;
			case 5:
				return 2.0f;
			case 6:
				return 3.0f;
			case 7:
				return 5.0f;
			case 8:
				return 7.5f;
			case 9:
				return 10.0f;
			case 10:
				return 15.0f;
			default:
				return 0.0f;
		}
	}

	public boolean hasMaxAmount(final int id) {
		return getAmountId(id) != 0;
	}

	@Override
	public int getAmountCount() {
		return 11;
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		for (int i = 0; i < 4; ++i) {
			tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(nbttagcompound.getCompoundTag("Fluid" + i)));
		}
		setWorkload(nbttagcompound.getShort("workload"));
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		for (int i = 0; i < 4; ++i) {
			if (tanks[i].getFluid() != null) {
				final NBTTagCompound compound = new NBTTagCompound();
				tanks[i].getFluid().writeToNBT(compound);
				nbttagcompound.setTag("Fluid" + i, compound);
			}
		}
		nbttagcompound.setShort("workload", (short) getWorkload());
		return nbttagcompound;
	}

	@Override
	public void checkGuiData(final ContainerManager conManager, final IContainerListener crafting, final boolean isNew) {
		super.checkGuiData(conManager, crafting, isNew);
		final ContainerLiquid con = (ContainerLiquid) conManager;
		for (int i = 0; i < 4; ++i) {
			boolean changed = false;
			final int id = 4 + i * 4;
			final int amount1 = 4 + i * 4 + 1;
			final int amount2 = 4 + i * 4 + 2;
			final int meta = 4 + i * 4 + 3;
			if ((isNew || con.oldLiquids[i] != null) && tanks[i].getFluid() == null) {
				updateGuiData(con, crafting, id, (short) (-1));
				changed = true;
			} else if (tanks[i].getFluid() != null) {
				if (isNew || con.oldLiquids[i] == null) {
					updateGuiData(con, crafting, id, (short) this.tanks[i].getFluid().amount);
					updateGuiData(con, crafting, amount1, getShortFromInt(true, tanks[i].getFluid().amount));
					updateGuiData(con, crafting, amount2, getShortFromInt(false, tanks[i].getFluid().amount));
					changed = true;
				} else {
					NetworkManager.sendToWorld(new PacketFluidSync(this.tanks[i].getFluid(), getPos(), world.provider.getDimension(), i), getWorld());
					if (con.oldLiquids[i].amount != tanks[i].getFluid().amount) {
						updateGuiData(con, crafting, amount1, getShortFromInt(true, tanks[i].getFluid().amount));
						updateGuiData(con, crafting, amount2, getShortFromInt(false, tanks[i].getFluid().amount));
						changed = true;
					}
				}
			}
			if (changed) {
				if (tanks[i].getFluid() == null) {
					con.oldLiquids[i] = null;
				} else {
					con.oldLiquids[i] = tanks[i].getFluid().copy();
				}
			}
		}
	}

	@Override
	public void receiveGuiData(int id, final short data) {
		if (id > 3) {
			id -= 4;
			final int tankid = id / 4;
			final int contentid = id % 4;
			if (contentid == 0) {
				if (data == -1) {
					tanks[tankid].setFluid(null);
				}
			} else if (tanks[tankid].getFluid() != null) {
				tanks[tankid].getFluid().amount = getIntFromShort(contentid == 1, tanks[tankid].getFluid().amount, data);
			}
		} else {
			super.receiveGuiData(id, data);
		}
	}

	private boolean isInput(final int id) {
		return id % 3 == 0;
	}

	private boolean isOutput(final int id) {
		return id % 3 == 1;
	}

	@Override
	public boolean isItemValidForSlot(final int slotId, @Nonnull ItemStack item) {
		if (isInput(slotId)) {
			return SlotLiquidManagerInput.isItemStackValid(item, this, -1);
		}
		if (isOutput(slotId)) {
			return SlotLiquidOutput.isItemStackValid(item);
		}
		return SlotLiquidFilter.isItemStackValid(item);
	}

	public int[] getAccessibleSlotsFromSide(final int side) {
		if (side == 1) {
			return TileEntityLiquid.topSlots;
		}
		if (side == 0) {
			return TileEntityLiquid.botSlots;
		}
		return TileEntityLiquid.sideSlots;
	}

	public boolean canInsertItem(final int slot, @Nonnull ItemStack item, final int side) {
		return side == 1 && isInput(slot) && isItemValidForSlot(slot, item);
	}

	public boolean canExtractItem(final int slot, @Nonnull ItemStack item, final int side) {
		return side == 0 && isOutput(slot);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return getValidTank(facing) != null;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) getValidTank(facing);
		}
		return super.getCapability(capability, facing);
	}

	public SCTank getValidTank(EnumFacing facing) {
		for (int i = 0; i < getTanks().length; i++) {
			if(isTankValid(i, facing)) {
				return getTanks()[i];
			}
		}
		return null;
	}

	private int facingToColorId(EnumFacing facing) {
		switch (facing.getIndex()) {
			case 2: return 3; // north, yellow
			case 3: return 2; // south, blue
			case 4: return 4; // west, green
			case 5: return 1; // east, red
			default: return 1;
		}
	}

	private int facingToTankId(EnumFacing facing) {
		switch (facing.getIndex()) {
			case 2: return 2; // north, yellow
			case 3: return 1; // south, blue
			case 4: return 3; // west, green
			case 5: return 0; // east, red
			default: return 0;
		}
	}

	static {
		topSlots = new int[] { 0, 3, 6, 9 };
		botSlots = new int[] { 1, 4, 7, 10 };
		sideSlots = new int[0];
	}
}
