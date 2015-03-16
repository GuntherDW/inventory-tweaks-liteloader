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
    @Accessor("fld_guiLeft") public abstract int setGuiLeft(int guiLeft);

    @Accessor("fld_guiTop")  public abstract int getGuiTop();
    @Accessor("fld_guiTop")  public abstract int setGuiTop(int guiTop);

    @Accessor("fld_xSize")   public abstract int getXSize();
    @Accessor("fld_xSize")   public abstract int setXSize(int xSize);

    @Accessor("fld_ySize")   public abstract int getYSize();
    @Accessor("fld_ySize")   public abstract int setYSize(int ySize);

}
