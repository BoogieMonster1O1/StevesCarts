package vswe.stevesvehicles.block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.IBlockAccess;

import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

public class BlockRailJunction extends BlockSpecialRailBase {

	private IIcon normalIcon;
	private IIcon cornerIcon;

	public BlockRailJunction() {
		super(false);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return meta >= 6 ? cornerIcon : normalIcon;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		normalIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":rails/junction");
		cornerIcon = register.registerIcon(StevesVehicles.instance.textureHeader + ":rails/junction_corner");
	}

	@Override
	public boolean canMakeSlopes(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
		if (cart instanceof EntityModularCart) {
			EntityModularCart modularCart = (EntityModularCart)cart;

			int meta = modularCart.getRailMeta(x, y, z);

			if (meta != -1) {
				return meta;
			}
		}


		return world.getBlockMetadata(x, y, z);
	}
}
