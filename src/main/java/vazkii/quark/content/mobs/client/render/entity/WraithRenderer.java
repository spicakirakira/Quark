package vazkii.quark.content.mobs.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import vazkii.quark.base.client.handler.ModelHandler;
import vazkii.quark.content.mobs.client.model.WraithModel;
import vazkii.quark.content.mobs.entity.Wraith;

import javax.annotation.Nonnull;

public class WraithRenderer extends MobRenderer<Wraith, WraithModel> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/model/entity/wraith.png");

	public WraithRenderer(EntityRendererProvider.Context context) {
		super(context, ModelHandler.model(ModelHandler.wraith), 0F);
	}

	@Nonnull
	@Override
	public ResourceLocation getTextureLocation(@Nonnull Wraith entity) {
		return TEXTURE;
	}

}

