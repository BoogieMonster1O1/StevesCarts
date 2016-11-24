package stevesvehicles.client.fancy;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CapeHandler extends FancyPancyHandler {
	public CapeHandler() {
		super("Cape");
	}

	@Override
	public String getDefaultUrl(AbstractClientPlayer player) {
		return null;
		// return
		// AbstractClientPlayer.getCapeUrl(StringUtils.stripControlCodes(player.getDisplayName()));
	}

	@Override
	public ResourceLocation getDefaultResource(AbstractClientPlayer player) {
		return null;
		// return
		// AbstractClientPlayer.getLocationCape(StringUtils.stripControlCodes(player.getDisplayName()));
	}

	@Override
	public ThreadDownloadImageData getCurrentTexture(AbstractClientPlayer player) {
		return null;
		// return player.getTextureCape();
	}

	@Override
	public ResourceLocation getCurrentResource(AbstractClientPlayer player) {
		return player.getLocationCape();
	}

	@Override
	public void setCurrentResource(AbstractClientPlayer player, ResourceLocation resource, String url) {
		ReflectionHelper.setPrivateValue(AbstractClientPlayer.class, player, resource, 4);
		ReflectionHelper.setPrivateValue(AbstractClientPlayer.class, player, tryToDownloadFancy(resource, url), 2);
	}

	@Override
	public LoadType getDefaultLoadType() {
		return LoadType.KEEP;
	}

	@Override
	public String getDefaultUrl() {
		return "http://skins.minecraft.net/MinecraftCloaks/";
	}
}
