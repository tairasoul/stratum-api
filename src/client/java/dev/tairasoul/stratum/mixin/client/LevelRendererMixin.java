package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	private ClientLevel level;

	@Definition(id = "level", field = "Lnet/minecraft/client/renderer/LevelRenderer;level:Lnet/minecraft/client/multiplayer/ClientLevel;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "pos", local = @Local(type = BlockPos.class))
	@Expression("this.level.getBlockState(pos)")
	@ModifyExpressionValue(
					method = "extractBlockOutline",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	private BlockState stratum$extractBlockOutline$blockState(BlockState original, @Local BlockPos pos) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptRender(pos, original);
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
