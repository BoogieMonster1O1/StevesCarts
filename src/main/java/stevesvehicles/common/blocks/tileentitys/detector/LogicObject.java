package stevesvehicles.common.blocks.tileentitys.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevesvehicles.api.network.DataReader;
import stevesvehicles.client.gui.screen.GuiDetector;
import stevesvehicles.common.blocks.tileentitys.TileEntityDetector;
import stevesvehicles.common.blocks.tileentitys.detector.modulestate.ModuleState;
import stevesvehicles.common.blocks.tileentitys.detector.modulestate.registry.ModuleStateRegistry;
import stevesvehicles.common.modules.datas.ModuleData;
import stevesvehicles.common.modules.datas.registries.ModuleRegistry;
import stevesvehicles.common.network.PacketHandler;
import stevesvehicles.common.network.packets.PacketDetector;
import stevesvehicles.common.vehicles.VehicleBase;

public abstract class LogicObject {
	private ArrayList<LogicObject> children;
	protected byte id;
	protected LogicObject parent;
	protected int x;
	protected int y;
	protected int level;
	protected short data;

	public LogicObject(byte id, short data) {
		this.id = id;
		this.data = data;
		children = new ArrayList<>();
	}

	public void setParentAndUpdate(LogicObject parent, TileEntityDetector detector) throws IOException {
		if (parent != null) {
			List<LogicObject> objects = new ArrayList<>();
			fillTree(objects, parent);
			PacketHandler.sendToServer(new PacketDetector(detector, objects));
		} else {
			PacketHandler.sendToServer(new PacketDetector(detector, id));
		}
	}

	public void fillTree(List<LogicObject> objects, LogicObject parent) {
		this.parent = parent;
		objects.add(this);
		for (LogicObject child : children) {
			child.fillTree(objects, this);
		}
	}

	public void setParent(LogicObject parent) {
		if (this.parent != null) {
			this.parent.children.remove(this);
		}
		this.parent = parent;
		if (this.parent != null && this.parent.hasRoomForChild()) {
			this.parent.children.add(this);
		}
	}

	public ArrayList<LogicObject> getChildren() {
		return children;
	}

	public LogicObject getParent() {
		return parent;
	}

	public byte getId() {
		return id;
	}

	public void setX(int val) {
		this.x = val;
	}

	public void setY(int val) {
		this.y = val;
	}

	public void setXCenter(int val) {
		setX(val - getWidth() / 2);
	}

	public void setYCenter(int val) {
		setY(val - getHeight() / 2);
	}

	@SideOnly(Side.CLIENT)
	public void draw(GuiDetector gui, int mouseX, int mouseY, int x, int y) {
		generatePosition(x - 100 / 2, y, 100, 0);
		draw(gui, mouseX, mouseY);
	}

	@SideOnly(Side.CLIENT)
	public void draw(GuiDetector gui, int mouseX, int mouseY) {
		if (parent != null && parent.getMaxChildCount() > 1) {
			int px1 = gui.getGuiLeft() + x;
			int py1 = gui.getGuiTop() + y;
			int px2 = gui.getGuiLeft() + parent.x;
			int py2 = gui.getGuiTop() + parent.y;
			// the middle of the parent
			py2 += 5;
			// the middle of the child
			px1 += getWidth() / 2;
			boolean tooClose = false;
			// the right side of the parent
			if (x > parent.x) {
				px2 += 20;
				if (px1 < px2) {
					tooClose = true;
				}
			} else if (px1 > px2) {
				tooClose = true;
			}
			if (!tooClose) {
				Gui.drawRect(px1, py2, px2, py2 + 1, 0xFF404040);
				Gui.drawRect(px1, py1, px1 + 1, py2, 0xFF404040);
				GL11.glColor4f(1F, 1F, 1F, 1F);
			}
		}
		for (LogicObject child : children) {
			child.draw(gui, mouseX, mouseY);
		}
	}

	public void generatePosition(int x, int y, int w, int level) {
		setXCenter(x + w / 2);
		setYCenter(y);
		this.level = level;
		int max = getMaxChildCount();
		for (int i = 0; i < children.size(); i++) {
			children.get(i).generatePosition(x + (w / max) * i, y + (children.get(i).getHeight()), w / max, level + (children.get(i).getMaxChildCount() > 1 ? 1 : 0));
		}
	}

	public boolean evaluateLogicTree(TileEntityDetector detector, VehicleBase vehicle, int depth) {
		return depth < 1000;
	}

	protected int getMaxChildCount() {
		return 0;
	}

	public boolean isChildValid(LogicObject child) {
		return false;
	}

	public boolean isValidAsChild(LogicObjectOperator parent) {
		return true;
	}

	public boolean hasRoomForChild() {
		return children.size() < getMaxChildCount();
	}

	public int[] getRect() {
		return new int[] { x, y, getWidth(), getHeight() };
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LogicObject) {
			LogicObject logic = (LogicObject) obj;
			return logic.id == id && ((logic.parent == null && parent == null) || (logic.parent != null && parent != null && logic.parent.id == parent.id)) && logic.getClass() == getClass() && logic.data == data;
		} else {
			return false;
		}
	}

	public boolean canBeRemoved() {
		return true;
	}

	protected int getWidth() {
		return 16;
	}

	protected int getHeight() {
		return 16;
	}

	public abstract LogicObject copy(LogicObject parent);

	public abstract String getName();

	public abstract int getType();

	public static void createObject(TileEntityDetector detector, byte id, DataReader dr) throws IOException {
		int parentId = dr.readByte();
		int type = dr.readByte();
		int dataId = dr.readShort();
		createObject(detector, id, (byte) parentId, (byte) type, (short) dataId);
	}

	private static void createObject(TileEntityDetector detector, byte id, byte parentId, byte type, short dataId) {
		LogicObject newObject = null;
		switch (type) {
			case 0:
				ModuleData moduleData = ModuleRegistry.getModuleFromId(dataId);
				if (moduleData != null) {
					newObject = new LogicObjectModule(id, moduleData);
				}
				break;
			case 1:
				OperatorObject operatorObject = OperatorObject.getAllOperators().get((byte) dataId);
				if (operatorObject != null) {
					newObject = new LogicObjectOperator(id, operatorObject);
				}
				break;
			case 2:
				ModuleState moduleState = ModuleStateRegistry.getStateFromId(dataId);
				if (moduleState != null) {
					newObject = new LogicObjectState(id, moduleState);
				}
				break;
		}
		if (newObject != null) {
			LogicObject parent = detector.getObjectFromId(detector.mainObj, parentId);
			if (parent != null) {
				newObject.setParent(parent);
			}
		}
	}

	public static void createObject(TileEntityDetector detector, short info, short data) {
		byte type = (byte) (info & 3);
		byte id = (byte) ((info >> 2) & 127);
		byte parentId = (byte) ((info >> 9) & 127);
		createObject(detector, id, parentId, type, data);
	}

	public short getInfoShort() {
		return (short) ((getType() & 3) | ((id & 127) << 2) | ((parent.getId() & 127) << 9));
	}

	public short getData() {
		return data;
	}
}
