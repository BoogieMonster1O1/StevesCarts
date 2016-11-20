package vswe.stevesvehicles.vehicle;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import vswe.stevesvehicles.StevesVehicles;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.container.ContainerVehicle;
import vswe.stevesvehicles.item.ModItems;
import vswe.stevesvehicles.module.IActivatorModule;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.cart.ComparatorWorkModule;
import vswe.stevesvehicles.module.cart.ModuleWorker;
import vswe.stevesvehicles.module.cart.tool.ModuleTool;
import vswe.stevesvehicles.module.common.addon.ModuleCreativeSupplies;
import vswe.stevesvehicles.module.common.engine.ModuleEngine;
import vswe.stevesvehicles.module.common.storage.tank.ModuleTank;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.ModuleDataItemHandler;
import vswe.stevesvehicles.module.data.ModuleDataPair;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.tileentity.TileEntityCartAssembler;
import vswe.stevesvehicles.tileentity.toggler.TogglerOption;
import vswe.stevesvehicles.transfer.TransferHandler;
import vswe.stevesvehicles.vehicle.entity.LockableEntityDataManager;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;
import vswe.stevesvehicles.vehicle.entity.IVehicleEntity;
import vswe.stevesvehicles.vehicle.version.VehicleVersion;

public class VehicleBase {
	private ForgeChunkManager.Ticket cartTicket;
	private ModuleWorker workingComponent;
	public TileEntityCartAssembler placeholderAssembler;
	public boolean isPlaceholder;
	protected int modularSpaceHeight;
	public boolean canScrollModules;
	private ArrayList<ModuleDataPair> moduleCounts;
	private int workingTime;
	private int motorRotation;
	protected boolean engineFlag = false;
	private VehicleType vehicleType;
	public static final int MODULAR_SPACE_WIDTH = 443;
	public static final int MODULAR_SPACE_HEIGHT = 168;
	private static final DataParameter<Boolean> IS_BURNING = EntityDataManager.createKey(EntityModularCart.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_DISANABLED = EntityDataManager.createKey(EntityModularCart.class, DataSerializers.BOOLEAN);
	private static Random rand = new Random();
	/**
	 * All Modules that belong to this cart
	 */
	private ArrayList<ModuleBase> modules;
	/**
	 * All Worker Modules that belong to this cart These modules can stop the
	 * cart while they perform some work during a certain amount of time
	 */
	private ArrayList<ModuleWorker> workModules;
	/**
	 * All Engine Modules that belong to this cart These modules power the cart
	 * and some modules
	 */
	private ArrayList<ModuleEngine> engineModules;
	/**
	 * All Tank Modules that belong to this cart These module can carry fluid
	 * for the cart. The cart itself will always say that it "can" carry fluids
	 * but if no tanks are present it will just fail to drain/fill anything
	 */
	private ArrayList<ModuleTank> tankModules;
	private ModuleCreativeSupplies creativeSupplies;
	private final IVehicleEntity vehicleEntity;
	private final Entity entity;

	public World getWorld() {
		return entity.world;
	}

	public VehicleBase(IVehicleEntity entity) {
		this.vehicleEntity = entity;
		this.entity = (Entity) entity;
		for (VehicleType type : VehicleRegistry.getInstance().getElements()) {
			if (type.getClazz().equals(entity.getClass())) {
				this.vehicleType = type;
				break;
			}
		}
	}

	public VehicleBase(IVehicleEntity entity, NBTTagCompound info, String name) {
		this(entity);
		cartVersion = info.getByte(VehicleVersion.NBT_VERSION_STRING);
		loadModules(info, true);
		this.name = name;
	}

	/**
	 * All Modules that belong to this cart
	 * 
	 * @return All Modules that belong to this cart
	 */
	public ArrayList<ModuleBase> getModules() {
		return modules;
	}

	/**
	 * These modules can stop the cart while they perform some work during a
	 * certain amount of time
	 * 
	 * @return All Worker Modules that belong to this cart
	 */
	public ArrayList<ModuleWorker> getWorkers() {
		return workModules;
	}

	/**
	 * These modules power the cart and some modules
	 * 
	 * @return All Engine Modules that belong to this cart
	 */
	public ArrayList<ModuleEngine> getEngines() {
		return engineModules;
	}

	/**
	 * These module can carry fluid for the cart. The cart itself will always
	 * say that it "can" carry fluids but if no tanks are present it will just
	 * fail to drain/fill anything
	 * 
	 * @return All Tank Modules that belong to this cart
	 */
	public ArrayList<ModuleTank> getTanks() {
		return tankModules;
	}

	/**
	 * The name the cart has if renamed /by an anvil)
	 */
	private String name;
	/**
	 * The version this cart has, for more info about cersion see
	 * {@link vswe.stevesvehicles.vehicle.version.VehicleVersion}
	 */
	public byte cartVersion;

	public ArrayList<ModuleDataPair> getModuleCounts() {
		return moduleCounts;
	}

	/**
	 * Load a placeholder's modules, this is a bit special since it can be done
	 * on existing cart. Therefore new modules should be loaded, old modules
	 * that still are there be ignored and old modules that are no longer
	 * present be removed.
	 * 
	 * @param data
	 *            The byte array representing the modules.
	 */
	public void loadPlaceholderModules(int[] data) {
		if (modules == null) {
			modules = new ArrayList<>();
			doLoadModules(data);
		} else {
			// Rule 1 -> IN OLD, NOT NEW -> remove module
			// Rule 2 -> IN NEW, NOT OLD -> add module
			// Rule 3 -> IN OLD, IN NEW -> keep the module, do nothing
			ArrayList<Integer> modulesToAdd = new ArrayList<>();
			ArrayList<Integer> oldModules = new ArrayList<>();
			for (ModuleBase module : modules) {
				oldModules.add(module.getModuleId());
			}
			for (int id : data) {
				boolean found = false;
				for (int j = 0; j < oldModules.size(); j++) {
					if (id == oldModules.get(j)) {
						// Rule 3
						found = true;
						oldModules.remove(j);
						break;
					}
				}
				if (!found) {
					// Rule 2
					modulesToAdd.add(id);
				}
			}
			for (int id : oldModules) {
				for (int i = 0; i < modules.size(); i++) {
					if (id == modules.get(i).getModuleId()) {
						// Rule 1
						modules.remove(i);
						break;
					}
				}
			}
			int[] newModuleData = new int[modulesToAdd.size()];
			for (int i = 0; i < modulesToAdd.size(); i++) {
				newModuleData[i] = modulesToAdd.get(i);
			}
			doLoadModules(newModuleData);
		}
		initModules();
		for (ModuleBase module : modules) {
			module.initSimulationInfo();
		}
	}

	private void loadModules(NBTTagCompound info, boolean isItemCompound) {
		NBTTagList list = info.getTagList(NBT_MODULES, 10);
		if (list == null) {
			return;
		}
		int[] ids = new int[list.tagCount()];
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound moduleCompound = list.getCompoundTagAt(i);
			ids[i] = moduleCompound.getShort(NBT_ID);
		}
		// on the server, make sure the version is correct
		if (!getWorld().isRemote) {
			ids = VehicleVersion.updateCart(this, ids);
		}
		loadModules(ids);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound moduleCompound = list.getCompoundTagAt(i);
			ModuleBase module = modules.get(i);
			if (isItemCompound) {
				int id = module.getModuleId();
				ModuleData data = ModuleRegistry.getModuleFromId(id);
				if (data != null) {
					data.readExtraData(moduleCompound, module);
				}
			} else {
				module.readFromNBT(moduleCompound);
			}
		}
	}

	/**
	 * Create and initiate the cart with the given modules.
	 * 
	 * @param ids
	 *            The byte array representing the modules.
	 */
	protected void loadModules(int[] ids) {
		modules = new ArrayList<>();
		doLoadModules(ids);
		initModules();
	}

	/**
	 * Create the given modules
	 * 
	 * @param ids
	 *            The array representing the modules.
	 */
	private void doLoadModules(int[] ids) {
		for (int id : ids) {
			try {
				Class<? extends ModuleBase> moduleClass = ModuleRegistry.getModuleFromId(id).getModuleClass();
				Constructor moduleConstructor = moduleClass.getConstructor(new Class[] { VehicleBase.class });
				Object moduleObject = moduleConstructor.newInstance(this);
				ModuleBase module = (ModuleBase) moduleObject;
				module.setModuleId(id);
				module.setPositionId(modules.size());
				modules.add(module);
			} catch (Exception e) {
				System.out.println("Failed to load module with ID " + id + "! More info below.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initiate the modules on the cart. This will allocate all required IDs,
	 * place them on the interface and initiate every module.
	 */
	private void initModules() {
		moduleCounts = new ArrayList<>();
		for (ModuleBase module : modules) {
			ModuleData data = ModuleRegistry.getModuleFromId(module.getModuleId());
			boolean found = false;
			if (!data.hasExtraData()) {
				for (ModuleDataPair count : moduleCounts) {
					if (count.isContainingData(data)) {
						count.increase();
						found = true;
						break;
					}
				}
			}
			if (!found) {
				ModuleDataPair count = new ModuleDataPair(data);
				moduleCounts.add(count);
				if (data.hasExtraData()) {
					NBTTagCompound compound = new NBTTagCompound();
					data.addExtraData(compound, module);
					count.setExtraData(compound);
				}
			}
		}
		// pre-initialize the modules
		for (ModuleBase module : modules) {
			module.preInit();
		}
		workModules = new ArrayList<>();
		engineModules = new ArrayList<>();
		tankModules = new ArrayList<>();
		int guiData = 0;
		int dataWatcher = 0;
		// generate all the models this cart should use
		if (getWorld().isRemote) {
			generateModels();
		}
		for (ModuleBase module : modules) {
			if (module instanceof ModuleWorker) {
				workModules.add((ModuleWorker) module);
			} else if (module instanceof ModuleEngine) {
				engineModules.add((ModuleEngine) module);
			} else if (module instanceof ModuleTank) {
				tankModules.add((ModuleTank) module);
			} else if (module instanceof ModuleCreativeSupplies) {
				creativeSupplies = (ModuleCreativeSupplies) module;
			}
		}
		ComparatorWorkModule sorter = new ComparatorWorkModule();
		Collections.sort(workModules, sorter);
		// gives all their modules a place to render their graphics on
		if (!isPlaceholder) {
			ArrayList<GuiAllocationHelper> lines = new ArrayList<>();
			int slots = 0;
			for (ModuleBase module : modules) {
				// only for modules that actually have an interface
				if (module.hasGui()) {
					boolean foundLine = false;
					// check if there's room in an already existing line, if so,
					// place it there
					for (GuiAllocationHelper line : lines) {
						if (line.width + module.guiWidth() <= MODULAR_SPACE_WIDTH) {
							module.setX(line.width);
							line.width += module.guiWidth();
							line.maxHeight = Math.max(line.maxHeight, module.guiHeight());
							line.modules.add(module);
							foundLine = true;
							break;
						}
					}
					// if there wasn't any room for the module, create a new
					// line for it
					if (!foundLine) {
						GuiAllocationHelper line = new GuiAllocationHelper();
						module.setX(0);
						line.width = module.guiWidth();
						line.maxHeight = module.guiHeight();
						line.modules.add(module);
						lines.add(line);
					}
					// initiate the gui data IDs
					module.setGuiDataStart(guiData);
					guiData += module.numberOfGuiData();
					// initiate the slots
					if (module.hasSlots()) {
						slots = module.generateSlots(slots);
					}
				}
				// initiate the data watchers and give the modules the correct
				// IDs
				module.setDataWatcherStart(dataWatcher);
				dataWatcher += module.numberOfDataWatchers();
				if (module.numberOfDataWatchers() > 0) {
					module.initDw();
				}
			}
			// when the interface has been generated, calculate if scrolling is
			// required and how that is done
			int currentY = 0;
			for (GuiAllocationHelper line : lines) {
				for (ModuleBase module : line.modules) {
					module.setY(currentY);
				}
				currentY += line.maxHeight;
			}
			if (currentY > MODULAR_SPACE_HEIGHT) {
				canScrollModules = true;
			}
			modularSpaceHeight = currentY;
		}
		// initialize the modules
		for (ModuleBase module : modules) {
			module.init();
		}
	}

	/**
	 * Gets if a cart has been disabled by an ADR
	 * 
	 * @return If it's disabled
	 */
	public boolean isDisabled() {
		return entity.getDataManager().get(IS_DISANABLED);
	}

	/**
	 * Sets if a cart has been disabled by an ADR
	 * 
	 * @param disabled
	 *            If it's disabled
	 */
	public void setIsDisabled(boolean disabled) {
		if (getWorld().isRemote) {
			return;
		}
		entity.getDataManager().set(IS_DISANABLED, disabled);
	}

	/**
	 * Get the engine's state, if it's on or off. This should not be used to
	 * determine if a module that requires power should run or not.
	 */
	public boolean isEngineBurning() {
		return entity.getDataManager().get(IS_BURNING);
	}

	/**
	 * Set the engine's state, if it's on or off.
	 * 
	 * @param on
	 *            The state of the engine
	 */
	public void setEngineBurning(boolean on) {
		if (getWorld().isRemote) {
			return;
		}
		entity.getDataManager().set(IS_BURNING, on);
	}

	/**
	 * Handles the fuel usage
	 */
	public void updateFuel() {
		// check how much power the cart needs the next tick
		int consumption = getConsumption();
		// if the cart needs power we need to consume it
		if (consumption > 0) {
			// get a engine to drain power from, if any
			ModuleEngine engine = getCurrentEngine();
			if (engine != null) {
				// consume
				engine.consumeFuel(consumption);
				// let the engine emit smoke
				if (!isPlaceholder && getWorld().isRemote && hasFuel() && !isDisabled()) {
					engine.smoke();
				}
			}
		}
		// set the current state of the engine
		setEngineBurning(hasFuel() && !isDisabled());
	}

	/**
	 * Get the engine that should be used for this tick
	 * 
	 * @return The engine, or null if no valid one were found
	 */
	private ModuleEngine getCurrentEngine() {
		if (modules == null) {
			return null;
		}
		// force stop it all?
		for (ModuleBase module : modules) {
			if (module.stopEngines()) {
				return null;
			}
		}
		// get the consumption when the cart is moving.
		int consumption = getConsumption(true);
		// get a list of all the working engines with the highest available
		// priority
		ArrayList<ModuleEngine> priority = new ArrayList<>();
		int mostImportant = -1;
		for (ModuleEngine engine : engineModules) {
			if (engine.hasFuel(consumption) && (mostImportant == -1 || mostImportant >= engine.getPriority())) {
				if (engine.getPriority() < mostImportant) {
					priority.clear();
				}
				mostImportant = engine.getPriority();
				priority.add(engine);
			}
		}
		// if there are valid engines, use one of them. If there's more than
		// one, use different ones on different ticks.
		if (priority.size() > 0) {
			if (motorRotation >= priority.size()) {
				motorRotation = 0;
			}
			motorRotation = (motorRotation + 1) % priority.size();
			return priority.get(motorRotation);
		}
		return null;
	}

	/**
	 * Get the current consumption value
	 * 
	 * @return The consumption for this tick
	 */
	public int getConsumption() {
		return getConsumption(!isDisabled() && isEngineBurning());
	}

	/**
	 * Get the "current" consumption value. The value is calculated depending on
	 * if the cart is assumed to be moving or not
	 * 
	 * @param isMoving
	 *            If the cart is powered to move
	 * @return The consumption for this tick
	 */
	public int getConsumption(boolean isMoving) {
		// one is the base when moving
		int consumption = isMoving ? 1 : 0;
		// loop through all the modules and sum up their consumption
		if (modules != null && !isPlaceholder) {
			for (ModuleBase module : modules) {
				consumption += module.getConsumption(isMoving);
			}
		}
		return consumption;
	}

	/**
	 * Whether a cart should drop its items when killed
	 */
	public boolean dropOnDeath() {
		if (isPlaceholder) {
			return false;
		}
		if (modules != null) {
			for (ModuleBase module : modules) {
				if (!module.dropOnDeath()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Return the color filter that should be applied to this cart
	 * 
	 * @return The color [R: 0-1, G: 0-1, B: 0-1]
	 */
	public float[] getColor() {
		if (modules != null) {
			for (ModuleBase module : getModules()) {
				float[] color = module.getColor();
				if (color[0] != 1F || color[1] != 1F || color[2] != 1F) {
					return color;
				}
			}
		}
		return new float[] { 1F, 1F, 1F };
	}

	/**
	 * Returns a module that want to use the whole interface for itself, this
	 * prevents all other modules to be able to acces the interface.
	 * 
	 * @return The module that wants to steal the interface, or null if no
	 *         module wants to.
	 */
	public ModuleBase getInterfaceThief() {
		if (modules != null) {
			for (ModuleBase module : getModules()) {
				if (module.doStealInterface()) {
					return module;
				}
			}
		}
		return null;
	}

	/**
	 * Updates the cart logic
	 */
	public void onUpdate() {
		if (modules != null) {
			updateFuel();
			for (ModuleBase module : modules) {
				module.update();
			}
			for (ModuleBase module : modules) {
				module.postUpdate();
			}
			work();
		}
	}

	/**
	 * Return if this cart has enough fuel to work
	 * 
	 * @return If it has enough fuel
	 */
	public boolean hasFuel() {
		if (isDisabled()) {
			return false;
		}
		if (modules != null) {
			for (ModuleBase module : modules) {
				if (module.stopEngines()) {
					return false;
				}
			}
		}
		return hasFuelForModule();
	}

	/**
	 * Return if this cart has enough fuel to work, doesn't care if the cart
	 * itself is not allowed to move
	 * 
	 * @return If it has enough fuel
	 */
	public boolean hasFuelForModule() {
		if (isPlaceholder) {
			return true;
		}
		int consumption = getConsumption(true);
		if (modules != null) {
			for (ModuleBase module : modules) {
				if (module.hasFuel(consumption)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Load chunks with the current ticket at the current position
	 */
	public void loadChunks() {
		loadChunks(cartTicket, x() >> 4, z() >> 4);
	}

	/**
	 * Loads chunks with the current ticket at the given position
	 * 
	 * @param chunkX
	 *            The chunk's X coordinate
	 * @param chunkZ
	 *            The chunk's Z coordinate
	 */
	public void loadChunks(int chunkX, int chunkZ) {
		loadChunks(cartTicket, chunkX, chunkZ);
	}

	/**
	 * Load chunks with the given ticket at the current position
	 * 
	 * @param ticket
	 *            The ticket to load with
	 */
	public void loadChunks(ForgeChunkManager.Ticket ticket) {
		loadChunks(ticket, x() >> 4, z() >> 4);
	}

	/**
	 * Load chunks with the given ticket at the given position
	 * 
	 * @param ticket
	 *            The ticket to load with
	 * @param chunkX
	 *            The chunk's X coordinate
	 * @param chunkZ
	 *            The chunk's Z coordinate
	 */
	public void loadChunks(ForgeChunkManager.Ticket ticket, int chunkX, int chunkZ) {
		if (getWorld().isRemote || ticket == null) {
			return;
		} else if (cartTicket == null) {
			cartTicket = ticket;
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				ForgeChunkManager.forceChunk(ticket, new ChunkPos(chunkX + i, chunkZ + j));
			}
		}
	}

	/**
	 * Starts loading chunks
	 */
	public void initChunkLoading() {
		if (getWorld().isRemote || cartTicket != null) {
			return;
		}
		cartTicket = ForgeChunkManager.requestTicket(StevesVehicles.instance, getWorld(), ForgeChunkManager.Type.ENTITY);
		if (cartTicket != null) {
			cartTicket.bindEntity(entity);
			cartTicket.setChunkListDepth(9);
			loadChunks();
		}
	}

	/**
	 * Stops loading chunks
	 */
	public void dropChunkLoading() {
		if (getWorld().isRemote) {
			return;
		}
		if (cartTicket != null) {
			ForgeChunkManager.releaseTicket(cartTicket);
			cartTicket = null;
		}
	}

	/**
	 * Sets the current worker module to be working for the cart
	 * 
	 * @param worker
	 *            The new worker module or null
	 */
	public void setWorker(ModuleWorker worker) {
		if (workingComponent != null && worker != null) {
			workingComponent.stopWorking();
		}
		workingComponent = worker;
		if (worker == null) {
			setWorkingTime(0);
		}
	}

	/**
	 * Gets the current worker module that is working for the cart
	 * 
	 * @return The Worker module or null
	 */
	public ModuleWorker getWorker() {
		return workingComponent;
	}

	/**
	 * Set the time left the current worker has before its done with its current
	 * task
	 * 
	 * @param val
	 *            The time in ticks
	 */
	public void setWorkingTime(int val) {
		workingTime = val;
	}

	/**
	 * Allows the current worker to work or allows the cart to assign a new
	 * worker(depending on priority)
	 */
	private void work() {
		if (isPlaceholder) {
			return;
		}
		// if this cart has fuel it is allowed to work
		if (!getWorld().isRemote && hasFuel()) {
			// if the work cool down is at zero it's time to work
			if (workingTime <= 0) {
				ModuleWorker oldComponent = workingComponent;
				if (workingComponent != null) {
					boolean result = workingComponent.work();
					if (workingComponent != null && oldComponent == workingComponent && workingTime <= 0 && !workingComponent.preventAutoShutdown()) {
						workingComponent.stopWorking();
					}
					if (result) {
						work();
						return;
					}
				}
				if (workModules != null) {
					for (ModuleWorker module : workModules) {
						if (module.work()) {
							return;
						}
					}
				}
			} else {
				// otherwise decrease the cool down
				workingTime--;
			}
		}
	}

	/**
	 * Allows the modules to render overlays on the screen
	 */
	@SideOnly(Side.CLIENT)
	public void renderOverlay(Minecraft minecraft) {
		if (modules != null) {
			for (ModuleBase module : modules) {
				module.renderOverlay(minecraft);
			}
		}
	}

	/**
	 * Handles a activator setting from the Module Toggler
	 * 
	 * @param option
	 *            The option to handle
	 * @param isOrange
	 *            Whether the cart is moving the orange direction or not
	 */
	public void handleActivator(TogglerOption option, boolean isOrange) {
		for (ModuleBase module : modules) {
			if (module instanceof IActivatorModule && option.getModule().isAssignableFrom(module.getClass())) {
				IActivatorModule activator = (IActivatorModule) module;
				if (option.shouldActivate(isOrange)) {
					activator.doActivate(option.getId());
				} else if (option.shouldDeactivate(isOrange)) {
					activator.doDeActivate(option.getId());
				} else if (option.shouldToggle()) {
					if (activator.isActive(option.getId())) {
						activator.doDeActivate(option.getId());
					} else {
						activator.doActivate(option.getId());
					}
				}
			}
		}
	}

	/**
	 * Get the lines to render on top of the cart
	 * 
	 * @return The lines to render
	 */
	public ArrayList<String> getLabel() {
		ArrayList<String> label = new ArrayList<>();
		if (getModules() != null) {
			for (ModuleBase module : getModules()) {
				module.addToLabel(label);
			}
		}
		return label;
	}

	/**
	 * Add an item to the cart's inventory
	 * 
	 * @param iStack
	 *            The item to put in the cart
	 */
	public void addItemToChest(ItemStack iStack) {
		TransferHandler.TransferItem(iStack, vehicleEntity, getCon(null), Slot.class, null, -1);
	}

	/**
	 * Add an item to the cart's inventory
	 * 
	 * @param iStack
	 *            The item to put in the cart
	 * @param start
	 *            The index of the first valid slot
	 * @param end
	 *            The index of the last valid slot
	 */
	public void addItemToChest(ItemStack iStack, int start, int end) {
		TransferHandler.TransferItem(iStack, vehicleEntity, start, end, getCon(null), Slot.class, null, -1);
	}

	/**
	 * Add an item to the cart's inventory
	 * 
	 * @param iStack
	 *            The item to put in the cart
	 * @param validSlot
	 *            The class of the valid slots
	 * @param invalidSlot
	 *            The class of the invalid slots
	 */
	public void addItemToChest(ItemStack iStack, java.lang.Class validSlot, java.lang.Class invalidSlot) {
		TransferHandler.TransferItem(iStack, vehicleEntity, getCon(null), validSlot, invalidSlot, -1);
	}

	/**
	 * Mark this cart as a placeholder cart, a cart that is simulated in the
	 * Cart Assembler's interface
	 * 
	 * @param assembler
	 *            The assembler the cart is simulated in
	 */
	public void setPlaceholder(TileEntityCartAssembler assembler) {
		isPlaceholder = true;
		placeholderAssembler = assembler;
	}

	/**
	 * Generate the models for this cart
	 */
	@SideOnly(Side.CLIENT)
	private void generateModels() {
		if (modules != null) {
			ArrayList<String> invalid = new ArrayList<>();
			// loops through the modules to remove all models that should be
			// prevented to render
			for (ModuleBase module : modules) {
				ModuleData data = module.getModuleData();
				if (data.haveRemovedModels()) {
					for (String remove : data.getRemovedModels()) {
						invalid.add(remove);
					}
				}
			}
			// loop through all the modules backwards so later modules will
			// "override" the early ones
			for (int i = modules.size() - 1; i >= 0; i--) {
				ModuleBase module = modules.get(i);
				ModuleData data = module.getModuleData();
				if (data != null) {
					if (data.haveModels(isPlaceholder)) {
						ArrayList<ModelVehicle> models = new ArrayList<>();
						// add all the models
						for (String str : data.getModels(isPlaceholder).keySet()) {
							if (!invalid.contains(str)) {
								models.add(data.getModels(isPlaceholder).get(str));
								// mark that this model has been added
								// somewhere, don't register it again
								invalid.add(str);
							}
						}
						// if there's any models, register them at the module
						if (models.size() > 0) {
							module.setModels(models);
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Returns the gui of this cart
	 **/
	public GuiScreen getGui(EntityPlayer player) {
		return new GuiVehicle(player.inventory, this);
	}

	/**
	 * Returns the container of this cart
	 **/
	public Container getCon(InventoryPlayer player) {
		return new ContainerVehicle(player, this);
	}

	private int scrollY;

	public void setScrollY(int val) {
		if (canScrollModules) {
			scrollY = val;
		}
	}

	public int getScrollY() {
		if (getInterfaceThief() != null) {
			return 0;
		} else {
			return scrollY;
		}
	}

	public int getRealScrollY() {
		return (int) (((modularSpaceHeight - MODULAR_SPACE_HEIGHT) / 198F) * getScrollY());
	}

	public String getVehicleName() {
		if (name == null || name.length() == 0) {
			return getVehicleType().getName();
		} else {
			return name;
		}
	}

	public String getVehicleRawName() {
		return name;
	}

	public boolean hasCreativeSupplies() {
		return creativeSupplies != null;
	}

	public void preDeath() {
		if (dropOnDeath() && !getWorld().isRemote) {
			entity.entityDropItem(getVehicleItem(), 0.0F); // TODO prevent this
			// from dropping in
			// creative?
			for (int i = 0; i < vehicleEntity.getSizeInventory(); ++i) {
				ItemStack itemstack = vehicleEntity.removeStackFromSlot(i);
				if (itemstack != null) {
					float offsetX = rand.nextFloat() * 0.8F + 0.1F;
					float offsetY = rand.nextFloat() * 0.8F + 0.1F;
					float offsetZ = rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem = new EntityItem(getWorld(), entity.posX + offsetX, entity.posY + offsetY, entity.posZ + offsetZ, itemstack.copy());
					entityitem.motionX = rand.nextGaussian() * 0.05F;
					entityitem.motionY = rand.nextGaussian() * 0.05F + 0.2F;
					entityitem.motionZ = rand.nextGaussian() * 0.05F;
					getWorld().spawnEntityInWorld(entityitem);
				}
			}
		}
	}

	public void postDeath() {
		// tell all the modules that the cart is being removed
		if (modules != null) {
			for (ModuleBase module : modules) {
				module.onDeath();
			}
		}
		// stop loading chunks
		dropChunkLoading();
	}

	public float getMountedYOffset() {
		if (modules != null && entity.getRidingEntity() != null) {
			for (ModuleBase module : modules) {
				float offset = module.mountedOffset(entity.getRidingEntity());
				if (offset != 0) {
					return offset;
				}
			}
		}
		return 0;
	}

	public float getMaxSpeed(float defaultSpeed) {
		// the calculated maximum speed
		float maxSpeed = defaultSpeed;
		if (modules != null) {
			for (ModuleBase module : modules) {
				float tempMax = module.getMaxSpeed();
				if (tempMax < maxSpeed) {
					maxSpeed = tempMax;
				}
			}
		}
		return maxSpeed;
	}

	public boolean isPoweredEntity() {
		return engineModules.size() > 0;
	}

	public boolean canBeAttacked(DamageSource type, float dmg) {
		if (isPlaceholder) {
			return false;
		}
		if (modules != null) {
			for (ModuleBase module : getModules()) {
				if (!module.receiveDamage(type, dmg)) {
					return false;
				}
			}
		}
		return true;
	}

	public void onInventoryUpdate() {
		if (modules != null) {
			for (ModuleBase module : modules) {
				module.onInventoryChanged();
			}
		}
	}

	public int getInventorySize() {
		int slotCount = 0;
		if (modules != null) {
			for (ModuleBase module : modules) {
				slotCount += module.getInventorySize();
			}
		}
		return slotCount;
	}

	public static final String NBT_MODULES = "Modules";
	public static final String NBT_SPARES = "Spares";
	public static final String NBT_ID = "Id";
	public static final String NBT_INTERRUPT_TIME = "current_time";
	public static final String NBT_INTERRUPT_MAX_TIME = "max_time";

	public void writeToNBT(NBTTagCompound compound) {
		if (name != null) {
			compound.setString("cartName", name);
		}
		compound.setShort("workingTime", (short) workingTime);
		compound.setByte("CartVersion", cartVersion);
		NBTTagList moduleCompoundList = new NBTTagList();
		if (modules != null) {
			for (ModuleBase module : modules) {
				NBTTagCompound moduleCompound = new NBTTagCompound();
				moduleCompound.setShort(NBT_ID, (short) module.getModuleId());
				module.writeToNBT(moduleCompound);
				moduleCompoundList.appendTag(moduleCompound);
			}
		}
		compound.setTag(NBT_MODULES, moduleCompoundList);
	}

	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("cartName")) {
			name = compound.getString("cartName");
		} else {
			name = null;
		}
		workingTime = compound.getShort("workingTime");
		cartVersion = compound.getByte("CartVersion");
		int oldVersion = cartVersion;
		loadModules(compound, false);
		if (oldVersion < 2) {
			int newSlot = -1;
			int slotCount = 0;
			for (ModuleBase module : modules) {
				if (module instanceof ModuleTool) {
					newSlot = slotCount;
					break;
				} else {
					slotCount += module.getInventorySize();
				}
			}
			if (newSlot != -1) {
				ItemStack lastitem = null;
				for (int i = newSlot; i < vehicleEntity.getSizeInventory(); i++) {
					ItemStack thisitem = vehicleEntity.getStackInSlot(i);
					vehicleEntity.setInventorySlotContents(i, lastitem);
					lastitem = thisitem;
				}
			}
		}
	}

	public boolean canInteractWithEntity(EntityPlayer player) {
		if (isPlaceholder) {
			return false;
		}
		if (modules != null && !player.isSneaking()) {
			boolean interrupt = false;
			for (ModuleBase module : modules) {
				if (module.onInteractFirst(player)) {
					interrupt = true;
				}
			}
			if (interrupt) {
				return false;
			}
		}
		return true;
	}

	public void onInteractWith(EntityPlayer player) {
		player.openGui(player, 0, getWorld(), entity.getEntityId(), 0, 0);
		vehicleEntity.openInventory(player);
	}

	/**
	 * The x coordinate of the cart
	 * 
	 * @return The x coordinate
	 */
	public int x() {
		return MathHelper.floor(entity.posX);
	}

	/**
	 * The y coordinate of the cart
	 * 
	 * @return The y coordinate
	 */
	public int y() {
		return MathHelper.floor(entity.posY);
	}

	/**
	 * The z coordinate of the cart
	 * 
	 * @return The y coordinate
	 */
	public int z() {
		return MathHelper.floor(entity.posZ);
	}

	/**
	 * The coordinates of the cart
	 * 
	 * @return The coordinates
	 */
	public BlockPos pos() {
		return new BlockPos(x(), y(), z());
	}

	public ItemStack getStack(int id) {
		if (modules != null) {
			for (ModuleBase module : modules) {
				if (id < module.getInventorySize()) {
					return module.getStack(id);
				} else {
					id -= module.getInventorySize();
				}
			}
		}
		return null;
	}
	
	public boolean func_191420_l() {
		if (modules != null) {
			for (ModuleBase module : modules) {
				for(int i = 0;i < module.getInventorySize();i++){
					if(!module.getStack(i).func_190926_b()){
						return false;
					}
				}
			}
		}
		return true;
	}

	public void setStack(int id, ItemStack item) {
		if (modules != null) {
			for (ModuleBase module : modules) {
				if (id < module.getInventorySize()) {
					module.setStack(id, item);
					break;
				} else {
					id -= module.getInventorySize();
				}
			}
		}
	}

	public ItemStack decreaseStack(int id, int count) {
		if (modules == null) {
			return null;
		}
		if (vehicleEntity.getStackInSlot(id) != null) {
			ItemStack item;
			if (vehicleEntity.getStackInSlot(id).func_190916_E() <= count) {
				item = vehicleEntity.getStackInSlot(id);
				vehicleEntity.setInventorySlotContents(id, null);
				return item;
			} else {
				item = vehicleEntity.getStackInSlot(id).splitStack(count);
				if (vehicleEntity.getStackInSlot(id).func_190916_E() == 0) {
					vehicleEntity.setInventorySlotContents(id, null);
				}
				return item;
			}
		} else {
			return null;
		}
	}

	public ItemStack removeStackFromSlot(int id) {
		if (vehicleEntity.getStackInSlot(id) != null) {
			ItemStack item = vehicleEntity.getStackInSlot(id);
			vehicleEntity.setInventorySlotContents(id, null);
			return item;
		} else {
			return null;
		}
	}

	public void openInventory(EntityPlayer player) {
		if (modules != null) {
			for (ModuleBase module : modules) {
				module.openInventory(player);
			}
		}
	}

	public void closeInventory(EntityPlayer player) {
		if (modules != null) {
			for (ModuleBase module : modules) {
				module.closeInventory(player);
			}
		}
	}

	public void writeSpawnData(ByteBuf data) {
		data.writeByte(modules.size());
		for (ModuleBase module : modules) {
			data.writeShort((short) module.getModuleId());
		}
		if (name == null) {
			data.writeByte(0);
		} else {
			data.writeByte(name.getBytes().length);
			for (byte b : name.getBytes()) {
				data.writeByte(b);
			}
		}
	}

	public void readSpawnData(ByteBuf data) {
		byte length = data.readByte();
		int[] ids = new int[length];
		for (int i = 0; i < length; i++) {
			ids[i] = data.readShort();
		}
		loadModules(ids);
		int nameLength = data.readByte();
		if (nameLength == 0) {
			name = null;
		} else {
			byte[] nameBytes = new byte[nameLength];
			for (int i = 0; i < nameLength; i++) {
				nameBytes[i] = data.readByte();
			}
			name = new String(nameBytes);
		}
		if (entity.getDataManager() instanceof LockableEntityDataManager) {
			((LockableEntityDataManager) entity.getDataManager()).release();
		}
	}

	/**
	 * Fills fluid into internal tanks, distribution is left to the
	 * ITankContainer.
	 * 
	 * @param resource
	 *            FluidStack representing the maximum amount of fluid filled
	 *            into the ITankContainer
	 * @param doFill
	 *            If false filling will only be simulated.
	 * @return Amount of resource that was filled into internal tanks.
	 */
	public int fill(FluidStack resource, boolean doFill) {
		int amount = 0;
		if (resource != null && resource.amount > 0) {
			FluidStack fluid = resource.copy();
			for (ModuleTank tankModule : tankModules) {
				int tempAmount = tankModule.fill(fluid, doFill);
				amount += tempAmount;
				fluid.amount -= tempAmount;
				if (fluid.amount <= 0) {
					break;
				}
			}
		}
		return amount;
	}

	public FluidStack drain(int maxDrain, boolean doDrain) {
		return drainInternal(null, maxDrain, doDrain);
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return drainInternal(resource, resource == null ? 0 : resource.amount, doDrain);
	}

	private FluidStack drainInternal(FluidStack resource, int maxDrain, boolean doDrain) {
		FluidStack ret = resource;
		if (ret != null) {
			ret = ret.copy();
			ret.amount = 0;
		}
		for (ModuleTank tankModule : tankModules) {
			FluidStack temp = tankModule.drain(maxDrain, doDrain);
			if (temp != null && (ret == null || ret.isFluidEqual(temp))) {
				if (ret == null) {
					ret = temp;
				} else {
					ret.amount += temp.amount;
				}
				maxDrain -= temp.amount;
				if (maxDrain <= 0) {
					break;
				}
			}
		}
		if (ret != null && ret.amount == 0) {
			return null;
		}
		return ret;
	}

	public int drain(Fluid type, int maxDrain, boolean doDrain) {
		int amount = 0;
		if (type != null && maxDrain > 0) {
			for (ModuleTank tank : tankModules) {
				FluidStack drained = tank.drain(maxDrain, false);
				if (drained != null && type.equals(drained.getFluid())) {
					amount += drained.amount;
					maxDrain -= drained.amount;
					if (doDrain) {
						tank.drain(drained.amount, true);
					}
					if (maxDrain <= 0) {
						break;
					}
				}
			}
		}
		return amount;
	}

	public IFluidTankProperties[] getTankProperties() {
		IFluidTankProperties[] ret = new IFluidTankProperties[tankModules.size()];
		for (int i = 0; i < ret.length; i++) {
			IFluidTank tank = tankModules.get(i);
			ret[i] = new FluidTankProperties(tank.getFluid(), tank.getCapacity());
		}
		return ret;
	}

	public boolean isItemValid(int id, ItemStack item) {
		if (modules != null) {
			for (ModuleBase module : modules) {
				if (id < module.getInventorySize()) {
					return module.getSlots().get(id).isItemValid(item);
				} else {
					id -= module.getInventorySize();
				}
			}
		}
		return false;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public String getName() {
		return "container.modular_vehicle";
	}

	public boolean getEngineFlag() {
		return engineFlag;
	}

	public void setEngineFlag(boolean engineFlag) {
		this.engineFlag = engineFlag;
	}

	public Entity getEntity() {
		return entity;
	}

	public IVehicleEntity getVehicleEntity() {
		return vehicleEntity;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public List<ModuleData> getModuleDataList() {
		List<ModuleData> result = new ArrayList<>();
		for (ModuleBase module : modules) {
			result.add(module.getModuleData());
		}
		return result;
	}

	public Random getRandom() {
		return rand;
	}

	public ItemStack getVehicleItem() {
		if (modules != null) {
			ItemStack vehicle = ModuleDataItemHandler.createModularVehicle(this);
			if (vehicle != null) {
				if (getVehicleRawName() != null && !getVehicleRawName().isEmpty()) {
					vehicle.setStackDisplayName(getVehicleRawName());
				}
				return vehicle;
			}
		} else {
			int id = VehicleRegistry.getInstance().getIdFromType(vehicleType);
			if (id != -1) {
				return new ItemStack(ModItems.vehicles, 1, id);
			}
		}
		return null;
	}

	private class GuiAllocationHelper {
		public int width;
		public int maxHeight;
		public List<ModuleBase> modules = new ArrayList<>();
	}
}
