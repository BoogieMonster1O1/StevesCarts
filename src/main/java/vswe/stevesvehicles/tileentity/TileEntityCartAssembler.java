package vswe.stevesvehicles.tileentity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.block.BlockCartAssembler;
import vswe.stevesvehicles.block.ModBlocks;
import vswe.stevesvehicles.client.gui.assembler.ModuleSortMode;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoInteger;
import vswe.stevesvehicles.client.gui.assembler.TitleBox;
import vswe.stevesvehicles.client.gui.screen.GuiBase;
import vswe.stevesvehicles.client.gui.screen.GuiCartAssembler;
import vswe.stevesvehicles.container.ContainerBase;
import vswe.stevesvehicles.container.ContainerCartAssembler;
import vswe.stevesvehicles.container.ContainerUpgrade;
import vswe.stevesvehicles.container.slots.SlotAssembler;
import vswe.stevesvehicles.container.slots.SlotAssemblerFuel;
import vswe.stevesvehicles.container.slots.SlotHull;
import vswe.stevesvehicles.container.slots.SlotOutput;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.localization.entry.block.LocalizationAssembler;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataHull;
import vswe.stevesvehicles.module.data.ModuleDataItemHandler;
import vswe.stevesvehicles.module.data.ModuleType;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.nbt.NBTHelper;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.tileentity.manager.ManagerTransfer;
import vswe.stevesvehicles.transfer.TransferHandler;
import vswe.stevesvehicles.upgrade.effect.BaseEffect;
import vswe.stevesvehicles.upgrade.effect.assembly.Disassemble;
import vswe.stevesvehicles.upgrade.effect.assembly.FreeModules;
import vswe.stevesvehicles.upgrade.effect.assembly.WorkEfficiency;
import vswe.stevesvehicles.upgrade.effect.external.Deployer;
import vswe.stevesvehicles.upgrade.effect.external.Manager;
import vswe.stevesvehicles.upgrade.effect.fuel.CombustionFuel;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCapacity;
import vswe.stevesvehicles.upgrade.effect.fuel.FuelCost;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlat;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlatCart;
import vswe.stevesvehicles.upgrade.effect.time.TimeFlatRemoved;
import vswe.stevesvehicles.vehicle.VehicleBase;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;
import vswe.stevesvehicles.vehicle.entity.IVehicleEntity;

/**
 * The tile entity used by the Cart Assembler
 * 
 * @author Vswe
 *
 */
public class TileEntityCartAssembler extends TileEntityInventory implements ISidedInventory {
	/**
	 * ASSEMBLING VARIABLES
	 */
	/**
	 * When this time is reached the cart is finished and will be placed in the
	 * output slot
	 */
	private int maxAssemblingTime;
	/**
	 * The time the cart has been assembled
	 */
	private float currentAssemblingTime = -1;
	/**
	 * The item that will appear in the output slot when the assembling is done,
	 * i.e. the cart being assembled
	 */
	protected ItemStack outputItem;
	/**
	 * Modules that are being removed by the assembler, used when modifying cart
	 */
	protected List<ItemStack> spareModules;
	/**
	 * Defines if the Cart Assembler is busy
	 */
	private boolean isAssembling;
	/**
	 * GRAPHICAL VARIABLES
	 */
	/**
	 * Whether the cached error list needs to be recalculated or not
	 */
	public boolean isErrorListOutdated;
	public boolean isFreeModulesOutdated;
	/**
	 * The graphical boxes drawn as module headers
	 */
	private List<TitleBox> titleBoxes;
	/**
	 * The graphical menus drawn in the dropdown menu at the simulated cart
	 */
	private List<SimulationInfo> dropDownItems;
	/**
	 * If the simulated cart should spin or not, this is false if the player is
	 * holding the cart with the mouse.
	 */
	private boolean shouldSpin = true;
	/**
	 * The simulated cart, this cart will only exist on the client side
	 */
	private VehicleBase placeholder;
	/**
	 * The current yaw (rotation) of the simulated cart
	 */
	private float yaw = 0F;
	/**
	 * The current roll (rotation) of the simulated cart
	 */
	private float roll = 0F;
	/**
	 * Whether the simulated cart is current rolling up or down.
	 */
	private boolean rollDown = false;
	/**
	 * Which tab is selected in the interface (this is here to prevent reset on
	 * reopening the interface)
	 */
	public int selectedTab;
	public ModuleSortMode sortMode = ModuleSortMode.NORMAL;
	/**
	 * SLOT VARIABLES
	 */
	/**
	 * All the slots this tile entity is using
	 */
	private List<SlotAssembler> slots;
	/**
	 * All the engine slots this tile entity is using
	 */
	private List<SlotAssembler> engineSlots;
	/**
	 * All the addon slots this tile entity is using
	 */
	private List<SlotAssembler> addonSlots;
	/**
	 * All storage slots this tile entity is using
	 */
	private List<SlotAssembler> chestSlots;
	/**
	 * All the attachment slots this tile entity is using
	 */
	private List<SlotAssembler> attachmentSlots;
	/**
	 * The hull slot of this tile entity, this is where the user puts the hull
	 * to start designing the cart.
	 */
	private SlotHull hullSlot;
	/**
	 * The tool slot of this tile entity
	 */
	private SlotAssembler toolSlot;
	/**
	 * The slot where the finished cart will be placed.
	 */
	private SlotOutput outputSlot;
	/**
	 * The slot where the user puts any fuel to power the cart assembler
	 */
	private SlotAssemblerFuel fuelSlot;
	/**
	 * All the slot indices that should be accessed from the top or the bottom
	 * of the Cart Assembler block
	 */
	private final int[] topBotSlots;
	/**
	 * All the slot indices that should be accessed from the sides of the Cart
	 * Assembler block
	 */
	private final int[] sideSlots;
	/**
	 * OTHER VARIABLES
	 */
	/**
	 * The hull that was the active one the last time the hull was checked. This
	 * is used to handle the change or removal of hulls.
	 */
	private ItemStack lastHull;
	/**
	 * The current loaded level of fuel. This is the level of the turquoise bar,
	 * not how much coal is in the slot.
	 */
	private float fuelLevel;
	/**
	 * A list of all the upgrades currently attached to this Cart Assembler
	 */
	private List<TileEntityUpgrade> upgrades;
	/**
	 * Used to properly detach any upgrades when the Cart Assembler block is
	 * broken
	 */
	public boolean isDead;

