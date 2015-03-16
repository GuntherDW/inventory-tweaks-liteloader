package invtweaks.liteloader.accessor;

import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.ObfTableClass;
import invtweaks.liteloader.obf.InvTweaksObf;

import java.util.List;

/**
 * @author GuntherDW
 */
@ObfTableClass(InvTweaksObf.class)
@Accessor("cls_GuiScreen")
public interface IGuiScreen {

    @Accessor("fld_buttonList") public abstract List getButtonList();
    @Accessor("fld_buttonList") public abstract void setButtonList(List buttonList);

}
