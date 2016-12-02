package stevesvehicles.common.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import stevesvehicles.common.core.Constants;

@Deprecated
public class RegistryLoader<R extends IRegistry<E>, E> {
	static List<RegistryLoader> registryLoaderList = new ArrayList<>();

	public RegistryLoader() {
		registryLoaderList.add(this);
	}

	private Map<E, Integer> objectToIdMapping = new HashMap<>();
	private Map<Integer, E> idToObjectMapping = new HashMap<>();
	Map<String, Integer> nameToIdMapping = new HashMap<>();
	private Map<String, R> registries = new HashMap<>();
	int nextId;

	public E getObjectFromName(String name) {
		if (nameToIdMapping.isEmpty()) {
			// if the mapping hasn't been loaded yet, improvise. Once the
			// mapping has been loaded this look up will go faster.
			String[] split = name.split(":");
			if (split.length == 2) {
				R registry = registries.get(split[0]);
				if (registry != null) {
					for (E e : registry.getElements()) {
						if (registry.getFullCode(e).equals(name)) {
							return e;
						}
					}
				}
			}
		} else {
			int id = getIdFromName(name);
			if (id != -1) {
				return getObjectFromId(id);
			}
		}
		return null;
	}

	public int getIdFromObject(E object) {
		Integer result = objectToIdMapping.get(object);
		return result == null ? -1 : result;
	}

	public E getObjectFromId(int id) {
		return idToObjectMapping.get(id);
	}

	public int getIdFromName(String name) {
		Integer result = nameToIdMapping.get(name);
		return result == null ? -1 : result;
	}

	private static final String NBT_REGISTRIES = "Registries";
	private static final String NBT_NEXT_ID = "NextModuleId";
	private static final String NBT_MODULES = "Modules";
	private static final String NBT_KEY = "K";
	private static final String NBT_VALUE = "V";
	private static final int NBT_COMPOUND_TYPE_ID = 10;

	public static void writeData(NBTTagCompound compound) {
		NBTTagList registriesList = new NBTTagList();
		for (RegistryLoader registryLoader : registryLoaderList) {
			NBTTagCompound registryCompound = new NBTTagCompound();
			registryLoader.writeRegistryData(registryCompound);
			registriesList.appendTag(registryCompound);
		}
		compound.setTag(NBT_REGISTRIES, registriesList);
	}

	private static final boolean REGISTRY_FILE_CLEANUP = false;

	private void writeRegistryData(NBTTagCompound compound) {
		compound.setShort(NBT_NEXT_ID, (short) nextId);
		NBTTagList list = new NBTTagList();
		for (Map.Entry<String, Integer> entry : nameToIdMapping.entrySet()) {
			// noinspection PointlessBooleanExpression, ConstantConditions
			if (!REGISTRY_FILE_CLEANUP || idToObjectMapping.containsKey(entry.getValue())) {
				NBTTagCompound moduleCompound = new NBTTagCompound();
				moduleCompound.setString(NBT_KEY, entry.getKey());
				moduleCompound.setShort(NBT_VALUE, (short) (int) entry.getValue());
				list.appendTag(moduleCompound);
			}
		}
		compound.setTag(NBT_MODULES, list);
	}

	public static void readData(NBTTagCompound compound) {
		if (compound.hasKey(NBT_REGISTRIES)) {
			NBTTagList registriesList = compound.getTagList(NBT_REGISTRIES, NBT_COMPOUND_TYPE_ID);
			for (int i = 0; i < registriesList.tagCount(); i++) {
				NBTTagCompound registryCompound = registriesList.getCompoundTagAt(i);
				registryLoaderList.get(i).readRegistryData(registryCompound);
			}
		} else {
			for (RegistryLoader registryLoader : registryLoaderList) {
				registryLoader.readRegistryData(null);
			}
		}
	}

	private void readRegistryData(NBTTagCompound compound) {
		clearLoadedRegistryData();
		if (compound != null && compound.hasKey(NBT_NEXT_ID)) {
			nextId = compound.getShort(NBT_NEXT_ID);
			NBTTagList tags = compound.getTagList(NBT_MODULES, NBT_COMPOUND_TYPE_ID);
			for (int i = 0; i < tags.tagCount(); i++) {
				NBTTagCompound moduleCompound = tags.getCompoundTagAt(i);
				String key = moduleCompound.getString(NBT_KEY);
				int value = moduleCompound.getShort(NBT_VALUE);
				if (Constants.debugMode) {
					System.out.println("Loaded name to id mapping. (K = " + key + ", V = " + value + ")"); // TODO
				}
				// Move
				// to
				// a
				// log
				// file?
				nameToIdMapping.put(key, value);
			}
		}
		loadFromRegistries();
	}

	void clearLoadedRegistryData() {
		nextId = 0;
		objectToIdMapping.clear();
		idToObjectMapping.clear();
		nameToIdMapping.clear();
	}

	void loadFromRegistries() {
		for (R moduleRegistry : registries.values()) {
			for (E module : moduleRegistry.getElements()) {
				String code = moduleRegistry.getFullCode(module);
				Integer id = nameToIdMapping.get(code);
				if (id == null) {
					id = nextId++;
					nameToIdMapping.put(code, id);
					if (Constants.debugMode) {
						System.out.println("Added new name to id mapping. (K = " + code + ", V = " + id + ")"); // TODO
					}
					// Move
					// to
					// a
					// log
					// file?
				}
				idToObjectMapping.put(id, module);
				objectToIdMapping.put(module, id);
			}
		}
	}

	public void add(R registry) {
		if (registries.containsKey(registry.getCode())) {
			if (Constants.debugMode) {
				System.err.println("A registry with this code has already been registered. Failed to register a second registry with code " + registry.getCode());
			}
		} else {
			registries.put(registry.getCode(), registry);
		}
	}

	public static void clearAllRegistryData() {
		for (RegistryLoader registryLoader : registryLoaderList) {
			registryLoader.clearLoadedRegistryData();
		}
	}
}
