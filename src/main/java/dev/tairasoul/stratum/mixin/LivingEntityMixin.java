package dev.tairasoul.stratum.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Definition(id = "level", method = "Lnet/minecraft/world/entity/LivingEntity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "posBelow", local = @Local(type = BlockPos.class))
	@Expression("this.level().getBlockState(posBelow)")
	@ModifyExpressionValue(
					at = @At("MIXINEXTRAS:EXPRESSION"),
					method = "travelInAir"
	)
	BlockState stratum$livingEntity$travelInAir$getBlockState(BlockState original, @Local BlockPos posBelow) {
		var lvl = ((Entity)(Object)this).level();
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(lvl, (layer) -> {
			var intercept = layer.interceptEffects(posBelow, original);
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
