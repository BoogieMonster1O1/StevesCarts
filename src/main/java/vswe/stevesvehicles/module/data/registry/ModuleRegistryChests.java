package vswe.stevesvehicles.module.data.registry;

import static vswe.stevesvehicles.item.ComponentTypes.BASKET;
import static vswe.stevesvehicles.item.ComponentTypes.BURNING_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.CHEST_LOCK;
import static vswe.stevesvehicles.item.ComponentTypes.CHOCOLATE_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.EXPLOSIVE_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.GLISTERING_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.GREEN_WRAPPING_PAPER;
import static vswe.stevesvehicles.item.ComponentTypes.PAINTED_EASTER_EGG;
import static vswe.stevesvehicles.item.ComponentTypes.RED_GIFT_RIBBON;
import static vswe.stevesvehicles.item.ComponentTypes.RED_WRAPPING_PAPER;
import static vswe.stevesvehicles.item.ComponentTypes.SIMPLE_PCB;
import static vswe.stevesvehicles.item.ComponentTypes.STUFFED_SOCK;
import static vswe.stevesvehicles.item.ComponentTypes.YELLOW_GIFT_RIBBON;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.client.rendering.models.common.ModelEggBasket;
import vswe.stevesvehicles.client.rendering.models.common.ModelExtractingChests;
import vswe.stevesvehicles.client.rendering.models.common.ModelFrontChest;
import vswe.stevesvehicles.client.rendering.models.common.ModelGiftStorage;
import vswe.stevesvehicles.client.rendering.models.common.ModelSideChests;
import vswe.stevesvehicles.client.rendering.models.common.ModelTopChest;
import vswe.stevesvehicles.holiday.GiftItem;
import vswe.stevesvehicles.holiday.HolidayType;
import vswe.stevesvehicles.localization.ILocalizedText;
import vswe.stevesvehicles.localization.entry.info.LocalizationMessage;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.storage.chest.ModuleEggBasket;
import vswe.stevesvehicles.module.common.storage.chest.ModuleExtractingChests;
import vswe.stevesvehicles.module.common.storage.chest.ModuleFrontChest;
import vswe.stevesvehicles.module.common.storage.chest.ModuleGiftStorage;
import vswe.stevesvehicles.module.common.storage.chest.ModuleSideChests;
import vswe.stevesvehicles.module.common.storage.chest.ModuleTopChest;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.vehicle.VehicleRegistry;


public class ModuleRegistryChests extends ModuleRegistry {

	private static final String PLANK = "plankWood";
	private static final String SLAB = "slabWood";

