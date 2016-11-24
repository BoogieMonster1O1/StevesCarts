package stevesvehicles.common.modules.datas;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;

public class ModuleDataPair {
	private ModuleData data;
	private int count;
	private String name;
	private NBTTagCompound extraData;

	public ModuleDataPair(ModuleData data) {
		this.data = data;
		count = 1;
		name = data.getUnlocalizedName();
	}

	public int getCount() {
		return count;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void increase() {
		count++;
	}

	public boolean isContainingData(ModuleData data) {
		return this.data.equals(data);
	}

	public ModuleData getData() {
		return data;
	}

	public void setExtraData(NBTTagCompound compound) {
		extraData = compound;
	}

	@Override
	public String toString() {
		String ret = data.getCartInfoText(I18n.translateToLocal(name), extraData);
		if (count != 1) {
			ret += " x" + count;
		}
		return ret;
	}
}
