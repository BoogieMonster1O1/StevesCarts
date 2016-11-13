package vswe.stevesvehicles.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import vswe.stevesvehicles.container.ContainerManager;
import vswe.stevesvehicles.nbt.NBTHelper;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.tileentity.manager.ManagerTransfer;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

public abstract class TileEntityManager extends TileEntityBase implements IInventory, ITickable {
	public TileEntityManager() {
		cargoItemStacks = new ItemStack[getSizeInventory()];
		moveTime = 0;
		standardTransferHandler = new ManagerTransfer();
	}



	@Override
	public ItemStack getStackInSlot(int i) {
		return cargoItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (cargoItemStacks[i] != null) {
			if (cargoItemStacks[i].stackSize <= j) {
				ItemStack itemstack = cargoItemStacks[i];
				cargoItemStacks[i] = null;
				markDirty();
				return itemstack;
			}

			ItemStack result = cargoItemStacks[i].splitStack(j);

			if (cargoItemStacks[i].stackSize == 0) {
				cargoItemStacks[i] = null;
			}

			markDirty();
			return result;
		}else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		cargoItemStacks[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public String getName() {
		return "container.manager";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", NBTHelper.COMPOUND.getId());
		cargoItemStacks = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound slotCompound = nbttaglist.getCompoundTagAt(i);
			byte byte0 = slotCompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < cargoItemStacks.length) {
				cargoItemStacks[byte0] = ItemStack.loadItemStackFromNBT(slotCompound);
			}
		}

		moveTime = nbttagcompound.getByte("moveTime");
		setLowestSetting(nbttagcompound.getByte("lowestNumber"));
		layoutType = nbttagcompound.getByte("layout");
		byte temp = nbttagcompound.getByte("toCart");
		byte temp2 = nbttagcompound.getByte("doReturn");

		for (int i = 0; i < 4; i++) {
			amount[i] = nbttagcompound.getByte("amount" + i);
			color[i] = nbttagcompound.getByte("color" + i);

			if (color[i] == 0) {
				color[i] = i + 1;
			}

			toCart[i] = (temp & (1 << i)) != 0;
			doReturn[i] = (temp2 & (1 << i)) != 0;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setByte("moveTime", (byte)moveTime);
		nbttagcompound.setByte("lowestNumber", (byte)getLowestSetting());
		nbttagcompound.setByte("layout", (byte)layoutType);
		byte temp = 0;
		byte temp2 = 0;
		for (int i = 0; i < 4; i++) {
			nbttagcompound.setByte("amount" + i, (byte)amount[i]);
			nbttagcompound.setByte("color" + i, (byte)color[i]);

			if (toCart[i]) {
				temp |= (1 << i);
			}

			if (doReturn[i]) {
				temp2 |= (1 << i);
			}
		}

		nbttagcompound.setByte("toCart", temp);
		nbttagcompound.setByte("doReturn", temp2);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < cargoItemStacks.length; i++) {
			if (cargoItemStacks[i] != null) {
				NBTTagCompound slotCompound = new NBTTagCompound();
				slotCompound.setByte("Slot", (byte) i);
				cargoItemStacks[i].writeToNBT(slotCompound);
				nbttaglist.appendTag(slotCompound);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
		return nbttagcompound;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	private ManagerTransfer standardTransferHandler;

	public EntityModularCart getCart() {
		return standardTransferHandler.getCart();
	}   

	public void setCart(EntityModularCart cart) {
		standardTransferHandler.setCart(cart);
	}

	public int getSetting() {
		return standardTransferHandler.getSetting();
	}

	public void setSetting(int val) {
		standardTransferHandler.setSetting(val);
	}   

	public int getSide() {
		return standardTransferHandler.getSide();
	}

	public void setSide(int val) {
		standardTransferHandler.setSide(val);
	}    

	public int getLastSetting() {
		return standardTransferHandler.getLastSetting();
	}    

	public void setLastSetting(int val) {
		standardTransferHandler.setLastSetting(val);
	} 

	public int getLowestSetting() {
		return standardTransferHandler.getLowestSetting();
	}    

	public void setLowestSetting(int val) {
		standardTransferHandler.setLowestSetting(val);
	}    

	public int getWorkload() {
		return standardTransferHandler.getWorkload();
	}    

	public void setWorkload(int val) {
		standardTransferHandler.setWorkload(val);
	}    

	@Override
	public void update() {

		if (worldObj.isRemote) {
			updateLayout();
			return;
		}

		if (getCart() == null || getCart().isDead ||  getSide() < 0 || getSide() > 3 || !getCart().getVehicle().isDisabled()) {
			standardTransferHandler.reset();
			moveTime = 0;
			return;
		}

		moveTime++;

		if (moveTime >= 24) {
			moveTime = 0;

			if (!exchangeItems(standardTransferHandler)) {

				getCart().releaseCart();
				if (doReturn[getSide()]) {
					getCart().turnback();
				}
				standardTransferHandler.reset();

			}
		}
	}


	public boolean exchangeItems(ManagerTransfer transfer) {
		for (transfer.setSetting(transfer.getLowestSetting()); transfer.getSetting() < 4; transfer.setSetting(transfer.getSetting() + 1)) {
			if (color[transfer.getSetting()] - 1 != transfer.getSide()) {
				continue;
			}

			transfer.setLowestSetting(transfer.getSetting());
			if (transfer.getLastSetting() != transfer.getSetting()) {
				transfer.setWorkload(0);
				transfer.setLastSetting(transfer.getSetting());
				return true;
			}

			if (!(toCart[transfer.getSetting()] ? transfer.getToCartEnabled() : transfer.getFromCartEnabled()) || !isTargetValid(transfer)) {
				transfer.setLowestSetting(transfer.getSetting() + 1);
				return true;					
			}

			if (doTransfer(transfer)) {
				return true;
			}
		}	

		return false;
	}


	public enum PacketId {
		TRANSFER_DIRECTION,
		RETURN_MODE,
		LAYOUT_TYPE,
		VEHICLE_PART,
		COLOR,
		AMOUNT
	}



	protected void receivePacket(PacketId id, DataReader dr) {
		int railId;
		int difference;
		switch (id) {
			case TRANSFER_DIRECTION:
				railId = dr.readByte();
				toCart[railId] = !toCart[railId];

				if (color[railId] - 1 == getSide()) {
					reset();
				}
				break;
			case RETURN_MODE:
				railId = dr.readByte();
				if (color[railId] != 5) {
					doReturn[color[railId]-1] = !doReturn[color[railId]-1] ;
				}
				break;
			case LAYOUT_TYPE:
				difference = dr.readBoolean() ? 1 : -1;

				layoutType += difference;

				if (layoutType > 2) {
					layoutType = 0;
				}else if (layoutType < 0) {
					layoutType = 2;
				}

				reset();
				break;
			case COLOR:
				railId = dr.readByte();
				difference = dr.readBoolean() ? 1 : -1;
				if (color[railId] != 5) {
					boolean willStillExist = false;
					for (int side=0;side<4;side++) {
						if (side != railId && color[railId] == color[side]) {
							willStillExist = true;
							break;
						}
					}
					if (!willStillExist) {
						doReturn[color[railId]-1] = false;
					}
				}
				color[railId] += difference;

				if (color[railId] > 5) {
					color[railId] = 1;
				}else if (color[railId] < 1) {
					color[railId] = 5;
				}

				if (color[railId] - 1 == getSide()) {
					reset();
				}
				break;
			case AMOUNT:
				railId = dr.readByte();
				difference = dr.readBoolean() ? 1 : -1;
				amount[railId] += difference;

				if (amount[railId] >= getAmountCount()) {
					amount[railId] = 0;
				}else if (amount[railId] < 0) {
					amount[railId] = getAmountCount() - 1;
				}

				if (color[railId] - 1 == getSide()) {
					reset();
				}
				break;
		}
	}

	@Override
	public void receivePacket(DataReader dr, EntityPlayer player) {
		PacketId id = dr.readEnum(PacketId.class);
		receivePacket(id, dr);
	}


	@Override
	public void initGuiData(Container con, IContainerListener crafting) {
		checkGuiData((ContainerManager)con, crafting, true);
	}	

	@Override
	public void checkGuiData(Container con, IContainerListener crafting) {
		checkGuiData((ContainerManager)con, crafting, false);
	}

	public void checkGuiData(ContainerManager con, IContainerListener crafting, boolean isNew) {
		short header = (short)(moveTime & 31);
		header |= (layoutType & 3) << 5;

		for (int i = 0; i < 4 ; i++) {
			header |= (toCart[i] ? 1 : 0) << (7+i);		
		}
		for (int i = 0; i < 4 ; i++) {
			header |= (doReturn[i] ? 1 : 0) << (11+i);		
		}	

		if (isNew || con.lastHeader != header) {
			updateGuiData(con, crafting, 0,header);
			con.lastHeader = header;
		}

		short colorShort = (short)0;
		for (int i = 0; i < 4; i++) {
			colorShort |= (color[i] & 7) << (i*3);
		}
		colorShort |= ((getLastSetting() & 7) << 12);
		if (isNew || con.lastColor != colorShort) {
			updateGuiData(con, crafting, 1, colorShort);
			con.lastColor = colorShort;
		}

		short amountShort = (short)0;
		for (int i = 0; i < 4; i++) {
			amountShort |= (amount[i] & 15) << (i*4);
		}
		if (isNew || con.lastAmount != amountShort) {
			updateGuiData(con, crafting, 3, amountShort);
			con.lastAmount = amountShort;
		}			
	}

	@Override
	public void receiveGuiData(int id, short data) {	
		if (id == 0) {
			moveTime = (data & 31);
			layoutType = (data & 96) >> 5;
			updateLayout();
			for (int i = 0; i < 4; i++) {
				toCart[i] = (data & (1 << 7+i)) != 0;
			}
			for (int i = 0; i < 4; i++) {
				doReturn[i] = (data & (1 << 11+i)) != 0;
			}				
		}else if(id == 1) {
			for (int i = 0; i < 4; i++) {
				color[i] = (data & (7 << (i*3))) >> (i*3);
			}
			setLastSetting((data & (7 << 12)) >> 12);
		}else if(id == 3) {
			for (int i = 0; i < 4; i++) {
				amount[i] = (data & (15 << (i*4))) >> (i*4);
			}
		}

	}


	public int moveProgressScaled(int i)  {
		return (moveTime * i) / 24;
	}

	@Override
	public void closeInventory(EntityPlayer entityPlayer) {
	}
	@Override
	public void openInventory(EntityPlayer entityPlayer) {
	}

	private ItemStack cargoItemStacks[];
	public int layoutType;
	//public int workload;
	public int moveTime;
	public boolean toCart[] =  new boolean[] {true, true, true, true};
	public boolean doReturn[] =  new boolean[] {false, false, false, false};

	public int amount[] = new int[] {0, 0, 0, 0};
	public int color[] = new int[] {1, 2, 3, 4};

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (this.cargoItemStacks[index] != null) {
			ItemStack var2 = this.cargoItemStacks[index];
			this.cargoItemStacks[index] = null;
			return var2;
		}else {
			return null;
		}
	}


	protected void updateLayout() {}
	protected abstract boolean isTargetValid(ManagerTransfer transfer);
	protected abstract boolean doTransfer(ManagerTransfer transfer);
	public abstract int getAmountCount();


	protected void reset() {
		moveTime = 0;
		setWorkload(0);
	}

	protected int getAmountId(int id) {
		return amount[id];
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

}
