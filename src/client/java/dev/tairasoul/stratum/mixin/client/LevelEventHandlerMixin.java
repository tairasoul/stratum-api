package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(LevelEventHandler.class)
public class LevelEventHandlerMixin {
	@Shadow
	@Final
	private ClientLevel level;

	@ModifyExpressionValue(
					at = @At(
									value = "INVOKE",
									target = "Lnet/minecraft/world/level/block/Block;stateById(I)Lnet/minecraft/world/level/block/state/BlockState;"
					),
					method = "levelEvent"
	)
	BlockState stratum$levelEventHandler$levelEvent$breakEvent(BlockState original, int i, BlockPos blockPos) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
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