	@SideOnly(Side.CLIENT)
	@Override
	public GuiBase getGui(InventoryPlayer inv) {
		return new GuiCartAssembler(inv, this);
	}

	@Override
	public ContainerBase getContainer(InventoryPlayer inv) {
		return new ContainerCartAssembler(inv, this);
	}

	public static final int MAX_ENGINE_SLOTS = 5;
	public static final int MAX_TOOL_SLOTS = 1;
	public static final int MAX_ATTACHMENT_SLOTS = 6;
	public static final int MAX_STORAGE_SLOTS = 6;
	public static final int MAX_ADDON_SLOTS = 12;
	public static final TitleBox ENGINE_BOX = new TitleBox(LocalizationAssembler.TITLE_ENGINES, 65, 0xF7941D);
	public static final TitleBox TOOL_BOX = new TitleBox(LocalizationAssembler.TITLE_TOOL, 100, 0x662D91);
	public static final TitleBox ATTACH_BOX = new TitleBox(LocalizationAssembler.TITLE_ATTACHMENTS, 135, 0x005B7F);
	public static final TitleBox STORAGE_BOX = new TitleBox(LocalizationAssembler.TITLE_STORAGE, 170, 0x9E0B0E);
	public static final TitleBox ADDON_BOX = new TitleBox(LocalizationAssembler.TITLE_ADDONS, 205, 0x005826);
	public static final TitleBox INFO_BOX = new TitleBox(LocalizationAssembler.TITLE_INFORMATION, 375, 30, 0xCCBE00);

	public TileEntityCartAssembler() {
		super(0);
		// create all the lists for everything
		upgrades = new ArrayList<>();
		spareModules = new ArrayList<>();
		dropDownItems = new ArrayList<>();
		slots = new ArrayList<>();
		engineSlots = new ArrayList<>();
		addonSlots = new ArrayList<>();
		chestSlots = new ArrayList<>();
		attachmentSlots = new ArrayList<>();
		titleBoxes = new ArrayList<>();
		int slotID = 0;
		// create the hull slot
		hullSlot = new SlotHull(this, slotID++, 18, 25);
		slots.add(hullSlot);
		// create the title boxes at certain positions with certain colors
		titleBoxes.add(ENGINE_BOX);
		titleBoxes.add(TOOL_BOX);
		titleBoxes.add(ATTACH_BOX);
		titleBoxes.add(STORAGE_BOX);
		titleBoxes.add(ADDON_BOX);
		titleBoxes.add(INFO_BOX);
		/// create the engine slots
		for (int i = 0; i < MAX_ENGINE_SLOTS; i++) {
			SlotAssembler slot = new SlotAssembler(this, slotID++, ENGINE_BOX.getX() + 2 + 18 * i, ENGINE_BOX.getY(), ModuleType.ENGINE, false, i);
			slot.invalidate();
			slots.add(slot);
			engineSlots.add(slot);
		}
		// create the tool slot
		for (int i = 0; i < MAX_TOOL_SLOTS; i++) {
			toolSlot = new SlotAssembler(this, slotID++, TOOL_BOX.getX() + 2, TOOL_BOX.getY(), ModuleType.TOOL, false, i);
			slots.add(toolSlot);
			toolSlot.invalidate();
		}
		// create the attachment slots
		for (int i = 0; i < MAX_ATTACHMENT_SLOTS; i++) {
			SlotAssembler slot = new SlotAssembler(this, slotID++, ATTACH_BOX.getX() + 2 + 18 * i, ATTACH_BOX.getY(), ModuleType.ATTACHMENT, false, i);
			slot.invalidate();
			slots.add(slot);
			attachmentSlots.add(slot);
		}
		// create the storage slots
		for (int i = 0; i < MAX_STORAGE_SLOTS; i++) {
			SlotAssembler slot = new SlotAssembler(this, slotID++, STORAGE_BOX.getX() + 2 + 18 * i, STORAGE_BOX.getY(), ModuleType.STORAGE, false, i);
			slot.invalidate();
			slots.add(slot);
			chestSlots.add(slot);
		}
		// create the addon slots
		for (int i = 0; i < MAX_ADDON_SLOTS; i++) {
			SlotAssembler slot = new SlotAssembler(this, slotID++, ADDON_BOX.getX() + 2 + 18 * (i % 6), ADDON_BOX.getY() + 18 * (i / 6), ModuleType.ADDON, false, i);
			slot.invalidate();
			slots.add(slot);
			addonSlots.add(slot);
		}
		// create the fuel and output slots
		fuelSlot = new SlotAssemblerFuel(this, slotID++, 395, 220);
		slots.add(fuelSlot);
		outputSlot = new SlotOutput(this, slotID, 450, 220);
		slots.add(outputSlot);
		// create the place to store all the items for the slots
		inventoryStacks = NonNullList.func_191197_a(slots.size(), ItemStack.field_190927_a);
		// create the arrays used by ISidedInventory
		topBotSlots = new int[] { getSizeInventory() - nonModularSlots() };
		sideSlots = new int[] { getSizeInventory() - nonModularSlots() + 1 };
		updateRenderMenu();
	}

