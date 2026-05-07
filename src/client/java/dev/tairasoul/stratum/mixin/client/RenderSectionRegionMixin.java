package dev.tairasoul.stratum.mixin.client;

import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSectionRegion.class)
public class RenderSectionRegionMixin {
	@Shadow
	@Final
	private Level level;

	@Inject(at = @At("HEAD"), method = "getBlockState", cancellable = true)
	void stratum$getBlockState(BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptCollide(blockPos, null);
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
