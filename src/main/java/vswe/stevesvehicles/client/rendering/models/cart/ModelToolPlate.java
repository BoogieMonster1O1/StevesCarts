package vswe.stevesvehicles.client.rendering.models.cart;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.client.rendering.models.ModelVehicle;
import vswe.stevesvehicles.module.ModuleBase;

@SideOnly(Side.CLIENT)
public class ModelToolPlate extends ModelVehicle {
	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/toolPlateModel.png");

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		return TEXTURE;
	}

	@Override
	protected int getTextureWidth() {
		return 32;
	}

	@Override
	protected int getTextureHeight() {
		return 8;
	}

	public ModelToolPlate() {
		ModelRenderer drillBase = new ModelRenderer(this, 0, 0);
		addRenderer(drillBase);
		drillBase.addBox(-cartWidth / 2 + 3, // X
				-cartHeight + 1, // Y
				-2.0F, // Z
				10, // Size X
				6, // Size Y
				1, // Size Z
				0.0F);
		drillBase.setRotationPoint(-cartLength / 2 + 1, // X
				cartOnGround, // Y
				0.0F // Z
		);
		drillBase.rotateAngleY = ((float) Math.PI / 2F);
	}
}
