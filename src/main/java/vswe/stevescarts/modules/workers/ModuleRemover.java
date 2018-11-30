package vswe.stevescarts.modules.workers;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevescarts.entitys.EntityMinecartModular;
import vswe.stevescarts.modules.IActivatorModule;

import javax.annotation.Nonnull;

public class ModuleRemover extends ModuleWorker implements IActivatorModule {
	@Nonnull
	private BlockPos remove;
	private DataParameter<Boolean> IS_ENABLED;

	public ModuleRemover(final EntityMinecartModular cart) {
		super(cart);
		remove = new BlockPos(0, -1, 0);
	}

	@Override
	public void initDw() {
		IS_ENABLED = createDw(DataSerializers.BOOLEAN);
		registerDw(IS_ENABLED, true);
	}

	@Override
	public int numberOfPackets() {
		return 1;
	}

	@Override
	public int numberOfDataWatchers() {
		return 1;
	}

	@Override
	public byte getWorkPriority() {
		return 120;
	}

	@Override
	protected boolean preventTurnback() {
		return true;
	}

	@Override
	public boolean work() {
		EntityMinecartModular cart = getCart();
		World world = cart.world;
		if (remove.getY() != -1 && (remove.getX() != cart.x() || remove.getZ() != cart.z()) && removeRail(world, remove, true)) {
			return false;
		}
		BlockPos next = getNextblock();
		BlockPos last = getLastblock();
		final boolean front = isRailAtCoords(world, next);
		final boolean back = isRailAtCoords(world, last);
		if (!front) {
			if (back) {
				turnback();
				if (removeRail(world, cart.getPosition(), false)) {
					return true;
				}
			}
		} else if (!back && removeRail(world, cart.getPosition(), false)) {
			return true;
		}
		return false;
	}

	private boolean isRailAtCoords(World world, BlockPos coords) {
		return BlockRailBase.isRailBlock(world, coords.up()) || BlockRailBase.isRailBlock(getCart().world, coords) || BlockRailBase.isRailBlock(getCart().world, coords.down());
	}

	private boolean removeRail(World world, BlockPos pos, final boolean flag) {
		if (flag) {
			IBlockState blockState = world.getBlockState(pos);
			if (BlockRailBase.isRailBlock(blockState)) {
				if (isRemovingEnabled()) {
					if (doPreWork()) {
						startWorking(12);
						return true;
					}
					@Nonnull
					ItemStack iStack = new ItemStack(blockState.getBlock(), 1, 0);
					getCart().addItemToChest(iStack);
					if (iStack.getCount() == 0) {
						world.setBlockToAir(pos);
					}
				}
				remove = new BlockPos(pos.getX(), -1, pos.getZ());
			} else {
				remove = new BlockPos(pos.getX(), -1, pos.getZ());
			}
		} else if (BlockRailBase.isRailBlock(world, pos.down())) {
			remove = pos.down();
		} else if (BlockRailBase.isRailBlock(world, pos)) {
			remove = pos;
		} else if (BlockRailBase.isRailBlock(world, pos.up())) {
			remove = pos.up();
		}
		stopWorking();
		return false;
	}

	private void enableRemoving(final boolean remove) {
		if (!isPlaceholder()) {
			updateDw(IS_ENABLED, remove);
		}
	}

	private boolean isRemovingEnabled() {
		return !isPlaceholder() && getDw(IS_ENABLED);
	}

	@Override
	public void doActivate(final int id) {
		enableRemoving(true);
	}

	@Override
	public void doDeActivate(final int id) {
		enableRemoving(false);
	}

	@Override
	public boolean isActive(final int id) {
		return isRemovingEnabled();
	}
}
