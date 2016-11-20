package vswe.stevesvehicles.module.common.addon.recipe;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.container.slots.ISpecialSlotSize;
import vswe.stevesvehicles.container.slots.SlotBase;
import vswe.stevesvehicles.container.slots.SlotChest;
import vswe.stevesvehicles.localization.entry.module.LocalizationProduction;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.addon.ModuleAddon;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.network.DataWriter;
import vswe.stevesvehicles.tileentity.TileEntityCargo;
import vswe.stevesvehicles.vehicle.VehicleBase;

public abstract class ModuleRecipe extends ModuleAddon {
	public ModuleRecipe(VehicleBase vehicleBase) {
		super(vehicleBase);
		target = 3;
		dirty = true;
		allTheSlots = new ArrayList<>();
		outputSlots = new ArrayList<>();
	}

	private int target;
	protected boolean dirty;

	protected abstract int getLimitStartX();

	protected abstract int getLimitStartY();

	protected static final int WORK_COOL_DOWN = 40;
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/gui/recipe.png");

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackground(GuiVehicle gui, int x, int y) {
		if (canUseAdvancedFeatures()) {
			int[] area = getArea();
			ResourceHelper.bindResource(TEXTURE);
			drawImage(gui, area[0] - 2, area[1] - 2, 1, 1, 20, 20);
			if (mode == 1) {
				for (int i = 0; i < 3; i++) {
					drawControlRect(gui, x, y, i);
				}
			} else {
				drawControlRect(gui, x, y, 1);
			}
		}
	}

	private void drawControlRect(GuiVehicle gui, int x, int y, int i) {
		int v = 1 + i * 12;
		int[] rect = getControlRect(i);
		drawImage(gui, rect, 20 + (inRect(x, y, rect) ? 25 : 2), v);
	}

