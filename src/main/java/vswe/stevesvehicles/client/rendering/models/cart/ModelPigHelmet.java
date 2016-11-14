package vswe.stevesvehicles.client.rendering.models.cart;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.hull.HullPig;

@SideOnly(Side.CLIENT)
public class ModelPigHelmet extends ModelVehicle {
	@Override
	public ResourceLocation getResource(ModuleBase module) {
		HullPig pig = (HullPig) module;
		return pig.getHelmetResource(isOverlay);
	}

	@Override
	protected int getTextureHeight() {
		return 32;
	}

	private final boolean isOverlay;

	public ModelPigHelmet(boolean isOverlay) {
		this.isOverlay = isOverlay;
		ModelRenderer helmet = new ModelRenderer(this, 0, 0);
		addRenderer(helmet);
		helmet.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, 0.0F);
		helmet.setRotationPoint(-12.2F + (isOverlay ? 0.2F : 0F), -5.4F, 0.0F);
		helmet.rotateAngleY = (float) Math.PI / 2;
	}

	@Override
	public void render(ModuleBase module, float yaw, float pitch, float roll, float multiplier, float partialTime) {
		if (module == null) {
			return;
		}
		HullPig pig = (HullPig) module;
		if (!pig.hasHelmet() || (isOverlay)) {
			return;
		}
		final float sizeMultiplier = 1F + 1F / 16F + 1F / 32F + (isOverlay ? 1F / 48F : 0);
		GL11.glScalef(sizeMultiplier, sizeMultiplier, sizeMultiplier);
		if (pig.hasHelmetColor(isOverlay)) {
			int color = pig.getHelmetColor(isOverlay);
			float red = (color >> 16 & 255) / 255.0F;
			float green = (color >> 8 & 255) / 255.0F;
			float blue = (color & 255) / 255.0F;
			GL11.glColor3f(red, green, blue);
		}
		super.render(module, yaw, pitch, roll, multiplier, partialTime);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glScalef(1 / sizeMultiplier, 1 / sizeMultiplier, 1 / sizeMultiplier);
	}
}
