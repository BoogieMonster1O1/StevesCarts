package vswe.stevesvehicles.arcade.invader;

import vswe.stevesvehicles.arcade.ArcadeGame;
import vswe.stevesvehicles.client.gui.screen.GuiVehicle;
import vswe.stevesvehicles.vehicle.VehicleBase;

public class InvaderGhast extends Unit {
	private int tentacleTextureId;
	private int shooting;
	protected boolean isPahiGhast;
	private boolean hasTarget;
	private int targetX;
	private int targetY;

	public InvaderGhast(ArcadeInvaders game, int x, int y) {
		super(game, x, y);
		tentacleTextureId = game.getModule().getVehicle().getRandom().nextInt(4);
		shooting = -10;
		if (game.canSpawnPahiGhast && !game.hasPahiGhast && game.getModule().getVehicle().getRandom().nextInt(1000) == 0) {
			isPahiGhast = true;
			game.hasPahiGhast = true;
		}
	}

	@Override
	public void draw(GuiVehicle gui) {
		if (isPahiGhast) {
			game.drawImageInArea(gui, x, y, 32, 32, 16, 16);
		} else {
			game.drawImageInArea(gui, x, y, shooting > -10 ? 16 : 0, 0, 16, 16);
		}
		game.drawImageInArea(gui, x, y + 16, 0, 16 + 8 * tentacleTextureId, 16, 8);
	}

	@Override
	public UpdateResult update() {
		if (hasTarget) {
			boolean flag = false;
			if (this.x != targetX) {
				if (this.x > targetX) {
					this.x = Math.max(targetX, this.x - 4);
				} else {
					this.x = Math.min(targetX, this.x + 4);
				}
				flag = true;
			}
			if (this.y != targetY) {
				if (this.y > targetY) {
					this.y = Math.max(targetY, this.y - 4);
				} else {
					this.y = Math.min(targetY, this.y + 4);
				}
				flag = true;
			}
			return flag ? UpdateResult.TARGET : UpdateResult.DONE;
		} else {
			if (super.update() == UpdateResult.DEAD) {
				return UpdateResult.DEAD;
			}
			if (shooting > -10) {
				if (shooting == 0) {
					ArcadeGame.playDefaultSound("mob.ghast.fireball", 0.1F, 1);
					game.projectiles.add(new Projectile(game, x + 8 - 3, y + 8 - 3, false));
				}
				shooting--;
			}
			if (game.moveDown > 0) {
				this.y += 1;
			} else {
				this.x += game.moveDirection * game.moveSpeed;
				if (y > 130) {
					return UpdateResult.GAME_OVER;
				} else if (this.x > VehicleBase.MODULAR_SPACE_WIDTH - 10 - 16 || this.x < 10) {
					return UpdateResult.TURN_BACK;
				}
			}
			if (!isPahiGhast && shooting == -10 && game.getModule().getVehicle().getRandom().nextInt(300) == 0) {
				shooting = 10;
			}
		}
		return UpdateResult.DONE;
	}

	@Override
	protected int getHitBoxWidth() {
		return 16;
	}

	@Override
	protected int getHitBoxHeight() {
		return 24;
	}

	public void setTarget(int x, int y) {
		hasTarget = true;
		targetX = x;
		targetY = y;
	}
}
