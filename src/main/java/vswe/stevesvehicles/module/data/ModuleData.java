package vswe.stevesvehicles.module.data;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.client.gui.ColorHelper;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.localization.ILocalizedText;
import vswe.stevesvehicles.localization.entry.info.LocalizationLabel;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.recipe.IRecipeOutput;
import vswe.stevesvehicles.recipe.ModuleRecipeShaped;
import vswe.stevesvehicles.recipe.ModuleRecipeShapeless;
import vswe.stevesvehicles.vehicle.VehicleType;

public class ModuleData implements IRecipeOutput {
	private final Class<? extends ModuleBase> moduleClass;
	private final String unlocalizedName;
	private String fullUnlocalizedName;
	private final int modularCost;
	private final ModuleType moduleType;
	private ArrayList<ModuleSide> sides;
	private boolean allowDuplicate;
	private ArrayList<ModuleData> nemesis;
	private ArrayList<ModuleDataGroup> requirement;
	private ModuleData parent;
	private boolean isValid;
	private boolean isLocked;
	private boolean defaultLock;
	private boolean hasRecipe;
	private ArrayList<ILocalizedText> message;
	private ArrayList<IRecipe> recipes;
	private ArrayList<VehicleType> validVehicles;
	private boolean extraData;

	@SideOnly(Side.CLIENT)
	private HashMap<String,ModelVehicle> models;
	@SideOnly(Side.CLIENT)
	private HashMap<String,ModelVehicle> modelsPlaceholder;
	@SideOnly(Side.CLIENT)
	private ArrayList<String> removedModels;
	@SideOnly(Side.CLIENT)
	private float modelMultiplier;
	/*@SideOnly(Side.CLIENT)
	private IIcon icon;*/

	public ModuleData(String unlocalizedName, Class<? extends ModuleBase> moduleClass, int modularCost) {
		this.moduleClass = moduleClass;
		if (unlocalizedName.contains(":")) {
			System.err.println("The raw unlocalized name can't contain colons. Any colons have been replaced with underscores.");
		}
		this.unlocalizedName = unlocalizedName.replace(":", "_");
		this.modularCost = modularCost;

		ModuleType moduleType = ModuleType.INVALID;
		for (ModuleType type : ModuleType.values()) {
			if (type.getClazz().isAssignableFrom(moduleClass)) {
				moduleType = type;
				break;
			}
		}
		this.moduleType = moduleType;
		System.out.println("Created " + this.unlocalizedName + "(" + this.moduleClass.getName() + ") with type " + this.moduleType.getName());
	}

	public final Class<? extends ModuleBase> getModuleClass() {
		return moduleClass;
	}

	public final String getRawUnlocalizedName() {
		return unlocalizedName;
	}

	public final String getFullRawUnlocalizedName() {
		return fullUnlocalizedName;
	}

	public final void setFullRawUnlocalizedName(String val) {fullUnlocalizedName = val;}

	public final int getCost() {
		return modularCost;
	}

	public final ModuleType getModuleType() {
		return moduleType;
	}

	public boolean getIsValid() {
		return isValid;
	}

	public boolean getIsLocked() {
		return isLocked;
	}

	public ModuleData lock() {
		isLocked = true;

		return this;
	}

	public boolean getEnabledByDefault() {
		return !defaultLock;
	}

	public ModuleData lockByDefault() {
		defaultLock = true;

		return this;
	}

	public ModuleData setAllowDuplicate(boolean b) {
		allowDuplicate = b;

		return this;
	}

	public boolean getAllowDuplicate() {
		return allowDuplicate;
	}



	public ModuleData setHasExtraData(boolean val) {
		extraData = val;

		return this;
	}

	public boolean hasExtraData() {
		return extraData;
	}

	public void addDefaultExtraData(NBTTagCompound compound) {}
	public void addExtraData(NBTTagCompound compound, ModuleBase module) {}
	public void readExtraData(NBTTagCompound compound, ModuleBase moduleBase) {}

	public String getModuleInfoText(NBTTagCompound compound) {
		return null;
	}

	public String getCartInfoText(String name, NBTTagCompound compound) {
		return name;
	}




	public ArrayList<ModuleSide> getSides() {
		return sides;
	}


	public ModuleData addSides(ModuleSide ... sides) {
		if (this.sides == null) {
			this.sides = new ArrayList<>();
		}
		Collections.addAll(this.sides, sides);

		return this;
	}

	public ModuleData addParent(ModuleData parent) {
		this.parent = parent;

		return this;
	}

	public ModuleData addMessage(ILocalizedText s) {
		if (message == null) {
			message = new ArrayList<>();
		}
		message.add(s);

		return this;
	}

	public void addNemesis(ModuleData nemesis) {
		if (this.nemesis == null) {
			this.nemesis = new ArrayList<>();
		}
		this.nemesis.add(nemesis);
	}

	public ModuleData addRequirement(ModuleDataGroup requirement) {
		if (this.requirement == null) {
			this.requirement = new ArrayList<>();
		}
		this.requirement.add(requirement);

		return this;
	}

