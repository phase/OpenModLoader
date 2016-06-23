package xyz.openmodloader.launcher;

import net.minecraft.launchwrapper.IClassTransformer;

public class OMLClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return basicClass;
    }
}
