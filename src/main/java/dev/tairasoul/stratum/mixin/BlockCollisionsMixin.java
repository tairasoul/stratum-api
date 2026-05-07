package dev.tairasoul.stratum.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(BlockCollisions.class)
public class BlockCollisionsMixin {
	@Final
	@Shadow
	private BlockPos.MutableBlockPos pos;

	@Definition(id = "chunk", local = @Local(type = BlockGetter.class))
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/BlockGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "pos", field = "Lnet/minecraft/world/level/BlockCollisions;pos:Lnet/minecraft/core/BlockPos$MutableBlockPos;")
	@Expression("chunk.getBlockState(this.pos)")
	@ModifyExpressionValue(
					at = @At("MIXINEXTRAS:EXPRESSION"),
					method = "computeNext"
	)
	BlockState stratum$BlockCollisions$computeNext$getBlockState(BlockState original, @Local BlockGetter chunk) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		if (chunk instanceof Level level) {
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
		}
		else if (chunk instanceof LevelChunk level) {
			MixinReplacementMethods.stratum$iterate(level.getLevel(), (layer) -> {
				var intercept = layer.interceptCollide(pos, original);
				if (intercept.getType() == InterceptType.MODIFY) {
					var s = intercept.getState();
					if (s != null)
						state.set(s);
					return true;
				}
				return false;
			});
		}
		return state.get();
	}
}
