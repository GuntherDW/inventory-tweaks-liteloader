package invtweaks.liteloader;

import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import invtweaks.InvTweaks;
import invtweaks.InvTweaksHandlerSorting;
import invtweaks.InvTweaksItemTreeLoader;
import invtweaks.InvTweaksObfuscation;
import invtweaks.api.IItemTreeListener;
import invtweaks.api.InvTweaksAPI;
import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * @author GuntherDW
 */
public class LiteModInvTweaks implements InvTweaksAPI, Tickable {

    public static final KeyBinding KEYBINDING_SORT = new KeyBinding("invtweaks.key.sort", Keyboard.KEY_R, "invtweaks.key.category");
    public boolean serverSupportEnabled = false;
    public boolean serverSupportDetected = false;

    public static LiteModInvTweaks instance;
    public static InvTweaks invTweaksInstance;


    // Helper for ASM transform of GuiTextField to disable sorting on focus.
    public static void setTextboxModeStatic(boolean enabled) {
        instance.setTextboxMode(enabled);
    }

    /**
     * Add a listener for ItemTree load events
     *
     * @param listener
     */
    @Override
    public void addOnLoadListener(IItemTreeListener listener) {
        InvTweaksItemTreeLoader.addOnLoadListener(listener);
    }

    /**
     * Remove a listener for ItemTree load events
     *
     * @param listener
     * @return true if the listener was previously added
     */
    @Override
    public boolean removeOnLoadListener(IItemTreeListener listener) {
        return InvTweaksItemTreeLoader.removeOnLoadListener(listener);
    }

    /**
     * Toggle sorting shortcut state.
     *
     * @param enabled
     */
    @Override
    public void setSortKeyEnabled(boolean enabled) {
        invTweaksInstance.setSortKeyEnabled(enabled);
    }

    public void sortComplete() {
        /* if (serverSupportEnabled) {
            invtweaksChannel.get(Side.CLIENT).writeOutbound(new ITPacketSortComplete());
        } */
    }

    /**
     * Toggle sorting shortcut supression.
     * Unlike setSortKeyEnabled, this flag is automatically cleared when GUIs are closed.
     *
     * @param enabled
     */
    @Override
    public void setTextboxMode(boolean enabled) {
        invTweaksInstance.setTextboxMode(enabled);
    }

    /**
     * Compare two items using the default (non-rule based) algorithm,
     * sutable for an implementation of Comparator&lt;ItemStack&gt;.
     *
     * @param i
     * @param j
     * @return A value with a sign representing the relative order of the item stacks
     */
    @Override
    public int compareItems(ItemStack i, ItemStack j) {
        return invTweaksInstance.compareItems(i, j);
    }

    /**
     * Initiate a sort as if the player had clicked on a sorting button or pressed the sort key.
     *
     * @param section
     * @param method
     */
    @Override
    public void sort(ContainerSection section, SortingMethod method) {
        // TODO: This seems like something useful enough to be a util method somewhere.
        Minecraft mc = Minecraft.getMinecraft();
        Container currentContainer = mc.thePlayer.inventoryContainer;
        if (mc.currentScreen instanceof GuiContainer) {
            currentContainer = ((GuiContainer) mc.currentScreen).inventorySlots;
        }

        try {
            new InvTweaksHandlerSorting(mc, invTweaksInstance.getConfigManager().getConfig(), section, method, InvTweaksObfuscation.getSpecialChestRowSize(currentContainer)).sort();
        } catch (Exception e) {
            InvTweaks.logInGameErrorStatic("invtweaks.sort.chest.error", e);
            e.printStackTrace();
        }
    }

    /**
     * Called every frame
     *
     * @param minecraft    Minecraft instance
     * @param partialTicks Partial tick value
     * @param inGame       True if in-game, false if in the menu
     * @param clock        True if this is a new tick, otherwise false if it's a regular frame
     */
    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (clock) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null) {
                if (mc.currentScreen != null) {
                    invTweaksInstance.onTickInGUI(mc.currentScreen);
                } else {
                    invTweaksInstance.onTickInGame();
                }
            }
        }
    }

    /**
     * Get the mod version string
     *
     * @return the mod version as a string
     */
    @Override
    public String getVersion() {
        return "0.1";
    }

    /**
     * Do startup stuff here, minecraft is not fully initialised when this function is called so mods *must not*
     * interact with minecraft in any way here
     *
     * @param configPath Configuration path to use
     */
    @Override
    public void init(File configPath) {
        instance = this;

        // Instantiate mod core
        Minecraft mc = Minecraft.getMinecraft();
        invTweaksInstance = new InvTweaks(mc);

        InvTweaks.log = LiteLoaderLogger.getLogger();

        LiteLoader.getInput().registerKeyBinding(KEYBINDING_SORT);
    }

    /**
     * Called when the loader detects that a version change has happened since this mod was last loaded
     *
     * @param version       new version
     * @param configPath    Path for the new version-specific config
     * @param oldConfigPath Path for the old version-specific config
     */
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    /**
     * Get the display name
     *
     * @return display name
     */
    @Override
    public String getName() {
        return "InventoryTweaks LiteLoader";
    }

    public void slotClick(PlayerControllerMP playerController, int windowId, int slot, int data, int action,
                          EntityPlayer player) {
        //int modiferKeys = (shiftHold) ? 1 : 0 /* XXX Placeholder */;
        /* if (serverSupportEnabled) {
            player.openContainer.slotClick(slot, data, action, player);

            invtweaksChannel.get(Side.CLIENT).writeOutbound(new ITPacketClick(slot, data, action, windowId));
        } else { */
            playerController.windowClick(windowId, slot, data, action, player);
        /* } */
    }
}
