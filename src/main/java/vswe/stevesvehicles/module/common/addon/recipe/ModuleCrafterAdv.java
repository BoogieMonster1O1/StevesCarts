package vswe.stevesvehicles.module.common.addon.recipe;

import vswe.stevesvehicles.vehicle.VehicleBase;

public class ModuleCrafterAdv extends ModuleCrafter {
	public ModuleCrafterAdv(VehicleBase vehicleBase) {
		super(vehicleBase);
	}

	@Override
	protected boolean canUseAdvancedFeatures() {
		return true;
	}
}
