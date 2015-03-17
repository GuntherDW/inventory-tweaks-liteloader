package invtweaks.liteloader.accessor;

import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.ObfTableClass;
import invtweaks.liteloader.obf.InvTweaksObf;

/**
 * @author GuntherDW
 */
@ObfTableClass(InvTweaksObf.class)
@Accessor("cls_GuiContainer")
public interface IGuiContainer {

    @Accessor("fld_guiLeft") public abstract int getGuiLeft();
    @Accessor("fld_guiTop")  public abstract int getGuiTop();
    @Accessor("fld_xSize")   public abstract int getXSize();
    @Accessor("fld_ySize")   public abstract int getYSize();

}
