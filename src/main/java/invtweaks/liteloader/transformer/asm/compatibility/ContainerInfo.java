package invtweaks.liteloader.transformer.asm.compatibility;

import invtweaks.liteloader.transformer.asm.ContainerTransformer;

@SuppressWarnings("UnusedDeclaration")
public class ContainerInfo {
    public boolean showButtons = false;
    public boolean validInventory = false;
    public boolean validChest = false;
    public boolean largeChest = false;
    public short rowSize = 9;
    public ASMMethodInfo slotMapMethod = ContainerTransformer.getVanillaSlotMapInfo("unknownContainerSlots");
    public ASMMethodInfo rowSizeMethod = null;
    public ASMMethodInfo largeChestMethod = null;

    public ContainerInfo() {
    }

    public ContainerInfo(boolean standard, boolean validInv, boolean validCh) {
        showButtons = standard;
        validInventory = validInv;
        validChest = validCh;
    }

    public ContainerInfo(boolean standard, boolean validInv, boolean validCh, boolean largeCh) {
        showButtons = standard;
        validInventory = validInv;
        validChest = validCh;
        largeChest = largeCh;
    }

    public ContainerInfo(boolean standard, boolean validInv, boolean validCh, ASMMethodInfo slotMap) {
        showButtons = standard;
        validInventory = validInv;
        validChest = validCh;
        slotMapMethod = slotMap;
    }

    public ContainerInfo(boolean standard, boolean validInv, boolean validCh, short rowS) {
        showButtons = standard;
        validInventory = validInv;
        validChest = validCh;
        rowSize = rowS;
    }

    public ContainerInfo(boolean standard, boolean validInv, boolean validCh, boolean largeCh, short rowS) {
        showButtons = standard;
        validInventory = validInv;
        validChest = validCh;
        largeChest = largeCh;
        rowSize = rowS;
    }

    public ContainerInfo(boolean standard, boolean validInv, boolean validCh, short rowS, ASMMethodInfo slotMap) {
        showButtons = standard;
        validInventory = validInv;
        validChest = validCh;
        rowSize = rowS;
        slotMapMethod = slotMap;
    }
}
