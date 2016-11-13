package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.ADVANCED_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.DYNAMITE;
import static vswe.stevesvehicles.item.ComponentTypes.REFINED_HARDENER;
import static vswe.stevesvehicles.item.ComponentTypes.REINFORCED_METAL;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import vswe.stevesvehicles.client.rendering.models.common.ModelDynamite;
import vswe.stevesvehicles.client.rendering.models.common.ModelShield;
import vswe.stevesvehicles.module.common.addon.ModuleShield;
import vswe.stevesvehicles.module.common.addon.chunk.ModuleChunkLoader;
import vswe.stevesvehicles.module.common.attachment.ModuleDynamite;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.vehicle.VehicleRegistry;


public class ModuleRegistryIndependence extends ModuleRegistry {
	public ModuleRegistryIndependence() {
		super("common.independence");

		ModuleData dynamite = new ModuleData("dynamite_carrier", ModuleDynamite.class, 3) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Tnt",new ModelDynamite());
			}
		};

		dynamite.addShapedRecipe(   null,           DYNAMITE,                   null,
				DYNAMITE,       Items.flint_and_steel,      DYNAMITE,
				null,           DYNAMITE,                   null);

		dynamite.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		dynamite.addSides(ModuleSide.TOP);
		register(dynamite);



		ModuleData shield = new ModuleData("divine_shield", ModuleShield.class, 60) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Shield", new ModelShield());
				setModelMultiplier(0.68F);
			}
		};

		shield.addShapedRecipe(     Blocks.obsidian,        REFINED_HARDENER,       Blocks.obsidian,
				REFINED_HARDENER,       Blocks.diamond_block,   REFINED_HARDENER,
				Blocks.obsidian,        REFINED_HARDENER,       Blocks.obsidian);

		shield.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(shield);



		ModuleData chunk = new ModuleData("chunk_loader", ModuleChunkLoader.class, 84);

		chunk.addShapedRecipe(  null,                   Items.ender_pearl,          null,
				SIMPLE_PCB,             Items.iron_ingot,           SIMPLE_PCB,
				REINFORCED_METAL,       ADVANCED_PCB,               REINFORCED_METAL);

		chunk.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(chunk);
	}
}
