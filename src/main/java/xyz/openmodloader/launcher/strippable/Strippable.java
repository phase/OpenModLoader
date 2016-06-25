package xyz.openmodloader.launcher.strippable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Strippable {
    Side side() default Side.UNIVERSAL;

    Environment environment() default Environment.UNIVERSAL;

    String[] mods() default {};

    String[] classes() default {};

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(value = InterfaceContainer.class)
    public static @interface Interface {
        String[] interfaces();
        
        Side side() default Side.UNIVERSAL;

        Environment environment() default Environment.UNIVERSAL;

        String[] mods() default {};

        String[] classes() default {};
    }
}