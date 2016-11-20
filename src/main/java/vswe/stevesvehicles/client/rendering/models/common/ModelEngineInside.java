package vswe.stevesvehicles.client.rendering.models.common;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesvehicles.client.ResourceHelper;
import vswe.stevesvehicles.module.ModuleBase;
import vswe.stevesvehicles.module.common.engine.ModuleCoalBase;

@SideOnly(Side.CLIENT)
public class ModelEngineInside extends ModelEngineBase {
	private static final ResourceLocation[] TEXTURES;
	static {
		TEXTURES = new ResourceLocation[5];
		TEXTURES[0] = ResourceHelper.getResource("/models/engine_back.png");
		for (int i = 1; i < TEXTURES.length; i++) {
			TEXTURES[i] = ResourceHelper.getResource("/models/engine_fire_" + i + ".png");
		}
	}

	@Override
	public ResourceLocation getResource(ModuleBase module) {
		int index = module == null ? 0 : ((ModuleCoalBase) module).getFireIndex();
		return TEXTURES[index];
	}

	@Override
	protected int getTextureWidth() {
		return 8;
	}

	@Override
	protected int getTextureHeight() {
		return 4;
	}

	public ModelEngineInside() {
		ModelRenderer back = new ModelRenderer(this, 0, 0);
		anchor.addChild(back);
		fixSize(back);
		back.addBox(-3.5F, // X
				-2F, // Y
				0.0F, // Z
				7, // Size X
				4, // Size Y
				0, // Size Z
				0.0F);
		back.setRotationPoint(0, // X
				-0.5F, // Y
				0.3F // Z
				);
	}
}
