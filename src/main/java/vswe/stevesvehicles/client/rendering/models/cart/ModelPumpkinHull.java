package vswe.stevesvehicles.client.rendering.models.cart;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.module.ModuleBase;
@SideOnly(Side.CLIENT)
public class ModelPumpkinHull extends ModelHull {

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return (module == null || isActive(module)) ? resourceActive : resourceIdle;
	}


	private final ResourceLocation resourceActive;
	private final ResourceLocation resourceIdle;
	public ModelPumpkinHull(ResourceLocation resourceActive, ResourceLocation resourceIdle) {
		super(resourceActive);
		this.resourceActive = resourceActive;
		this.resourceIdle = resourceIdle;
	}

	private boolean isActive(ModuleBase module) {
		long time = module.getVehicle().getWorld().getWorldInfo().getWorldTime() % 24000;
		return time >= 12000 && time <= 18000;
	}

}
