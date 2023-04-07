//package vazkii.quark.mixin;
//
//import com.electronwill.nightconfig.core.ConfigSpec;
//import net.minecraft.world.level.block.Block;
//import net.minecraftforge.common.ForgeConfigSpec;
//import net.minecraftforge.fml.config.IConfigSpec;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import vazkii.quark.base.Quark;
//import vazkii.quark.base.module.ModuleLoader;
//import vazkii.quark.content.world.module.GlimmeringWealdModule;
//
//import java.util.List;
//
////TODO: remove before releasing!
//@Deprecated(forRemoval = true)
//@Mixin(ForgeConfigSpec.class)
//public abstract class DebugConfigHackeryMixin implements IConfigSpec<ForgeConfigSpec> {
//
//    @Redirect(method = "correct(Lcom/electronwill/nightconfig/core/UnmodifiableConfig;Lcom/electronwill/nightconfig/core/CommentedConfig;Ljava/util/LinkedList;Ljava/util/List;Lcom/electronwill/nightconfig/core/ConfigSpec$CorrectionListener;Lcom/electronwill/nightconfig/core/ConfigSpec$CorrectionListener;Z)I",
//            at = @At(value = "INVOKE", target = "Lcom/electronwill/nightconfig/core/ConfigSpec$CorrectionListener;onCorrect(Lcom/electronwill/nightconfig/core/ConfigSpec$CorrectionAction;Ljava/util/List;Ljava/lang/Object;Ljava/lang/Object;)V"),
//            remap = false, require = 0)
//    public void watchWhatsWrong(ConfigSpec.CorrectionListener instance, ConfigSpec.CorrectionAction correctionAction, List<String> strings, Object old, Object newVal) {
//        if (ModuleLoader.INSTANCE.getConfig().getSpec() == this) {
//            Quark.LOG.error("Incorrect config detected. Forge will now create a backup. Reason:\n" +
//                    "parent path: {}, old value: {}, new value: {}", strings, old, newVal);
//        }
//
//        instance.onCorrect(correctionAction, strings, old, newVal);
//    }
//}
