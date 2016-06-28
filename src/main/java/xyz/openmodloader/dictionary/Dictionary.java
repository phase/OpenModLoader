package xyz.openmodloader.dictionary;

import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class Dictionary<E> {

    private final SetMultimap<String, E> map =
            Multimaps.newSetMultimap(Maps.newConcurrentMap(), Sets::newConcurrentHashSet);

    /**
     * Registers a value.
     *
     * @param key the key
     * @param value the value
     */
    public void register(String key, E value) {
        map.put(key, value);
    }

    /**
     * Gets the registered elements for the specified key.
     * This set is automatically updated when new elements
     * are registered. Cache this.
     *
     * @param key the key
     * @return the sets the
     */
    public Set<E> get(String key) {
        return map.get(key);
    }
}