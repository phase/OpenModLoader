package xyz.openmodloader.util;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionHelper {

	public static <T, V> V getValue(Class<T> clazz, T object, String... names) throws ReflectiveOperationException {
		for (String name : names) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				return (V)f.get(object);
			} catch (NoSuchFieldException ignored) {}
		}
		throw new IllegalArgumentException(String.format("Class %s does not contain any of the fields %s", clazz.getName(), Arrays.toString(names)));
	}

	public static <T> void setValue(Class<T> clazz, T object, Object value, String... names) throws ReflectiveOperationException {
		for (String name : names) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				f.set(object, value);
				return;
			} catch (NoSuchFieldException ignored) {}
		}
		throw new IllegalArgumentException(String.format("Class %s does not contain any of the fields %s", clazz.getName(), Arrays.toString(names)));
	}

}
