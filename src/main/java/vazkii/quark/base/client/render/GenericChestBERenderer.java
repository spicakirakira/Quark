package vazkii.quark.base.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import javax.annotation.Nonnull;
import java.util.Calendar;

// A copy of ChestTileEntityRenderer from vanilla but less private
public abstract class GenericChestBERenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<T> {

	public final ModelPart lid;
	public final ModelPart bottom;
	public final ModelPart lock;
	public final ModelPart doubleLeftLid;
	public final ModelPart doubleLeftBottom;
	public final ModelPart doubleLeftLock;
	public final ModelPart doubleRightLid;
	public final ModelPart doubleRightBottom;
	public final ModelPart doubleRightLock;
	public boolean isChristmas;

	public GenericChestBERenderer(BlockEntityRendererProvider.Context context) {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26) {
			this.isChristmas = true;
		}

		ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
		this.bottom = modelpart.getChild("bottom");
		this.lid = modelpart.getChild("lid");
		this.lock = modelpart.getChild("lock");
		ModelPart modelpart1 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
		this.doubleLeftBottom = modelpart1.getChild("bottom");
		this.doubleLeftLid = modelpart1.getChild("lid");
		this.doubleLeftLock = modelpart1.getChild("lock");
		ModelPart modelpart2 = context.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
		this.doubleRightBottom = modelpart2.getChild("bottom");
		this.doubleRightLid = modelpart2.getChild("lid");
		this.doubleRightLock = modelpart2.getChild("lock");
	}

	public static LayerDefinition createSingleBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
		partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
		partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public static LayerDefinition createDoubleBodyRightLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
		partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
		partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public static LayerDefinition createDoubleBodyLeftLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
		partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
		partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void render(T chest, float partialTicks, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource buffer, int light, int overlay) {
		Level world = chest.getLevel();
		boolean flag = world != null;
		BlockState blockstate = flag ? chest.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.getValues().containsKey(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = blockstate.getBlock();
		if (block instanceof AbstractChestBlock<?> abstractchestblock) {
			boolean flag1 = chesttype != ChestType.SINGLE;
			matrix.pushPose();
			float f = blockstate.getValue(ChestBlock.FACING).toYRot();
			matrix.translate(0.5D, 0.5D, 0.5D);
			matrix.mulPose(Vector3f.YP.rotationDegrees(-f));
			matrix.translate(-0.5D, -0.5D, -0.5D);
			DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.combine(blockstate, world, chest.getBlockPos(), true);
			} else {
				icallbackwrapper = DoubleBlockCombiner.Combiner::acceptNone;
			}

			float f1 = icallbackwrapper.apply(ChestBlock.opennessCombiner(chest)).get(partialTicks);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.apply(new BrightnessCombiner<>()).applyAsInt(light);
			Material material = getMaterialFinal(chest, chesttype); // <- Changed here
			if(material != null) {
				VertexConsumer ivertexbuilder = material.buffer(buffer, RenderType::entityCutout);
				if (flag1) {
					if (chesttype == ChestType.RIGHT) {
						this.render(matrix, ivertexbuilder, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, overlay);
					} else {
						this.render(matrix, ivertexbuilder, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, overlay);
					}
				} else {
					this.render(matrix, ivertexbuilder, this.lid, this.lock, this.bottom, f1, i, overlay);
				}
			}

			matrix.popPose();
		}
	}

	public final Material getMaterialFinal(T t, ChestType type) {
		if(isChristmas)
			return Sheets.chooseMaterial(t, type, true);

		return getMaterial(t, type);
	}

	public abstract Material getMaterial(T t, ChestType type);

	public void render(PoseStack matrix, VertexConsumer vertexConsumer, ModelPart lid, ModelPart lock, ModelPart bottom, float openness, int brightness, int overlay) {
		lid.xRot = -(openness * ((float)Math.PI / 2F));
		lock.xRot = lid.xRot;
		lid.render(matrix, vertexConsumer, brightness, overlay);
		lock.render(matrix, vertexConsumer, brightness, overlay);
		bottom.render(matrix, vertexConsumer, brightness, overlay);
	}
}
