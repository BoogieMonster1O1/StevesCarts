package stevesvehicles.client.gui.screen;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerSpecial /* extends GuiScreen */ extends GuiContainer {
	public GuiContainerSpecial(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}
	/*
	 * private static final float BRIGHTNESS_X = 240F; private static final
	 * float BRIGHTNESS_Y = 240F; private static final int SLOT_SIZE = 16;
	 * private static final int SLOT_HOVER_COLOR = 0x80FFFFFF; private static
	 * final int ITEM_OFFSET_X = 8; private static final int ITEM_OFFSET_Y = 8;
	 * private static final int TOUCHSCREEN_ITEM_OFFSET_Y = 16; private static
	 * final int TOUCHSCREEN_ITEM_TEXT_OFFSET_Y = 16; private static final int
	 * TOUCHSCREEN_RETURN_TIME = 100; private static final int MOUSE_LEFT_CLICK
	 * = 0; private static final int MOUSE_RIGHT_CLICK = 1; private static final
	 * int PICK_BUTTON_OFFSET = 100; private static final int FAKE_SLOT_ID =
	 * -999; private static final int INVALID_SLOT_ID = -1; private static final
	 * int CLICK_MODE_NORMAL = 0; private static final int CLICK_MODE_SHIFT = 1;
	 * private static final int CLICK_MODE_OUTSIDE = 4; private static final int
	 * CLICK_DRAG_RELEASE = 5; private static final int CLICK_MODE_DOUBLE_CLICK
	 * = 6; private static final int CLICK_DRAG_TYPE_PRE = 0; private static
	 * final int CLICK_DRAG_TYPE_SLOT = 1; private static final int
	 * CLICK_DRAG_TYPE_POST = 2; private static final int
	 * TOUCHSCREEN_DROP_DELAY_SAME_SLOT = 1250; private static final int
	 * TOUCHSCREEN_DROP_DELAY_OTHER_SLOT = 500; private static final int
	 * DOUBLE_CLICK_MAX_DELAY = 250; private static final int HOTBAR_SLOTS = 9;
	 * public Container container; protected int xSize; protected int ySize;
	 * protected int guiLeft; protected int guiTop; private Slot selectedSlot;
	 * private Slot clickedSlot; private boolean isRightMouseClick; private
	 * ItemStack touchscreenItem; private int touchscreenDropX; private int
	 * touchscreenDropY; private Slot touchscreenReturnDestination; private long
	 * touchscreenDropTime; private ItemStack touchscreenReturnItem; private
	 * Slot touchscreenDropSlot; private long touchscreenValidDropTime;
	 * protected final Set<Slot> draggedItemSlots = new HashSet<>(); protected
	 * boolean isItemBeingDragged; private int lastValidDragMouseButton; private
	 * int dragMouseButton; private boolean preventItemRelease; private int
	 * remainingDraggedItems; private long doubleClickTime; private Slot
	 * doubleClickSlot; private int doubleClickButton; private boolean
	 * isDoubleClicking; private ItemStack doubleClickItem; public
	 * GuiContainerSpecial(Container container) { this.container = container;
	 * preventItemRelease = true; xSize = 176; ySize = 166; }
	 * @Override public void initGui() { super.initGui();
	 * mc.thePlayer.openContainer = container; guiLeft = (width - xSize) / 2;
	 * guiTop = (height - ySize) / 2; }
	 * @Override public void drawScreen(int x, int y, float f) {
	 * drawDefaultBackground(); drawGuiContainerBackgroundLayer(f, x, y);
	 * GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	 * RenderHelper.disableStandardItemLighting();
	 * GL11.glDisable(GL11.GL_LIGHTING); GL11.glDisable(GL11.GL_DEPTH_TEST);
	 * super.drawScreen(x, y, f); RenderHelper.enableGUIStandardItemLighting();
	 * GL11.glPushMatrix(); GL11.glTranslatef(guiLeft, guiTop, 0);
	 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	 * GL11.glEnable(GL12.GL_RESCALE_NORMAL); selectedSlot = null;
	 * OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
	 * BRIGHTNESS_X, BRIGHTNESS_Y); GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	 * renderSlots(x, y); GL11.glDisable(GL11.GL_LIGHTING);
	 * drawGuiContainerForegroundLayer(x, y); GL11.glEnable(GL11.GL_LIGHTING);
	 * InventoryPlayer inventory = mc.thePlayer.inventory; ItemStack movingItem
	 * = touchscreenItem == null ? inventory.getItemStack() : touchscreenItem;
	 * if (movingItem != null) { String itemInfo = null; if (touchscreenItem !=
	 * null && isRightMouseClick) { movingItem = movingItem.copy();
	 * movingItem.stackSize = movingItem.stackSize - movingItem.stackSize / 2;
	 * }else if (isItemBeingDragged && draggedItemSlots.size() > 1) { movingItem
	 * = movingItem.copy(); movingItem.stackSize = remainingDraggedItems; if
	 * (movingItem.stackSize == 0){ itemInfo = TextFormatting.YELLOW + "0"; } }
	 * int itemOffsetY = touchscreenItem == null ? ITEM_OFFSET_Y :
	 * TOUCHSCREEN_ITEM_OFFSET_Y; drawDraggedItem(movingItem, x - guiLeft -
	 * ITEM_OFFSET_X, y - guiTop - itemOffsetY, itemInfo, touchscreenItem !=
	 * null); } if (touchscreenReturnItem != null) { float returnPart =
	 * (float)(Minecraft.getSystemTime() - touchscreenDropTime) /
	 * TOUCHSCREEN_RETURN_TIME; if (returnPart >= 1) { returnPart = 1;
	 * touchscreenReturnItem = null; } int distanceX =
	 * touchscreenReturnDestination.xDisplayPosition - touchscreenDropX; int
	 * distanceY = touchscreenReturnDestination.yDisplayPosition -
	 * touchscreenDropY; int currentX = touchscreenDropX + (distanceX
	 * returnPart); int currentY = touchscreenDropY + (distanceY returnPart);
	 * drawDraggedItem(touchscreenReturnItem, currentX, currentY, null, false);
	 * } GL11.glPopMatrix(); if (inventory.getItemStack() == null &&
	 * selectedSlot != null && selectedSlot.getHasStack()) { ItemStack hoverItem
	 * = selectedSlot.getStack(); renderToolTip(hoverItem, x, y); }
	 * GL11.glEnable(GL11.GL_LIGHTING); GL11.glEnable(GL11.GL_DEPTH_TEST);
	 * RenderHelper.enableStandardItemLighting(); } private void
	 * drawDraggedItem(ItemStack item, int x, int y, String info, boolean
	 * useTextOffset) { GL11.glTranslatef(0, 0, 32); zLevel = 200;
	 * itemRender.zLevel = 200; FontRenderer fontRenderer = item != null ?
	 * item.getItem().getFontRenderer(item) : null; if (fontRenderer == null) {
	 * fontRenderer = fontRendererObj; }
	 * itemRender.renderItemAndEffectIntoGUI(item, x, y); int textY = y -
	 * (useTextOffset ? TOUCHSCREEN_ITEM_TEXT_OFFSET_Y : 0);
	 * itemRender.renderItemOverlayIntoGUI(fontRendererObj, item, x, textY,
	 * info); zLevel = 0; itemRender.zLevel = 0; }
	 * @Override protected void drawGuiContainerForegroundLayer(int mX, int mY)
	 * { }
	 * @Override protected abstract void drawGuiContainerBackgroundLayer(float
	 * f, int mX, int mY); private void renderSlot(Slot slot, int x, int y) {
	 * ItemStack slotItem = slot.getStack(); boolean
	 * shouldSlotUnderlayBeRendered = false; boolean shouldSlotOverlayBeRendered
	 * = false; boolean shouldSlotBeRendered = slot != clickedSlot ||
	 * touchscreenItem == null || isRightMouseClick; ItemStack movingItem =
	 * mc.thePlayer.inventory.getItemStack(); String info = null; if (slot ==
	 * clickedSlot && touchscreenItem != null && isRightMouseClick && slotItem
	 * != null) { slotItem = slotItem.copy(); slotItem.stackSize /= 2; } else if
	 * (isItemBeingDragged && draggedItemSlots.contains(slot) && movingItem !=
	 * null) { if (draggedItemSlots.size() == 1) { return; } if
	 * (Container.canAddItemToSlot(slot, movingItem, true) &&
	 * container.canDragIntoSlot(slot)) { slotItem = movingItem.copy();
	 * shouldSlotUnderlayBeRendered = true;
	 * Container.computeStackSize(draggedItemSlots, lastValidDragMouseButton,
	 * slotItem, slot.getStack() == null ? 0 : slot.getStack().stackSize); int
	 * maxStackSize = Math.min(slot.getSlotStackLimit(),
	 * slotItem.getMaxStackSize()); if (slotItem.stackSize > maxStackSize) {
	 * info = TextFormatting.YELLOW + String.valueOf(maxStackSize);
	 * slotItem.stackSize = maxStackSize; } } else {
	 * draggedItemSlots.remove(slot); updateDraggedItemDistribution(); } }
	 * zLevel = 100; itemRender.zLevel = 100; if (slotItem == null) {
	 * TextureAtlasSprite icon = slot.getBackgroundSprite(); if (icon != null) {
	 * GL11.glDisable(GL11.GL_LIGHTING);
	 * mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
	 * drawTexturedModalRect(slot.xDisplayPosition, slot.yDisplayPosition, icon,
	 * SLOT_SIZE, SLOT_SIZE); GL11.glEnable(GL11.GL_LIGHTING);
	 * shouldSlotBeRendered = false; } } if (isMouseOverSlot(slot, x, y) &&
	 * slot.canBeHovered()) { selectedSlot = slot; shouldSlotOverlayBeRendered =
	 * true; } renderSlot(slot, slotItem, shouldSlotBeRendered, true,
	 * shouldSlotUnderlayBeRendered, shouldSlotOverlayBeRendered, info);
	 * itemRender.zLevel = 0; zLevel = 0; } protected void renderSlots(int x,
	 * int y) { for (int i = 0; i < container.inventorySlots.size(); i++) { Slot
	 * slot = container.inventorySlots.get(i); renderSlot(slot, x, y); } }
	 * protected void renderSlot(Slot slot, ItemStack slotItem, boolean
	 * shouldSlotBeRendered, boolean shouldSlotItemBeRendered, boolean
	 * shouldSlotUnderlayBeRendered, boolean shouldSlotOverlayBeRendered, String
	 * info) { if (shouldSlotBeRendered) { if (shouldSlotUnderlayBeRendered) {
	 * drawRect(slot.xDisplayPosition, slot.yDisplayPosition,
	 * slot.xDisplayPosition + SLOT_SIZE, slot.yDisplayPosition + SLOT_SIZE,
	 * SLOT_HOVER_COLOR); } GL11.glEnable(GL11.GL_DEPTH_TEST); if
	 * (shouldSlotItemBeRendered && slotItem != null) {
	 * itemRender.renderItemAndEffectIntoGUI(slotItem, slot.xDisplayPosition,
	 * slot.yDisplayPosition);
	 * itemRender.renderItemOverlayIntoGUI(fontRendererObj, slotItem,
	 * slot.xDisplayPosition, slot.yDisplayPosition, info); } if
	 * (shouldSlotOverlayBeRendered) { GL11.glEnable(GL11.GL_DEPTH_TEST);
	 * drawRect(slot.xDisplayPosition, slot.yDisplayPosition,
	 * slot.xDisplayPosition + SLOT_SIZE, slot.yDisplayPosition + SLOT_SIZE,
	 * SLOT_HOVER_COLOR); GL11.glEnable(GL11.GL_DEPTH_TEST); } } } private void
	 * updateDraggedItemDistribution() { ItemStack movingItem =
	 * mc.thePlayer.inventory.getItemStack(); if (movingItem != null &&
	 * isItemBeingDragged) { remainingDraggedItems = movingItem.stackSize; for
	 * (Slot slot : draggedItemSlots) { ItemStack item = movingItem.copy(); int
	 * slotStackSize = slot.getHasStack() ? slot.getStack().stackSize : 0;
	 * Container.computeStackSize(draggedItemSlots, lastValidDragMouseButton,
	 * item, slotStackSize); int maxStackSize =
	 * Math.min(slot.getSlotStackLimit(), item.getMaxStackSize()); if
	 * (item.stackSize > maxStackSize) { item.stackSize = maxStackSize; }
	 * remainingDraggedItems -= item.stackSize - slotStackSize; } } } private
	 * Slot getSlotAtPosition(int x, int y) { for (int i = 0; i <
	 * container.inventorySlots.size(); i++) { Slot slot =
	 * container.inventorySlots.get(i); if (isMouseOverSlot(slot, x, y)) {
	 * return slot; } } return null; }
	 * @Override protected void mouseClicked(int x, int y, int button) throws
	 * IOException { super.mouseClicked(x, y, button); boolean pickItem = button
	 * == mc.gameSettings.keyBindPickBlock.getKeyCode() + PICK_BUTTON_OFFSET;
	 * Slot slot = getSlotAtPosition(x, y); long time =
	 * Minecraft.getSystemTime(); isDoubleClicking = doubleClickSlot == slot &&
	 * time - doubleClickTime < DOUBLE_CLICK_MAX_DELAY && doubleClickButton ==
	 * button; preventItemRelease = false; if (button == MOUSE_LEFT_CLICK ||
	 * button == MOUSE_RIGHT_CLICK || pickItem) { boolean inInterface = guiLeft
	 * <= x && x < guiLeft + xSize && guiTop <= y && y < guiTop + ySize; if
	 * (mc.gameSettings.touchscreen && !inInterface &&
	 * mc.thePlayer.inventory.getItemStack() == null) {
	 * mc.displayGuiScreen(null); return; } int slotId; if (!inInterface) {
	 * slotId = FAKE_SLOT_ID; } else if (slot != null) { slotId =
	 * slot.slotNumber; } else { slotId = INVALID_SLOT_ID; } if (slotId !=
	 * INVALID_SLOT_ID) { if (mc.gameSettings.touchscreen) { if (slot != null &&
	 * slot.getHasStack()) { clickedSlot = slot; touchscreenItem = null;
	 * isRightMouseClick = button == MOUSE_RIGHT_CLICK; } else { clickedSlot =
	 * null; } } else if (!isItemBeingDragged) { if
	 * (mc.thePlayer.inventory.getItemStack() == null) { if (pickItem) {
	 * handleMouseClick(slot, slotId, button, ClickType.CLONE); } else { boolean
	 * isShiftClicking = slotId != FAKE_SLOT_ID && isShiftKeyDown(); ClickType
	 * clickMode = ClickType.PICKUP; if (isShiftClicking) { doubleClickItem =
	 * slot.getHasStack() ? slot.getStack() : null; clickMode =
	 * ClickType.QUICK_MOVE; } else if (slotId == FAKE_SLOT_ID) { clickMode =
	 * CLICK_MODE_OUTSIDE; } handleMouseClick(slot, slotId, button, clickMode);
	 * } preventItemRelease = true; } else { isItemBeingDragged = true;
	 * dragMouseButton = button; draggedItemSlots.clear(); if (button ==
	 * MOUSE_LEFT_CLICK || button == MOUSE_RIGHT_CLICK) {
	 * lastValidDragMouseButton = dragMouseButton; } } } } } doubleClickSlot =
	 * slot; doubleClickTime = time; doubleClickButton = button; }
	 * @Override protected void mouseClickMove(int x, int y, int button, long
	 * timeSinceClick) { Slot slot = getSlotAtPosition(x, y); ItemStack
	 * movingItem = mc.thePlayer.inventory.getItemStack(); if (clickedSlot !=
	 * null && mc.gameSettings.touchscreen) { if (button == MOUSE_LEFT_CLICK ||
	 * button == MOUSE_RIGHT_CLICK) { if (touchscreenItem == null) { if (slot !=
	 * clickedSlot) { touchscreenItem = clickedSlot.getStack().copy(); } } else
	 * if (touchscreenItem.stackSize > 1 && slot != null &&
	 * Container.canAddItemToSlot(slot, touchscreenItem, false)) { long time =
	 * Minecraft.getSystemTime(); if (touchscreenDropSlot == slot) { if (time >
	 * touchscreenValidDropTime) { handleMouseClick(clickedSlot,
	 * clickedSlot.slotNumber, MOUSE_LEFT_CLICK, ClickType.PICKUP);
	 * handleMouseClick(slot, slot.slotNumber, MOUSE_RIGHT_CLICK,
	 * ClickType.PICKUP); handleMouseClick(clickedSlot, clickedSlot.slotNumber,
	 * MOUSE_LEFT_CLICK, ClickType.PICKUP); touchscreenValidDropTime = time +
	 * TOUCHSCREEN_DROP_DELAY_OTHER_SLOT; touchscreenItem.stackSize--; } } else
	 * { touchscreenDropSlot = slot; touchscreenValidDropTime = time +
	 * TOUCHSCREEN_DROP_DELAY_SAME_SLOT; } } } } else if (isItemBeingDragged &&
	 * slot != null && movingItem != null && movingItem.stackSize >
	 * draggedItemSlots.size() && Container.canAddItemToSlot(slot, movingItem,
	 * true) && slot.isItemValid(movingItem) && container.canDragIntoSlot(slot))
	 * { draggedItemSlots.add(slot); updateDraggedItemDistribution(); } }
	 * @Override protected void mouseReleased(int mouseX, int mouseY, int state)
	 * { super.mouseReleased(mouseX, mouseY, state); Slot slot =
	 * getSlotAtPosition(mouseX, mouseY); boolean inInterface = guiLeft <=
	 * mouseX && mouseX < guiLeft + xSize && guiTop <= mouseY && mouseY < guiTop
	 * + ySize; int slotId; if (!inInterface) { slotId = FAKE_SLOT_ID; } else if
	 * (slot != null) { slotId = slot.slotNumber; } else { slotId =
	 * INVALID_SLOT_ID; } if (isDoubleClicking && slot != null && state ==
	 * MOUSE_LEFT_CLICK && container.canMergeSlot(null, slot)) { if
	 * (isShiftKeyDown()) { if (slot.inventory != null && doubleClickItem !=
	 * null) { for (Object obj : container.inventorySlots) { Slot inventorySlot
	 * = (Slot) obj; if (inventorySlot != null &&
	 * inventorySlot.canTakeStack(mc.thePlayer) && inventorySlot.getHasStack()
	 * && inventorySlot.inventory == slot.inventory &&
	 * Container.canAddItemToSlot(inventorySlot, doubleClickItem, true)) {
	 * handleMouseClick(inventorySlot, inventorySlot.slotNumber, state,
	 * ClickType.QUICK_MOVE); } } } } else { handleMouseClick(slot, slotId,
	 * state, CLICK_MODE_DOUBLE_CLICK); } isDoubleClicking = false;
	 * doubleClickTime = 0L; } else { if (isItemBeingDragged && dragMouseButton
	 * != state) { isItemBeingDragged = false; draggedItemSlots.clear();
	 * preventItemRelease = true; return; } if (preventItemRelease) {
	 * preventItemRelease = false; return; } if (clickedSlot != null &&
	 * mc.gameSettings.touchscreen) { if (state == MOUSE_LEFT_CLICK || state ==
	 * MOUSE_RIGHT_CLICK) { if (touchscreenItem == null && slot != clickedSlot)
	 * { touchscreenItem = clickedSlot.getStack(); } if (slotId !=
	 * INVALID_SLOT_ID && touchscreenItem != null &&
	 * Container.canAddItemToSlot(slot, touchscreenItem, false)) {
	 * handleMouseClick(clickedSlot, clickedSlot.slotNumber, state,
	 * ClickType.PICKUP); handleMouseClick(slot, slotId, MOUSE_LEFT_CLICK,
	 * ClickType.PICKUP); if (mc.thePlayer.inventory.getItemStack() != null) {
	 * handleMouseClick(clickedSlot, clickedSlot.slotNumber, state,
	 * ClickType.PICKUP); dropTouchscreenItem(mouseX, mouseY); } else {
	 * touchscreenReturnItem = null; } } else if (touchscreenItem != null) {
	 * dropTouchscreenItem(mouseX, mouseY); } touchscreenItem = null;
	 * clickedSlot = null; } } else if (isItemBeingDragged &&
	 * !draggedItemSlots.isEmpty()) { int quickcraftMask =
	 * Container.getQuickcraftMask(CLICK_DRAG_TYPE_PRE,
	 * lastValidDragMouseButton); handleMouseClick(null, FAKE_SLOT_ID,
	 * quickcraftMask, CLICK_DRAG_RELEASE); for (Slot draggedItemSlot :
	 * draggedItemSlots) { handleMouseClick(draggedItemSlot,
	 * draggedItemSlot.slotNumber, quickcraftMask, CLICK_DRAG_RELEASE); }
	 * handleMouseClick(null, FAKE_SLOT_ID, quickcraftMask, CLICK_DRAG_RELEASE);
	 * } else if (mc.thePlayer.inventory.getItemStack() != null) { if (state ==
	 * mc.gameSettings.keyBindPickBlock.getKeyCode() + PICK_BUTTON_OFFSET) {
	 * handleMouseClick(slot, slotId, state, ClickType.CLONE); } else { boolean
	 * isShiftClicking = slotId != FAKE_SLOT_ID && isShiftKeyDown(); if
	 * (isShiftClicking) { doubleClickItem = slot != null && slot.getHasStack()
	 * ? slot.getStack() : null; } handleMouseClick(slot, slotId, state,
	 * isShiftClicking ? ClickType.QUICK_MOVE : ClickType.PICKUP); } } } if
	 * (mc.thePlayer.inventory.getItemStack() == null) { doubleClickTime = 0; }
	 * isItemBeingDragged = false; } private void dropTouchscreenItem(int x, int
	 * y) { touchscreenDropX = x - guiLeft; touchscreenDropY = y - guiTop;
	 * touchscreenReturnDestination = clickedSlot; touchscreenReturnItem =
	 * touchscreenItem; touchscreenDropTime = Minecraft.getSystemTime(); }
	 * protected boolean isMouseOverSlot(Slot slot, int mX, int mY) { return
	 * inSlotRect(slot.xDisplayPosition, slot.yDisplayPosition, SLOT_SIZE,
	 * SLOT_SIZE, mX, mY); } private boolean inSlotRect(int x, int y, int width,
	 * int height, int mX, int mY) { mX -= guiLeft; mY -= guiTop; return mX >= x
	 * - 1 && mX < x + width + 1 && mY >= y - 1 && mY < y + height + 1; }
	 * protected void handleMouseClick(Slot slot, int slotId, int button,
	 * ClickType clickType) { if (slot != null) { slotId = slot.slotNumber; }
	 * mc.playerController.windowClick(container.windowId, slotId, button,
	 * clickType, mc.thePlayer); }
	 * @Override protected void keyTyped(char character, int key) { if (key == 1
	 * || key == mc.gameSettings.keyBindInventory.getKeyCode()) {
	 * mc.thePlayer.closeScreen(); } checkHotbarKeys(key); if (selectedSlot !=
	 * null && selectedSlot.getHasStack()) { if (key ==
	 * mc.gameSettings.keyBindPickBlock.getKeyCode()) {
	 * handleMouseClick(selectedSlot, selectedSlot.slotNumber, MOUSE_LEFT_CLICK,
	 * ClickType.CLONE); } else if (key ==
	 * mc.gameSettings.keyBindDrop.getKeyCode()) {
	 * handleMouseClick(selectedSlot, selectedSlot.slotNumber, isCtrlKeyDown() ?
	 * MOUSE_LEFT_CLICK : MOUSE_RIGHT_CLICK, CLICK_MODE_OUTSIDE); } } }
	 * @Override protected boolean checkHotbarKeys(int key) { if
	 * (mc.thePlayer.inventory.getItemStack() == null && selectedSlot != null) {
	 * for (int i = 0; i < HOTBAR_SLOTS; i++) { if (key ==
	 * mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
	 * handleMouseClick(selectedSlot, selectedSlot.slotNumber, i,
	 * ClickType.SWAP); return true; } } } return false; }
	 * @Override public void onGuiClosed() { if (mc.thePlayer != null) {
	 * container.onContainerClosed(mc.thePlayer); } }
	 * @Override public boolean doesGuiPauseGame() { return false; }
	 * @Override public void updateScreen() { super.updateScreen(); if
	 * (!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead) {
	 * mc.thePlayer.closeScreen(); } }
	 */
}
