package dev.tairasoul.stratum.mixin;

import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.Layer;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import dev.tairasoul.stratum.quack.LayeredWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public class LightEngineMixin {
	@Shadow
	@Final
	protected LightChunkGetter chunkSource;

	@Inject(at = @At("HEAD"), method = "getState", cancellable = true)
	void stratum$lightEngine$getState(BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
		var level = chunkSource.getLevel();
		if (level instanceof Level lvl) {
			MixinReplacementMethods.stratum$iterate(lvl, (layer) -> {
				var intercept = layer.interceptLight(blockPos, lvl.getBlockState(blockPos));
				if (intercept.getType() == InterceptType.MODIFY) {
					var s = intercept.getState();
					if (s != null)
						cir.setReturnValue(s);
					return true;
				}
				return false;
			});
		}
	}
}
