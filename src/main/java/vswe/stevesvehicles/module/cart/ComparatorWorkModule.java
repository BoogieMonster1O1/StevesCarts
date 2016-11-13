package vswe.stevesvehicles.module.cart;

import java.util.Comparator;

public class ComparatorWorkModule implements Comparator<ModuleWorker> {
	@Override
	public int compare(ModuleWorker work1, ModuleWorker work2) {
		return work1.getWorkPriority() < work2.getWorkPriority() ? -1 : (work1.getWorkPriority() > work2.getWorkPriority() ? 1 : 0);
	}
}