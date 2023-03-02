package vazkii.quark.base.handler;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.content.building.block.VariantChestBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

//this is a hacky class to defer creative tab registration after the modules have been instantiated, so we can use their conditions properly
//tbh it should have been integrated in ARL
public class CreativeTabHandler {

    private static final List<TabInfo> TAB_INFOS = new ArrayList<>();
    private static boolean finalized = false;


    public static void addTab(IQuarkBlock block, @Nullable CreativeModeTab creativeTab, BooleanSupplier isEnabled) {
        if(finalized){
            //if has been finalized we assign them immediately. This usually shouldn't happen with blocks from quark but other dependencies might. If it does conditions might not be taken into account
            RegistryHelper.setCreativeTab((Block) block, creativeTab);
        }
        if (creativeTab != null) {
            TAB_INFOS.add(new TabInfo(block, creativeTab, isEnabled));
        }
    }

    private static final List<IQuarkBlock> test = new ArrayList<>();

    public static void addTab(IQuarkBlock block, @Nullable CreativeModeTab creativeTab) {
        addTab(block, creativeTab, block::isEnabled);
    }

    //actually registers the tabs
    public static void finalizeTabs() {
        TAB_INFOS.forEach(i-> {
            if(i.enabled.getAsBoolean()) {
                RegistryHelper.setCreativeTab((Block) i.block, i.tab);
            }
        });
        TAB_INFOS.clear();
        finalized = true;
    }

    private record TabInfo(IQuarkBlock block, CreativeModeTab tab, BooleanSupplier enabled) {
    }

}
