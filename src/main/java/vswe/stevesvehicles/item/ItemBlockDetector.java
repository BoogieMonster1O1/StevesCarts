package vswe.stevesvehicles.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import vswe.stevesvehicles.tab.CreativeTabLoader;
import vswe.stevesvehicles.tileentity.detector.DetectorType;

public class ItemBlockDetector extends ItemBlock {
	public ItemBlockDetector(Block b) {
		super(b);
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(CreativeTabLoader.blocks);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		if (item != null && item.getItemDamage() >= 0 && item.getItemDamage() < DetectorType.values().length) {
			DetectorType detectorType = DetectorType.getTypeFromInt(item.getItemDamage());
			return detectorType.getUnlocalizedName();
		}
		return "item.unknown";
	}

	@Override
	public int getMetadata(int dmg) {
		return dmg;
	}
}
