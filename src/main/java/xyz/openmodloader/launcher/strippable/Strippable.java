package xyz.openmodloader.launcher.strippable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to a method, constructor, field or class
 * to remove it if the specified conditions aren't met.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Strippable {

    /**
     * Strips the element unless the runtime side matches the specified one.
     */
    Side side() default Side.UNIVERSAL;

    /**
     * Strips the element unless the runtime environment type matches the specified one.
     */
    Environment environment() default Environment.UNIVERSAL;

    /**
     * Strips the element unless the specified mods are loaded.
     */
    String[] mods() default {};

    /**
     * Strips the element unless the specified classes exist.
     */
    String[] classes() default {};

    /**
     * Removes the specified interfaces unless the
     * specified conditions are met.
     */
    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(value = InterfaceContainer.class)
    public static @interface Interface {
        
        /**
         * The interfaces to strip.
         */
        String[] interfaces();
        
        /**
         * Strips the interfaces unless the runtime side matches the specified one.
         */
        Side side() default Side.UNIVERSAL;

        /**
         * Strips the interfaces unless the runtime environment type matches the specified one.
         */
        Environment environment() default Environment.UNIVERSAL;

        /**
         * Strips the interfaces unless the specified mods are loaded.
         */
        String[] mods() default {};

        /**
         * Strips the interfaces unless the specified classes exist.
         */
        String[] classes() default {};
    }
}