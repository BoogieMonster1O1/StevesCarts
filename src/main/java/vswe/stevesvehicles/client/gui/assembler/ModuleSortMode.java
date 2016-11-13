package vswe.stevesvehicles.client.gui.assembler;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import vswe.stevesvehicles.container.slots.SlotAssembler;
import vswe.stevesvehicles.localization.ILocalizedText;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataGroup;
import vswe.stevesvehicles.module.data.ModuleDataHull;
import vswe.stevesvehicles.module.data.ModuleDataItemHandler;
import vswe.stevesvehicles.module.data.ModuleSide;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;

public enum ModuleSortMode {
	RELAXED(LocalizationAssembler.SEARCH_RELAXED) {
		@Override
		public boolean isValid(TileEntityCartAssembler assembler, ModuleDataHull hull, ModuleData moduleData) {
			return true;
		}
	},
	NORMAL(LocalizationAssembler.SEARCH_NORMAL) {
		@Override
		public boolean isValid(TileEntityCartAssembler assembler, ModuleDataHull hull, ModuleData moduleData) {
			moduleRecursiveCache.clear();
			return doesModuleDataFit(assembler, moduleData) && isModuleValid(hull, moduleData);
		}

		private List<ModuleData> moduleRecursiveCache = new ArrayList<>();

		private boolean isModuleValid(ModuleDataHull hull, ModuleData module) {
			if (module.getCost() > hull.getComplexityMax()) {
				return false;
			}

			if (hull.getSides() != null && module.getSides() != null) {
				for (ModuleSide side : hull.getSides()) {
					if (module.getSides().contains(side)) {
						return false;
					}
				}
			}

			try {
				if (moduleRecursiveCache.contains(module)) {
					return true;
				}
				moduleRecursiveCache.add(module);

				if (module.getParent() != null && ! isModuleValid(hull, module.getParent())) {
					return false;
				}else if(module.getRequirement() != null) {
					for (ModuleDataGroup moduleDataGroup : module.getRequirement()) {
						boolean isAnyValid = false;

						for (ModuleData moduleData : moduleDataGroup.getModules()) {
							if (isModuleValid(hull, moduleData)) {
								isAnyValid = true;
								break;
							}
						}

						if (!isAnyValid) {
							return false;
						}
					}
				}

				return true;
			}finally {
				moduleRecursiveCache.remove(module) ;
			}
		}
	},
	STRICT(LocalizationAssembler.SEARCH_STRICT) {
		@Override
		public boolean isValid(TileEntityCartAssembler assembler, ModuleDataHull hull, ModuleData moduleData) {
			if (!doesModuleDataFit(assembler, moduleData)) {
				return false;
			}

			List<ModuleData> modules = new ArrayList<>();
			modules.addAll(assembler.getModules(false));
			modules.add(moduleData);
			return ModuleDataItemHandler.checkForErrors(hull.getVehicle(), hull, modules) == null;
		}
	};


	private static boolean doesModuleDataFit(TileEntityCartAssembler assembler, ModuleData module) {
		ItemStack item = module.getItemStack();
		for (SlotAssembler slot : assembler.getSlots()) {
			if (!slot.getHasStack() && slot.isItemValid(item)) {
				return true;
			}
		}
		return false;
	}


	private ILocalizedText name;

	ModuleSortMode(ILocalizedText name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name.translate();
	}

	public abstract boolean isValid(TileEntityCartAssembler assembler, ModuleDataHull hull, ModuleData moduleData);


}
