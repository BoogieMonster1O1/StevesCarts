package stevesvehicles.common.arcade.tracks;

import java.util.ArrayList;

import stevesvehicles.api.modules.data.ILocalizedText;
import stevesvehicles.client.localization.entry.arcade.stories.LocalizationTheBeginning;

public class TrackStory {
	public static ArrayList<TrackStory> stories;
	static {
		stories = new ArrayList<>();
		// A new day
		TrackLevel newday = TrackLevel.loadMap(new byte[] { 0, 9, 65, 32, 110, 101, 119, 32, 100, 97, 121, 1, 103, 54, 33, 0, 16, 33, 0, 16, 34, 0, 16, 35, 0, 16, 36, 0, 16, 37, 0, 64, 38, 0, 32, 70, 0, 16, -125, 0, 16, -124, 0, 16, -123, 0, 80, -122, 0,
				32, 102, 0, 16, -126, 0, 48, -127, 0, 32, -95, 0, 16, -30, 0, 16, -29, 0, 16, -28, 0, 16, -27, 0, 16, -26, 0, 96, -31, 0, 32, -63 });
		newday.setName(LocalizationTheBeginning.LEVEL_1);
		newday.addMessage(new LevelMessage(12, 1, 10, LocalizationTheBeginning.MISSION));
		newday.addMessage(new LevelMessage(12, 4, 10, LocalizationTheBeginning.START).setMustNotBeRunning());
		newday.addMessage(new LevelMessage(12, 4, 10, LocalizationTheBeginning.STOP).setMustBeRunning());
		newday.addMessage(new LevelMessage(12, 7, 10, LocalizationTheBeginning.MAP).setMustBeDone());
		// The Operator
		TrackLevel operator = TrackLevel.loadMap(new byte[] { 0, 12, 84, 104, 101, 32, 79, 112, 101, 114, 97, 116, 111, 114, 1, -95, 118, 99, 0, 16, 99, 0, 16, 100, 0, 16, 101, 0, 16, 102, 1, -64, 103, 0, 16, 104, 0, 16, 105, 0, 16, 106, 0, 16, 107, 0, 16,
				108, 0, 16, 109, 0, 16, -56, 0, 16, -55, 0, 96, -57, 0, 32, -121, 0, 32, -89, 0, 16, -54, 0, 16, -53, 0, 16, -52, 0, 16, -51, 0, 80, -50, 0, 32, -114, 0, 32, -82, 0, -96, 110, 0, 32, 46, 0, 32, 78 });
		operator.setName(LocalizationTheBeginning.LEVEL_2);
		operator.addMessage(new LevelMessage(16, 1, 10, LocalizationTheBeginning.TRACK_OPERATOR));
		operator.addMessage(new LevelMessage(16, 6, 10, LocalizationTheBeginning.GOOD_JOB).setMustBeDone());
		operator.addMessage(new LevelMessage(16, 6, 10, LocalizationTheBeginning.CHANGE_JUNCTION).setMustBeStill().setMustNotBeDone());
		// Escape the loop
		TrackLevel loop = TrackLevel.loadMap(new byte[] { 0, 15, 69, 115, 99, 97, 112, 101, 32, 116, 104, 101, 32, 108, 111, 111, 112, 3, -120, 76, -108, 0, 16, -112, 0, 16, -111, 0, 16, -110, 0, 16, -109, 0, 16, -108, 0, 16, -107, 0, 16, -106, 0, 0, -113,
				0, 16, -114, 1, 32, -115, 0, 32, -83, 0, 32, -51, 0, -48, -19, 0, 16, -18, 0, 80, -17, 0, 32, -81, 0, 32, -49, 0, 16, -22, 0, 16, -21, 0, 16, -20, 0, 32, 77, 0, 32, 109, 0, 112, 45, 0, 16, 46, 0, 64, 47, 0, 32, 79, 0, 32, 111, 0, 16, 42, 0,
				16, 43, 0, 16, 44, 1, 48, 41, 0, 16, 37, 0, 16, 38, 0, 16, 39, 0, 16, 40, 0, 48, 36, 0, 32, 68, 0, 32, 100, 0, 32, 73, 0, 32, 105, 1, 16, -119, 0, 32, -87, 1, -128, -55, 0, -128, -23, 0, 33, 9, 1, 32, -124, 0, 16, -123, 0, 16, -122, 0, 16,
				-121, 0, 16, -120, 0, 16, -59, 0, 16, -58, 0, 16, -57, 0, 16, -56, 0, 96, -60, 0, 32, -92 });
		loop.setName(LocalizationTheBeginning.LEVEL_3);
		loop.addMessage(new LevelMessage(17, 6, 9, LocalizationTheBeginning.BLAST));
		// Hard as steel
		TrackLevel steel = TrackLevel.loadMap(new byte[] { 0, 13, 72, 97, 114, 100, 32, 97, 115, 32, 115, 116, 101, 101, 108, 4, -11, 90, 56, 0, 32, 56, 0, 32, 88, 0, 32, 120, 0, 32, -104, 0, 80, -72, 0, 16, -73, 0, 16, -85, 0, -128, -74, 0, 32, -42, 0,
				32, -10, 0, 32, -106, 0, 32, 118, 0, 64, 86, 0, 16, -84, 0, 80, -83, 0, 32, -115, 0, 32, 109, 1, 64, 77, 0, 16, 80, 0, 16, 79, 0, 16, 78, 0, -80, 81, 0, 32, 113, 0, 81, 22, 0, 17, 21, 0, 17, 20, 0, -32, -47, 0, 32, -15, 1, 97, 17, 0, 17,
				18, 0, 17, 19, 0, 17, 16, 0, 17, 14, 0, 16, -48, 0, 32, -79, 0, 16, -50, 0, 48, -51, 0, 32, -19, 0, 48, -113, 0, 32, -81, 0, 16, -112, 0, 0, -111, 1, 81, 13, 0, -111, 15, 0, 0, -49, 0, 32, -17, 0, 17, 12, 0, 17, 11, 0, 17, 10, 0, 16, 76, 0,
				16, 75, 0, 16, 74, 0, 16, 73, 1, -80, 72, 0, 16, 71, 0, -60, -90, 0, 36, -58, 0, 36, -26, 0, 36, -122, 0, 36, 102, 0, 36, -56, 0, 20, -89, 0, 36, -24, 1, -27, 8, 0, 21, 7, 0, 101, 6, 0, 21, 9, 0, -28, -88, 0, 36, -120, 0, 36, 104, 0, 52,
				70, 1, -76, 83, 0, -44, -109, 0, 20, 85, 0, 20, 84, 0, 20, 82, 0, 20, -110, 0, 20, -108, 0, 36, 115 });
		steel.setName(LocalizationTheBeginning.LEVEL_4);
		steel.addMessage(new LevelMessage(0, 1, 5, LocalizationTheBeginning.STEEL));
		// Moving world
		TrackLevel moving = TrackLevel.loadMap(new byte[] { 0, 12, 77, 111, 118, 105, 110, 103, 32, 119, 111, 114, 108, 100, 3, 53, 110, -89, 0, 16, -89, 0, 36, 107, 0, 36, -117, 0, 84, -85, 0, 20, 74, 0, 20, 76, 0, 16, 71, 8, 34, -123, 75, 0, 0, 96, -91,
				0, 16, -90, 0, 32, 101, 0, 48, 69, 0, 16, 70, 0, 16, 79, 1, 64, 80, 0, 32, 112, 0, 16, 81, 1, 52, 77, 0, 20, 78, 0, 36, 109, 0, 32, -115, 0, 32, -83, 1, 64, 82, 1, 64, 84, 0, 32, 114, 0, 32, 116, 0, 16, 83, 0, 64, 86, 0, 16, 85, 1, 96, -76,
				1, 96, -78, 0, 96, -80, 0, 16, -79, 0, 16, -77, 0, 16, -75, 0, 80, -74, 0, -12, -106, 1, -92, 118, 0, 20, 119, 0, 20, -105, 16, 34, -112, 118, -96, 44, 1, 16, 34, -110, 82, -20, -96, 2, 12, 34, -108, -106, -102, 0, 0, -76, 75, 1, 100, -88,
				0, 20, -87, 0, 20, -86, 0, 20, 73, 0, 36, -120, 0, 36, 104, 1, 68, 72 });
		moving.setName(LocalizationTheBeginning.LEVEL_5);
		moving.addMessage(new LevelMessage(1, 7, 25, LocalizationTheBeginning.DETECTOR));
		// The code lock
		TrackLevel lock = TrackLevel.loadMap(new byte[] { 0, 13, 84, 104, 101, 32, 99, 111, 100, 101, 32, 108, 111, 99, 107, 6, -72, -97, 32, 0, 17, 32, 0, 17, 33, 0, 17, 34, 0, 17, 42, 0, 17, 43, 0, 17, 40, 0, 17, 39, 0, 17, 37, 0, 17, 36, 0, -111, 35, 0,
				-111, 38, 0, -111, 41, 0, 17, 45, 1, -43, 46, 1, -43, 47, 1, -43, 48, 0, 21, 49, 0, 21, 50, 0, 85, 51, 0, 37, 19, 0, 33, 3, 0, 32, -29, 0, 33, 6, 0, 32, -26, 0, 33, 9, 0, 32, -23, 8, 34, -61, 46, 1, 0, 16, -92, 0, 16, -91, 12, 2, -90, 47,
				97, 2, 0, 32, -58, 0, 32, -122, 0, 112, -93, 0, 16, -94, 0, 96, -95, 0, 32, -127, 0, 16, -89, 0, 16, -88, 1, 16, -87, 0, 32, -55, 0, 32, -119, 0, 48, 105, 0, 16, 106, 0, 16, 107, 0, 16, 109, 0, 37, 14, 0, 37, 15, 0, 37, 16, 0, 48, 65, 0,
				32, 97, 0, 16, 66, 0, 48, 35, 0, 96, 99, 0, 16, 100, 0, 16, 68, 0, 16, 36, 0, 16, 101, 0, 64, 102, 12, 2, 67, 46, 95, 2, 1, 96, 69, 0, 64, 37, 0, 16, 70, 0, 16, 71, 1, 80, 108, 0, 0, 76, 0, 48, 44, 0, 16, 45, 0, 16, 46, 0, 16, 75, 1, 96,
				74, 1, 80, 72, 0, 32, 40, 0, 48, 8, 0, 16, 9, 0, 64, 10, 0, 32, 42, 8, 18, 73, 48, 1, 0, 16, 111, 0, 0, 110, 0, 16, 77, 0, 64, 78, 0, -96, 112, 16, 98, -82, 46, 95, -62, 4, 0, 16, -81, 0, 112, 48, 8, 18, 47, 46, 1, 0, 32, 80, 0, 16, 49, 0,
				16, 50, 0, -112, -80, 0, 64, 51, 0, 80, -77, 0, 16, -78, 0, 16, -79, 0, 32, 83, 0, 32, -109, 0, 32, 115, 9, 2, -112, 48, 1, 0, 16, -113, 0, 0, -114, 0, 16, -115, 0, 48, -116, 0, 32, -84, 0, 32, -52, 0, 32, -20, 0, 33, 12, 1, 97, 44 });
		lock.setName(LocalizationTheBeginning.LEVEL_6);
		// So close
		TrackLevel close = TrackLevel.loadMap(new byte[] { 0, 8, 83, 111, 32, 99, 108, 111, 115, 101, 6, -94, 38, 71, 0, 16, 68, 0, 16, 70, 0, 16, 71, 0, 16, 72, 0, 16, 7, 0, 16, 8, 1, -28, -91, 0, 20, -92, 0, 100, -93, 0, 16, 66, 0, 16, 65, 0, 16, 64, 0,
				20, -90, 0, 36, -123, 0, 36, -125, 0, 64, 9, 0, 32, 41, 0, 80, 73, 0, 16, -89, 1, 64, -88, 1, 80, -87, 0, 96, -56, 0, 48, -119, 0, 16, -55, 12, 18, -118, -91, 80, 1, 8, 18, -86, -51, 0, 12, 18, -54, -91, 90, 0, 0, 16, -53, 0, 16, -85, 0,
				16, -117, 0, -32, -51, 0, 0, -83, 0, -96, -115, 0, 16, -116, 0, 16, -84, 0, 16, -52, 0, 32, 109, 0, 32, -19, 0, 16, -82, 0, 17, 12, 0, 17, 11, 0, 17, 10, 0, 17, 9, 0, 17, 8, 0, 17, 7, 0, 17, 6, 0, -111, 13, 0, 17, 14, 0, -76, 45, 0, 36, 77,
				0, 20, 44, 0, 20, 46, 0, 16, 47, 8, 18, 48, -51, 0, 0, 16, -81, 0, 16, -80, 0, -96, -78, 0, 16, 49, 0, 16, -79, 0, 16, 115, 0, 32, -108, 0, 32, -76, 0, 32, -12, 0, 17, 19, 0, 17, 18, 8, 2, -44, -91, 0, 0, 96, -46, 0, 16, -45, 0, 17, 17, 0,
				17, 15, 8, 19, 16, 5, 0, 0, 97, 1, 0, 48, -63, 0, 16, -62, 0, 64, -61, 0, 32, -29, 1, 97, 3, 0, 17, 4, 0, 17, 5, 0, 17, 2, 12, 34, -31, 5, -102, 1, 0, 16, -43, 0, -96, -42, 0, 32, -74, 0, 32, -106, 0, 64, 118, 0, 16, 117, 0, 112, 116, 1,
				81, 20, 0, 17, 21, 0, 81, 22, 0, 32, -10, 1, 68, 5, 0, 20, 6, 0, 20, 4, 0, 36, 37, 0, 36, 99, 0, 36, 35, 0, 36, 101, 0, 4, 69, 0, 4, 67, 0, 52, 3, 8, 34, -110, 5, 0, 1, 116, 114, 0, 36, 82, 0, 68, 50 });
		close.setName(LocalizationTheBeginning.LEVEL_7);
		close.addMessage(new LevelMessage(20, 0, 7, LocalizationTheBeginning.OUT_OF_REACH).setMustNotBeDone());
		close.addMessage(new LevelMessage(20, 0, 7, LocalizationTheBeginning.OUT_OF_REACH_2).setMustBeDone());
		// Madness
		TrackLevel madness = TrackLevel.loadMap(new byte[] { 0, 7, 77, 97, 100, 110, 101, 115, 115, 16, -32, 30, 99, 0, 32, 32, 0, 32, 64, 0, 32, 96, 0, 48, 0, 0, 32, -128, 0, 32, -96, 0, 32, -32, 0, 32, -64, 0, 33, 0, 0, 97, 32, 1, -47, 33, 0, 81, 34, 0,
				49, 1, 12, 3, 2, 43, -109, 1, 0, 112, -30, 0, 96, -31, 0, 32, -63, 0, 16, 1, 0, 64, 2, 0, 32, 66, 1, 16, 34, 0, 48, 33, 0, 32, 65, 0, 32, 97, 0, -95, 3, 0, 64, -29, 0, 32, -127, 8, 34, -95, 34, 0, 0, 97, 35, 0, 81, 37, 0, 97, 4, 0, 32, -28,
				0, 64, -60, 0, 16, -61, 0, 96, -62, 12, 19, 36, 44, 21, 2, 0, 65, 6, 0, 36, -94, 0, 36, 98, 1, 36, -126, 0, 68, -125, 0, 96, -93, 0, 80, -92, 0, 32, -124, 0, 64, 100, 0, 16, 99, 1, 49, 5, 0, 97, 38, 0, 17, 39, 0, 17, 40, 0, 81, 41, 0, -79,
				9, 0, 97, 7, 8, 19, 8, 69, 0, 0, -95, 10, 0, 64, -22, 0, 97, 42, 0, 16, -23, 0, 0, -25, 0, 16, -24, 0, 16, -26, 0, 96, -27, 1, -59, 43, 1, 69, 44, 0, 21, 45, 1, 85, 46, 0, 4, -18, 0, 21, 47, 0, 85, 48, 0, 4, -16, 0, 48, -82, 0, 64, -80, 8,
				18, -81, -49, 0, 0, 48, -59, 0, 16, -58, 0, 0, -57, 0, 16, -90, 0, 96, -91, 0, 32, -123, 0, 36, 3, 0, 100, 67, 0, 36, 101, 0, 20, 68, 0, -76, 69, 0, 5, 14, 0, 5, 16, 0, 4, -50, 0, 4, -48, 1, 68, -49, 0, 16, -52, 0, 16, -51, 1, 80, -53, 0,
				16, -47, 0, 16, -46, 0, 64, -45, 0, -112, -17, 0, 17, 15, 0, 16, -19, 0, 16, -20, 0, 48, -21, 0, 97, 11, 8, 19, 12, 116, 0, 0, 17, 13, 0, 17, 17, 0, 16, -15, 0, 16, -14, 0, 17, 18, 0, 0, -13, 0, 1, 19, 0, 81, 51, 0, 17, 50, 0, 49, 49, 1,
				64, -89, 0, 84, 70, 0, 36, 38, 0, 68, 6, 0, 52, 4, 0, 84, 37, 0, 4, 5, 0, 20, -56, 0, 20, -54, 1, 84, -55, 0, 4, -87, 0, 20, -88, 0, 20, -86, 1, 16, -85, 0, -128, -117, 0, 64, -116, 0, 64, 107, 0, 48, -119, 0, 80, -118, 1, -64, 106, 0, 16,
				105, 0, 48, 102, 1, 48, 104, 0, 16, 103, 0, 96, -122, 0, 80, -120, 12, 18, -121, 69, 22, 1, 0, 100, -84, 0, 84, -83, 0, -12, -115, 0, 20, -114, 8, 34, 39, -115, 0, 0, 48, 7, 0, 16, 8, 0, 96, 71, 1, 96, 41, 0, 48, 40, 1, 80, 72, 0, 16, 73,
				0, 16, 74, 0, 16, 75, 0, 4, 109, 0, 4, 13, 0, 4, 45, 0, 20, 14, 0, 20, 46, 0, 64, 9, 0, 48, 10, 1, 80, 42, 0, 16, 44, 0, 16, 12, 0, 16, 11, 0, 52, 78, 0, 68, 79, 0, 4, 111, 1, 84, -113, 0, 16, -112, 12, 18, 112, 48, -94, 0, 0, 96, -79, 0,
				-32, -111, 1, 20, 77, 0, 116, 76, 0, 100, 108, 0, 0, 113, 0, 64, 114, 0, 32, -110, 0, 80, -78, 0, -12, 35, 1, 100, 36, 8, 18, 43, 35, 0, 1, 52, 48, 1, -124, 81, 0, 100, 80, 1, -48, 110, 0, 20, 15, 0, -28, 49, 0, 20, 47, 1, -80, 17, 0, 20,
				16, 0, 64, 18, 0, 32, 50, 0, 96, 82, 0, 64, 83, 0, 32, 115, 8, 34, -109, 69, 0, 0, 96, -77, 0, 48, 19, 0, 96, 51, 0, 64, 52, 1, -92, 84, 1, -92, 116, 1, -92, -108, 1, 16, 85, 1, 16, 117, 0, 32, 53, 0, 0, 21, 1, 16, -107, 0, 80, -75, 1, 84,
				-76, 0, -32, -12, 0, 48, -44, 1, 1, 20, 0, 97, 52, 0, 17, 53, 0, 17, 54, 12, 18, -43, -108, -88, 0, 0, 81, 55, 0, 97, 56, 0, 113, 23, 0, -95, 24, 16, 19, 57, 116, 40, 81, 1, 0, 81, 58, 0, 65, 26, 0, 97, 25, 0, 48, -11, 0, 97, 21, 12, 19,
				22, 116, -88, 0, 0, 112, -7, 1, 64, -8, 0, -112, -9, 0, 16, -10, 0, 80, -6, 8, 18, 20, 84, 0, 0, 64, -41, 0, -112, -42, 0, 48, -74, 0, 16, -73, 0, 64, -72, 0, 96, -40, 0, 64, -38, 0, -112, -39, 0, 48, -71, 0, 80, -70, 0, 32, -102, 0, 32,
				122, 0, 32, 90, 0, 32, 58, 0, 16, 25, 0, 16, 24, 1, 48, 23, 1, 64, 22, 0, 64, 26, 0, 32, 55, 0, 32, 119, 0, 96, -106, 1, 32, 86, 0, 32, 54, 0, 0, 87, 0, -112, -105, 12, 34, 118, 116, 40, 1, 0, 80, -103, 0, 32, 121, 0, 64, 57, 1, 80, -104,
				0, 48, 56, 0, -96, 88, 8, 34, 120, -108, 0, 8, 34, 89, 84, 0 });
		madness.setName(LocalizationTheBeginning.LEVEL_8);
		// The cake
		TrackLevel cake = new TrackLevel(LocalizationTheBeginning.LEVEL_9, 6, 4, TrackOrientation.Direction.RIGHT, 20, 4);
		Track.addTrack(cake.getTracks(), 4, 4, 9, 4);
		cake.getTracks().add(new TrackEnderHandler(10, 4, TrackOrientation.STRAIGHT_HORIZONTAL, true));
		Track.addTrack(cake.getTracks(), 11, 4, 21, 4);
		cake.getTracks().add(new TrackEnderHandler(22, 4, TrackOrientation.STRAIGHT_HORIZONTAL, false));
		cake.addMessage(new LevelMessage(4, 1, 23, LocalizationTheBeginning.LONG_JOURNEY));
		cake.addMessage(new LevelMessage(3, 6, 21, LocalizationTheBeginning.END).setMustBeStill());
		cake.addMessage(new LevelMessage(9, 9, 8, LocalizationTheBeginning.THANKS).setMustBeStill());
		TrackStory story0 = new TrackStory(LocalizationTheBeginning.TITLE);
		story0.add(newday);
		story0.add(operator);
		story0.add(loop);
		story0.add(steel);
		story0.add(moving);
		story0.add(lock);
		story0.add(close);
		story0.add(madness);
		story0.add(cake);
	}
	private ILocalizedText name;
	private ArrayList<TrackLevel> maps;

	public TrackStory(ILocalizedText name) {
		this.name = name;
		maps = new ArrayList<>();
		stories.add(this);
	}

	public void add(TrackLevel map) {
		maps.add(map);
	}

	public String getName() {
		return name.translate();
	}

	public ArrayList<TrackLevel> getLevels() {
		return maps;
	}
}
