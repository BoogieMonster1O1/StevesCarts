package vswe.stevesvehicles.vehicle;

import net.minecraft.util.text.translation.I18n;
import vswe.stevesvehicles.vehicle.entity.IVehicleEntity;

public class VehicleType {
	private Class<? extends IVehicleEntity> clazz;
	private final String unlocalizedName;

	public VehicleType(Class<? extends IVehicleEntity> clazz, String unlocalizedName) {
		this.clazz = clazz;
		this.unlocalizedName = unlocalizedName;
	}

	public String getUnlocalizedNameForItem() {
		return "steves_vehicles:item.vehicle:" + unlocalizedName;
	}

	public String getUnlocalizedName() {
		return getUnlocalizedNameForItem() + ".name";
	}

	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	public Class<? extends IVehicleEntity> getClazz() {
		return clazz;
	}

	public String getName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}
	/*
	 * @SideOnly(Side.CLIENT) private IIcon fallbackIcon;
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister register)
	 * { fallbackIcon =
	 * register.registerIcon(StevesVehicles.instance.textureHeader +
	 * ":vehicles/" + getRawUnlocalizedName()); }
	 * @SideOnly(Side.CLIENT) public IIcon getFallbackIcon() { return
	 * fallbackIcon; }
	 */
}
