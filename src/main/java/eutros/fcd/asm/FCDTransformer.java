package eutros.fcd.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

@SuppressWarnings("unused")
public class FCDTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(!name.equals("com.jaquadro.minecraft.storagedrawers.inventory.ContainerFramingTable")) {
            return basicClass;
        }

        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        FCDTweaker.accept(node);

        ClassWriter writer = new FCDClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);

        return basicClass;
    }

}
