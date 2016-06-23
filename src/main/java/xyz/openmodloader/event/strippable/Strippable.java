package xyz.openmodloader.event.strippable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Strippable {
    Side side() default Side.UNIVERSAL;

    String[] mods() default {};

    String[] classes() default {};
}