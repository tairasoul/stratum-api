package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
	@Definition(id = "player", local = @Local(type = Player.class, argsOnly = true))
	@Definition(id = "level", method = "Lnet/minecraft/world/entity/player/Player;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "testPos", local = @Local(type = BlockPos.MutableBlockPos.class))
	@Expression("player.level().getBlockState(testPos)")
	@ModifyExpressionValue(
					method = "getViewBlockingState",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private static BlockState stratum$getViewBlockingState$getBlockState(BlockState original, @Local BlockPos.MutableBlockPos testPos, @Local(argsOnly = true) Player player) {
		Level level = player.level();
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptRender(testPos, original);
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
