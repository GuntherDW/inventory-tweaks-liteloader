package invtweaks.liteloader.transformer;

import com.mumfrey.liteloader.transformers.access.AccessorTransformer;
import invtweaks.liteloader.obf.InvTweaksObf;

/**
 * @author GuntherDW
 */
public class InvTweaksAccessTransformer extends AccessorTransformer {

    /**
     * Subclasses should add their accessors here
     */
    @Override
    protected void addAccessors() {
        this.addAccessor(InvTweaksObf.cls_IGuiContainer.name);
        this.addAccessor(InvTweaksObf.cls_IGuiScreen.name);
    }
}
