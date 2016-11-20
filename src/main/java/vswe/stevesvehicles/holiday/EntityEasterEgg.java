package vswe.stevesvehicles.holiday;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEasterEgg extends EntityEgg {
	public EntityEasterEgg(World world) {
		super(world);
	}

	public EntityEasterEgg(World world, EntityLivingBase thrower) {
		super(world, thrower);
	}

	public EntityEasterEgg(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	protected void onImpact(RayTraceResult data) {
		if (data.entityHit != null) {
			data.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0);
		}
		if (!this.world.isRemote) {
			if (this.rand.nextInt(8) == 0) {
				if (rand.nextInt(32) == 0) {
					EntityPig entitypig = new EntityPig(world);
					entitypig.setGrowingAge(-24000);
					entitypig.setLocationAndAngles(posX, posY, posZ, this.rotationYaw, 0.0F);
					world.spawnEntityInWorld(entitypig);
				} else {
					EntityChicken entitychicken = new EntityChicken(world);
					entitychicken.setGrowingAge(-24000);
					entitychicken.setLocationAndAngles(posX, posY, posZ, this.rotationYaw, 0.0F);
					world.spawnEntityInWorld(entitychicken);
				}
			} else {
				List<ItemStack> items = GiftItem.generateItems(rand, GiftItem.EasterList, 25 + rand.nextInt(300), 1);
				for (ItemStack item : items) {
					EntityItem eItem = new EntityItem(world, posX, posY, posZ, item);
					eItem.motionX = rand.nextGaussian() * 0.05F;
					eItem.motionY = rand.nextGaussian() * 0.25F;
					eItem.motionZ = rand.nextGaussian() * 0.05F;
					world.spawnEntityInWorld(eItem);
				}
			}
		}
		for (int i = 0; i < 8; i++) {
			// noinspection SpellCheckingInspection
			world.spawnParticle(EnumParticleTypes.SNOWBALL, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
		}
		if (!world.isRemote) {
			setDead();
		}
	}
}
