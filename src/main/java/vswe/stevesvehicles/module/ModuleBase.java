package vswe.stevesvehicles.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.EntityDataManager.DataEntry;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfo;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoBoolean;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoInteger;
import vswe.stevesvehicles.client.gui.assembler.SimulationInfoMultiBoolean;
import vswe.stevesvehicles.client.gui.screen.GuiBase;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.container.ContainerVehicle;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.module.data.ModuleData;
import vswe.stevesvehicles.module.data.registry.ModuleRegistry;
import vswe.stevesvehicles.nbt.NBTHelper;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.network.DataWriter;
import vswe.stevesvehicles.network.PacketHandler;
import vswe.stevesvehicles.network.PacketType;
import vswe.stevesvehicles.vehicle.VehicleBase;
import vswe.stevesvehicles.vehicle.entity.EntityModularCart;

/**
 * The base for all modules. This is what's used by the vehicle to add features,
 * models and interfaces for the vehicle. should not be confused with ModuleData
 * which is the data used for adding a module to the vehicle in the vehicle
 * Assembler.
 * 
 * @author Vswe
 *
 */
public abstract class ModuleBase {
	// the vehicle this module is part of
	private VehicleBase vehicle;
	// the inventory this module is using, could be an empty array.
	// getInventorySize is controlling the size of this
	private ItemStack[] cargo;
	// where in the interface the module is located, used to draw things where
	// they should be.
	// the values are calculated in the vehicle on initializing
	private int offSetX;
	private int offSetY;
	// offsets for Gui Data, Data Watchers and Packets. These offsets works as a
	// header when
	// sending values between the client and the server
	private int guiDataOffset;
	private int dataWatcherOffset;
	// the slot global start index for this module, used to transfer the local
	// indices to global ones
	protected int slotGlobalStart;
	// the id of this module, this is assigned on creation. The id is the same
	// as the ModuleData which created the module.
	private int moduleId;
	// the of this module, this is the position among modules that this module
	// has.
	private int positionId;
	// the models this module is using, this is generated from the ModuleData
	// creating this module
	private ArrayList<ModelVehicle> models;

	/**
	 * Creates a new instance of this module, the module will be created at the
	 * given vehicle.
	 * 
	 * @param vehicle
	 *            The vehicle this module is created on
	 */
	public ModuleBase(vswe.stevesvehicles.vehicle.VehicleBase vehicle) {
		// save the vehicle
		this.vehicle = vehicle;
		// initialize the inventory of this module
		cargo = new ItemStack[getInventorySize()];
	}

	/**
	 * Initializes the modules, this is done after all modules has been added to
	 * the vehicle, and given proper IDs and everything.
	 */
	public void init() {
	}

	/**
	 * Initializes the modules, this is done after all modules has been added to
	 * the vehicle but before most of the initializing code
	 */
	public void preInit() {
	}

	/**
	 * Get the vehicle this module is a part of
	 * 
	 * @return The vehicle this module was created at
	 */
	public vswe.stevesvehicles.vehicle.VehicleBase getVehicle() {
		return vehicle;
	}

	/**
	 * If this module is part of a placeholder vehicle, a placeholder vehicle is
	 * a client side only vehicle used in the vehicle assembler.
	 * 
	 * @return If this module is a placeholder module
	 */
	public boolean isPlaceholder() {
		return getVehicle().isPlaceholder;
	}

	/**
	 * Sets the modular id of this module, this is basically the id of the
	 * {@link ModuleData} used to create this module.
	 * 
	 * @param val
	 *            The module id
	 */
	public void setModuleId(int val) {
		moduleId = val;
	}

