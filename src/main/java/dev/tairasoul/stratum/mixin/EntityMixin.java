package dev.tairasoul.stratum.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	private Level level;

	@Shadow
	private BlockPos blockPosition;

	@Shadow
	public abstract BlockPos getBlockPosBelowThatAffectsMyMovement();

	@Definition(id = "getLevel", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "pos", local = @Local(type = BlockPos.class))
	@Expression("this.getLevel().getBlockState(pos)")
	@ModifyExpressionValue(
					method = "method_30022",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	public BlockState stratum$replaceBlockStateInWall(BlockState original, @Local(argsOnly = true) BlockPos pos) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptEffects(blockPosition, original);
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

	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "blockPosition", method = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;")
	@Definition(id = "getBlock", method = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;")
	@Expression("this.level().getBlockState(this.blockPosition()).getBlock()")
	@ModifyExpressionValue(
					method = "getBlockJumpFactor",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	public Block stratum$replaceBlockJumpFactorBlockPos(Block original) {
		AtomicReference<Block> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptEffects(blockPosition, original.defaultBlockState());
			if (intercept.getType() == InterceptType.MODIFY) {
				var s = intercept.getState();
				if (s != null)
					state.set(s.getBlock());
				return true;
			}
			return false;
		});
		return state.get();
	}

	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlock", method = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;")
	@Definition(id = "getBlockPosBelowThatAffectsMyMovement", method = "Lnet/minecraft/world/entity/Entity;getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;")
	@Expression("this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock()")
	@ModifyExpressionValue(
					method = "getBlockJumpFactor",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	public Block stratum$replaceBlockJumpFactorMovementPos(Block original) {
		AtomicReference<Block> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptEffects(getBlockPosBelowThatAffectsMyMovement(), original.defaultBlockState());
			if (intercept.getType() == InterceptType.MODIFY) {
				var s = intercept.getState();
				if (s != null)
					state.set(s.getBlock());
				return true;
			}
			return false;
		});
		return state.get();
	}

	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "blockPosition", method = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;")
	@Expression("this.level().getBlockState(this.blockPosition())")
	@ModifyExpressionValue(
					method = "getBlockSpeedFactor",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	public BlockState stratum$replaceBlockStateSpeedFactor(BlockState original) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptEffects(getBlockPosBelowThatAffectsMyMovement(), original);
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

	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "blockIntersection", local = @Local(type = BlockPos.class))
	@Expression("this.level().getBlockState(blockIntersection)")
	@ModifyExpressionValue(
					method = "method_67632",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	public BlockState stratum$replaceInsideBlocksState(BlockState original, @Local(argsOnly = true) BlockPos blockIntersection) {
		AtomicReference<BlockState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptCollide(blockIntersection, original);
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

	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getFluidState", method = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;")
	@Definition(id = "blockPosition", field = "Lnet/minecraft/world/entity/Entity;blockPosition:Lnet/minecraft/core/BlockPos;")
	@Expression("this.level().getFluidState(this.blockPosition)")
	@ModifyExpressionValue(
					method = "updateSwimming",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	public FluidState stratum$updateSwimming$getFluidState(FluidState original) {
		var blockPos = this.blockPosition;
		var level = this.level;
		AtomicReference<FluidState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptSwim(blockPos, original.createLegacyBlock());
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

	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getFluidState", method = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;")
	@Definition(id = "pos", local = @Local(type = BlockPos.class))
	@Expression("this.level().getFluidState(pos)")
	@ModifyExpressionValue(
					method = "updateFluidOnEyes",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	FluidState stratum$updateFluidOnEyes$getFluidState(FluidState original, @Local BlockPos blockPos) {
		var level = this.level;
		AtomicReference<FluidState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptDrown(blockPos, original.createLegacyBlock());
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

	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getFluidState", method = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;")
	@Definition(id = "pos", local = @Local(type = BlockPos.MutableBlockPos.class))
	@Expression("this.level().getFluidState(pos)")
	@ModifyExpressionValue(
					method = "updateFluidHeightAndDoFluidPushing",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	FluidState stratum$updateFluidHeightAndDoFluidPushing$getFluidState(FluidState original, @Local BlockPos.MutableBlockPos pos) {
		var level = this.level;
		AtomicReference<FluidState> state = new AtomicReference<>(original);
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var intercept = layer.interceptCollide(pos, original.createLegacyBlock());
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

	@Definition(id = "level", method = "Lnet/minecraft/world/entity/Entity;level()Lnet/minecraft/world/level/Level;")
	@Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
	@Definition(id = "pos", local = @Local(type = BlockPos.class))
	@Expression("this.level().getBlockState(pos)")
	@ModifyExpressionValue(
					at = @At("MIXINEXTRAS:EXPRESSION"),
					method = "spawnSprintParticle"
	)
	BlockState stratum$entity$spawnSprintParticle$getBlockState(BlockState original, @Local BlockPos pos) {
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