	public static void addNemesis(ModuleData m1, ModuleData m2) {
		m2.addNemesis(m1);
		m1.addNemesis(m2);
	}

	@SideOnly(Side.CLIENT)
	public float getModelMultiplier() {
		return modelMultiplier;
	}

	@SideOnly(Side.CLIENT)
	public ModuleData setModelMultiplier(float val) {
		modelMultiplier = val;

		return this;
	}

	@SideOnly(Side.CLIENT)
	public ModuleData addModel(String tag, ModelVehicle model) {
		addModel(tag, model, false);
		addModel(tag, model, true);
		return this;
	}
	@SideOnly(Side.CLIENT)
	public ModuleData addModel(String tag, ModelVehicle model, boolean placeholder) {
		if (placeholder) {
			if (modelsPlaceholder == null) {
				modelsPlaceholder = new HashMap<>();
			}

			modelsPlaceholder.put(tag, model);
		}else{
			if (models == null) {
				models = new HashMap<>();
			}

			models.put(tag, model);
		}

		return this;
	}

	@SideOnly(Side.CLIENT)
	public HashMap<String,ModelVehicle> getModels(boolean placeholder) {
		if (placeholder) {
			return modelsPlaceholder;
		}else{
			return models;
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean haveModels(boolean placeholder) {
		if (placeholder) {
			return modelsPlaceholder != null;
		}else{
			return models != null;
		}
	}

	@SideOnly(Side.CLIENT)
	public ModuleData removeModel(String tag) {
		if (removedModels == null) {
			removedModels = new ArrayList<>();
		}

		if(!removedModels.contains(tag)) {
			removedModels.add(tag);
		}
		return this;
	}

	@SideOnly(Side.CLIENT)
	public ArrayList<String> getRemovedModels() {
		return removedModels;
	}

	@SideOnly(Side.CLIENT)
	public boolean haveRemovedModels() {
		return removedModels != null;
	}

	public String getName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}

	public String getUnlocalizedName() {
		return getUnlocalizedNameForItem() + ".name";
	}

	public String getUnlocalizedNameForItem() {
		return "steves_vehicles:item." + getFullRawUnlocalizedName();
	}

	public ModuleData getParent() {
		return parent;
	}

	public ArrayList<ModuleData> getNemesis() {
		return nemesis;
	}

	public ArrayList<ModuleDataGroup> getRequirement() {
		return requirement;
	}

	public boolean getHasRecipe() {
		return hasRecipe;
	}


	public void addSpecificInformation(List<String> list) {
		list.add(ColorHelper.LIGHT_GRAY + LocalizationLabel.MODULAR_COST.translate() + ": " + modularCost);
	}

	public static final String NBT_MODULE_EXTRA_DATA = "ExtraData";
	public final void addInformation(List<String> list, NBTTagCompound compound) {
		addSpecificInformation(list);
		if (compound != null && compound.hasKey(NBT_MODULE_EXTRA_DATA)) {
			NBTTagCompound extraData = compound.getCompoundTag(NBT_MODULE_EXTRA_DATA);
			if (extraData != null) {
				String extraDataInfo = getModuleInfoText(extraData);
				if (extraDataInfo != null) {
					list.add(ColorHelper.WHITE + extraDataInfo);
				}
			}
		}


		if (GuiScreen.isShiftKeyDown()) {

			if (sides == null || sides.size() == 0) {
				list.add(ColorHelper.CYAN + LocalizationLabel.NO_SIDES.translate());
			}else{
				String sidesText = "";
				for (int i = 0; i < sides.size(); i++) {
					ModuleSide side = sides.get(i);

					if(i == 0) {
						sidesText += side.toString();
					}else if (i == sides.size() - 1) {
						sidesText += " " + LocalizationLabel.AND.translate() + " " + side.toString();
					}else{
						sidesText += ", " + side.toString();
					}
				}


				list.add(ColorHelper.CYAN + LocalizationLabel.SIDES.translate(sidesText, String.valueOf(sides.size())));
			}

			if (getNemesis() != null && getNemesis().size() != 0) {
				if (sides == null || sides.size() == 0) {
					list.add(ColorHelper.RED + LocalizationLabel.MODULE_CONFLICT_HOWEVER.translate() + ":");
				}else{
					list.add(ColorHelper.RED + LocalizationLabel.MODULE_CONFLICT_ALSO.translate() + ":");
				}
				for (ModuleData module : getNemesis()) {
					list.add(ColorHelper.RED + module.getName());
				}
			}

			if (parent != null) {
				list.add(ColorHelper.YELLOW + LocalizationLabel.REQUIRES.translate() + " " + parent.getName());
			}

			if (getRequirement() != null && getRequirement().size() != 0) {
				for (ModuleDataGroup group : getRequirement()) {
					list.add(ColorHelper.YELLOW + LocalizationLabel.REQUIRES.translate() + " " + group.getCountName() + " " + group.getName());
				}
			}

			if (getAllowDuplicate()) {
				list.add(ColorHelper.LIME + LocalizationLabel.DUPLICATES.translate());
			}

			if (validVehicles == null || validVehicles.isEmpty()) {
				list.add(ColorHelper.RED + LocalizationLabel.MISSING_VEHICLE_ERROR.translate());
			}else{
				String vehicleText = "";
				for (int i = 0; i < validVehicles.size(); i++) {
					VehicleType vehicle = validVehicles.get(i);

					if(i == 0) {
						vehicleText += vehicle.getName();
					}else if (i == validVehicles.size() - 1) {
						vehicleText += " " + LocalizationLabel.AND.translate() + " " + vehicle.getName();
					}else{
						vehicleText += ", " + vehicle.getName();
					}
				}


				list.add(ColorHelper.MAGENTA + LocalizationLabel.VEHICLE_TYPES.translate(vehicleText, String.valueOf(validVehicles.size())));
			}
		}

		list.add(ColorHelper.LIGHT_BLUE + LocalizationLabel.TYPE.translate() + ": " + moduleType.getName());
		addExtraMessage(list);
	}


	private static final int MAX_MESSAGE_ROW_LENGTH = 30;
	public void addExtraMessage(List<String> list) {
		if (message != null) {
			list.add("");
			for (ILocalizedText m : message) {
				String str = m.translate();
				if (str.length() <= MAX_MESSAGE_ROW_LENGTH) {
					addExtraMessage(list, str);
				}else{
					String[] words = str.split(" ");
					String row = "";
					for (String word : words) {
						String next = (row + " " + word).trim();
						if (next.length() <= MAX_MESSAGE_ROW_LENGTH) {
							row = next;
						}else{
							addExtraMessage(list, row);
							row = word;
						}
					}
					addExtraMessage(list, row);
				}
			}
		}
	}

	private void addExtraMessage(List<String> list, String str) {
		list.add(ColorHelper.GRAY + "\u00a7o" + str + "\u00a7r");
	}

	public void loadRecipes() {
		if (!isLocked) {
			isValid = true;
			if(recipes != null) {
				hasRecipe = true;
				for (IRecipe recipe : recipes) {
					GameRegistry.addRecipe(recipe);
				}
			}
		}
	}

	public ModuleData addRecipe(IRecipe recipe) {
		if(this.recipes == null) {
			this.recipes = new ArrayList<>();
		}

		this.recipes.add(recipe);

		return this;
	}

	public ModuleData addShapedRecipeWithSize(int width, int height, Object ... recipe) {
		addRecipe(new ModuleRecipeShaped(this, width, height, recipe));

		return this;
	}

	public ModuleData addShapedRecipe(Object ... recipe) {
		if (recipe.length == 9) {
			addShapedRecipeWithSize(3, 3, recipe);
		}else if(recipe.length == 4) {
			addShapedRecipeWithSize(2, 2, recipe);
		}

		return this;
	}

	public ModuleData addShapelessRecipe(Object ... recipe) {
		addRecipe(new ModuleRecipeShapeless(this, recipe));

		return this;
	}

	public ModuleData addVehicles(VehicleType ... types) {
		if (validVehicles == null) {
			validVehicles = new ArrayList<>();
		}

		for (VehicleType type : types) {
			if (validVehicles.size() > 0 && moduleType == ModuleType.HULL) {
				System.err.println("You can't add more than one vehicle type to a hull module. Failed to add type " + type.getUnlocalizedName() + " to " + getRawUnlocalizedName());
				break;
			}
			validVehicles.add(type);
		}

		return this;
	}

	public ArrayList<VehicleType> getValidVehicles() {
		return validVehicles;
	}

	@SideOnly(Side.CLIENT)
	protected void loadModels() {}

	@SideOnly(Side.CLIENT)
	public void loadClientValues() {
		modelMultiplier = 0.75F;
		loadModels();

		//TODO do this in a nicer way
		if (sides != null && sides.contains(ModuleSide.TOP)) {
			removeModel("Rails");
		}
	}


	/*@SideOnly(Side.CLIENT)
	public void createIcon(IIconRegister register) {
		icon = register.registerIcon(StevesVehicles.instance.textureHeader + ":modules/" + getFullRawUnlocalizedName().replace(".", "/").replace(":", "/"));
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return icon;
	}*/

	public ItemStack getItemStack(int count) {
		int id = ModuleRegistry.getIdFromModule(this);
		if (id >= 0) {
			ItemStack item = new ItemStack(ModItems.modules, count, id);
			if (hasExtraData()) {
				NBTTagCompound compound = new NBTTagCompound();
				NBTTagCompound moduleCompound = new NBTTagCompound();
				addDefaultExtraData(moduleCompound);
				compound.setTag(NBT_MODULE_EXTRA_DATA, moduleCompound);
				item.setTagCompound(compound);
			}
			return item;
		}else{
			return null;
		}
	}

	@Override
	public ItemStack getItemStack() {
		return getItemStack(1);
	}



}
