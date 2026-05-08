package dev.tairasoul.stratum.mixin.client.sodium;

import dev.tairasoul.stratum.Stratum;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelSlice.class)
public class LevelSliceMixin {
	@Shadow
	@Final
	private ClientLevel level;

	@Inject(at = @At("HEAD"), method = "getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;", require = 0, cancellable = true)
	void stratum$sodium$levelSlice$getBlockState(int blockX, int blockY, int blockZ, CallbackInfoReturnable<BlockState> cir) {
		var pos = new BlockPos(
						blockX,
						blockY,
						blockZ
		);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptRender(pos, level.getBlockState(pos));
			if (intercept.getType() == InterceptType.MODIFY) {
				var s = intercept.getState();
				if (s != null) {
					cir.setReturnValue(s);
				}
				return true;
			}
			return false;
		});
	}
}