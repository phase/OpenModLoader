package xyz.openmodloader.launcher;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Sets;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import xyz.openmodloader.launcher.strippable.Environment;
import xyz.openmodloader.launcher.strippable.InterfaceContainer;
import xyz.openmodloader.launcher.strippable.Side;
import xyz.openmodloader.launcher.strippable.Strippable;

public class OMLStrippableTransformer implements IClassTransformer {
    static Set<String> MODS;
    static Side SIDE;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an: classNode.visibleAnnotations) {
                System.out.println(an.desc);
                Type anType = Type.getType(an.desc);
                if (anType.equals(Type.getType(Strippable.Interface.class))) {
                    handleStrippableInterface(classNode, an);
                } else if (anType.equals(Type.getType(InterfaceContainer.class))) {
                    for (Object o: (Iterable<?>) an.values.get(1)) {
                        handleStrippableInterface(classNode, (AnnotationNode) o);
                    }
                }
            }
        }

        if (remove(classNode.visibleAnnotations)) {
            throw new RuntimeException(String.format("Loading class %s on wrong side %s", name, SIDE.toString()));
        }

        classNode.methods.removeIf(method -> remove(method.visibleAnnotations));
        classNode.fields.removeIf(fields -> remove(fields.visibleAnnotations));

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void handleStrippableInterface(ClassNode classNode, AnnotationNode an) {
        List<Object> values = an.values;
        System.out.println(values);
        String side = Side.UNIVERSAL.name();
        String envo = Environment.UNIVERSAL.name();
        List<String> mods = Collections.emptyList();
        List<String> classes = Collections.emptyList();
        final Set<String> interfaces = Sets.newHashSet();
        for (int i = 0; i < values.size() - 1; i += 2) {
            Object key = values.get(i);
            Object value = values.get(i + 1);
            if (key instanceof String && ((String) key).equals("mods")) {
                mods = (List<String>) value;
            } else if (key instanceof String && ((String) key).equals("classes")) {
                classes = (List<String>) value;
            } else if (key instanceof String && ((String) key).equals("side")) {
                side = ((String[]) value)[1];
            } else if (key instanceof String && ((String) key).equals("environment")) {
                envo = ((String[]) value)[1];
            } else if (key instanceof String && ((String) key).equals("interfaces")) {
                interfaces.addAll((List<String>) value);
            }
        }
        if (!side.equals("UNIVERSAL") && !SIDE.toString().equals(side)) {
            classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
            return;
        }
        if (!envo.equals("UNIVERSAL") && !getEnvironment().toString().equals(side)) {
            classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
            return;
        }
        for (String mod: mods) {
            if (!MODS.contains(mod)) {
                classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
                return;
            }
        }
        for (String cls: classes) {
            try {
                Class.forName(cls, false, Launch.classLoader);
            } catch (ClassNotFoundException e) {
                classNode.interfaces.removeIf((i) -> interfaces.contains(i.replace('/', '.')));
                return;
            }
        }
    }

    public boolean remove(List<AnnotationNode> annotations) {
        if (annotations != null) {
            for (AnnotationNode annotation : annotations) {
                if (Type.getType(annotation.desc).equals(Type.getType(Strippable.class))) {
                    List<Object> values = annotation.values;
                    for (int i = 0; i < values.size() - 1; i += 2) {
                        Object key = values.get(i);
                        Object value = values.get(i + 1);
                        if (key instanceof String && ((String) key).equals("side")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                System.out.println(side);
                                if (!side.equals("UNIVERSAL") && !side.equals(SIDE.toString())) {
                                    return true;
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("environment")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                if (!side.equals("UNIVERSAL") && !side.equals(getEnvironment().toString())) {
                                    return true;
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("mods")) {
                            if (value instanceof List) {
                                List<?> mods = (List<?>) value;
                                for (Object mod: mods) {
                                    if (!MODS.contains(mod)) {
                                        return true;
                                    }
                                }
                            }
                        } else if (key instanceof String && ((String) key).equals("classes")) {
                            if (value instanceof List) {
                                List<?> classes = (List<?>) value;
                                for (Object cls: classes) {
                                    try {
                                        Class.forName((String) cls, false, Launch.classLoader);
                                    } catch (ClassNotFoundException e) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static Environment environment;

    public static Environment getEnvironment() {
        if (environment == null) {
            try {
                Class.forName("net.minecraft.block.Block", false, Launch.classLoader);
                environment = Environment.DEVELOPMENT;
            } catch (ClassNotFoundException e) {
                environment = Environment.PRODUCTION;
            }
        }
        return environment;
    }
}
