package vswe.stevesvehicles.fancy;


import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OverheadHandler extends FancyPancyHandler {

	private ModelRenderer model;


	private Map<String, OverheadData> dataObjects;

	private class OverheadData {
		private ResourceLocation resourceLocation;
		private ThreadDownloadImageData image;

		private OverheadData(AbstractClientPlayer player) {
			resourceLocation = getDefaultResource(player);
			dataObjects.put(StringUtils.stripControlCodes(player.getName()), this);
		}
	}

	public OverheadHandler() {
		super("Overhead");
		MinecraftForge.EVENT_BUS.register(this);
		dataObjects = new HashMap<>();

		ModelBase base = new ModelBase() {};
		model = new ModelRenderer(base, 0, 0);
		model.addBox(-16, 0, 0, 32, 23, 0);
	}

	private OverheadData getData(AbstractClientPlayer player) {
		OverheadData data = dataObjects.get(StringUtils.stripControlCodes(player.getName()));
		if (data == null) {
			data = new OverheadData(player);
		}

		return data;
	}

	@Override
	public String getDefaultUrl(AbstractClientPlayer player) {
		return null;
	}

	@Override
	public ResourceLocation getDefaultResource(AbstractClientPlayer player) {
		return null;
	}

	@Override
	public ThreadDownloadImageData getCurrentTexture(AbstractClientPlayer player) {
		return getData(player).image;
	}

	@Override
	public ResourceLocation getCurrentResource(AbstractClientPlayer player) {
		return getData(player).resourceLocation;
	}

	@Override
	public void setCurrentResource(AbstractClientPlayer player, ResourceLocation resource, String url) {
		OverheadData data = getData(player);

		data.resourceLocation = resource;
		data.image = tryToDownloadFancy(resource, url);
	}

	@Override
	public LoadType getDefaultLoadType() {
		return LoadType.OVERRIDE;
	}

	@Override
	public String getDefaultUrl() {
		return null;
	}

	@SubscribeEvent
	public void render(RenderLivingEvent.Specials.Post event) {
		Entity entity = event.getEntity();
		RenderLivingBase livingBase = event.getRenderer();
		
		if (entity instanceof AbstractClientPlayer && livingBase instanceof RenderPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer)entity;
			if (!player.isInvisible()) {
				RenderPlayer render = (RenderPlayer)livingBase;
				EntityPlayer observer = Minecraft.getMinecraft().thePlayer;
				boolean isObserver = player == observer;

				double distanceSq = player.getDistanceSqToEntity(observer);
				double distanceLimit = player.isSneaking() ? RenderLivingBase.NAME_TAG_RANGE_SNEAK : RenderLivingBase.NAME_TAG_RANGE;

				if (distanceSq < distanceLimit * distanceLimit) {
					if (player.isPlayerSleeping()) {
						renderOverHead(render, player, event.getX(), event.getY() - 1.5D, event.getZ(), isObserver);
					}else{
						renderOverHead(render, player, event.getX(), event.getY(), event.getZ(), isObserver);
					}
				}

			}
		}
	}

	private void renderOverHead(RenderPlayer renderer, AbstractClientPlayer player, double x, double y, double z, boolean isObserver) {
		OverheadData data = getData(player);
		if (FancyPancyLoader.isImageReady(data.image)) {
			RenderManager renderManager = ReflectionHelper.getPrivateValue(Render.class, renderer, 1);
			//check if it's in an inventory
			if (isObserver && player.openContainer != null && renderManager.playerViewY == 180 /* set to 180 when rendering, it might be 180 at other points but won't be the end of the world*/) {
				return;
			}
			renderManager.renderEngine.bindTexture(data.resourceLocation);



			GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y + player.height + (isObserver ? 0.8F : 1.1F), (float)z);
			GL11.glNormal3f(0, 1, 0);
			GL11.glRotatef(-renderManager.playerViewY, 0, 1, 0);
			GL11.glRotatef(renderManager.playerViewX, 1, 0, 0);

			GL11.glScalef(-1, -1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_BLEND);

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
			model.render(0.015F);



			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GL11.glPopMatrix();
		}
	}



}
