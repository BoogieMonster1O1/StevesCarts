package stevesvehicles.common.recipe.old.nei;

/*
 * public class RecipeHandlerVehicleNormal extends RecipeHandlerVehicle {
 * private class CachedVehicleRecipeNormal extends CachedVehicleRecipe { private
 * PositionedStack result; private boolean valid; public
 * CachedVehicleRecipeNormal(ItemStack result) { this.result = new
 * PositionedStack(result.copy(), RESULT_X + BIG_SLOT_OFFSET, RESULT_Y +
 * BIG_SLOT_OFFSET); List<ModuleData> modules =
 * ModuleDataItemHandler.getModulesFromItem(result); if (modules != null) { for
 * (ModuleData module : modules) { if (module instanceof ModuleDataHull) {
 * initHull((ModuleDataHull)module); List<ItemStack> items =
 * ModuleDataItemHandler.getModularItems(result); loadVehicleStats(items); if
 * (items != null) { for (ItemStack item : items) { ModuleData moduleData =
 * ModItems.modules.getModuleData(item); if (moduleData != null) {
 * addModuleItem(moduleData, item); } } valid = true; } } } } }
 * @Override public PositionedStack getResult() { return result; }
 * @Override protected boolean isValid() { return valid; }
 * @Override public List<PositionedStack> getIngredients() { return ingredients;
 * } } public RecipeHandlerVehicleNormal() { }
 * @Override public void loadCraftingRecipes(ItemStack result) { if (result !=
 * null && result.getItem() == ModItems.vehicles) { CachedVehicleRecipe cache =
 * new CachedVehicleRecipeNormal(result); if (cache.isValid()) {
 * arecipes.add(cache); } } }
 * @Override protected boolean hasButtons() { return false; } }
 */
