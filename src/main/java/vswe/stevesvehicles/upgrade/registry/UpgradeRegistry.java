package vswe.stevesvehicles.upgrade.registry;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vswe.stevesvehicles.registry.IRegistry;
import vswe.stevesvehicles.registry.RegistryLoader;
import vswe.stevesvehicles.upgrade.EffectNameLoader;
import vswe.stevesvehicles.upgrade.Upgrade;

public class UpgradeRegistry implements IRegistry<Upgrade> {

	private static void preInit() {
		loader = new RegistryLoader<>();
		allUpgrades = new ArrayList<>();
	}

	public static void init() {
		add(new UpgradeRegistryPower());
		add(new UpgradeRegistryProduction());
		add(new UpgradeRegistryControl());
		EffectNameLoader.initNames();
	}

	private static List<Upgrade> allUpgrades;
	private static RegistryLoader<UpgradeRegistry, Upgrade> loader;
	private Map<String, Upgrade> upgrades;

	private final String code;

	public UpgradeRegistry(String code) {
		if (code.contains(":")) {
			System.err.println("The code can't contain colons. Any colons have been replaced with underscores.");
		}
		this.code = code.replace(":", "_");
		upgrades = new HashMap<>();
	}

	@Override
	public final String getCode() {
		return code;
	}

	public static void add(UpgradeRegistry registry) {
		if(loader == null) {
			preInit();
		}

		loader.add(registry);
	}

	public void register(Upgrade upgrade) {
		if (loader == null) {
			preInit();
		}

		if (upgrades.containsKey(upgrade.getRawUnlocalizedName())) {
			System.err.println("An upgrade with this raw name has already been registered in this registry. Failed to register a second upgrade with the raw name " + upgrade.getRawUnlocalizedName() + " in registry with code " + getCode());
		}else{
			upgrades.put(upgrade.getRawUnlocalizedName(), upgrade);
			allUpgrades.add(upgrade);
			upgrade.setFullRawUnlocalizedName(getFullCode(upgrade));
		}
	}

	@Override
	public String getFullCode(Upgrade upgrade) {
		return getCode() + ":" + upgrade.getRawUnlocalizedName();
	}

	@Override
	public Collection<Upgrade> getElements() {
		return upgrades.values();
	}


	public static List<Upgrade> getAllUpgrades() {
		return allUpgrades;
	}

	public static Upgrade getUpgradeFromId(int type) {
		return loader.getObjectFromId(type);
	}

	public static int getIdFromUpgrade(Upgrade upgrade) {
		return loader.getIdFromObject(upgrade);
	}
}
