package invtweaks.liteloader.accessor;

import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.ObfTableClass;
import invtweaks.liteloader.obf.InvTweaksObf;
import net.minecraft.inventory.Slot;

/**
 * @author GuntherDW
 */
@ObfTableClass(InvTweaksObf.class)
@Accessor("cls_GuiContainerCreative$CreativeSlot")
public interface IGuiContainerCreative$CreativeSlot {
    @Accessor("Slot") public abstract Slot getSlot();
}