	private int[] getControlRect(int i) {
		return new int[] { getLimitStartX(), getLimitStartY() + 12 * i, 22, 11 };
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeground(GuiVehicle gui) {
		if (canUseAdvancedFeatures()) {
			String str;
			switch (mode) {
				case 0:
					str = LocalizationProduction.INFINITE.translate();
					break;
				case 1:
					str = String.valueOf(maxItemCount);
					break;
				default:
					str = "X";
			}
			drawString(gui, str, getControlRect(1), 0x404040);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackgroundItems(GuiVehicle gui, int x, int y) {
		if (canUseAdvancedFeatures()) {
			ItemStack icon;
			if (isTargetInvalid()) {
				icon = new ItemStack(Items.MINECART, 1);
			} else {
				icon = TileEntityCargo.itemSelections.get(target).getIcon();
			}
			int[] area = getArea();
			drawItemInInterface(gui, icon, area[0], area[1]);
		}
	}

	private boolean isTargetInvalid() {
		return target < 0 || target >= TileEntityCargo.itemSelections.size() || TileEntityCargo.itemSelections.get(target).getValidSlot() == null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawMouseOver(GuiVehicle gui, int x, int y) {
		if (canUseAdvancedFeatures()) {
			String str = LocalizationProduction.OUTPUT.translate() + "\n" + LocalizationProduction.SELECTION.translate() + ": ";
			if (isTargetInvalid()) {
				str += LocalizationProduction.INVALID.translate();
			} else {
				str += TileEntityCargo.itemSelections.get(target).getName();
			}
			drawStringOnMouseOver(gui, str, x, y, getArea());
			for (int i = 0; i < 3; i++) {
				if (i == 1) {
					str = LocalizationProduction.CHANGE_MODE.translate() + "\n" + LocalizationProduction.SELECTION.translate() + ": ";
					switch (mode) {
						case 0:
							str += LocalizationProduction.NO_LIMIT.translate();
							break;
						case 1:
							str += LocalizationProduction.LIMIT.translate();
							break;
						default:
							str += LocalizationProduction.DISABLED.translate();
					}
				} else if (mode != 1) {
					str = null;
				} else {
					str = LocalizationProduction.CHANGE_LIMIT.translate(i == 0 ? "0" : "1") + "\n" + LocalizationProduction.CHANGE_LIMIT_TEN.translate() + "\n" + LocalizationProduction.CHANGE_LIMIT_STACK.translate();
				}
				if (str != null) {
					drawStringOnMouseOver(gui, str, x, y, getControlRect(i));
				}
			}
		}
	}

	protected abstract int[] getArea();

	@Override
	public int numberOfGuiData() {
		return canUseAdvancedFeatures() ? 3 : 0;
	}

	@Override
	protected void checkGuiData(Object[] info) {
		if (canUseAdvancedFeatures()) {
			updateGuiData(info, 0, (short) target);
			updateGuiData(info, 1, (short) mode);
			updateGuiData(info, 2, (short) maxItemCount);
		}
	}

	@Override
	public void receiveGuiData(int id, short data) {
		if (canUseAdvancedFeatures()) {
			if (id == 0) {
				target = data;
			} else if (id == 1) {
				mode = data;
			} else if (id == 2) {
				maxItemCount = data;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void mouseClicked(GuiVehicle gui, int x, int y, int button) {
		if (canUseAdvancedFeatures()) {
			if (inRect(x, y, getArea())) {
				DataWriter dw = getDataWriter(PacketId.TARGET);
				dw.writeBoolean(button == 0);
				sendPacketToServer(dw);
			}
			for (int i = 0; i < 3; i++) {
				if (mode == 1 || i == 1) {
					if (inRect(x, y, getControlRect(i))) {
						if (i == 1) {
							DataWriter dw = getDataWriter(PacketId.MODE);
							dw.writeBoolean(button == 0);
							sendPacketToServer(dw);
						} else {
							DataWriter dw = getDataWriter(PacketId.MAX_COUNT);
							dw.writeBoolean(i == 0);
							dw.writeBoolean(GuiScreen.isCtrlKeyDown());
							dw.writeBoolean(GuiScreen.isShiftKeyDown());
							sendPacketToServer(dw);
						}
						break;
					}
				}
			}
		}
	}

	private DataWriter getDataWriter(PacketId id) {
		DataWriter dw = getDataWriter();
		dw.writeEnum(id);
		return dw;
	}

	public enum PacketId {
		TARGET, MODE, MAX_COUNT
	}

	@Override
	protected void receivePacket(DataReader dr, EntityPlayer player) {
		if (canUseAdvancedFeatures()) {
			PacketId id = dr.readEnum(PacketId.class);
			switch (id) {
				case TARGET:
					dirty = true;
					changeTarget(dr.readBoolean());
					break;
				case MODE:
					if (dr.readBoolean()) {
						if (++mode > 2) {
							mode = 0;
						}
					} else {
						if (--mode < 0) {
							mode = 2;
						}
					}
					break;
				case MAX_COUNT:
					int dif = dr.readBoolean() ? 1 : -1;
					if (dr.readBoolean()) {
						dif *= 64;
					} else if (dr.readBoolean()) {
						dif *= 10;
					}
					maxItemCount = Math.min(Math.max(1, maxItemCount + dif), 999);
					break;
			}
		}
	}

	private void changeTarget(boolean up) {
		if (!up) {
			if (--target < 0) {
				target = TileEntityCargo.itemSelections.size() - 1;
			}
		} else if (++target >= TileEntityCargo.itemSelections.size()) {
			target = 0;
		}
		if (isTargetInvalid()) {
			changeTarget(up);
		}
	}

	protected abstract boolean canUseAdvancedFeatures();

	protected Class getValidSlot() {
		if (isTargetInvalid()) {
			return null;
		} else {
			return TileEntityCargo.itemSelections.get(target).getValidSlot();
		}
	}

	@Override
	protected void load(NBTTagCompound tagCompound) {
		if (canUseAdvancedFeatures()) {
			target = tagCompound.getByte("Target");
			mode = tagCompound.getByte("Mode");
			maxItemCount = tagCompound.getShort("MaxItems");
		}
	}

	@Override
	protected void save(NBTTagCompound tagCompound) {
		if (canUseAdvancedFeatures()) {
			tagCompound.setByte("Target", (byte) target);
			tagCompound.setByte("Mode", (byte) mode);
			tagCompound.setShort("MaxItems", (short) maxItemCount);
		}
	}

	protected ArrayList<SlotBase> inputSlots;
	protected ArrayList<SlotBase> outputSlots;
	protected ArrayList<SlotBase> allTheSlots;

	protected void prepareLists() {
		if (inputSlots == null) {
			inputSlots = new ArrayList<>();
			for (ModuleBase module : getVehicle().getModules()) {
				if (module.getSlots() != null) {
					for (SlotBase slot : module.getSlots()) {
						if (slot instanceof SlotChest) {
							inputSlots.add(slot);
						}
					}
				}
			}
		}
		if (dirty) {
			allTheSlots.clear();
			outputSlots.clear();
			Class validSlot = getValidSlot();
			for (ModuleBase module : getVehicle().getModules()) {
				if (module.getSlots() != null) {
					for (SlotBase slot : module.getSlots()) {
						if (validSlot.isInstance(slot)) {
							outputSlots.add(slot);
							allTheSlots.add(slot);
						} else if (slot instanceof SlotChest) {
							allTheSlots.add(slot);
						}
					}
				}
			}
			dirty = false;
		}
	}

	private int maxItemCount = 1;
	private int mode;
	// 0 - infinite
	// 1 - limit
	// 2 - disabled

	protected boolean canCraftMoreOfResult(ItemStack result) {
		if (mode == 0) {
			return true;
		} else if (mode == 2) {
			return false;
		} else {
			int count = 0;
			for (SlotBase outputSlot : outputSlots) {
				ItemStack item = outputSlot.getStack();
				if (item != null && item.isItemEqual(result) && ItemStack.areItemStackTagsEqual(item, result)) {
					if (outputSlot instanceof ISpecialSlotSize) {
						count += ((ISpecialSlotSize) outputSlot).getItemSize();
					} else {
						count += item.getCount();
					}
					if (count >= maxItemCount) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
