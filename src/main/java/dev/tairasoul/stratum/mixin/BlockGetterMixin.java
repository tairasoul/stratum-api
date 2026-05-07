package dev.tairasoul.stratum.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "pos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Expression("this.getBlockState(pos)")
	@ModifyExpressionValue(
					method = "method_17743",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	default BlockState stratum$BlockGetter$clip$getBlockState(BlockState original, @Local(argsOnly = true) BlockPos pos) {
		var level = (Level)this;
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptCollide(pos, original);
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
