package invtweaks.liteloader.obf;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * @author GuntherDW
 */
public class InvTweaksObf extends Obf {

    // CLASSES
    public static final InvTweaksObf                         cls_Container = new InvTweaksObf("net.minecraft.inventory.Container",                                    "aib");
    public static final InvTweaksObf                      cls_GuiContainer = new InvTweaksObf("net.minecraft.client.gui.inventory.GuiContainer",                      "byl");
    public static final InvTweaksObf                         cls_GuiScreen = new InvTweaksObf("net.minecraft.client.gui.GuiScreen",                                   "bxf");
    public static final InvTweaksObf cls_GuiContainerCreative$CreativeSlot = new InvTweaksObf("net.minecraft.client.gui.inventory.GuiContainerCreative$CreativeSlot", "bzb");
    public static final InvTweaksObf                 cls_ContainerCreative = new InvTweaksObf("net.minecraft.client.gui.inventory.ContainerCreative",                 "byz");
    public static final InvTweaksObf                      cls_GuiTextField = new InvTweaksObf("net.minecraft.client.gui.GuiTextField",                                "bul");
    public static final InvTweaksObf                   cls_ContainerPlayer = new InvTweaksObf("net.minecraft.inventory.ContainerPlayer",                              "ajb");
    public static final InvTweaksObf                 cls_ContainerMerchant = new InvTweaksObf("net.minecraft.inventory.ContainerMerchant",                            "ajf");
    public static final InvTweaksObf                   cls_ContainerRepair = new InvTweaksObf("net.minecraft.inventory.ContainerRepair",                              "aid");
    public static final InvTweaksObf                   cls_ContainerHopper = new InvTweaksObf("net.minecraft.inventory.ContainerHopper",                              "aix");
    public static final InvTweaksObf                   cls_ContainerBeacon = new InvTweaksObf("net.minecraft.inventory.ContainerBeacon",                              "aig");
    public static final InvTweaksObf             cls_ContainerBrewingStand = new InvTweaksObf("net.minecraft.inventory.ContainerBrewingStand",                        "aii");
    public static final InvTweaksObf                cls_ContainerWorkbench = new InvTweaksObf("net.minecraft.inventory.ContainerWorkbench",                           "aio");
    public static final InvTweaksObf              cls_ContainerEnchantment = new InvTweaksObf("net.minecraft.inventory.ContainerEnchantment",                         "aiq");
    public static final InvTweaksObf                  cls_ContainerFurnace = new InvTweaksObf("net.minecraft.inventory.ContainerFurnace",                             "aiv");
    public static final InvTweaksObf                cls_ContainerDispenser = new InvTweaksObf("net.minecraft.inventory.ContainerDispenser",                           "aip");
    public static final InvTweaksObf                    cls_ContainerChest = new InvTweaksObf("net.minecraft.inventory.ContainerChest",                               "aim");

    // STATIC ACCESSORS
    public static final InvTweaksObf                     cls_IGuiContainer = new InvTweaksObf("invtweaks.liteloader.accessor.IGuiContainer"                            );
    public static final InvTweaksObf                        cls_IGuiScreen = new InvTweaksObf("invtweaks.liteloader.accessor.IGuiScreen"                               );

    // METHODS
    public static final InvTweaksObf                       mthd_setFocused = new InvTweaksObf("func_146195_b",     "b",  "setFocused");


    // FIELDS
    public static final InvTweaksObf                           fld_guiLeft = new InvTweaksObf("field_147003_i",    "i",  "guiLeft");
    public static final InvTweaksObf                            fld_guiTop = new InvTweaksObf("field_147009_r",    "r",  "guiTop");
    public static final InvTweaksObf                             fld_xSize = new InvTweaksObf("field_146999_f",    "f",  "xSize");
    public static final InvTweaksObf                             fld_ySize = new InvTweaksObf("field_147000_g",    "g",  "ySize");
    public static final InvTweaksObf                        fld_buttonList = new InvTweaksObf("field_146292_n",    "n",  "buttonList");



    /**
     * @param seargeName
     * @param obfName
     * @param mcpName
     */
    protected InvTweaksObf(String seargeName, String obfName, String mcpName) {
        super(seargeName, obfName, mcpName);
    }

    /**
     * @param seargeName
     * @param obfName
     */
    protected InvTweaksObf(String seargeName, String obfName) {
        super(seargeName, obfName);
    }

    /**
     * @param mcpName
     */
    protected InvTweaksObf(String mcpName) {
        super(mcpName);
    }
}
