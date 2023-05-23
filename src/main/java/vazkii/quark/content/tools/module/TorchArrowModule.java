package vazkii.quark.content.tools.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.item.QuarkArrowItem;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tools.client.render.entity.TorchArrowRenderer;
import vazkii.quark.content.tools.entity.TorchArrow;

@LoadModule(category = ModuleCategory.TOOLS)
public class TorchArrowModule extends QuarkModule {

	@Config public static boolean extinguishOnMiss = false;
	
	public static EntityType<TorchArrow> torchArrowType;
	
	@Hint public static Item torch_arrow;
	
	@Override
	public void register() {
		torch_arrow = new QuarkArrowItem.Impl("torch_arrow", this, (level, stack, living) -> new TorchArrow(level, living));
		
		torchArrowType = EntityType.Builder.<TorchArrow>of(TorchArrow::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(4)
				.updateInterval(20) // update interval
				.build("torch_arrow");
		RegistryHelper.register(torchArrowType, "torch_arrow", Registry.ENTITY_TYPE_REGISTRY);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		EntityRenderers.register(torchArrowType, TorchArrowRenderer::new);
	}
	
}
