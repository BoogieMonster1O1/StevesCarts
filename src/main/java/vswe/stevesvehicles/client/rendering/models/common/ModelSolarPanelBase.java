package vswe.stevesvehicles.client.rendering.models.common;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.module.ModuleBase;
@SideOnly(Side.CLIENT)
public class ModelSolarPanelBase extends ModelSolarPanel {

	private static final ResourceLocation TEXTURE = ResourceHelper.getResource("/models/panelModelBase.png");

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
		return 32;
	}

	public ModelSolarPanelBase() {
		ModelRenderer base = new ModelRenderer(this, 0, 0);
		addRenderer(base);

		base.addBox(
				-1, 	    //X
				-5, 	    //Y
				-1,	 	    //Z
				2,			//Size X
				10,			//Size Y
				2,			//Size Z
				0.0F
				);

		base.setRotationPoint(
				0, 		    //X
				-4.5F,		//Y
				0			//Z
				);

		ModelRenderer moving = createMovingHolder(8,0);
		moving.addBox(
				-2, 	    //X
				-3.5F, 	    //Y
				-2,	 	    //Z
				4,			//Size X
				7,			//Size Y
				4,			//Size Z
				0.0F
				);

		ModelRenderer top = new ModelRenderer(this, 0, 12);
		fixSize(top);
		moving.addChild(top);

		top.addBox(
				-6, 	    //X
				-1.5F, 	    //Y
				-2,	 	    //Z
				12,			//Size X
				3,			//Size Y
				4,			//Size Z
				0.0F
				);

		top.setRotationPoint(
				0, 		    //X
				-5F,		//Y
				0			//Z
				);
	}
}
