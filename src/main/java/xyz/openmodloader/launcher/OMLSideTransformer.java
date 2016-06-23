package xyz.openmodloader.launcher;

import java.util.List;

import xyz.openmodloader.OpenModLoader;
import xyz.openmodloader.event.strippable.Side;
import xyz.openmodloader.event.strippable.Strippable;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

public class OMLSideTransformer implements IClassTransformer {
    static Side SIDE;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        if (remove(classNode.visibleAnnotations)) {
            throw new RuntimeException(String.format("Loading class %s on wrong side %s", name, SIDE.toString()));
        }

        classNode.methods.removeIf(method -> remove(method.visibleAnnotations));
        classNode.fields.removeIf(fields -> remove(fields.visibleAnnotations));

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public boolean remove(List<AnnotationNode> annotations) {
        if (annotations != null) {
            for (AnnotationNode annotation : annotations) {
                if (Type.getType(annotation.desc).equals(Type.getType(Strippable.class))) {
                    List<Object> values = annotation.values;
                    for (int i = 0; i < values.size() - 1; i += 2) {
                        Object key = values.get(i);
                        Object value = values.get(i + 1);
                        if (key instanceof String && ((String) key).equalsIgnoreCase("side")) {
                            if (value instanceof String[]) {
                                String side = ((String[]) value)[1];
                                if (!side.equalsIgnoreCase("universal") && !side.equalsIgnoreCase(SIDE.toString())) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