	public ModuleRegistryChests() {
		super("common.chests");

		ModuleData side = new ModuleData("side_chests", ModuleSideChests.class, 3) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideChest", new ModelSideChests());
			}
		};

		side.addShapedRecipe(   PLANK,       SLAB,          PLANK,
				PLANK,       CHEST_LOCK,    PLANK,
				PLANK,       SLAB,          PLANK);


		side.addSides(ModuleSide.LEFT, ModuleSide.RIGHT);
		side.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(side);



		ModuleData top = new ModuleData("top_chest", ModuleTopChest.class, 6) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				removeModel("Top");
				addModel("TopChest", new ModelTopChest());
			}
		};

		top.addShapedRecipe(    SLAB,       SLAB,           SLAB,
				SLAB,       CHEST_LOCK,     SLAB,
				PLANK,      PLANK,          PLANK);


		top.addSides(ModuleSide.TOP);
		top.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(top);



		ModuleData front = new ModuleData("front_chest", ModuleFrontChest.class, 5) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("FrontChest", new ModelFrontChest());
				setModelMultiplier(0.68F);
			}
		};

		front.addShapedRecipe(  null,        PLANK,           null,
				SLAB,        CHEST_LOCK,      SLAB,
				PLANK,       PLANK,           PLANK);


		front.addSides(ModuleSide.FRONT);
		front.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(front);


		ModuleData internal = new ModuleData("internal_storage", ModuleFrontChest.class, 25);
		internal.addShapedRecipe(   SLAB,       SLAB,           SLAB,
				SLAB,       CHEST_LOCK,     SLAB,
				SLAB,       SLAB,           SLAB);


		internal.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		internal.setAllowDuplicate(true);
		register(internal);


		ModuleData extracting = new ModuleData("extracting_chests", ModuleExtractingChests.class, 75) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideChest", new ModelExtractingChests());
			}
		};

		extracting.addShapedRecipe(     Items.iron_ingot,     SIMPLE_PCB,       Items.iron_ingot,
				Items.iron_ingot,     CHEST_LOCK,       Items.iron_ingot,
				Items.iron_ingot,     SIMPLE_PCB,       Items.iron_ingot);


		extracting.addSides(ModuleSide.LEFT, ModuleSide.RIGHT, ModuleSide.CENTER);
		extracting.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(extracting);



		ModuleData basket = new ModuleDataTreatStorage("egg_basket", ModuleEggBasket.class, 14, LocalizationMessage.EGG) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("TopChest", new ModelEggBasket());
			}

			@Override
			protected void spawnTreat(ModuleBase module) {
				Random rand = module.getVehicle().getRandom();
				int eggs = 1 + rand.nextInt(4) + rand.nextInt(4);
				ItemStack easterEgg = PAINTED_EASTER_EGG.getItemStack(eggs);
				module.setStack(0, easterEgg);
			}
		};

		basket.addShapedRecipe(     new ItemStack(Blocks.wool, 1, 4),       new ItemStack(Blocks.wool, 1, 4),           new ItemStack(Blocks.wool, 1, 4),
				EXPLOSIVE_EASTER_EGG,                   CHEST_LOCK,                                 BURNING_EASTER_EGG,
				GLISTERING_EASTER_EGG,                  BASKET,                                     CHOCOLATE_EASTER_EGG);


		basket.addSides(ModuleSide.TOP);
		basket.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(basket);

		if (!StevesVehicles.holidays.contains(HolidayType.EASTER)) {
			basket.lock();
		}


		ModuleData gift = new ModuleDataTreatStorage("gift_storage", ModuleGiftStorage.class, 12, LocalizationMessage.GIFT) {
			@Override
			@SideOnly(Side.CLIENT)
			public void loadModels() {
				addModel("SideChest", new ModelGiftStorage());
			}

			@Override
			protected void spawnTreat(ModuleBase module) {
				Random rand = module.getVehicle().getRandom();
				ArrayList<ItemStack> items = GiftItem.generateItems(rand, GiftItem.ChristmasList, 50 + rand.nextInt(700), 1 + rand.nextInt(5));
				for (int i = 0; i < items.size(); i++) {
					module.getVehicle().setStack(i, items.get(i));
				}
			}
		};

		gift.addShapedRecipe(   YELLOW_GIFT_RIBBON,       null,             RED_GIFT_RIBBON,
				RED_WRAPPING_PAPER,       CHEST_LOCK,       GREEN_WRAPPING_PAPER,
				RED_WRAPPING_PAPER,      STUFFED_SOCK,      GREEN_WRAPPING_PAPER);


		gift.addSides(ModuleSide.LEFT, ModuleSide.RIGHT);
		gift.addVehicles(VehicleRegistry.CART, VehicleRegistry.BOAT);
		register(gift);

		if (!StevesVehicles.holidays.contains(HolidayType.CHRISTMAS)) {
			gift.lock();
		}

	}

	private static final String STORAGE_OPENED = "Opened";
	private static abstract class ModuleDataTreatStorage extends ModuleData {
		private ILocalizedText fullText;
		public ModuleDataTreatStorage(String unlocalizedName, Class<? extends ModuleBase> moduleClass, int modularCost, ILocalizedText fullText) {
			super(unlocalizedName, moduleClass, modularCost);
			setHasExtraData(true);
			this.fullText = fullText;
		}

		@Override
		public void addDefaultExtraData(NBTTagCompound compound) {
			compound.setBoolean(STORAGE_OPENED, false);
		}

		@Override
		public void addExtraData(NBTTagCompound compound, ModuleBase module) {
			compound.setBoolean(STORAGE_OPENED, true);
		}

		@Override
		public void readExtraData(NBTTagCompound compound, ModuleBase moduleBase) {
			if (!compound.getBoolean(STORAGE_OPENED)) {
				spawnTreat(moduleBase);
			}
		}

		@Override
		public String getCartInfoText(String name, NBTTagCompound compound) {
			if (compound.getBoolean(STORAGE_OPENED)) {
				return LocalizationMessage.EMPTY_STORAGE.translate() + " " + name;
			}else{
				return LocalizationMessage.FULL_STORAGE.translate() + " " + name;
			}
		}

		@Override
		public String getModuleInfoText(NBTTagCompound compound) {
			if (compound.getBoolean(STORAGE_OPENED)) {
				return LocalizationMessage.EMPTY_STORAGE.translate();
			}else{
				return fullText.translate();
			}
		}

		protected abstract void spawnTreat(ModuleBase module);
	}
}