	/**
	 * Clears the list of upgrades, used before refilling it again to keep it up
	 * to date
	 */
	public void clearUpgrades() {
		upgrades.clear();
	}

	/**
	 * Add an upgrade to the list of upgrades for this Cart Assembler
	 * 
	 * @param upgrade
	 *            The upgrade to be added
	 */
	public void addUpgrade(TileEntityUpgrade upgrade) {
		upgrades.add(upgrade);
	}

	/**
	 * Remove an upgrade from the list of upgrades for this Cart Assembler
	 * 
	 * @param upgrade
	 *            The upgrade to be removed
	 */
	public void removeUpgrade(TileEntityUpgrade upgrade) {
		upgrades.remove(upgrade);
	}

	/**
	 * Get a list of all the upgrades. This is not the upgrades themselves, but
	 * rather the tile entities holding the upgrades
	 * 
	 * @return A list of the tile entities
	 */
	public List<TileEntityUpgrade> getUpgradeTiles() {
		return upgrades;
	}

	/**
	 * Get a list of all the upgrade effects of this Cart Assembler. This is
	 * every effect on the upgrade on every tile entity upgrade.
	 * 
	 * @return A list of all the effects.
	 */
	public ArrayList<BaseEffect> getEffects() {
		ArrayList<BaseEffect> lst = new ArrayList<>();
		// go through all the upgrades attached to the cart assembler
		for (TileEntityUpgrade tile : upgrades) {
			List<BaseEffect> effects = tile.getEffects();
			if (effects != null) {
				lst.addAll(effects);
			}
		}
		return lst;
	}

	/**
	 * Get the menu items in the drop down menu used by the player to change the
	 * simulation of the cart being designed
	 * 
	 * @return The drop down menus
	 */
	public List<SimulationInfo> getDropDown() {
		return dropDownItems;
	}

	/**
	 * Get the title boxes used for rendering titles for the different module
	 * groups
	 * 
	 * @return The list of title boxes
	 */
	public List<TitleBox> getTitleBoxes() {
		return titleBoxes;
	}

	/**
	 * Get the ItemStack size that is representing that a module is marked for
	 * removal
	 * 
	 * @return The size value
	 */
	public static int getRemovedSize() {
		return -1;
	}

	/**
	 * Get the ItemStack size that is representing that a module is marked to be
	 * kept
	 * 
	 * @return The size value
	 */
	public static int getKeepSize() {
		return 0;
	}

	/**
	 * Get all the slots used by this Cart Assembler
	 * 
	 * @return All the slots this tile entity is using
	 */
	public List<SlotAssembler> getSlots() {
		return slots;
	}

	/**
	 * Get all the engine slots used by this Cart Assembler
	 * 
	 * @return All the engine slots this tile entity is using
	 */
	public List<SlotAssembler> getEngines() {
		return engineSlots;
	}

	/**
	 * Get all the storage slots used by this Cart Assembler
	 * 
	 * @return All the storage slots this tile entity is using
	 */
	public List<SlotAssembler> getChests() {
		return chestSlots;
	}

	/**
	 * Get all the addon slots used by this Cart Assembler
	 * 
	 * @return All the addon slots this tile entity is using
	 */
	public List<SlotAssembler> getAddons() {
		return addonSlots;
	}

	/**
	 * Get all the attachment slots used by this Cart Assembler
	 * 
	 * @return All the attachment slots this tile entity is using
	 */
	public List<SlotAssembler> getAttachments() {
		return attachmentSlots;
	}

	/**
	 * Get the tool slot that is used by this Cart Assembler
	 * 
	 * @return The tool slot of this tile entity
	 */
	public SlotAssembler getToolSlot() {
		return toolSlot;
	}

	/**
	 * Get the full time it takes to complete assembling the cart. Doesn't take
	 * into account the time it has been worked on or how fast it is working.
	 * 
	 * @return The total time in ticks
	 */
	public int getMaxAssemblingTime() {
		return maxAssemblingTime;
	}

	/**
	 * Get the time the assembler has been working on the cart. This time is
	 * increased faster if the assembler is working faster.
	 * 
	 * @return The current time in ticks
	 */
	public int getAssemblingTime() {
		return (int) currentAssemblingTime;
	}

	/**
	 * Set the amount of ticks the assembler has been working on the cart.
	 * 
	 * @param val
	 *            The amount of ticks
	 */
	private void setAssemblingTime(int val) {
		currentAssemblingTime = val;
	}

	/**
	 * Get if the Cart Assembler is busy working or not
	 * 
	 * @return If it's busy
	 */
	public boolean getIsAssembling() {
		return isAssembling;
	}

	/**
	 * Starts the assembling process of the cart in the designer if possible
	 */
	public void doAssemble() {
		// a cart is only allowed to be created if there's no errors
		if (!hasErrors()) {
			// calculate the time it will take to create the cart and save that
			maxAssemblingTime = generateAssemblingTime();
			// create the cart, it will save the created cart, any spare modules
			// as well as clearing the design area
			createCartFromModules();
			// mark that the assembler is busy
			isAssembling = true;
			// remove the cart being edited, if any
			for (BaseEffect effect : getEffects()) {
				if (effect instanceof Disassemble) {
					Disassemble disassemble = (Disassemble) effect;
					disassemble.onVehicleCreation(outputItem);
				}
			}
		}
	}

