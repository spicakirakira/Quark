package vazkii.quark.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.item.QuarkMusicDiscItem;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Inject(method = "playStreamingMusic(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/RecordItem;)V",
			remap = false,
			at = @At(value = "JUMP", ordinal = 1),
			cancellable = true)
	public void playStreamingMusic(SoundEvent soundIn, BlockPos pos, RecordItem musicDiscItem, CallbackInfo info) {
		if(musicDiscItem instanceof QuarkMusicDiscItem quarkDisc && quarkDisc.playAmbientSound(pos))
			info.cancel();
	}


}
