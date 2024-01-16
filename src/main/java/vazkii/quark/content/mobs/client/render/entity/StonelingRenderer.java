package vazkii.quark.content.mobs.client.render.entity;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.layer.StonelingItemLayer;
import vazkii.quark.content.mobs.client.layer.StonelingLichenLayer;
import vazkii.quark.content.mobs.client.model.StonelingModel;
import vazkii.quark.content.mobs.entity.Stoneling;

@OnlyIn(Dist.CLIENT)
public class StonelingRenderer extends MobRenderer<Stoneling, StonelingModel> {

	public StonelingRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.stoneling), 0.3F);
		addLayer(new StonelingItemLayer(this));
		addLayer(new StonelingLichenLayer(this));
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Stoneling entity) {
		return entity.getVariant().getTexture();
	}

}