	@Override
	public void receivePacket(DataReader dr, EntityPlayer player) {
		int id = dr.readByte();
		if (id == 0) {
			// if a player clicked the assemble button, try to assemble the cart
			doAssemble();
		} else if (id == 1) {
			// if a slot was clicked with a module of an already existing cart,
			// mark it for removal or to keep it depending on what it already
			// is. This is also used to remove modules in free mode.
			int slotId = dr.readByte();
			if (slotId >= 0 && slotId < getSlots().size()) {
				SlotAssembler slot = getSlots().get(slotId);
				if (slot.getStack() != null) {
					if (slot.getStack().func_190916_E() > 0) {
						boolean canRemove = freeMode;
						if (canRemove && slotId == 0) {
							for (int i = 1; i < getSlots().size() - nonModularSlots(); i++) {
								if (getSlots().get(i).getHasStack()) {
									canRemove = false;
									break;
								}
							}
						}
						if (canRemove) {
							slot.putStack(null);
						}
					} else if (slotId != 0) {
						if (slot.getStack().func_190916_E() == getKeepSize()) {
							slot.getStack().func_190920_e(getRemovedSize());
						} else {
							slot.getStack().func_190920_e(getKeepSize());
						}
					}
				}
			}
		} else if (id == 2) {
			int val = dr.readShort();
			ModuleData moduleData = ModuleRegistry.getModuleFromId(val);
			if (moduleData != null) {
				if (moduleData instanceof ModuleDataHull && getHullModule() != null) {
					ItemStack item = moduleData.getItemStack();
					hullSlot.putStack(item);
					invalidateAll();
					validateAll();
					for (SlotAssembler slotAssembler : getSlots()) {
						if (!slotAssembler.isValid()) {
							slotAssembler.putStack(null);
						}
					}
					lastHull = item;
				} else {
					ItemStack item = moduleData.getItemStack();
					for (SlotAssembler slot : slots) {
						if (!slot.getHasStack() && slot.isItemValid(item)) {
							slot.putStack(item);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Called when a upgrade is removed or added to this Cart Assembler
	 */
	public void onUpgradeUpdate() {
		// will also get modules that is just kept when modifying a cart, this
		// is a huge problem (and the reason why this code isn't used)
		/*
		 * ArrayList<ModuleData> modules =
		 * ModuleData.getModulesFromItems(ModuleData.getModularItems(outputItem)
		 * ); ArrayList<ModuleData> removed = new ArrayList<ModuleData>(); for
		 * (ItemStack item : spareModules) { ModuleData module =
		 * StevesCarts.instance.modules.getModuleData(item); if (module != null)
		 * { removed.add(module); } } maxAssemblingTime =
		 * generateAssemblingTime(modules, removed);
		 */
		freeMode = false;
		for (BaseEffect baseEffect : getEffects()) {
			if (baseEffect instanceof FreeModules) {
				freeMode = true;
				break;
			}
		}
	}

	/**
	 * Generate the time it takes to assemble the cart in the designer, note
	 * that this is not the full time of the cart currently being assembled.
	 * That can however be retrieved with getMaxAssemblingTime()
	 * 
	 * @return The time it takes to make the cart
	 */
	public int generateAssemblingTime() {
		return generateAssemblingTime(getModules(true, new int[] { getKeepSize(), getRemovedSize() }), getModules(true, new int[] { getKeepSize(), 1 }));
	}

	/**
	 * Generate the time it takes to assemble a bunch of modules
	 * 
	 * @param modules
	 *            A list of modules to assemble
	 * @param removed
	 *            A list of module to disassemble
	 * @return The time it takes
	 */
	private int generateAssemblingTime(ArrayList<ModuleData> modules, ArrayList<ModuleData> removed) {
		int timeRequired = FLAT_VEHICLE_BASE_TIME;
		for (ModuleData module : modules) {
			timeRequired += getAssemblingTime(module, false);
		}
		for (ModuleData module : removed) {
			timeRequired += getAssemblingTime(module, true);
		}
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof TimeFlatCart) {
				timeRequired += ((TimeFlatCart) effect).getTicks();
			}
		}
		return Math.max(0, timeRequired);
	}

	/**
	 * Get assembling or disassembling time for a specific module
	 * 
	 * @param module
	 *            The module to assemble
	 * @param isRemoved
	 *            If the module is being added(assembled) or
	 *            removed(disassembled)
	 * @return The time it takes
	 */
	private int getAssemblingTime(ModuleData module, boolean isRemoved) {
		int time = getAssemblingTime(module);
		if (isRemoved) {
			time /= 4;
		}
		time += getTimeDecreased(isRemoved);
		return Math.max(0, time);
	}

	public static int FLAT_VEHICLE_BASE_TIME = 100;

	public static int getAssemblingTime(ModuleData data) {
		return (int) (4 * Math.pow(data.getCost(), 2));
	}

	/**
	 * Get the cart that is the result of the modules in the design view. All
	 * the modules will however be left where they are.
	 * 
	 * @param isSimulated
	 *            If this is just a simulation
	 * @return An assembled cart
	 */
	public ItemStack getCartFromModules(boolean isSimulated) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); i++) {
			ItemStack item = getStackInSlot(i);
			if (item != null) {
				if (item.func_190916_E() != getRemovedSize()) {
					items.add(item);
				} else if (!isSimulated) {
					ItemStack spare = item.copy();
					spare.func_190920_e(1);
					spareModules.add(spare);
				}
			}
		}
		return ModuleDataItemHandler.createModularVehicle(items);
	}

	/**
	 * Create a cart and store it to be put in the output slot when the Cart
	 * Assembler is done. Will also clear all the modules from the design view.
	 */
	private void createCartFromModules() {
		spareModules.clear();
		// create the cart
		outputItem = getCartFromModules(false);
		if (outputItem != null) {
			// if a cart was properly made, remove all the modules
			for (int i = 0; i < getSizeInventory() - nonModularSlots(); i++) {
				setInventorySlotContents(i, null);
			}
		} else {
			// if something went wrong, clear the spare modules, no free modules
			// here.
			spareModules.clear();
		}
	}

	public ArrayList<ModuleData> getNonHullModules() {
		return getModules(false);
	}

	public ArrayList<ModuleData> getModules(boolean includeHull) {
		return getModules(includeHull, new int[] { getRemovedSize() });
	}

	public ArrayList<ModuleData> getModules(boolean includeHull, int[] invalid) {
		ArrayList<ModuleData> modules = new ArrayList<>();
		for (int i = includeHull ? 0 : 1; i < getSizeInventory() - nonModularSlots(); i++) {
			ItemStack item = getStackInSlot(i);
			if (item != null) {
				boolean validSize = true;
				for (int invalidItem : invalid) {
					if (invalidItem == item.func_190916_E() || (invalidItem > 0 && item.func_190916_E() > 0)) {
						validSize = false;
						break;
					}
				}
				if (validSize) {
					ModuleData module = ModItems.modules.getModuleData(item);
					if (module != null) {
						modules.add(module);
					}
				}
			}
		}
		return modules;
	}

	public ModuleDataHull getHullModule() {
		if (getStackInSlot(0) != null) {
			ModuleData hullData = ModItems.modules.getModuleData(getStackInSlot(0));
			if (hullData instanceof ModuleDataHull) {
				return (ModuleDataHull) hullData;
			}
		}
		return null;
	}

	private boolean hasErrors() {
		return getErrors().size() > 0;
	}

	public ArrayList<String> getErrors() {
		ArrayList<String> errors = new ArrayList<>();
		if (hullSlot.getStack() == null) {
			errors.add(LocalizationAssembler.NO_HULL.translate());
		} else {
			ModuleData hullData = ModItems.modules.getModuleData(getStackInSlot(0));
			if (hullData == null || !(hullData instanceof ModuleDataHull)) {
				errors.add(LocalizationAssembler.INVALID_HULL_SHORT.translate());
			} else {
				if (isAssembling) {
					errors.add(LocalizationAssembler.BUSY_ASSEMBLER.translate());
				} else if (outputSlot != null && outputSlot.getStack() != null) {
					errors.add(LocalizationAssembler.OCCUPIED_DEPARTURE_BAY.translate());
				}
				ArrayList<ModuleData> modules = new ArrayList<>();
				for (int i = 0; i < getSizeInventory() - nonModularSlots(); i++) {
					if (getStackInSlot(i) != null) {
						ModuleData data = ModItems.modules.getModuleData(getStackInSlot(i));
						if (data != null && getStackInSlot(i).func_190916_E() != getRemovedSize()) {
							modules.add(data);
						}
					}
				}
				String error = ModuleDataItemHandler.checkForErrors((ModuleDataHull) hullData, modules);
				if (error != null) {
					errors.add(error);
				}
			}
		}
		return errors;
	}

	public int getTotalCost() {
		ArrayList<ModuleData> modules = new ArrayList<>();
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); i++) {
			if (getStackInSlot(i) != null) {
				ModuleData data = ModItems.modules.getModuleData(getStackInSlot(i));
				if (data != null) {
					modules.add(data);
				}
			}
		}
		return ModuleDataItemHandler.getTotalCost(modules);
	}

	@Override
	public void initGuiData(Container con, IContainerListener crafting) {
		updateGuiData(con, crafting, 0, getShortFromInt(true, maxAssemblingTime));
		updateGuiData(con, crafting, 1, getShortFromInt(false, maxAssemblingTime));
		updateGuiData(con, crafting, 2, getShortFromInt(true, getAssemblingTime()));
		updateGuiData(con, crafting, 3, getShortFromInt(false, getAssemblingTime()));
		updateGuiData(con, crafting, 4, (short) (isAssembling ? 1 : 0));
		updateGuiData(con, crafting, 5, getShortFromInt(true, getFuelLevel()));
		updateGuiData(con, crafting, 6, getShortFromInt(false, getFuelLevel()));
	}

	@Override
	public void checkGuiData(Container container, IContainerListener crafting) {
		ContainerCartAssembler con = (ContainerCartAssembler) container;
		if (con.lastMaxAssemblingTime != maxAssemblingTime) {
			updateGuiData(con, crafting, 0, getShortFromInt(true, maxAssemblingTime));
			updateGuiData(con, crafting, 1, getShortFromInt(false, maxAssemblingTime));
			con.lastMaxAssemblingTime = maxAssemblingTime;
		}
		if (con.lastIsAssembling != isAssembling) {
			updateGuiData(con, crafting, 4, (short) (isAssembling ? 1 : 0));
			con.lastIsAssembling = isAssembling;
		}
		if (con.lastFuelLevel != getFuelLevel()) {
			updateGuiData(con, crafting, 5, getShortFromInt(true, getFuelLevel()));
			updateGuiData(con, crafting, 6, getShortFromInt(false, getFuelLevel()));
			con.lastFuelLevel = getFuelLevel();
		}
	}

	@Override
	public void receiveGuiData(int id, short data) {
		if (id == 0) {
			maxAssemblingTime = getIntFromShort(true, maxAssemblingTime, data);
		} else if (id == 1) {
			maxAssemblingTime = getIntFromShort(false, maxAssemblingTime, data);
		} else if (id == 2) {
			setAssemblingTime(getIntFromShort(true, getAssemblingTime(), data));
		} else if (id == 3) {
			setAssemblingTime(getIntFromShort(false, getAssemblingTime(), data));
		} else if (id == 4) {
			isAssembling = data != 0;
			if (!isAssembling) {
				setAssemblingTime(0);
			}
		} else if (id == 5) {
			setFuelLevel(getIntFromShort(true, getFuelLevel(), data));
		} else if (id == 6) {
			setFuelLevel(getIntFromShort(false, getFuelLevel(), data));
		}
	}

	private void invalidateAll() {
		for (int i = 0; i < getEngines().size(); i++) {
			getEngines().get(i).invalidate();
		}
		for (int i = 0; i < getAddons().size(); i++) {
			getAddons().get(i).invalidate();
		}
		for (int i = 0; i < getChests().size(); i++) {
			getChests().get(i).invalidate();
		}
		for (int i = 0; i < getAttachments().size(); i++) {
			getAttachments().get(i).invalidate();
		}
		getToolSlot().invalidate();
	}

	private void validateAll() {
		if (hullSlot == null) {
			return;
		}
		ArrayList<SlotAssembler> slots = getValidSlotFromHullItem(hullSlot.getStack());
		if (slots != null) {
			for (SlotAssembler slot : slots) {
				slot.validate();
			}
		}
	}

	public ArrayList<SlotAssembler> getValidSlotFromHullItem(ItemStack hullItem) {
		if (hullItem != null) {
			ModuleData data = ModItems.modules.getModuleData(hullItem);
			if (data != null && data instanceof ModuleDataHull) {
				ModuleDataHull hull = (ModuleDataHull) data;
				return getValidSlotFromHull(hull);
			}
		}
		return null;
	}

	private ArrayList<SlotAssembler> getValidSlotFromHull(ModuleDataHull hull) {
		ArrayList<SlotAssembler> slots = new ArrayList<>();
		for (int i = 0; i < hull.getEngineMaxCount(); i++) {
			slots.add(getEngines().get(i));
		}
		for (int i = 0; i < hull.getAddonMaxCount(); i++) {
			slots.add(getAddons().get(i));
		}
		for (int i = 0; i < hull.getStorageMaxCount(); i++) {
			slots.add(getChests().get(i));
		}
		for (int i = 0; i < getAttachments().size(); i++) {
			slots.add(getAttachments().get(i));
		}
		slots.add(getToolSlot());
		return slots;
	}

	public int getMaxFuelLevel() {
		int capacity = 4000;
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof FuelCapacity) {
				capacity += ((FuelCapacity) effect).getFuelCapacity();
			}
		}
		if (capacity > 200000) {
			capacity = 200000;
		} else if (capacity < 1) {
			capacity = 1;
		}
		return capacity;
	}

	public boolean isCombustionFuelValid() {
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof CombustionFuel) {
				return true;
			}
		}
		return false;
	}

	public int getFuelLevel() {
		return (int) fuelLevel;
	}

	public void setFuelLevel(int val) {
		fuelLevel = val;
	}

	private int getTimeDecreased(boolean isRemoved) {
		int timeDecrement = 0;
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof TimeFlat && !(effect instanceof TimeFlatRemoved)) {
				timeDecrement += ((TimeFlat) effect).getTicks();
			}
		}
		if (isRemoved) {
			for (BaseEffect effect : getEffects()) {
				if (effect instanceof TimeFlatRemoved) {
					timeDecrement += ((TimeFlat) effect).getTicks();
				}
			}
		}
		return timeDecrement;
	}

	private float getFuelCost() {
		float cost = 1.0F;
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof FuelCost) {
				cost *= ((FuelCost) effect).getCost();
			}
		}
		return cost;
	}

	public float getEfficiency() {
		float efficiency = 1.0F;
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof WorkEfficiency) {
				efficiency += ((WorkEfficiency) effect).getEfficiency();
			}
		}
		return efficiency;
	}

	private void deployCart() {
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof Deployer) {
				TileEntityUpgrade tile = effect.getUpgrade();
				BlockPos pos = tile.getPos();
				pos = pos.add(pos);
				pos = pos.add(-this.pos.getX(), -this.pos.getY(), -this.pos.getZ());
				if (tile.getPos().getY() > this.pos.getY()) {
					pos.up();
				}
				if (BlockRailBase.isRailBlock(world, pos)) {
					try {
						NBTTagCompound info = outputItem.getTagCompound();
						if (info != null) {
							EntityModularCart cart = new EntityModularCart(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, info, outputItem.hasDisplayName() ? outputItem.getDisplayName() : null);
							world.spawnEntityInWorld(cart);
							cart.temppushX = tile.getPos().getX() - pos.getX();
							cart.temppushZ = tile.getPos().getZ() - pos.getZ();
							managerInteract(cart, true);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		outputSlot.putStack(outputItem);
	}

	public void managerInteract(EntityModularCart cart, boolean toCart) {
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof Manager) {
				TileEntityUpgrade tile = effect.getUpgrade();
				BlockPos tilePos = tile.getPos();
				BlockPos pos = tilePos;
				pos = pos.add(pos);
				pos = pos.add(-this.pos.getX(), -this.pos.getY(), -this.pos.getZ());
				if (tile.getPos().getY() > this.pos.getY()) {
					pos.up();
				}
				TileEntity managerTile = world.getTileEntity(pos);
				if (managerTile != null && managerTile instanceof TileEntityManager) {
					ManagerTransfer transfer = new ManagerTransfer();
					transfer.setCart(cart);
					if (tilePos.getY() != this.pos.getY()) {
						transfer.setSide(-1);
					} else if (tilePos.getX() < this.pos.getX()) {
						// red
						transfer.setSide(0);
					} else if (tilePos.getX() > this.pos.getX()) {
						// green
						transfer.setSide(3);
					} else if (tilePos.getZ() < this.pos.getZ()) {
						// blue
						transfer.setSide(1);
					} else if (tilePos.getZ() > this.pos.getZ()) {
						// yellow
						transfer.setSide(2);
					}
					if (toCart) {
						transfer.setFromCartEnabled(false);
					} else {
						transfer.setToCartEnabled(false);
					}
					TileEntityManager manager = ((TileEntityManager) managerTile);
					// noinspection StatementWithEmptyBody
					while (manager.exchangeItems(transfer)) {
						;
					}
				}
			}
		}
	}

	private void deploySpares() {
		for (BaseEffect effect : getEffects()) {
			if (effect instanceof Disassemble) {
				for (ItemStack item : spareModules) {
					TransferHandler.TransferItem(item, effect.getUpgrade(), new ContainerUpgrade(null, effect.getUpgrade()), 1);
					if (item.func_190916_E() > 0) {
						puke(item);
					}
				}
				return;
			}
		}
		for (ItemStack item : spareModules) {
			puke(item);
		}
	}

	public void puke(ItemStack item) {
		EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY() + 0.25, pos.getZ(), item);
		entityitem.motionX = (0.5F - world.rand.nextFloat()) / 10;
		entityitem.motionY = 0.15F;
		entityitem.motionZ = (0.5F - world.rand.nextFloat()) / 10;
		world.spawnEntityInWorld(entityitem);
	}

	private boolean loaded;

	public void updateEntity() {
		if (!loaded) {
			((BlockCartAssembler) ModBlocks.CART_ASSEMBLER.getBlock()).updateMultiBlock(world, pos);
			loaded = true;
		}
		if (!isAssembling && outputSlot != null) {
			ItemStack itemInSlot = outputSlot.getStack();
			if (itemInSlot != null && itemInSlot.getItem() == ModItems.vehicles) {
				NBTTagCompound info = itemInSlot.getTagCompound();
				if (info != null && info.hasKey(VehicleBase.NBT_INTERRUPT_MAX_TIME)) {
					ItemStack newItem = ModuleDataItemHandler.createModularVehicle(ModuleDataItemHandler.getModularItems(itemInSlot));
					spareModules = ModuleDataItemHandler.getSpareItems(itemInSlot);
					maxAssemblingTime = info.getInteger(VehicleBase.NBT_INTERRUPT_MAX_TIME);
					setAssemblingTime(info.getInteger(VehicleBase.NBT_INTERRUPT_TIME));
					if (itemInSlot.hasDisplayName()) {
						newItem.setStackDisplayName(itemInSlot.getDisplayName());
					}
					isAssembling = true;
					outputItem = newItem;
					outputSlot.putStack(null);
				}
			}
		}
		if (getFuelLevel() > getMaxFuelLevel()) {
			setFuelLevel(getMaxFuelLevel());
		}
		if (isAssembling && outputSlot != null) {
			if (getFuelLevel() >= getFuelCost()) {
				currentAssemblingTime += getEfficiency();
				fuelLevel -= getFuelCost();
				if (getFuelLevel() <= 0) {
					setFuelLevel(0);
				}
				if (getAssemblingTime() >= maxAssemblingTime) {
					isAssembling = false;
					setAssemblingTime(0);
					if (!world.isRemote) {
						deployCart();
						outputItem = null;
						deploySpares();
						spareModules.clear();
					}
				}
			}
		}
		if (!world.isRemote && fuelSlot != null && fuelSlot.getStack() != null) {
			int fuel = fuelSlot.getFuelLevel(fuelSlot.getStack());
			if (fuel > 0 && getFuelLevel() + fuel <= getMaxFuelLevel()) {
				setFuelLevel(getFuelLevel() + fuel);
				if (fuelSlot.getStack().getItem().hasContainerItem(fuelSlot.getStack())) {
					fuelSlot.putStack(new ItemStack(fuelSlot.getStack().getItem().getContainerItem()));
				} else {
					fuelSlot.getStack().func_190918_g(1);
				}
				if (fuelSlot.getStack().func_190916_E() <= 0) {
					fuelSlot.putStack(null);
				}
			}
		}
		updateSlots();
		handlePlaceholder();
	}

	public void updateSlots() {
		if (hullSlot != null) {
			if (lastHull != null && hullSlot.getStack() == null) {
				invalidateAll();
			} else if (lastHull == null && hullSlot.getStack() != null) {
				validateAll();
			} else if (lastHull != hullSlot.getStack()) {
				invalidateAll();
				validateAll();
			}
			lastHull = hullSlot.getStack();
		}
		for (SlotAssembler slot : slots) {
			slot.update();
		}
	}

	public void resetPlaceholder() {
		placeholder = null;
	}

	public vswe.stevesvehicles.vehicle.VehicleBase getPlaceholder() {
		return placeholder;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setYaw(float val) {
		yaw = val;
	}

	public void setRoll(float val) {
		roll = val;
	}

	public void setSpinning(boolean val) {
		shouldSpin = val;
	}

	public int nonModularSlots() {
		return 2;
	}

	private void handlePlaceholder() {
		if (world.isRemote) {
			if (placeholder == null) {
				return;
			}
			if (!StevesVehicles.freezeCartSimulation) {
				int minRoll = -5;
				int maxRoll = 25;
				if (shouldSpin) {
					yaw += 2F;
					roll = roll % 360;
					if (!rollDown) {
						if (roll < minRoll - 3) {
							roll += 5;
						} else {
							roll += 0.2F;
						}
						if (roll > maxRoll) {
							rollDown = true;
						}
					} else {
						if (roll > maxRoll + 3) {
							roll -= 5;
						} else {
							roll -= 0.2F;
						}
						if (roll < minRoll) {
							rollDown = false;
						}
					}
				}
			}
			placeholder.onUpdate();
			if (placeholder == null) {
				return;
			}
			placeholder.updateFuel();
		}
	}

	public boolean createPlaceholder() {
		if (placeholder == null) {
			ModuleDataHull hull = getHullModule();
			if (hull != null && hull.getValidVehicles() != null && !hull.getValidVehicles().isEmpty()) {
				try {
					Constructor<? extends IVehicleEntity> constructor = hull.getValidVehicles().get(0).getClazz().getConstructor(World.class);
					Object obj = constructor.newInstance(world);
					placeholder = ((IVehicleEntity) obj).getVehicle();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (placeholder != null) {
					placeholder.setPlaceholder(this);
					updatePlaceholder();
				}
			}
		}
		return placeholder != null;
	}

	public void updatePlaceholder() {
		if (placeholder != null) {
			ModuleDataHull hull = getHullModule();
			if (hull == null || hull.getValidVehicles() == null || hull.getValidVehicles().isEmpty()) {
				resetPlaceholder();
			} else {
				Class<? extends IVehicleEntity> placeHolderClass = placeholder.getVehicleEntity().getClass();
				Class<? extends IVehicleEntity> hullClass = hull.getValidVehicles().get(0).getClazz();
				if (!placeHolderClass.equals(hullClass)) {
					resetPlaceholder();
				}
			}
		}
		if (placeholder != null) {
			placeholder.loadPlaceholderModules(getModularInfoIds());
			updateRenderMenu();
		}
		isErrorListOutdated = true;
	}

	private void updateRenderMenu() {
		dropDownItems = new ArrayList<>();
		if (StevesVehicles.hasGreenScreen) {
			dropDownItems.add(new SimulationInfoInteger(LocalizationAssembler.INFO_BACKGROUND, null, 0, 3, 1));
		}
		if (placeholder != null) {
			for (ModuleBase moduleBase : placeholder.getModules()) {
				moduleBase.addSimulationInfo(dropDownItems);
			}
		}
	}

	private int[] getModularInfoIds() {
		List<Integer> dataList = new ArrayList<>();
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); i++) {
			if (getStackInSlot(i) != null) {
				ModuleData data = ModItems.modules.getModuleData(getStackInSlot(i));
				if (data != null && getStackInSlot(i).func_190916_E() != getRemovedSize()) {
					dataList.add(getStackInSlot(i).getItemDamage());
				}
			}
		}
		int[] ids = new int[dataList.size()];
		for (int i = 0; i < dataList.size(); i++) {
			ids[i] = dataList.get(i);
		}
		return ids;
	}

	public boolean getIsDisassembling() {
		for (int i = 0; i < getSizeInventory() - nonModularSlots(); i++) {
			if (getStackInSlot(i) != null && getStackInSlot(i).func_190916_E() <= 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "container.vehicle_assembler";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		NBTTagList spares = tagCompound.getTagList("Spares", NBTHelper.COMPOUND.getId());
		spareModules.clear();
		for (int i = 0; i < spares.tagCount(); ++i) {
			NBTTagCompound item = spares.getCompoundTagAt(i);
			ItemStack iStack = new ItemStack(item);
			spareModules.add(iStack);
		}
		NBTTagCompound outputTag = (NBTTagCompound) tagCompound.getTag("Output");
		if (outputTag != null) {
			outputItem = new ItemStack(outputTag);
		}
		// Backwards comparability
		if (tagCompound.hasKey("Fuel")) {
			setFuelLevel(tagCompound.getShort("Fuel"));
		} else {
			setFuelLevel(tagCompound.getInteger("IntFuel"));
		}
		maxAssemblingTime = tagCompound.getInteger("maxTime");
		setAssemblingTime(tagCompound.getInteger("currentTime"));
		isAssembling = tagCompound.getBoolean("isAssembling");
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		NBTTagList spares = new NBTTagList();
		for (ItemStack iStack : spareModules) {
			if (iStack != null) {
				NBTTagCompound item = new NBTTagCompound();
				// item.setByte("Slot", (byte)i);
				iStack.writeToNBT(item);
				spares.appendTag(item);
			}
		}
		tagCompound.setTag("Spares", spares);
		if (outputItem != null) {
			NBTTagCompound outputTag = new NBTTagCompound();
			outputItem.writeToNBT(outputTag);
			tagCompound.setTag("Output", outputTag);
		}
		tagCompound.setInteger("IntFuel", getFuelLevel());
		tagCompound.setInteger("maxTime", maxAssemblingTime);
		tagCompound.setInteger("currentTime", getAssemblingTime());
		tagCompound.setBoolean("isAssembling", isAssembling);
		return tagCompound;
	}

	public ItemStack getOutputOnInterrupt() {
		if (outputItem == null) {
			return null;
		} else if (!outputItem.hasTagCompound()) {
			return null;
		} else {
			NBTTagCompound info = outputItem.getTagCompound();
			info.setInteger(VehicleBase.NBT_INTERRUPT_TIME, getAssemblingTime());
			info.setInteger(VehicleBase.NBT_INTERRUPT_MAX_TIME, maxAssemblingTime);
			ModuleDataItemHandler.addSparesToVehicleItems(outputItem, spareModules);
			return outputItem;
		}
	}

	@Override
	public boolean isItemValidForSlot(int slotId, ItemStack item) {
		return slotId >= 0 && slotId < slots.size() && slots.get(slotId).isItemValid(item);
	}

	// slots
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.UP || side == EnumFacing.DOWN ? topBotSlots : sideSlots;
	}

	// in
	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return (side == EnumFacing.UP || side == EnumFacing.DOWN) && this.isItemValidForSlot(slot, item);
	}

	// out
	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return true;
	}

	public void increaseFuel(int val) {
		fuelLevel += val;
		if (fuelLevel > getMaxFuelLevel()) {
			fuelLevel = getMaxFuelLevel();
		}
	}

	private boolean freeMode;

	public boolean isInFreeMode() {
		return freeMode;
	}

	public int getBackground() {
		if (StevesVehicles.hasGreenScreen) {
			return ((SimulationInfoInteger) getDropDown().get(0)).getValue();
		} else {
			return 1;
		}
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}
	
    public boolean func_191420_l()
    {
        for (ItemStack itemstack : spareModules){
            if (!itemstack.func_190926_b())
            {
                return false;
            }
        }
        return true;
    }
}
