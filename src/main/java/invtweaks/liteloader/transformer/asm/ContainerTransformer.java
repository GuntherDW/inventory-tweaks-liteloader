package invtweaks.liteloader.transformer.asm;

import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.util.ObfuscationUtilities;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import invtweaks.liteloader.obf.InvTweaksObf;
import invtweaks.liteloader.transformer.asm.compatibility.ASMMethodInfo;
import invtweaks.liteloader.transformer.asm.compatibility.CompatibilityConfigLoader;
import invtweaks.liteloader.transformer.asm.compatibility.ContainerInfo;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.BlockPos;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class ContainerTransformer implements IClassTransformer {
    public static final String VALID_INVENTORY_METHOD = "invtweaks$validInventory";
    public static final String VALID_CHEST_METHOD = "invtweaks$validChest";
    public static final String LARGE_CHEST_METHOD = "invtweaks$largeChest";
    public static final String SHOW_BUTTONS_METHOD = "invtweaks$showButtons";
    public static final String ROW_SIZE_METHOD = "invtweaks$rowSize";
    public static final String SLOT_MAP_METHOD = "invtweaks$slotMap";
    // public static final String CONTAINER_CLASS_INTERNAL = "net/minecraft/inventory/Container";
    public static final String SLOT_MAPS_VANILLA_CLASS = "invtweaks/containers/VanillaSlotMaps";
    public static final String SLOT_MAPS_MODCOMPAT_CLASS = "invtweaks/containers/CompatibilitySlotMaps";
    public static final String ANNOTATION_CHEST_CONTAINER = "Linvtweaks/api/container/ChestContainer;";
    public static final String ANNOTATION_CHEST_CONTAINER_ROW_CALLBACK = "Linvtweaks/api/container/ChestContainer$RowSizeCallback;";
    public static final String ANNOTATION_CHEST_CONTAINER_LARGE_CALLBACK = "Linvtweaks/api/container/ChestContainer$IsLargeCallback;";
    public static final String ANNOTATION_INVENTORY_CONTAINER = "Linvtweaks/api/container/InventoryContainer;";
    public static final String ANNOTATION_IGNORE_CONTAINER = "Linvtweaks/api/container/IgnoreContainer;";
    public static final String ANNOTATION_CONTAINER_SECTION_CALLBACK = "Linvtweaks/api/container/ContainerSectionCallback;";

    public static ObfType obfType;
    enum ObfType { OBF, FML, MCP }

    private static Map<String, ContainerInfo> standardClasses = new HashMap<String, ContainerInfo>();
    private static Map<String, ContainerInfo> compatibilityClasses = new HashMap<String, ContainerInfo>();
    private static Map<String, ContainerInfo> configClasses = new HashMap<String, ContainerInfo>();
    private static String containerClassName;

    public ContainerTransformer() {
        // Check out which kind of Obf we are under.
        // If BlockPos is obfuscated we are definitely under OBF
        if(!BlockPos.class.getSimpleName().equals("BlockPos"))
            obfType = ObfType.OBF;
        else
            obfType = ObfuscationUtilities.fmlIsPresent() ? ObfType.FML : ObfType.MCP;
        LiteLoaderLogger.getLogger().info("[InventoryTweaks] ObfType detected : "+obfType);
    }

    public static String get_CONTAINER_CLASS_INTERNAL() {
        if(obfType == ObfType.OBF)
            return InvTweaksObf.cls_Container.obf.replace('.', '/');
        else
            return InvTweaksObf.cls_Container.srg.replace('.', '/');
    }

    public static String getClassName(InvTweaksObf obf) {
        if(obfType == ObfType.OBF)
            return obf.obf.replace('.', '/');
        else
            return obf.srg.replace('.', '/');
    }

    public static String getSimpleClassName(InvTweaksObf obf) {
        if (obfType == ObfType.OBF)
            return obf.obf;
        else
            return obf.srg;
    }

    /**
     * Alter class to contain information contained by ContainerInfo
     *
     * @param clazz Class to alter
     * @param info  Information used to alter class
     */
    public static void transformContainer(ClassNode clazz, ContainerInfo info) {
        ASMHelper.generateBooleanMethodConst(clazz, SHOW_BUTTONS_METHOD, info.showButtons);
        ASMHelper.generateBooleanMethodConst(clazz, VALID_INVENTORY_METHOD, info.validInventory);
        ASMHelper.generateBooleanMethodConst(clazz, VALID_CHEST_METHOD, info.validChest);

        if(info.largeChestMethod != null) {
            if(info.largeChestMethod.isStatic) {
                ASMHelper.generateForwardingToStaticMethod(clazz, LARGE_CHEST_METHOD, info.largeChestMethod.methodName,
                        info.largeChestMethod.methodType.getReturnType(),
                        info.largeChestMethod.methodClass,
                        info.largeChestMethod.methodType.getArgumentTypes()[0]);
            } else {
                ASMHelper.generateSelfForwardingMethod(clazz, LARGE_CHEST_METHOD, info.largeChestMethod.methodName,
                        info.largeChestMethod.methodType.getReturnType());
            }
        } else {
            ASMHelper.generateBooleanMethodConst(clazz, LARGE_CHEST_METHOD, info.largeChest);
        }

        if(info.rowSizeMethod != null) {
            if(info.rowSizeMethod.isStatic) {
                ASMHelper.generateForwardingToStaticMethod(clazz, ROW_SIZE_METHOD, info.rowSizeMethod.methodName,
                        info.rowSizeMethod.methodType.getReturnType(),
                        info.rowSizeMethod.methodClass,
                        info.rowSizeMethod.methodType.getArgumentTypes()[0]);
            } else {
                ASMHelper.generateSelfForwardingMethod(clazz, ROW_SIZE_METHOD, info.rowSizeMethod.methodName,
                        info.rowSizeMethod.methodType.getReturnType());
            }
        } else {
            ASMHelper.generateIntegerMethodConst(clazz, ROW_SIZE_METHOD, info.rowSize);
        }

        if(info.slotMapMethod.isStatic) {
            ASMHelper.generateForwardingToStaticMethod(clazz, SLOT_MAP_METHOD, info.slotMapMethod.methodName,
                    info.slotMapMethod.methodType.getReturnType(),
                    info.slotMapMethod.methodClass,
                    info.slotMapMethod.methodType.getArgumentTypes()[0]);
        } else {
            ASMHelper.generateSelfForwardingMethod(clazz, SLOT_MAP_METHOD, info.slotMapMethod.methodName,
                    info.slotMapMethod.methodType.getReturnType());
        }
    }

    /**
     * Alter class to contain default implementations of added methods.
     *
     * @param clazz Class to alter
     */
    public static void transformBaseContainer(ClassNode clazz) {
        ASMHelper.generateBooleanMethodConst(clazz, SHOW_BUTTONS_METHOD, false);
        ASMHelper.generateBooleanMethodConst(clazz, VALID_INVENTORY_METHOD, false);
        ASMHelper.generateBooleanMethodConst(clazz, VALID_CHEST_METHOD, false);
        ASMHelper.generateBooleanMethodConst(clazz, LARGE_CHEST_METHOD, false);
        ASMHelper.generateIntegerMethodConst(clazz, ROW_SIZE_METHOD, (short) 9);
        ASMHelper.generateForwardingToStaticMethod(clazz, SLOT_MAP_METHOD, "unknownContainerSlots",
                Type.getObjectType("java/util/Map"),
                Type.getObjectType(SLOT_MAPS_VANILLA_CLASS),
                Type.getObjectType(get_CONTAINER_CLASS_INTERNAL()));
    }

    public static void transformCreativeContainer(ClassNode clazz) {
        /* FIXME: Reqired methods cannot be compiled until SpecialSource update
        ASMHelper.generateForwardingToStaticMethod(clazz, STANDARD_INVENTORY_METHOD, "containerCreativeIsInventory",
                                                   Type.BOOLEAN_TYPE, Type.getObjectType(SLOT_MAPS_VANILLA_CLASS));
        ASMHelper.generateForwardingToStaticMethod(clazz, VALID_INVENTORY_METHOD, "containerCreativeIsInventory",
                                                   Type.BOOLEAN_TYPE, Type.getObjectType(SLOT_MAPS_VANILLA_CLASS));
        ASMHelper.generateBooleanMethodConst(clazz, VALID_CHEST_METHOD, false);
        ASMHelper.generateBooleanMethodConst(clazz, LARGE_CHEST_METHOD, false);
        ASMHelper.generateIntegerMethodConst(clazz, ROW_SIZE_METHOD, (short) 9);
        ASMHelper.generateForwardingToStaticMethod(clazz, SLOT_MAP_METHOD, "containerCreativeSlots",
                                                   Type.getObjectType("java/util/Map"),
                                                   Type.getObjectType(SLOT_MAPS_VANILLA_CLASS));
         */
    }

    private static void transformTextField(ClassNode clazz) {

        MethodInfo func_146195_b = new MethodInfo(InvTweaksObf.cls_GuiTextField, InvTweaksObf.mthd_setFocused, Void.TYPE, Boolean.TYPE);

        for(MethodNode method : (List<MethodNode>) clazz.methods) {

            if(func_146195_b.matches(method.name, method.desc)) {
                InsnList code = method.instructions;
                AbstractInsnNode returnNode = null;
                for(ListIterator<AbstractInsnNode> iterator = code.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode insn = iterator.next();

                    if(insn.getOpcode() == Opcodes.RETURN) {
                        returnNode = insn;
                        break;
                    }
                }

                if(returnNode != null) {
                    // Insert a call to helper method to disable sorting while a text field is focused
                    code.insertBefore(returnNode, new VarInsnNode(Opcodes.ILOAD, 1));
                    code.insertBefore(returnNode,
                            new MethodInsnNode(Opcodes.INVOKESTATIC, "invtweaks/liteloader/LiteModInvTweaks",
                                    "setTextboxModeStatic", "(Z)V"));

                    LiteLoaderLogger.getLogger().info("InvTweaks: successfully transformed setFocused/func_146195_b");
                } else {
                    LiteLoaderLogger.getLogger().warn("InvTweaks: unable to find return in setFocused/func_146195_b");
                }
            }
        }
    }

    public static ASMMethodInfo getCompatiblitySlotMapInfo(String name) {
        return getSlotMapInfo(Type.getObjectType(SLOT_MAPS_MODCOMPAT_CLASS), name, true);
    }

    public static ASMMethodInfo getVanillaSlotMapInfo(String name) {
        return getSlotMapInfo(Type.getObjectType(SLOT_MAPS_VANILLA_CLASS), name, true);
    }

    public static ASMMethodInfo getSlotMapInfo(Type mClass, String name, boolean isStatic) {
        return new ASMMethodInfo(
                Type.getMethodType(Type.getObjectType("java/util/Map"), Type.getObjectType(containerClassName)), mClass,
                name, isStatic);
    }

    // This needs to have access to the FML remapper so it needs to run when we know it's been set up correctly.
    private void lateInit() {
        // TODO: ContainerCreative handling
        // Standard non-chest type
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerPlayer),
                new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerPlayerSlots")));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerMerchant), new ContainerInfo(true, true, false));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerRepair),
                new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerPlayerSlots")));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerHopper), new ContainerInfo(true, true, false));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerBeacon), new ContainerInfo(true, true, false));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerBrewingStand),
                new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerBrewingSlots")));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerWorkbench),
                new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerWorkbenchSlots")));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerEnchantment),
                new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerEnchantmentSlots")));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerFurnace),
                new ContainerInfo(true, true, false, getVanillaSlotMapInfo("containerFurnaceSlots")));

        // Chest-type
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerDispenser),
                new ContainerInfo(true, false, true, (short) 3,
                        getVanillaSlotMapInfo("containerChestDispenserSlots")));
        standardClasses.put(getSimpleClassName(InvTweaksObf.cls_ContainerChest), new ContainerInfo(true, false, true,
                getVanillaSlotMapInfo(
                        "containerChestDispenserSlots")));

        // Mod compatibility
        // Equivalent Exchange 3
        compatibilityClasses.put("com.pahimar.ee3.inventory.ContainerAlchemicalBag",
                new ContainerInfo(true, false, true, true, (short) 13));
        compatibilityClasses.put("com.pahimar.ee3.inventory.ContainerAlchemicalChest",
                new ContainerInfo(true, false, true, true, (short) 13));
        compatibilityClasses.put("com.pahimar.ee3.inventory.ContainerPortableCrafting",
                new ContainerInfo(true, true, false,
                        getCompatiblitySlotMapInfo("ee3PortableCraftingSlots")));

        // Ender Storage
        // TODO Row size method. A bit less important because it's a config setting and 2 of 3 options give rowsize 9.
        compatibilityClasses.put("codechicken.enderstorage.storage.item.ContainerEnderItemStorage",
                new ContainerInfo(true, false, true));

        // Galacticraft
        compatibilityClasses.put("micdoodle8.mods.galacticraft.core.inventory.GCCoreContainerPlayer",
                new ContainerInfo(true, true, false,
                        getCompatiblitySlotMapInfo("galacticraftPlayerSlots")));


        try {
            configClasses = CompatibilityConfigLoader.load("config/InvTweaksCompatibility.xml");
        } catch(FileNotFoundException ex) {
            configClasses = new HashMap<String, ContainerInfo>();
        } catch(Exception ex) {
            configClasses = new HashMap<String, ContainerInfo>();
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(containerClassName == null) {
            containerClassName = get_CONTAINER_CLASS_INTERNAL();
            lateInit();
        }

        // Sanity checking so it doesn't look like this mod caused crashes when things were missing.
        if(bytes == null || bytes.length == 0) {
            return bytes;
        }

        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode(Opcodes.ASM4);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        cr.accept(cn, 0);

        // if("net.minecraft.inventory.Container".equals(transformedName)) {
        if(InvTweaksObf.cls_Container.matches(transformedName)) {
            LiteLoaderLogger.getLogger().info("InvTweaks: " + transformedName);

            transformBaseContainer(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }

        // if("net.minecraft.client.gui.inventory.ContainerCreative".equals(transformedName)) {
        if(InvTweaksObf.cls_ContainerCreative.matches(transformedName)) {
            LiteLoaderLogger.getLogger().info("InvTweaks: "+transformedName);

            transformCreativeContainer(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }

        // Transform classes with explicitly specified information
        ContainerInfo info = standardClasses.get(transformedName);
        if(info != null) {
            LiteLoaderLogger.getLogger().info("InvTweaks: "+transformedName);

            transformContainer(cn, info);

            cn.accept(cw);
            return cw.toByteArray();
        }

        if("invtweaks.InvTweaksObfuscation".equals(transformedName)) {
            LiteLoaderLogger.getLogger().info("InvTweaks: "+transformedName);

            Type containertype = Type.getObjectType(containerClassName);
            for(MethodNode method : (List<MethodNode>) cn.methods) {
                if("isValidChest".equals(method.name)) {
                    ASMHelper.replaceSelfForwardingMethod(method, VALID_CHEST_METHOD, containertype);
                } else if("isValidInventory".equals(method.name)) {
                    ASMHelper.replaceSelfForwardingMethod(method, VALID_INVENTORY_METHOD, containertype);
                } else if("showButtons".equals(method.name)) {
                    ASMHelper.replaceSelfForwardingMethod(method, SHOW_BUTTONS_METHOD, containertype);
                } else if("getSpecialChestRowSize".equals(method.name)) {
                    ASMHelper.replaceSelfForwardingMethod(method, ROW_SIZE_METHOD, containertype);
                } else if("getContainerSlotMap".equals(method.name)) {
                    ASMHelper.replaceSelfForwardingMethod(method, SLOT_MAP_METHOD, containertype);
                } else if("isLargeChest".equals(method.name)) {
                    ASMHelper.replaceSelfForwardingMethod(method, LARGE_CHEST_METHOD, containertype);
                }
            }

            cn.accept(cw);
            return cw.toByteArray();
        }


        info = configClasses.get(transformedName);
        if(info != null) {
            LiteLoaderLogger.getLogger().info("InvTweaks: "+transformedName);

            transformContainer(cn, info);

            cn.accept(cw);
            return cw.toByteArray();
        }

        if(cn.visibleAnnotations != null) {
            for(AnnotationNode annotation : (List<AnnotationNode>) cn.visibleAnnotations) {
                if(annotation != null) {
                    ContainerInfo apiInfo = null;

                    if(ANNOTATION_CHEST_CONTAINER.equals(annotation.desc)) {
                        short rowSize = 9;
                        boolean isLargeChest = false;
                        boolean showButtons = true;

                        if(annotation.values != null) {
                            for(int i = 0; i < annotation.values.size(); i += 2) {
                                String valueName = (String) annotation.values.get(i);
                                Object value = annotation.values.get(i + 1);

                                if("rowSize".equals(valueName)) {
                                    rowSize = (short) ((Integer) value).intValue();
                                } else if("isLargeChest".equals(valueName)) {
                                    isLargeChest = (Boolean) value;
                                } else if("showButtons".equals(valueName)) {
                                    showButtons = (Boolean) value;
                                }
                            }
                        }

                        apiInfo = new ContainerInfo(showButtons, false, true, isLargeChest, rowSize);

                        MethodNode row_method = findAnnotatedMethod(cn, ANNOTATION_CHEST_CONTAINER_ROW_CALLBACK);

                        if(row_method != null) {
                            apiInfo.rowSizeMethod = new ASMMethodInfo(Type.getMethodType(row_method.desc),
                                    Type.getObjectType(cn.name), row_method.name);
                        }

                        MethodNode large_method = findAnnotatedMethod(cn, ANNOTATION_CHEST_CONTAINER_LARGE_CALLBACK);

                        if(large_method != null) {
                            apiInfo.largeChestMethod = new ASMMethodInfo(Type.getMethodType(large_method.desc),
                                    Type.getObjectType(cn.name), large_method.name);
                        }
                    } else if(ANNOTATION_INVENTORY_CONTAINER.equals(annotation.desc)) {
                        boolean showOptions = true;

                        if(annotation.values != null) {
                            for(int i = 0; i < annotation.values.size(); i += 2) {
                                String valueName = (String) annotation.values.get(i);
                                Object value = annotation.values.get(i + 1);

                                if("showOptions".equals(valueName)) {
                                    showOptions = (Boolean) value;
                                }
                            }
                        }

                        apiInfo = new ContainerInfo(showOptions, true, false);
                    } else if(ANNOTATION_IGNORE_CONTAINER.equals(annotation.desc)) {
                        // Annotation to restore default properties.

                        transformBaseContainer(cn);

                        cn.accept(cw);
                        return cw.toByteArray();
                    }

                    if(apiInfo != null) {
                        // Search methods to see if any have the ContainerSectionCallback attribute.
                        MethodNode method = findAnnotatedMethod(cn, ANNOTATION_CONTAINER_SECTION_CALLBACK);

                        if(method != null) {
                            apiInfo.slotMapMethod = new ASMMethodInfo(Type.getMethodType(method.desc),
                                    Type.getObjectType(cn.name), method.name);
                        }

                        transformContainer(cn, apiInfo);

                        cn.accept(cw);
                        return cw.toByteArray();
                    }
                }
            }
        }

        info = compatibilityClasses.get(transformedName);
        if(info != null) {
            LiteLoaderLogger.getLogger().info("InvTweaks: "+transformedName);

            transformContainer(cn, info);

            cn.accept(cw);
            return cw.toByteArray();
        }

        // if("net.minecraft.client.gui.GuiTextField".equals(transformedName)) {
        if(InvTweaksObf.cls_GuiTextField.matches(transformedName)) {
            LiteLoaderLogger.getLogger().info("InvTweaks: "+transformedName);

            transformTextField(cn);

            cn.accept(cw);
            return cw.toByteArray();
        }

        return bytes;
    }

    private MethodNode findAnnotatedMethod(ClassNode cn, String annotationDesc) {
        for(MethodNode method : (List<MethodNode>) cn.methods) {
            if(method.visibleAnnotations != null) {
                for(AnnotationNode methodAnnotation : (List<AnnotationNode>) method.visibleAnnotations) {
                    if(annotationDesc.equals(methodAnnotation.desc)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
}