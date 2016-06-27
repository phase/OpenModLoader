package xyz.openmodloader.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking a method as an event handler.
 * Must be non-static.
 * The method must take 1 parameter, a class extending {@link Event}.
 * @see EventBus#register(Object) 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
}
