package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.DYNAMITE;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;

import net.minecraft.init.Items;

import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.client.rendering.models.common.ModelCake;
import vswe.stevesvehicles.holiday.HolidayType;
import vswe.stevesvehicles.localization.entry.info.LocalizationMessage;
import vswe.stevesvehicles.module.common.attachment.ModuleCakeServer;
import vswe.stevesvehicles.module.common.attachment.ModuleCakeServerDynamite;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.vehicle.VehicleRegistry;


public class ModuleRegistryCake extends ModuleRegistry {
	public ModuleRegistryCake() {
		super("common.cake");

		ModuleData cake = new ModuleData("cake_server", ModuleCakeServer.class, 10) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Cake", new ModelCake());
			}
		};

		cake.addShapedRecipe(   null,           Items.cake,         null,
				"slabWood",     "slabWood",         "slabWood",
				null,           SIMPLE_PCB,         null);

		cake.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		cake.addSides(ModuleSide.TOP);
		cake.addMessage(LocalizationMessage.YEAR);
		register(cake);


		ModuleData trick = new ModuleData("trick_or_treat_cake_server", ModuleCakeServerDynamite.class, 15) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("Cake", new ModelCake());
			}
		};

		trick.addShapedRecipe(      null,           Items.cake,         null,
				"slabWood",     "slabWood",         "slabWood",
				DYNAMITE,       SIMPLE_PCB,         DYNAMITE);

		trick.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		trick.addSides(ModuleSide.TOP);
		register(trick);

		if (!StevesVehicles.holidays.contains(HolidayType.HALLOWEEN)) {
			trick.lock();
		}
	}
}
