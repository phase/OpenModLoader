package xyz.openmodloader.modloader;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

class DependencySorter {

    public static List<ModContainer> sort(Iterable<ModContainer> unsorted) {
        LinkedList<ModContainer> sorted = Lists.newLinkedList();
        for (ModContainer mod: unsorted) {
            if (sorted.isEmpty() || mod.getDependencySet().isEmpty())
                sorted.addFirst(mod);
            else {
                boolean b = false;
                for (int i = 0; i < sorted.size(); i++)
                    if (sorted.get(i).getDependencySet().contains(mod.getModID())) {
                        sorted.add(i, mod);
                        b = true;
                        break;
                    }
                if (!b)
                    sorted.addLast(mod);
            }
        }
        return sorted;
    }
}
