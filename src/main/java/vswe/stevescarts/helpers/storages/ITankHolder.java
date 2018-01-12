package vswe.stevescarts.helpers.storages;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevescarts.guis.GuiBase;

import javax.annotation.Nonnull;

public interface ITankHolder {
	@Nonnull
	ItemStack getInputContainer(final int p0);

	void setInputContainer(final int p0, ItemStack stack);

	void addToOutputContainer(final int p0, @Nonnull ItemStack p1);

	void onFluidUpdated(final int p0);

	@SideOnly(Side.CLIENT)
	void drawImage(final int p0, final GuiBase p1, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8);
}
