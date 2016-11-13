package vswe.stevesvehicles.container.slots;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotBuilder extends SlotBase {
	public SlotBuilder(IInventory inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return Block.getBlockFromItem(itemstack.getItem()) == Blocks.rail;
	}
}
