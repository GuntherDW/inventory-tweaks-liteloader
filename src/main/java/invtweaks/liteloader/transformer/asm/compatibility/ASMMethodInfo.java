package invtweaks.liteloader.transformer.asm.compatibility;

import org.objectweb.asm.Type;

public class ASMMethodInfo {
    public Type methodType;
    public Type methodClass;
    public String methodName;
    public boolean isStatic = false;

    public ASMMethodInfo(Type mType, Type mClass, String name) {
        methodType = mType;
        methodClass = mClass;
        methodName = name;
    }

    public ASMMethodInfo(Type mType, Type mClass, String name, boolean stat) {
        methodType = mType;
        methodClass = mClass;
        methodName = name;
        isStatic = stat;
    }
}
