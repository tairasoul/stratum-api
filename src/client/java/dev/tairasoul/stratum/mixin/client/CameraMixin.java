package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	@Final
	private BlockPos.MutableBlockPos blockPosition;

	@Shadow
	private Level level;

	@Definition(id = "level", field = "Lnet/minecraft/client/Camera;level:Lnet/minecraft/world/level/Level;")
	@Definition(id = "getFluidState", method = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;")
	@Definition(id = "blockPosition", field = "Lnet/minecraft/client/Camera;blockPosition:Lnet/minecraft/core/BlockPos$MutableBlockPos;")
	@Expression("this.level.getFluidState(this.blockPosition)")
	@ModifyExpressionValue(
					method = "getFluidInCamera",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	FluidState stratum$getFluidInCamera$getFluidState(FluidState original) {
		var blockPos = this.blockPosition;
		var level = this.level;
		AtomicReference<FluidState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptRender(blockPos, original.createLegacyBlock());
			if (intercept.getType() == InterceptType.MODIFY) {
				var s = intercept.getState();
				if (s != null)
					state.set(s.getFluidState());
				return true;
			}
			return false;
		});
		return state.get();
	}

	@Definition(id = "level", field = "Lnet/minecraft/client/Camera;level:Lnet/minecraft/world/level/Level;")
	@Definition(id = "getFluidState", method = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;")
	@Definition(id = "checkPos", local = @Local(type = BlockPos.class))
	@Expression("this.level.getFluidState(checkPos)")
	@ModifyExpressionValue(
					method = "getFluidInCamera",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	FluidState stratum$getFluidInCamera$nearPlane$getFluidState(FluidState original, @Local BlockPos checkPos) {
		var level = this.level;
		AtomicReference<FluidState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptRender(checkPos, original.createLegacyBlock());
			if (intercept.getType() == InterceptType.MODIFY) {
				var s = intercept.getState();
				if (s != null)
					state.set(s.getFluidState());
				return true;
			}
			return false;
		});
		return state.get();
	}
}
