package vswe.stevesvehicles.arcade.monopoly;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import vswe.stevesvehicles.client.gui.screen.GuiVehicle;

public class Place {
	protected ArcadeMonopoly game;

	public Place(ArcadeMonopoly game) {
		this.game = game;
	}

	protected int getTextureId() {
		return -1;
	}

	public void draw(GuiVehicle gui, EnumSet<PlaceState> states) {
		int t;
		int u;
		int v;
		if (getTextureId() == -1) {
			t = 1;
			u = 0;
			v = 0;
		} else {
			t = 3 + getTextureId() / 6;
			u = getTextureId() % 3;
			v = (getTextureId() % 6) / 3;
		}
		game.loadTexture(gui, t);
		applyColorFilter(gui, states);
		game.getModule().drawImage(gui, 0, 0, ArcadeMonopoly.PLACE_WIDTH * u, ArcadeMonopoly.PLACE_HEIGHT * v, ArcadeMonopoly.PLACE_WIDTH, ArcadeMonopoly.PLACE_HEIGHT);
	}

	public void applyColorFilter(GuiVehicle gui, EnumSet<PlaceState> states) {
		if (states.contains(PlaceState.SELECTED)) {
			if (states.contains(PlaceState.HOVER)) {
				GL11.glColor4f(1F, 0.8F, 0.5F, 1.0F);
			} else {
				GL11.glColor4f(1F, 1F, 0.75F, 1.0F);
			}
		} else if (states.contains(PlaceState.MARKED)) {
			if (states.contains(PlaceState.HOVER)) {
				GL11.glColor4f(1F, 0.75F, 1F, 1.0F);
			} else {
				GL11.glColor4f(1F, 0.85F, 0.85F, 1.0F);
			}
		} else if (states.contains(PlaceState.HOVER)) {
			GL11.glColor4f(0.9F, 0.9F, 1F, 1.0F);
		}
	}

	public void drawText(GuiVehicle gui, EnumSet<PlaceState> states) {
	}

	public void drawPiece(GuiVehicle gui, Piece piece, int total, int pos, int area, EnumSet<PlaceState> states) {
		final int SIZE = 24;
		final int PADDING = 5;
		final int MARGIN = 2;
		int allowedWidth = getAllowedWidth(area) - PADDING * 2;
		int fullWidth = total * (SIZE + MARGIN) - MARGIN;
		int startX;
		int offSet;
		if (allowedWidth < fullWidth && total > 1) {
			startX = PADDING;
			offSet = ((allowedWidth - SIZE) / (total - 1));
		} else {
			startX = PADDING + (allowedWidth - fullWidth) / 2;
			offSet = SIZE + MARGIN;
		}
		game.getModule().drawImage(gui, startX + offSet * pos, getPieceYPosition(area), 256 - SIZE, piece.getV() * SIZE, SIZE, SIZE);
	}

	protected int getPieceYPosition(int area) {
		return 70;
	}

	protected int getAllowedWidth(int area) {
		return ArcadeMonopoly.PLACE_WIDTH;
	}

	public void onPiecePass(Piece piece) {
	}

	public boolean onPieceStop(Piece piece) {
		return true;
	}

	public static enum PlaceState {
		HOVER, SELECTED, MARKED, ZOOMED
	}

	public void onClick() {
	}

	public int getPieceAreaCount() {
		return 1;
	}

	public int getPieceAreaForPiece(Piece piece) {
		return 0;
	}
}
