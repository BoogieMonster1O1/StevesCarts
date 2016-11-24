package stevesvehicles.common.modules.datas;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import stevesvehicles.client.localization.entry.info.LocalizationLabel;
import stevesvehicles.common.items.ModItems;
import stevesvehicles.common.modules.ModuleBase;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.utils.NBTHelper;
import stevesvehicles.common.utils.Tuple;
import stevesvehicles.common.vehicles.VehicleBase;
import stevesvehicles.common.vehicles.VehicleRegistry;
import stevesvehicles.common.vehicles.VehicleType;
import stevesvehicles.common.vehicles.version.VehicleVersion;

public final class ModuleDataItemHandler {
	public static String checkForErrors(ModuleDataHull hull, ArrayList<ModuleData> modules) {
		if (hull.getValidVehicles() == null || hull.getValidVehicles().isEmpty()) {
			return LocalizationLabel.NO_VEHICLE_TYPE.translate();
		}
		VehicleType vehicleType = hull.getValidVehicles().get(0);
		return checkForErrors(vehicleType, hull, modules);
	}

	public static String checkForErrors(VehicleType vehicle, ModuleDataHull hull, List<ModuleData> modules) {
		// Normal errors here
		if (getTotalCost(modules) > hull.getModularCapacity()) {
			return LocalizationLabel.CAPACITY_ERROR.translate();
		}
		for (int i = 0; i < modules.size(); i++) {
			ModuleData mod1 = modules.get(i);
			if (mod1.getValidVehicles() == null || !mod1.getValidVehicles().contains(vehicle)) {
				return LocalizationLabel.INVALID_VEHICLE_TYPE.translate(mod1.getName(), vehicle.getName());
			}
			if (mod1.getCost() > hull.getComplexityMax()) {
				return LocalizationLabel.COMPLEXITY_ERROR.translate(mod1.getName());
			}
			if (mod1.getParent() != null && !modules.contains(mod1.getParent())) {
				return LocalizationLabel.PARENT_ERROR.translate(mod1.getName(), mod1.getParent().getName());
			}
			if (mod1.getNemesis() != null) {
				for (ModuleData nemesis : mod1.getNemesis()) {
					if (modules.contains(nemesis)) {
						return LocalizationLabel.NEMESIS_ERROR.translate(mod1.getName(), nemesis.getName());
					}
				}
			}
			if (mod1.getRequirement() != null) {
				for (ModuleDataGroup group : mod1.getRequirement()) {
					int count = 0;
					for (ModuleData mod2 : group.getModules()) {
						for (ModuleData mod3 : modules) {
							if (mod2.equals(mod3)) {
								count++;
							}
						}
					}
					if (count < group.getCount()) {
						return LocalizationLabel.PARENT_ERROR.translate(mod1.getName(), group.getCountName() + " " + group.getName());
					}
				}
			}
			for (int j = i + 1; j < modules.size(); j++) {
				ModuleData mod2 = modules.get(j);
				if (mod1 == mod2) {
					if (!mod1.getAllowDuplicate()) {
						return LocalizationLabel.DUPLICATE_ERROR.translate(mod1.getName());
					}
				} else if (mod1.getSides() != null && mod2.getSides() != null) {
					ModuleSide clash = null;
					for (ModuleSide side1 : mod1.getSides()) {
						for (ModuleSide side2 : mod2.getSides()) {
							if (side1 == side2) {
								clash = side1;
								break;
							}
						}
						if (clash != null) {
							break;
						}
					}
					if (clash != null) {
						return LocalizationLabel.CLASH_ERROR.translate(mod1.getName(), mod2.getName(), clash.toString());
					}
				}
			}
		}
		return null;
	}

	public static int getTotalCost(List<ModuleData> modules) {
		int currentCost = 0;
		for (ModuleData module : modules) {
			currentCost += module.getCost();
		}
		return currentCost;
	}

	public static boolean isValidModuleItem(ModuleType type, ItemStack itemstack) {
		ModuleData module = ModItems.modules.getModuleData(itemstack);
		return isValidModuleItem(type, module);
	}

	public static boolean isValidModuleItem(ModuleType type, ModuleData module) {
		return module != null && module.getModuleType() == type;
	}

