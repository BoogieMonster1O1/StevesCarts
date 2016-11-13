package vswe.stevesvehicles.upgrade.effect.assembly;


import vswe.stevesvehicles.tileentity.TileEntityUpgrade;
import vswe.stevesvehicles.upgrade.effect.BaseEffect;

public class WorkEfficiency extends BaseEffect {



	private float efficiency;
	public WorkEfficiency(TileEntityUpgrade upgrade, Float efficiency) {
		super(upgrade);
		this.efficiency = efficiency;
	}


	public float getEfficiency() {
		return efficiency;
	}


}