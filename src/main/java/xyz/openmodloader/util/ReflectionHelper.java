package xyz.openmodloader.util;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionHelper {

	public static <T, V> V getValue(Class<T> clazz, T object, String... names) throws ReflectiveOperationException {
		for (String name : names) {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return (V)f.get(object);
		}
		throw new IllegalArgumentException(String.format("Class %s does not contain any of the fields %s", clazz.getName(), Arrays.toString(names)));
	}

}