	/**
	 * Returns which modular id this module is associated with
	 * 
	 * @return The module id
	 */
	public int getModuleId() {
		return moduleId;
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	/**
	 * Is called when the vehicle's inventory has been changed
	 */
	public void onInventoryChanged() {
	}

	/**
	 * Used to get where to start draw the interface, this is calculated by the
	 * vehicle.
	 * 
	 * @return The x offset of the interface
	 */
	public int getX() {
		if (doStealInterface()) {
			return 0;
		} else {
			return offSetX;
		}
	}

	/**
	 * Used to get where to start draw the interface, this is calculated by the
	 * vehicle.
	 * 
	 * @return The y offset of the interface
	 */
	public int getY() {
		if (doStealInterface()) {
			return 0;
		} else {
			return offSetY;
		}
	}

	/**
	 * Used to set where the interface of this module starts, this is set by the
	 * vehicle
	 * 
	 * @param val
	 *            The x offset to use
	 */
	public void setX(int val) {
		offSetX = val;
	}

	/**
	 * Used to set where the interface of this module starts, this is set by the
	 * vehicle
	 * 
	 * @param val
	 *            The y offset to use
	 */
	public void setY(int val) {
		offSetY = val;
	}

	/**
	 * Returns the amount of stacks that this module can store. This will use
	 * hasSlots, getInventoryWidth and getInventoryHeight to calculate the size,
	 * this can however be overridden for more advanced usages.
	 * 
	 * @return The size of the inventory of this module
	 */
	public int getInventorySize() {
		if (!hasSlots()) {
			return 0;
		} else {
			return getInventoryWidth() * getInventoryHeight();
		}
	}

	/**
	 * Returns the size this module wants to allocate in the interface. One
	 * shouldn't draw anything outside this area.
	 * 
	 * @return The width of the module's interface
	 */
	public int guiWidth() {
		return 15 + getInventoryWidth() * 18;
	}

	/**
	 * Returns the size this module wants to allocate in the interface. One
	 * shouldn't draw anything outside this area.
	 * 
	 * @return The height of the module's interface
	 */
	public int guiHeight() {
		return 27 + getInventoryHeight() * 18;
	}

	/**
	 * The width of slots in the basic slot allocation. Used by the default
	 * getInventorySize to make standard slot allocation easier
	 * 
	 * @return The number of slots next to each other
	 */
	protected int getInventoryWidth() {
		return 3;
	}

	/**
	 * The height of slots in the basic slot allocation. Used by the default
	 * getInventorySize to make standard slot allocation easier
	 * 
	 * @return The number of slots on top of each other
	 */
	protected int getInventoryHeight() {
		return 1;
	}

	/**
	 * Called by the interface when the user has pressed a key on the keyboard
	 * 
	 * @param character
	 *            The character pressed
	 * @param extraInformation
	 *            Extra information of special keys
	 */
	public void keyPress(GuiVehicle gui, char character, int extraInformation) {
	}

	// the list of the slots used by this module
	protected ArrayList<SlotBase> slotList;

	/**
	 * Get the list of slots used by this module. These have already been
	 * generated by generateSlots
	 * 
	 * @return The ArrayList of SlotBase with the slots
	 */
	public ArrayList<SlotBase> getSlots() {
		return slotList;
	}

	/**
	 * Generates the slots used for this module, this is used both for the
	 * Container and the Interface. For most modules just leave this and use
	 * getSlot instead (as well as setting getInventoryWidth and
	 * getInventoryHeight)
	 * 
	 * @param slotCount
	 *            The number of slots that has already been added to the
	 *            vehicle. This is for generating the corred slot id
	 * @return The number of slots that the vehicle have added after this module
	 *         has generated its slots.
	 */
	public int generateSlots(int slotCount) {
		slotGlobalStart = slotCount;
		slotList = new ArrayList<>();
		for (int j = 0; j < getInventoryHeight(); j++) {
			for (int i = 0; i < getInventoryWidth(); i++) {
				slotList.add(getSlot(slotCount++, i, j));
			}
		}
		return slotCount;
	}

	/**
	 * Returns a new slot with the given id, x and y coordinate. This is used to
	 * generate the slots easier. Just override this function and return a new
	 * slots depending on where it's located. Shouldn't be used if you're
	 * overriding generateSlots
	 * 
	 * @param slotId
	 *            The id of the slot to be created
	 * @param x
	 *            The x value of the slot, this is not the interface coordinate
	 *            but just which column it's in.
	 * @param y
	 *            The y value of the slot, this is not hte interface coordinate
	 *            but just which row it's in.
	 * @return The created SlotBase
	 */
	protected SlotBase getSlot(int slotId, int x, int y) {
		return null;
	}

	/**
	 * Whether this module has slots or not. By default a module is thought to
	 * have slots if it has an interface. This is however overridden if it's not
	 * the case.
	 * 
	 * @return If it should use slots or not
	 */
	public boolean hasSlots() {
		return hasGui();
	}

	/**
	 * Called every time the vehicle is being updated.
	 */
	public void update() {
	}

	/**
	 * Returns if this module has enough fuel to keep the vehicle going one tick
	 * more. This should however be moved to engineModuleBase
	 * 
	 * @param consumption
	 *            The amount of fuel units the vehicle wants to consume
	 * @return If it has fuel or not
	 */
	public boolean hasFuel(int consumption) {
		return false;
	}

	/**
	 * The maximum speed this module allows the vehicle to move in. The maximum
	 * speed of the vehicle will therefore be set to the lowest value all of
	 * it's modules allow.
	 * 
	 * @return The maximum speed of the vehicle
	 */
	public float getMaxSpeed() {
		return 1.1F;
	}

	/**
	 * Returns the Y value this vehicle should try to be on. By returning -1
	 * this module won't care about where the vehicle should be. If no modules
	 * do care about this the vehicle will just continue where it already is.
	 * 
	 * @return The Y value
	 */
	public int getYTarget() {
		return -1;
	}

	/**
	 * Called when the vehicle travels over a rail. Used to allow modules to
	 * react to specific rails.
	 * 
	 * @param x
	 *            X coordinate in the world
	 * @param y
	 *            Y coordinate in the world
	 * @param z
	 *            Z coordinate in the world
	 */
	public void moveMinecartOnRail(BlockPos pos) {
	}

	/**
	 * Used to get the ItemStack in a specific slot of this module
	 * 
	 * @param slot
	 *            The slot id, this is the local id for this module.
	 * @return The ItemStack in the slot, could of course be null
	 */
	public ItemStack getStack(int slot) {
		return cargo[slot];
	}

	/**
	 * Used to set the ItemStack in specific slot of this module.
	 * 
	 * @param slot
	 *            The slot id, this is the local id for this module.
	 * @param item
	 *            The ItemStack to be set.
	 */
	public void setStack(int slot, ItemStack item) {
		cargo[slot] = item;
	}

	/**
	 * Used to try to merge/add the ItemStack in specific slots of this module.
	 * 
	 * @param slotStart
	 *            The slot start id, this is the local id for this module.
	 * @param slotEnd
	 *            The slot end id, this is the local id for this module.
	 * @param item
	 *            The ItemStack to be set.
	 */
	public void addStack(int slotStart, int slotEnd, ItemStack item) {
		getVehicle().addItemToChest(item, slotGlobalStart + slotStart, slotGlobalStart + slotEnd);
	}

	/**
	 * Used to try to merge/add the ItemStack in a specific slot of this module.
	 * 
	 * @param slot
	 *            The slot id, this is the local id for this module.
	 * @param item
	 *            The ItemStack to be set.
	 */
	public void addStack(int slot, ItemStack item) {
		addStack(slot, slot, item);
	}

	/**
	 * Used to prevent the vehicle to drop things when it breaks. If any module
	 * returns false the vehicle won't drop anything.
	 * 
	 * @return If this module allows the vehicle to drop on death
	 */
	public boolean dropOnDeath() {
		return true;
	}

	/**
	 * Called when the vehicle breaks
	 */
	public void onDeath() {
	}

	/**
	 * Whether the vehicle should allocate room for this interface. By default
	 * this also allocates slots, see hasSlots
	 * 
	 * @return If the module is using an interface
	 */
	public boolean hasGui() {
		return false;
	}

	/**
	 * If the module should draw any foreground, it is done here.
	 * 
	 * @param gui
	 *            The GUI that will draw the interface
	 */
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiVehicle gui) {
	}

