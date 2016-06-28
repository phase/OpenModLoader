package xyz.openmodloader.launcher;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import net.minecraft.launchwrapper.IClassTransformer;

public class OMLAccessTransformer implements IClassTransformer {
    private static SetMultimap<String, String> entries = Multimaps.newSetMultimap(Maps.newHashMap(), Sets::newHashSet);

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        Set<String> entries = OMLAccessTransformer.entries.get(transformedName);
        if (entries != null) {
            ClassNode c = createClassNode(basicClass);
            c.fields.stream().filter(f -> entries.contains(f.name) || entries.contains("*")).forEach(f -> f.access = getPublicAccess(f.access));
            c.methods.stream().filter(f -> entries.contains(f.name + f.desc) || entries.contains("*()")).forEach(f -> f.access = getPublicAccess(f.access));
            return getBytes(c);
        }
        return basicClass;
    }

    private int getPublicAccess(int access) {
        if (Modifier.isFinal(access)) {
            access -= Modifier.FINAL;
        }
        if (Modifier.isPublic(access)) {
            return access;
        }
        if (Modifier.isProtected(access)) {
            access -= Modifier.PROTECTED;
        } else if (Modifier.isPrivate(access)) {
            access -= Modifier.PRIVATE;
        }
        return access + Modifier.PUBLIC;
    }

    private static byte[] getBytes(ClassNode c) {
        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        c.accept(w);
        return w.toByteArray();
    }

    private static ClassNode createClassNode(byte[] b) {
        ClassNode c = new ClassNode();
        ClassReader r = new ClassReader(b);
        r.accept(c, ClassReader.EXPAND_FRAMES);
        return c;
    }

    public static Multimap<String, String> getEntries() {
        return entries;
    }

    public static void loadAccessTransformers(List<String> lines) {
        Multimap<String, String> ats = OMLAccessTransformer.getEntries();
        lines.stream().filter(line -> line.matches("\\w+((\\.\\w+)+|)\\s+(\\w+(\\(\\S+|)|\\*\\(\\)|\\*)")).forEach(line -> {
            String[] parts = line.split("\\s");
            ats.put(parts[0], parts[1]);
        });
    }
}
