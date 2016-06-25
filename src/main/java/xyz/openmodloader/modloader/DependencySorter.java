package xyz.openmodloader.modloader;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

class DependencySorter {

    public static List<ManifestModContainer> sort(Iterable<ManifestModContainer> unsorted) {
        LinkedList<ManifestModContainer> sorted = Lists.newLinkedList();
        for (ManifestModContainer mod: unsorted) {
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
