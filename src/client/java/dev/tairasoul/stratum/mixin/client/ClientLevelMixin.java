package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
//	@Inject(
//		method = "doAnimateTick",
//		at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/util/BlockPos$MutableBlockPos;set(III)V",
//			shift = At.Shift.AFTER
//		)
//	)
	@Inject(
					method = "doAnimateTick",
					at = @At(
									value = "INVOKE",
									target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
									shift = At.Shift.AFTER
					),
					cancellable = true
	)
	void stratum$clientLevel$doAnimateTick(int i, int j, int k, int l, RandomSource randomSource, Block block, BlockPos.MutableBlockPos mutableBlockPos, CallbackInfo ci) {
		var level = (Level)(Object)this;
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptRender(mutableBlockPos, level.getBlockState(mutableBlockPos));
			if (intercept.getType() != InterceptType.PASSTHROUGH) {
				ci.cancel();
				return true;
			}
			return false;
		});
	}

	@Definition(id = "getBlockState", method = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "blockPos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Expression("this.getBlockState(blockPos)")
	@ModifyExpressionValue(
					at = @At("MIXINEXTRAS:EXPRESSION"),
					method = "addBreakingBlockEffect"
	)
	BlockState stratum$clientLevel$addBreakingBlockEffect(BlockState original, BlockPos blockPos) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate((Level)(Object)this, (layer) -> {
			var intercept = layer.interceptRender(blockPos, original);
			if (intercept.getType() == InterceptType.MODIFY) {
				var s = intercept.getState();
				if (s != null)
					state.set(s);
				return true;
			}
			return false;
		});
		return state.get();
	}
}
