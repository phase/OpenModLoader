package xyz.openmodloader.launcher.strippable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import xyz.openmodloader.launcher.strippable.Strippable.Interface;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InterfaceContainer {
    Interface[] value();
}