	public static boolean isItemOfModularType(ItemStack itemstack, Class<? extends ModuleBase> validClass) {
		if (itemstack.getItem() == ModItems.modules) {
			ModuleData module = ModItems.modules.getModuleData(itemstack);
			if (module != null) {
				if (validClass.isAssignableFrom(module.getModuleClass())) {
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack createModularVehicle(List<ItemStack> moduleItems) {
		VehicleType vehicleType = null;
		List<ModuleData> modules = new ArrayList<>();
		List<NBTTagCompound> moduleCompounds = new ArrayList<>();
		for (ItemStack moduleItem : moduleItems) {
			ModuleData moduleData = ModItems.modules.getModuleData(moduleItem);
			if (moduleData != null) {
				modules.add(moduleData);
				NBTTagCompound moduleCompound = null;
				if (moduleItem.hasTagCompound() && moduleItem.getTagCompound().hasKey(ModuleData.NBT_MODULE_EXTRA_DATA)) {
					moduleCompound = moduleItem.getTagCompound().getCompoundTag(ModuleData.NBT_MODULE_EXTRA_DATA);
				}
				moduleCompounds.add(moduleCompound);
				if (moduleData.getModuleType() == ModuleType.HULL) {
					if (moduleData.getValidVehicles() == null || moduleData.getValidVehicles().isEmpty()) {
						return null;
					}
					vehicleType = moduleData.getValidVehicles().get(0);
				}
			}
		}
		if (vehicleType != null) {
			return createModularVehicle(vehicleType, modules, null, moduleCompounds);
		} else {
			return null;
		}
	}

	public static ItemStack createModularVehicle(VehicleType vehicle, List<ModuleData> moduleDataList, List<ModuleBase> modules, List<NBTTagCompound> moduleSourceCompounds) {
		if (vehicle == null) {
			return null;
		}
		int vehicleId = VehicleRegistry.getInstance().getIdFromType(vehicle);
		if (vehicleId < 0) {
			return null;
		}
		NBTTagCompound data = new NBTTagCompound();
		data.setTag(VehicleBase.NBT_MODULES, getModuleList(moduleDataList, modules, moduleSourceCompounds));
		ItemStack vehicleItem = new ItemStack(ModItems.vehicles, 1, vehicleId);
		vehicleItem.setTagCompound(data);
		VehicleVersion.addVersion(vehicleItem);
		return vehicleItem;
	}

	private static NBTTagList getModuleList(List<ModuleData> moduleDataList, List<ModuleBase> modules, List<NBTTagCompound> moduleSourceCompounds) {
		NBTTagList modulesCompoundList = new NBTTagList();
		for (int i = 0; i < moduleDataList.size(); i++) {
			ModuleData moduleData = moduleDataList.get(i);
			ModuleBase module = modules != null ? modules.get(i) : null;
			NBTTagCompound moduleSourceCompound = moduleSourceCompounds != null ? moduleSourceCompounds.get(i) : null;
			NBTTagCompound moduleCompound = moduleSourceCompound == null ? new NBTTagCompound() : (NBTTagCompound) moduleSourceCompound.copy();
			int id = ModuleRegistry.getIdFromModule(moduleData);
			if (id >= 0) {
				moduleCompound.setShort(VehicleBase.NBT_ID, (short) id);
				if (module != null) {
					moduleData.addExtraData(moduleCompound, module);
				} else if (moduleSourceCompound == null) {
					moduleData.addDefaultExtraData(moduleCompound);
				}
				modulesCompoundList.appendTag(moduleCompound);
			}
		}
		return modulesCompoundList;
	}

	public static void addSparesToVehicleItems(ItemStack vehicle, List<ItemStack> spares) {
		List<ModuleData> modules = new ArrayList<>();
		List<NBTTagCompound> moduleCompounds = new ArrayList<>();
		for (ItemStack moduleItem : spares) {
			ModuleData moduleData = ModItems.modules.getModuleData(moduleItem);
			if (moduleData != null) {
				modules.add(moduleData);
				NBTTagCompound moduleCompound = null;
				if (moduleItem.hasTagCompound() && moduleItem.getTagCompound().hasKey(ModuleData.NBT_MODULE_EXTRA_DATA)) {
					moduleCompound = moduleItem.getTagCompound().getCompoundTag(ModuleData.NBT_MODULE_EXTRA_DATA);
				}
				moduleCompounds.add(moduleCompound);
			}
		}
		vehicle.getTagCompound().setTag(VehicleBase.NBT_SPARES, getModuleList(modules, null, moduleCompounds));
	}

	public static ItemStack createModularVehicle(VehicleBase vehicle) {
		return createModularVehicle(vehicle.getVehicleType(), vehicle.getModuleDataList(), vehicle.getModules(), null);
	}

	public static List<ModuleData> getModulesFromItem(ItemStack item) {
		return getModulesFromItem(item, VehicleBase.NBT_MODULES);
	}

	public static List<ModuleData> getSpareModulesFromItem(ItemStack item) {
		return getModulesFromItem(item, VehicleBase.NBT_SPARES);
	}

	private static List<ModuleData> getModulesFromItem(ItemStack item, String tag) {
		NBTTagCompound compound = item.getTagCompound();
		if (compound != null && compound.hasKey(tag)) {
			List<ModuleData> modules = new ArrayList<>();
			NBTTagList modulesList = compound.getTagList(tag, NBTHelper.COMPOUND.getId());
			for (int i = 0; i < modulesList.tagCount(); i++) {
				int id = modulesList.getCompoundTagAt(i).getShort(VehicleBase.NBT_ID);
				ModuleData module = ModuleRegistry.getModuleFromId(id);
				if (module != null) {
					modules.add(module);
				}
			}
			return modules;
		}
		return null;
	}

	public static List<Tuple<ModuleData, NBTTagCompound>> getModulesAndCompoundsFromItem(ItemStack item) {
		return getModulesAndCompoundsFromItem(item, VehicleBase.NBT_MODULES);
	}

	public static List<Tuple<ModuleData, NBTTagCompound>> getSpareModulesAndCompoundsFromItem(ItemStack item) {
		return getModulesAndCompoundsFromItem(item, VehicleBase.NBT_SPARES);
	}

	private static List<Tuple<ModuleData, NBTTagCompound>> getModulesAndCompoundsFromItem(ItemStack item, String tag) {
		NBTTagCompound compound = item.getTagCompound();
		if (compound != null && compound.hasKey(tag)) {
			List<Tuple<ModuleData, NBTTagCompound>> modules = new ArrayList<>();
			NBTTagList modulesList = compound.getTagList(tag, NBTHelper.COMPOUND.getId());
			for (int i = 0; i < modulesList.tagCount(); i++) {
				NBTTagCompound moduleCompound = modulesList.getCompoundTagAt(i);
				int id = moduleCompound.getShort(VehicleBase.NBT_ID);
				ModuleData module = ModuleRegistry.getModuleFromId(id);
				if (module != null) {
					modules.add(new Tuple<>(module, moduleCompound));
				}
			}
			return modules;
		}
		return null;
	}

	private ModuleDataItemHandler() {
	}

	public static List<ItemStack> getModularItems(ItemStack vehicle) {
		return getModularItemsFromData(getModulesAndCompoundsFromItem(vehicle));
	}

	public static List<ItemStack> getSpareItems(ItemStack vehicle) {
		return getModularItemsFromData(getSpareModulesAndCompoundsFromItem(vehicle));
	}

	private static List<ItemStack> getModularItemsFromData(List<Tuple<ModuleData, NBTTagCompound>> modules) {
		List<ItemStack> items = new ArrayList<>();
		for (Tuple<ModuleData, NBTTagCompound> module : modules) {
			ModuleData moduleData = module.getFirstObject();
			NBTTagCompound compound = module.getSecondObject();
			ItemStack item = new ItemStack(ModItems.modules, 1, ModuleRegistry.getIdFromModule(moduleData));
			if (moduleData.hasExtraData() && compound != null) {
				NBTTagCompound moduleCompound = compound.copy();
				moduleCompound.removeTag(VehicleBase.NBT_ID);
				NBTTagCompound itemCompound = new NBTTagCompound();
				item.setTagCompound(itemCompound);
				itemCompound.setTag(ModuleData.NBT_MODULE_EXTRA_DATA, moduleCompound);
			}
			items.add(item);
		}
		return items;
	}

	public static boolean hasModules(ItemStack vehicle) {
		NBTTagCompound compound = vehicle.getTagCompound();
		return compound != null && compound.hasKey(VehicleBase.NBT_MODULES);
	}
}
