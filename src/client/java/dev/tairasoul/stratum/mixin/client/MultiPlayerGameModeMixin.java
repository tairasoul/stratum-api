package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Definition(id = "minecraft", field = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;minecraft:Lnet/minecraft/client/Minecraft;")
	@Definition(id = "level", field = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "blockPos", local = @Local(type = BlockPos.class, argsOnly = true))
	@Expression("this.minecraft.level.getBlockState(blockPos)")
	@ModifyExpressionValue(
					at = @At("MIXINEXTRAS:EXPRESSION"),
					method = "continueDestroyBlock"
	)
	BlockState stratum$multiPlayerGameMode$continueDestroyBlock(BlockState original, BlockPos blockPos) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		assert minecraft.level != null;
		MixinReplacementMethods.stratum$iterate(minecraft.level, (layer) -> {
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
