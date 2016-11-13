package vswe.stevesvehicles.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import vswe.stevesvehicles.buoy.EntityBuoy;
import vswe.stevesvehicles.client.gui.screen.GuiBuoy;
import vswe.stevesvehicles.network.DataReader;
import vswe.stevesvehicles.network.DataWriter;
import vswe.stevesvehicles.network.PacketHandler;
import vswe.stevesvehicles.network.PacketType;
import vswe.stevesvehicles.tileentity.TileEntityBase;


public class ContainerBuoy extends ContainerBase {
	private EntityBuoy entityBuoy;

	public ContainerBuoy(EntityBuoy entityBuoy) {
		this.entityBuoy = entityBuoy;
	}

	@Override
	public IInventory getMyInventory() {
		return null;
	}

	@Override
	public TileEntityBase getTileEntity() {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistanceSqToEntity(entityBuoy) <= 64;
	}

	@Override
	public void addCraftingToCrafters(ICrafting player) {
		super.addCraftingToCrafters(player);
		if (player instanceof EntityPlayer) {
			DataWriter dw = PacketHandler.getDataWriter(PacketType.BUOY);

			PacketHandler.sendPacketToPlayer(dw, (EntityPlayer)player);
		}
	}

	@SideOnly(Side.CLIENT)
	public GuiBuoy gui;

	public void receiveInfo(DataReader dr, boolean server) {
		if (server) {
			int entityId = dr.readSignedInteger();
			Entity entity = entityBuoy.worldObj.getEntityByID(entityId);
			EntityBuoy otherBuoy = null;
			if (entity instanceof EntityBuoy && !entity.isDead) {
				otherBuoy = (EntityBuoy)entity;
			}

			boolean next = dr.readBoolean();


			EntityBuoy oldNext = entityBuoy.getBuoy(next);
			if (oldNext != null) {
				oldNext.setBuoy(null, !next);
			}
			entityBuoy.setBuoy(otherBuoy, next);

			if (otherBuoy != null) {
				EntityBuoy oldPrev = otherBuoy.getBuoy(!next);
				if (oldPrev != null) {
					oldPrev.setBuoy(null, next);
				}
				otherBuoy.setBuoy(entityBuoy, !next);
			}


		}
	}
}
