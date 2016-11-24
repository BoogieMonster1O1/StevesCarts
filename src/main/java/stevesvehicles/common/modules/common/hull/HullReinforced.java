package stevesvehicles.common.modules.common.hull;

import stevesvehicles.common.vehicles.VehicleBase;

public class HullReinforced extends ModuleHull {
	public HullReinforced(VehicleBase vehicle) {
		super(vehicle);
	}

	@Override
	public int getConsumption(boolean isMoving) {
		if (!isMoving) {
			return super.getConsumption(false);
		} else {
			// ToDo: Keep energy consumption as constants
			return 3;
		}
	}
}
