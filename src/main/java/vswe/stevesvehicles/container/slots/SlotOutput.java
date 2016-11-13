package vswe.stevesvehicles.container.slots;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.module.data.ModuleType;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class SlotOutput extends SlotAssembler {

	public SlotOutput(TileEntityCartAssembler assembler, int id, int x, int y) {
		super(assembler, id, x, y, ModuleType.INVALID, true, 0);

	}

	@Override
	public void validate() {

	}	

	@Override
	public void invalidate() {

	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		if (!getAssembler().getIsAssembling() && itemstack.getItem() == ModItems.vehicles) {
			NBTTagCompound info = itemstack.getTagCompound();
			if (info != null && info.hasKey(VehicleBase.NBT_INTERRUPT_MAX_TIME)) {
				return true;
			}

		}
		return false;
	}	

	@Override
	public boolean shouldUpdatePlaceholder() {
		return false;
	}

}
