package vswe.stevesvehicles.module.data.registry.cart;

import static vswe.stevesvehicles.item.ComponentTypes.CLEANING_CORE;
import static vswe.stevesvehicles.item.ComponentTypes.CLEANING_TUBE;
import static vswe.stevesvehicles.item.ComponentTypes.LIQUID_CLEANING_CORE;
import static vswe.stevesvehicles.item.ComponentTypes.LIQUID_CLEANING_TUBE;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.cart.ModelCleaner;
import vswe.stevesvehicles.client.rendering.models.cart.ModelLiquidDrainer;
import vswe.stevesvehicles.client.rendering.models.common.ModelHullTop;
import vswe.stevesvehicles.module.cart.attachment.ModuleCleaner;
import vswe.stevesvehicles.module.cart.attachment.ModuleExperience;
import vswe.stevesvehicles.module.cart.attachment.ModuleLiquidDrainer;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.vehicle.VehicleRegistry;



public class ModuleRegistryCartCleaning extends ModuleRegistry {
	public ModuleRegistryCartCleaning() {
		super("cart.cleaning");

		ModuleData cleaner = new ModuleData("cleaning_machine", ModuleCleaner.class, 23) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Top",  new ModelHullTop(ResourceHelper.getResource("/models/cleanerModelTop.png"), false));
				addModel("Cleaner", new ModelCleaner());
			}
		};
		cleaner.addShapedRecipe(    CLEANING_TUBE,      CLEANING_CORE,      CLEANING_TUBE,
				CLEANING_TUBE,      null,               CLEANING_TUBE,
				CLEANING_TUBE,      null,               CLEANING_TUBE);


		cleaner.addSides(ModuleSide.CENTER);
		cleaner.addVehicles(VehicleRegistry.CART);
		register(cleaner);


		ModuleData liquid = new ModuleData("liquid_cleaner", ModuleLiquidDrainer.class, 30) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Top",  new ModelHullTop(ResourceHelper.getResource("/models/cleanerModelTop.png"), false));
				addModel("Cleaner", new ModelLiquidDrainer());
			}
		};
		liquid.addShapedRecipe(     LIQUID_CLEANING_TUBE,      LIQUID_CLEANING_CORE,        LIQUID_CLEANING_TUBE,
				LIQUID_CLEANING_TUBE,      null,                        LIQUID_CLEANING_TUBE,
				LIQUID_CLEANING_TUBE,      null,                        LIQUID_CLEANING_TUBE);


		liquid.addSides(ModuleSide.CENTER);
		liquid.addVehicles(VehicleRegistry.CART);
		liquid.lockByDefault(); //TODO remove this when it works properly
		register(liquid);


		//TODO figure out how to do these
		//addNemesis(frontChest, cleaner);
		//addNemesis(frontTank, cleaner);
		//addNemesis(frontTank, liquid);
		//addNemesis(frontChest, liquid);
		//liquid.addParent(liquidsensors)


		ModuleData experience = new ModuleData("experience_bank", ModuleExperience.class, 36);
		experience.addShapedRecipe( null,                   Items.REDSTONE,     null,
				Items.GLOWSTONE_DUST,   Items.EMERALD,      Items.GLOWSTONE_DUST,
				SIMPLE_PCB,             Items.CAULDRON,    SIMPLE_PCB);


		experience.addVehicles(VehicleRegistry.CART);
		register(experience);
	}
}