	/**
	 * Draws a one lined string in the center of the given rectangle. It will
	 * handle scrolling as well as module offset.
	 * 
	 * @param gui
	 *            The gui to draw it on.
	 * @param str
	 *            The string to be drawn.
	 * @param rect
	 *            The rectangle
	 * @param c
	 *            The color to be used
	 */
	@SideOnly(Side.CLIENT)
	public void drawString(GuiVehicle gui, String str, int[] rect, int c) {
		if (rect.length < 4) {
			return;
		} else {
			drawString(gui, str, rect[0] + (rect[2] - gui.getFontRenderer().getStringWidth(str)) / 2, rect[1] + (rect[3] - gui.getFontRenderer().FONT_HEIGHT + 3) / 2, c);
		}
	}

	/**
	 * Draws a string at the given location. It will handle scrolling as well as
	 * module offset.
	 * 
	 * @param gui
	 *            The gui to draw it on.
	 * @param str
	 *            The string to be draw
	 * @param x
	 *            The local x coordinate
	 * @param y
	 *            The local y coordinate
	 * @param c
	 *            The color to be used
	 */
	@SideOnly(Side.CLIENT)
	public void drawString(GuiVehicle gui, String str, int x, int y, int c) {
		drawString(gui, str, x, y, -1, false, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawString(GuiVehicle gui, String str, int x, int y, int w, boolean center, int c) {
		int j = gui.getGuiLeft();
		int k = gui.getGuiTop();
		int[] rect = new int[] { x, y, w, 8 };
		boolean stealInterface = doStealInterface();
		int dif = 0;
		// scroll the bounding box
		if (!stealInterface) {
			dif = handleScroll(rect);
		}
		if (rect[3] > 0) {
			if (!stealInterface) {
				gui.setupAndStartScissor();
			}
			if (center) {
				gui.getFontRenderer().drawString(str, rect[0] + (rect[2] - gui.getFontRenderer().getStringWidth(str)) / 2 + getX(), rect[1] + getY() + dif, c);
			} else {
				gui.getFontRenderer().drawString(str, rect[0] + getX(), rect[1] + getY() + dif, c);
			}
			if (!stealInterface) {
				gui.stopScissor();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void drawScaledCenteredString(GuiVehicle gui, String str, int x, int y, int w, float multiplier, int color) {
		x -= gui.getGuiLeft();
		y -= gui.getGuiTop();
		GL11.glPushMatrix();
		GL11.glScalef(multiplier, multiplier, 1F);
		int width = gui.getFontRenderer().getStringWidth(str);
		x += (w - width * multiplier) / 2;
		x += getX();
		y += getY() - getVehicle().getRealScrollY();
		gui.setupAndStartScissor();
		gui.getFontRenderer().drawString(str, (int) ((x + gui.getGuiLeft()) / multiplier), (int) ((y + gui.getGuiTop()) / multiplier), color);
		gui.stopScissor();
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	public void drawStringWithShadow(GuiVehicle gui, String str, int x, int y, int c) {
		int j = gui.getGuiLeft();
		int k = gui.getGuiTop();
		int[] rect = new int[] { x, y, 0, 8 };
		// scroll the bounding box
		if (!doStealInterface()) {
			handleScroll(rect);
		}
		// just draw the text if the whole text can be drawn
		if (rect[3] == 8) {
			gui.getFontRenderer().drawStringWithShadow(str, rect[0] + getX(), rect[1] + getY(), c);
		}
	}

	/**
	 * Draws a multiline string at the given location. It will handle scrolling
	 * as well as module offset.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param str
	 *            The string to be drawn
	 * @param x
	 *            The local x coordinate
	 * @param y
	 *            The local y coordinate
	 * @param w
	 *            The maximum width of the text area
	 * @param c
	 *            The color to be used
	 */
	@SideOnly(Side.CLIENT)
	public void drawSplitString(GuiVehicle gui, String str, int x, int y, int w, int c) {
		drawSplitString(gui, str, x, y, w, false, c);
	}

	@SideOnly(Side.CLIENT)
	public void drawSplitString(GuiVehicle gui, String str, int x, int y, int w, boolean center, int c) {
		// split the string in multiple lines
		List newlines = gui.getFontRenderer().listFormattedStringToWidth(str, w);
		// loop through the lines and draw then using drawString
		for (int i = 0; i < newlines.size(); i++) {
			String line = newlines.get(i).toString();
			drawString(gui, line, x, y + i * 8, w, center, c);
		}
	}

	public void drawItemInInterface(GuiVehicle gui, ItemStack item, int x, int y) {
		int[] rect = new int[] { x, y, 16, 16 };
		int dif = handleScroll(rect);
		if (rect[3] > 0) {
			final RenderItem renderitem = Minecraft.getMinecraft().getRenderItem();
			gui.setZLevel(100);
			renderitem.zLevel = 100;
			gui.setupAndStartScissor();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			renderitem.renderItemAndEffectIntoGUI(item, rect[0] + getX(), rect[1] + getY() + dif);
			gui.stopScissor();
			renderitem.zLevel = 0;
			gui.setZLevel(0);
		}
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimensions.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param targetX
	 *            The local x coordinate to draw it on
	 * @param targetY
	 *            The local y coordinate to draw it on
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            The y coordinate in the source file
	 * @param sizeX
	 *            The width of the image
	 * @param sizeY
	 *            The height of the image
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY) {
		drawImage(gui, targetX, targetY, srcX, srcY, sizeX, sizeY, GuiBase.RenderRotation.NORMAL);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimensions.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param targetX
	 *            The local x coordinate to draw it on
	 * @param targetY
	 *            The local y coordinate to draw it on
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            The y coordinate in the source file
	 * @param sizeX
	 *            The width of the image
	 * @param sizeY
	 *            The height of the image
	 * @param rotation
	 *            The rotation this will be drawn with
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY, GuiBase.RenderRotation rotation) {
		// create a rectangle and call the other drawImage function to do the
		// job
		drawImage(gui, new int[] { targetX, targetY, sizeX, sizeY }, srcX, srcY, rotation);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimentiosn.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param rect
	 *            The rectangle indicating where to draw it {targetX, targetY,
	 *            sizeX, sizeY}
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            They y coordinate in the source file
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int[] rect, int srcX, int srcY) {
		drawImage(gui, rect, srcX, srcY, GuiBase.RenderRotation.NORMAL);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimentiosn.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param rect
	 *            The rectangle indicating where to draw it {targetX, targetY,
	 *            sizeX, sizeY}
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            They y coordinate in the source file
	 * @param rotation
	 *            The rotation this will be drawn with
	 */
	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, int[] rect, int srcX, int srcY, GuiBase.RenderRotation rotation) {
		// the rectangle need to be valid
		if (rect.length < 4) {
			return;
		} else {
			// clones the rectangle and scroll the clone
			rect = cloneRect(rect);
			if (!doStealInterface()) {
				srcY -= handleScroll(rect);
			}
			// if there's still something to draw(that it's not scrolled outside
			// the screen)
			if (rect[3] > 0) {
				gui.drawRect(gui.getGuiLeft() + rect[0] + getX(), gui.getGuiTop() + rect[1] + getY(), srcX, srcY, rect[2], rect[3], rotation, textureSize);
			}
		}
	}

	/**
	 * Draw an icon in the given interface, using the current texture and using
	 * the given dimensions.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param icon
	 *            The Icon to draw
	 * @param targetX
	 *            The local x coordinate to draw it on
	 * @param targetY
	 *            The local y coordinate to draw it on
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            The y coordinate in the source file
	 * @param sizeX
	 *            The width of the image
	 * @param sizeY
	 *            The height of the image
	 */
	//TODO: sprites
	/*@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, IIcon icon, int targetX, int targetY, int srcX, int srcY, int sizeX, int sizeY) {
		// create a rectangle and call the other drawImage function to do the
		// job
		drawImage(gui, icon, new int[] { targetX, targetY, sizeX, sizeY }, srcX, srcY);
	}

	/**
	 * Draw an image in the given interface, using the current texture and using
	 * the given dimentiosn.
	 * 
	 * @param gui
	 *            The gui to draw it on
	 * @param rect
	 *            The rectangle indicating where to draw it {targetX, targetY,
	 *            sizeX, sizeY}
	 * @param srcX
	 *            The x coordinate in the source file
	 * @param srcY
	 *            They y coordinate in the source file

	@SideOnly(Side.CLIENT)
	public void drawImage(GuiVehicle gui, IIcon icon, int[] rect, int srcX, int srcY) {
		// the rectangle need to be valid
		if (rect.length < 4) {
			return;
		} else {
			// clones the rectangle and scroll the clone
			rect = cloneRect(rect);
			if (!doStealInterface()) {
				srcY -= handleScroll(rect);
			}
			// if there's still something to draw(that it's not scrolled outside
			// the screen)
			if (rect[3] > 0) {
				gui.drawIcon(icon, gui.getGuiLeft() + rect[0] + getX(), gui.getGuiTop() + rect[1] + getY(), rect[2] / 16F, rect[3] / 16F, srcX / 16F, srcY / 16F);
			}
		}
	}*/

	/**
	 * Scrolls a given rectangle accordingly to the scrollbar in the interface
	 * 
	 * @param rect
	 *            The rectangle to scroll {targetX, targetY, sizeX, sizeY}
	 * @return The start offset caused by the scroll, i.e. if the middle part of
	 *         the rectangle is the topmost visible part. Used to change the
	 *         srcY when drawing images for instance, see drawImage.
	 */
	public int handleScroll(int rect[]) {
		// scroll the rectangle
		rect[1] -= getVehicle().getRealScrollY();
		// calculate the y val
		int y = rect[1] + getY();
		// if it's too far up
		if (y < 4) {
			int dif = (y - 4);
			rect[3] += dif;
			y = 4;
			rect[1] = y - getY();
			return dif;
			// if it's too far down
		} else if (y + rect[3] > vswe.stevesvehicles.vehicle.VehicleBase.MODULAR_SPACE_HEIGHT) {
			rect[3] = Math.max(0, vswe.stevesvehicles.vehicle.VehicleBase.MODULAR_SPACE_HEIGHT - y);
			return 0;
			// if the whole rectangle do fit
		} else {
			return 0;
		}
	}

	/**
	 * Clones a rectangle
	 * 
	 * @param rect
	 *            The rectangle to be clones {targetX, targetY, sizeX, sizeY}
	 * @return The cloned rectangle {targetX, targetY, sizeX, sizeY}
	 */
	private int[] cloneRect(int[] rect) {
		return new int[] { rect[0], rect[1], rect[2], rect[3] };
	}

	/**
	 * Whether the module is using client/server buttons. Currently not used
	 * 
	 * @return whether buttons are used or not
	 */
	public boolean useButtons() {
		return false;
	}

	/**
	 * Allows the module to override the direction the vehicle is going. This
	 * mechanic is not finished and hence won't work perfectly.
	 * 
	 * @param pos
	 *            The coordinates in the world
	 * @return The direction to go, default means that the module won't chane it
	 */
	public RailDirection getSpecialRailDirection(BlockPos pos) {
		return RailDirection.DEFAULT;
	}

	public void openInventory(EntityPlayer player) {
	}

	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * Handles the different directions that the module can force a vehicle to
	 * go in. {@see getSpecialRailDirection}
	 * 
	 * @author Vswe
	 *
	 */
	public enum RailDirection {
		DEFAULT, NORTH, WEST, SOUTH, EAST, LEFT, FORWARD, RIGHT
	}

	/**
	 * Initializing any server/client buttons
	 */
	protected void loadButtons() {
	}

	/**
	 * Handles the writing of the NBT data when the world is being saved
	 * 
	 * @param tagCompound
	 *            The tag compound to write the data to
	 */
	public final void writeToNBT(NBTTagCompound tagCompound) {
		// write the content of the slots to the tag compound
		if (getInventorySize() > 0) {
			NBTTagList items = new NBTTagList();
			for (int i = 0; i < getInventorySize(); ++i) {
				if (getStack(i) != null) {
					NBTTagCompound item = new NBTTagCompound();
					item.setByte("Slot", (byte) i);
					getStack(i).writeToNBT(item);
					items.appendTag(item);
				}
			}
			tagCompound.setTag("Items", items);
		}
		// writes module specific data
		save(tagCompound);
	}

	/**
	 * Allows a module to save specific data when world is saved
	 * 
	 * @param tagCompound
	 *            The NBT tag compound to write to
	 *
	 */
	protected void save(NBTTagCompound tagCompound) {
	}

	/**
	 * Handles the reading of the NBT data when the world is being loaded
	 * 
	 * @param tagCompound
	 *            The tag compound to read the data from
	 */
	public final void readFromNBT(NBTTagCompound tagCompound) {
		// read the content of the slots to the tag compound
		if (getInventorySize() > 0) {
			NBTTagList items = tagCompound.getTagList("Items", NBTHelper.COMPOUND.getId());
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = items.getCompoundTagAt(i);
				int slot = item.getByte("Slot") & 255;
				if (slot >= 0 && slot < getInventorySize()) {
					setStack(slot, new ItemStack(item));
				}
			}
		}
		// reads module specific data
		load(tagCompound);
	}

	/**
	 * Allows a module to load specific data when world is loaded
	 * 
	 * @param tagCompound
	 *            The NBT tag compound to read from
	 *
	 */
	protected void load(NBTTagCompound tagCompound) {
	}

	/**
	 * Used to draw background for a module
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 */
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
	}

	@SideOnly(Side.CLIENT)
	public void drawBackgroundItems(GuiVehicle gui, int x, int y) {
	}

	/**
	 * Used to handle mouse clicks on the module's interface
	 * 
	 * @param gui
	 *            The gui that was clicked
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 * @param button
	 *            The button that was pressed on the mouse
	 */
	@SideOnly(Side.CLIENT)
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
	}

	/**
	 * Used to handle mouse movement and move releases in the module's interface
	 * 
	 * @param gui
	 *            The gui that is being used
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 * @param button
	 *            The button that was released, or -1 if the cursor is just
	 *            being moved
	 */
	@SideOnly(Side.CLIENT)
	public void mouseMovedOrUp(GuiVehicle gui, int x, int y, int button) {
	}

	/**
	 * Used to draw mouse over text for a module
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordiante of the mouse
	 */
	@SideOnly(Side.CLIENT)
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
	}

	/**
	 * Detects if the given mouse coordinates are within the given rectangle
	 * 
	 * @param x
	 *            The mouse x coordinate
	 * @param y
	 *            The mouse y coordinate
	 * @param x1
	 *            The x coordinate of the rectangle
	 * @param y1
	 *            The y coordinate of the rectangle
	 * @param sizeX
	 *            The width of the rectangle
	 * @param sizeY
	 *            The height of the rectangle
	 * @return If the mouse was inside the rectangle
	 */
	protected boolean inRect(int x, int y, int x1, int y1, int sizeX, int sizeY) {
		// creates a rectangle and call the other inRect
		return inRect(x, y, new int[] { x1, y1, sizeX, sizeY });
	}

	/**
	 * Detects if the given mouse coordinates are within the given rectangle
	 * 
	 * @param x
	 *            The mouse x coordinate
	 * @param y
	 *            The mouse y coordinate
	 * @param rect
	 *            The rectangle to check for {x,y,width, height}
	 * @return If the mouse was inside the rectangle
	 */
	public boolean inRect(int x, int y, int[] rect) {
		// check if we have a valid rectangle
		if (rect.length < 4) {
			return false;
		} else {
			// clone the rectangle and scroll that clone
			rect = cloneRect(rect);
			if (!doStealInterface()) {
				handleScroll(rect);
			}
			// check if the mouse is inside the scrolled rectangle
			return x >= rect[0] && x <= rect[0] + rect[2] && y >= rect[1] && y <= rect[1] + rect[3];
		}
	}

	/**
	 * Let's the module handle when damage is caused to the vehicle
	 * 
	 * @param source
	 *            The source of the damage
	 * @param val
	 *            The damage
	 * @return True if the vehicle should take the damage, False to prevent the
	 *         damage
	 */
	public boolean receiveDamage(DamageSource source, float val) {
		return true;
	}

	/**
	 * Tells the vehicle to turn around, if this module is allowed to tell the
	 * vehicle to do so.
	 */
	protected void turnback() {
		if (getVehicle().getEntity() instanceof EntityModularCart) {
			// check if this module is allowed to tell the vehicle
			for (ModuleBase module : getVehicle().getModules()) {
				if (module != this && module.preventTurnBack()) {
					return;
				}
			}
			// if so, turn bakc
			((EntityModularCart) getVehicle().getEntity()).turnback();
		}
	}

	/**
	 * Allows a module to take all control of a vehicle's turn back condition
	 * 
	 * @return True to prevent other modules from turning the vehicle around
	 */
	protected boolean preventTurnBack() {
		return false;
	}

	protected DataWriter getDataWriter(boolean hasInterfaceOpen) {
		DataWriter dw = PacketHandler.getDataWriter(PacketType.VEHICLE);
		if (!hasInterfaceOpen) {
			dw.writeInteger(getVehicle().getEntity().getEntityId());
		}
		dw.writeByte(getPositionId());
		return dw;
	}

	protected DataWriter getDataWriter() {
		return getDataWriter(true);
	}

	protected void sendPacketToServer(DataWriter dw) {
		PacketHandler.sendPacketToServer(dw);
	}

	protected void sendPacketToPlayer(DataWriter dw, EntityPlayer player) {
		PacketHandler.sendPacketToPlayer(dw, player);
	}

	protected void receivePacket(DataReader dr, EntityPlayer player) {
	}

	public static void delegateReceivedPacket(VehicleBase vehicle, DataReader dr, EntityPlayer player) {
		int id = dr.readByte();
		if (id >= 0 && id < vehicle.getModules().size()) {
			vehicle.getModules().get(id).receivePacket(dr, player);
		}
	}

	/**
	 * The number of datawatchers this module wants to use
	 * 
	 * @return The amount of datawatchers
	 */
	public int numberOfDataWatchers() {
		return 0;
	}

	/**
	 * Sets the offset of the datawatchers, this is used as a header to know
	 * which module owns the datawatcher. This is set by the vehicle.
	 * 
	 * @return The datawatcher offset
	 */
	public int getDataWatcherStart() {
		return dataWatcherOffset;
	}

	/**
	 * Gets the offset of the datawatchers, this is used as a header to know
	 * which module owns the datawatcher.
	 * 
	 * @param val
	 *            The datawatcher offset
	 */
	public void setDataWatcherStart(int val) {
		dataWatcherOffset = val;
	}

	/**
	 * Used to initiate the datawatchers
	 */
	public void initDw() {
	}

	/**
	 * Generate a free datawatcher id to use
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @return The datawatcher id
	 */
	private int getDwId(int id) {
		id += this.getDataWatcherStart();
		return id;
	}

	/**
	 * Register a data parameter
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @param val
	 *            The value to add
	 */
	protected final <T> void registerDw(DataParameter<T> key, T value) {
		for (DataEntry entry : getVehicle().getEntity().getDataManager().getAll()) {
			if (entry.getKey() == key) {
				return;
			}
		}
		getVehicle().getEntity().getDataManager().register(key, value);
	}

	/**
	 * Updates a datawatcher
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @param val
	 *            The value to update it to
	 */
	protected final <T> void updateDw(DataParameter<T> key, T value) {
		getVehicle().getEntity().getDataManager().set(key, value);
	}

	/**
	 * Get a datawatcher
	 * 
	 * @param id
	 *            The local datawatcher id
	 * @return The value of the datawatcher
	 */
	protected <T> T getDw(DataParameter<T> key) {
		return getVehicle().getEntity().getDataManager().get(key);
	}

	private int ids = 0;

	protected <T> DataParameter<T> createDw(DataSerializer<T> serializer) {
		return serializer.createKey(getDwId(ids++));
	}

	/**
	 * The amount of Gui data this module want to use. Gui data is used for
	 * sending information from the server to the client when the specific
	 * client has the the interface open
	 * 
	 * @return The number of Gui data
	 */
	public int numberOfGuiData() {
		return 0;
	}

	/**
	 * Get the gui data offset. This is used as a header to know which module
	 * owns a specific gui data.
	 * 
	 * @return
	 */
	public int getGuiDataStart() {
		return guiDataOffset;
	}

	/**
	 * Set the gui data offset. This is used as a header to know which module
	 * owns a specific gui data. This is set by the vehicle.
	 * 
	 * @param val
	 */
	public void setGuiDataStart(int val) {
		guiDataOffset = val;
	}

	/**
	 * Updates the gui data for a bunch of players. This is the part that
	 * actually updates the values. It's however the other updateGuiData which
	 * handles most parts
	 * 
	 * @param con
	 *            The containers that i used
	 * @param players
	 *            The players to update
	 * @param id
	 *            The global gui data id
	 * @param data
	 *            The data to update to
	 */
	private final void updateGuiData(Container con, List players, int id, short data) {
		Iterator iterator = players.iterator();
		while (iterator.hasNext()) {
			IContainerListener player = (IContainerListener) iterator.next();
			player.sendProgressBarUpdate(con, id, data);
		}
	}

	/**
	 * Updates the gui data sing the supplied info, this is what is being called
	 * from a module and it's also the function that actually handles the
	 * update.
	 * 
	 * @param info
	 *            The information about the update, should be formatted as
	 *            follows: {Container, Players, isNew}
	 * @param id
	 *            The local gui data id
	 * @param data
	 *            The data to update to
	 */
	public final void updateGuiData(Object[] info, int id, short data) {
		// get the container and see if it's valid
		ContainerVehicle con = (ContainerVehicle) info[0];
		if (con == null) {
			return;
		}
		// calculates the gloval id
		int globalId = id + getGuiDataStart();
		List players = (List) info[1];
		boolean isNew = (Boolean) info[2];
		boolean flag = isNew;
		if (!flag) {
			// if the value is not new, see if it has changed, if so we should
			// update
			if (con.cache != null) {
				Short val = con.cache.get((short) globalId);
				if (val != null) {
					flag = val != data;
				} else {
					flag = true;
				}
			} else {
				flag = true;
			}
		}
		// if the value is new or has changed we should update it
		if (flag) {
			if (con.cache == null) {
				con.cache = new HashMap<>();
			}
			// update it and cache it
			updateGuiData(con, players, globalId, data);
			con.cache.put((short) globalId, data);
		}
	}

	/**
	 * Initializes everything when a player has opened the interface
	 * 
	 * @param con
	 *            The container used by the interface
	 * @param player
	 *            The player that opened it
	 */
	public final void initGuiData(Container con, IContainerListener player) {
		ArrayList players = new ArrayList();
		players.add(player);
		// simulate a value change but mark it as new instead
		checkGuiData(con, players, true);
	}

	/**
	 * Used to send gui data information
	 * 
	 * @param info
	 *            The information that should be sent as the first parameter to
	 *            updateGuiData
	 */
	protected void checkGuiData(Object[] info) {
	}

	/**
	 * Prepares the gui data check
	 * 
	 * @param con
	 *            The container to be used
	 * @param players
	 *            The players that should be receive the information
	 * @param isNew
	 *            If this data is new or not
	 */
	public final void checkGuiData(Container con, List players, boolean isNew) {
		if (con == null) {
			return;
		}
		checkGuiData(new Object[] { con, players, isNew });
	}

	/**
	 * Receive gui data on the client side
	 * 
	 * @param id
	 *            The local gui data id
	 * @param data
	 *            The value of the gui data
	 */
	public void receiveGuiData(int id, short data) {
	}

	/**
	 * Get the consumption for this module
	 * 
	 * @param isMoving
	 *            A flag telling you if the vehicle is moving or not
	 * @return The consumption
	 */
	public int getConsumption(boolean isMoving) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void setModels(ArrayList<ModelVehicle> models) {
		this.models = models;
	}

	@SideOnly(Side.CLIENT)
	public ArrayList<ModelVehicle> getModels() {
		return models;
	}

	public boolean haveModels() {
		return models != null;
	}

	/**
	 * Draw a specific mouse over string if the mouse is in a specific rectangle
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param str
	 *            The string to be drawn
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            the y coordinate of the mouse
	 * @param x1
	 *            The x coordinate of the rectangle
	 * @param y1
	 *            The y coordinate of the rectangle
	 * @param w
	 *            The width of the rectangle
	 * @param h
	 *            The height of the rectangle
	 */
	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, String str, int x, int y, int x1, int y1, int w, int h) {
		// creates a rectangle and calls the other drawStringOnMouseOver
		drawStringOnMouseOver(gui, str, x, y, new int[] { x1, y1, w, h });
	}

	/**
	 * Draw a specific mouse over string if the mouse is in a specific rectangle
	 * 
	 * @param gui
	 *            The gui to draw on
	 * @param str
	 *            The string to be drawn
	 * @param x
	 *            The x coordinate of the mouse
	 * @param y
	 *            The y coordinate of the mouse
	 * @param rect
	 *            The rectangle that the mouse has to be in, defin as
	 *            {x,y,width,height}
	 */
	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, String str, int x, int y, int[] rect) {
		// if it's not in the rectangle the text shouldn't be written
		if (!inRect(x, y, rect)) {
			return;
		}
		// convert to global coordinates
		x += getX();
		y += getY();
		// draw the mouse overlay
		gui.drawMouseOver(str, x, y);
	}

	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, String str, int x, int y) {
		// convert to global coordinates
		x += getX();
		y += getY();
		// draw the mouse overlay
		gui.drawMouseOver(str, x, y);
	}

	@SideOnly(Side.CLIENT)
	public final void drawStringOnMouseOver(GuiVehicle gui, List<String> info, int x, int y) {
		// convert to global coordinates
		x += getX();
		y += getY();
		// draw the mouse overlay
		gui.drawMouseOver(info, x, y);
	}

	/**
	 * Draws an image overlay on the screen. Observe that this is not when a
	 * special interface is open.
	 * 
	 * @param rect
	 *            The rectangle for the image's dimensions {targetX, targetY,
	 *            width, height}
	 * @param sourceX
	 *            The x coordinate in the source file
	 * @param sourceY
	 *            The y coordinate in the source file
	 */
	protected void drawImage(int[] rect, int sourceX, int sourceY) {
		drawImage(rect[0], rect[1], sourceX, sourceY, rect[2], rect[3]);
	}

	/**
	 * Draws an image overlay on the screen. Observe that this is not when a
	 * special interface is open.
	 * 
	 * @param targetX
	 *            The x coordinate of the image
	 * @param targetY
	 *            The y coordinate of the image
	 * @param sourceX
	 *            The x coordinate in the source file
	 * @param sourceY
	 *            The y coordinate in the source file
	 * @param width
	 *            The width of the image
	 * @param height
	 *            The height of the image
	 */
	protected void drawImage(int targetX, int targetY, int sourceX, int sourceY, int width, int height) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		final Tessellator tess = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tess.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(targetX + 0, targetY + height, -90.0).tex((sourceX + 0) * var7, (sourceY + height) * var8).endVertex();
		vertexbuffer.pos(targetX + width, targetY + height, -90.0).tex((sourceX + width) * var7, (sourceY + height) * var8).endVertex();
		vertexbuffer.pos(targetX + width, targetY + 0, -90.0).tex((sourceX + width) * var7, (sourceY + 0) * var8).endVertex();
		vertexbuffer.pos(targetX + 0, targetY + 0, -90.0).tex((sourceX + 0) * var7, (sourceY + 0) * var8).endVertex();
		tess.draw();
	}

	/**
	 * Gets the player using the client. Used for example to check if a player
	 * is the active player.
	 * 
	 * @return The palyer
	 */
	@SideOnly(Side.CLIENT)
	protected EntityPlayer getClientPlayer() {
		if (net.minecraft.client.Minecraft.getMinecraft() != null) {
			return net.minecraft.client.Minecraft.getMinecraft().thePlayer;
		}
		return null;
	}

	/**
	 * Used to render graphical overlays on the screen
	 * 
	 * @param minecraft
	 *            The mincraft instance to use with the rendering
	 */
	@SideOnly(Side.CLIENT)
	public void renderOverlay(net.minecraft.client.Minecraft minecraft) {
	}

	/**
	 * Allows a module to stop the engines, won't stop modules using the engine
	 * though
	 * 
	 * @return True if the module is forcing the engines to stop
	 */
	public boolean stopEngines() {
		return false;
	}

	/**
	 * Allows a module to stop the vehicle from being rendered
	 * 
	 * @return False if the vehicle sohuldn't be rendered
	 */
	public boolean shouldVehicleRender() {
		return true;
	}

	/**
	 * Allows a module to tell the vehicle to use a specific push factor
	 * 
	 * @return the push factor, or -1 to use the default value
	 */
	public double getPushFactor() {
		return -1;
	}

	/**
	 * Allows a module to change the color of the vehicle
	 * 
	 * @return The color of the vehicle {Red 0.0F to 1.0F, Green 0.0F to 1.0F,
	 *         Blue 0.0F to 1.0F}
	 */
	public float[] getColor() {
		return new float[] { 1F, 1F, 1F };
	}

	/**
	 * Allows a module to change the y offset the mounted entity should be at
	 * 
	 * @param rider
	 *            The mounted entity
	 * @return The offset, or 0 if this module don't wish to change the offset.
	 */
	public float mountedOffset(Entity rider) {
		return 0F;
	}

	/**
	 * Determines if a block counts as air by the modules, for example a vehicle
	 * will count snow as air, or long grass or the like
	 * 
	 * @param pos
	 *            The coordinates of the block
	 * @return If this block counts as air by the modules
	 */
	protected boolean countsAsAir(BlockPos pos) {
		if (getVehicle().getWorld().isAirBlock(pos)) {
			return true;
		}
		Block b = getVehicle().getWorld().getBlockState(pos).getBlock();
		if (b instanceof BlockSnow) {
			return true;
		} else if (b instanceof BlockFlower) {
			return true;
		} else if (b instanceof BlockVine) {
			return true;
		}
		return false;
	}

	/**
	 * Called when the vehicle is passing a vanilla activator rail
	 * 
	 * @param x
	 *            The X coordinate of the rail
	 * @param y
	 *            The Y coordinate of the rail
	 * @param z
	 *            The Z coordinate of the rail
	 * @param active
	 *            If the rail is active or not
	 */
	public void activatedByRail(int x, int y, int z, boolean active) {
	}

	/**
	 * Return the {@link ModuleData} which represents this module
	 * 
	 * @return
	 */
	public ModuleData getModuleData() {
		return ModuleRegistry.getModuleFromId(getModuleId());
	}

	/**
	 * Allows a module to steal the whole interface, preventing any other module
	 * from using the interface. This is not meant to be permanent, use it when
	 * a lot of interface is required, then when the user clicks on something to
	 * close it then return false again.
	 * 
	 * @return
	 */
	public boolean doStealInterface() {
		return false;
	}

	protected FakePlayer getFakePlayer() {
		return FakePlayerFactory.getMinecraft((WorldServer) getVehicle().getWorld());
	}

	public boolean disableStandardKeyFunctionality() {
		return false;
	}

	public void addToLabel(ArrayList<String> label) {
	}

	public boolean onInteractFirst(EntityPlayer entityplayer) {
		return false;
	}

	public void postUpdate() {
	}

	public String getModuleName() {
		ModuleData data = getModuleData();
		return data == null ? null : data.getName();
	}

	private boolean hasSimulationInfoBeenLoaded;
	private List<SimulationInfo> simulationInfo;

	public final void initSimulationInfo() {
		if (!hasSimulationInfoBeenLoaded) {
			simulationInfo = new ArrayList<>();
			loadSimulationInfo(simulationInfo);
			hasSimulationInfoBeenLoaded = true;
		}
	}

	public void loadSimulationInfo(List<SimulationInfo> simulationInfo) {
	}

	public SimulationInfo getSimulationInfo(int id) {
		return simulationInfo.get(id);
	}

	public SimulationInfo getSimulationInfo() {
		return getSimulationInfo(0);
	}

	public void addSimulationInfo(List<SimulationInfo> simulationInfo) {
		simulationInfo.addAll(this.simulationInfo);
	}

	public boolean getBooleanSimulationInfo() {
		return ((SimulationInfoBoolean) getSimulationInfo()).getValue();
	}

	public int getIntegerSimulationInfo() {
		return ((SimulationInfoInteger) getSimulationInfo()).getValue();
	}

	public int getMultiBooleanIntegerSimulationInfo() {
		return ((SimulationInfoMultiBoolean) getSimulationInfo()).getIntegerValue();
	}

	private static final int DEFAULT_TEXTURE_SIZE = 256;
	private int textureSize = DEFAULT_TEXTURE_SIZE;

	public void setTextureSize(int val) {
		this.textureSize = val;
	}

	public void resetTextureSize() {
		setTextureSize(DEFAULT_TEXTURE_SIZE);
	}

	private static final ResourceLocation TOGGLE_TEXTURE = ResourceHelper.getResource("/gui/toggle_base.png");
	private static final int TEXTURE_SPACING = 1;
	private static final int TOGGLE_IMAGE_BORDER_SRC_X = 1;
	private static final int TOGGLE_IMAGE_BORDER_SRC_Y = 19;
	protected static final int[] TOGGLE_BOX_RECT = new int[] { 10, 21, 8, 8 };
	protected static final int[] TOGGLE_IMAGE_RECT = new int[] { 20, 16, 18, 18 };
	private ResourceLocation toggleImageTexture;

	@SideOnly(Side.CLIENT)
	protected void drawToggleBox(GuiVehicle gui, String texture, boolean enabled, int x, int y) {
		if (toggleImageTexture == null) {
			toggleImageTexture = ResourceHelper.getResource("/gui/toggle/" + texture + ".png");
		}
		ResourceHelper.bindResource(TOGGLE_TEXTURE);
		int backgroundId = enabled ? 1 : 0;
		int borderID = inRect(x, y, TOGGLE_BOX_RECT) ? 1 : 0;
		ResourceHelper.bindResource(toggleImageTexture);
		setTextureSize(16);
		drawImage(gui, TOGGLE_IMAGE_RECT[0] + 1, TOGGLE_IMAGE_RECT[1] + 1, 0, 0, TOGGLE_IMAGE_RECT[2] - 2, TOGGLE_IMAGE_RECT[3] - 2);
		resetTextureSize();
		ResourceHelper.bindResource(TOGGLE_TEXTURE);
		drawImage(gui, TOGGLE_IMAGE_RECT, TOGGLE_IMAGE_BORDER_SRC_X, TOGGLE_IMAGE_BORDER_SRC_Y + (enabled ? 0 : TEXTURE_SPACING + TOGGLE_IMAGE_RECT[3]));
		drawImage(gui, TOGGLE_BOX_RECT, TEXTURE_SPACING + (TEXTURE_SPACING + TOGGLE_BOX_RECT[2]) * backgroundId, TEXTURE_SPACING + (TEXTURE_SPACING + TOGGLE_BOX_RECT[3]) * borderID);
	}
}