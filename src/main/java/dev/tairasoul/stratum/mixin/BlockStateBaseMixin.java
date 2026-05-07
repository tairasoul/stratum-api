package dev.tairasoul.stratum.mixin;

import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.intercept.InterceptType;
import dev.tairasoul.stratum.quack.LayeredWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
	@Shadow
	@Final
	private float destroySpeed;
	@Unique
	boolean getInteractionShapeSkip = false;
	@Unique
	boolean getVisualShapeSkip = false;
	@Unique
	boolean getCollisionShapeSkip = false;

	@Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
	void stratum$getCollisionShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
		if (getCollisionShapeSkip) return;
		if (blockGetter instanceof LayeredWorld layered) {
			MixinReplacementMethods.stratum$iterate(layered, (layer) -> {
				var inter = layer.interceptCollide(blockPos, null);
				if (inter.getType() != InterceptType.PASSTHROUGH) {
					getCollisionShapeSkip = true;
					var s = inter.getState();
					if (s != null)
						cir.setReturnValue(s.getCollisionShape(blockGetter, blockPos));
					getCollisionShapeSkip = false;
					return true;
				} else return false;
			});
		}
	}

	@Inject(at = @At("HEAD"), method = "getVisualShape", cancellable = true)
	void stratum$getVisualShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir) {
		if (getVisualShapeSkip) return;
		if (blockGetter instanceof LayeredWorld layered) {
			MixinReplacementMethods.stratum$iterate(layered, (layer) -> {
				var inter = layer.interceptRender(blockPos, null);
				if (inter.getType() != InterceptType.PASSTHROUGH) {
					getVisualShapeSkip = true;
					var s = inter.getState();
					if (s != null)
						cir.setReturnValue(s.getVisualShape(blockGetter, blockPos, collisionContext));
					getVisualShapeSkip = false;
					return true;
				}
				else return false;
			});
		}
	}

	@Inject(at = @At("HEAD"), method = "getInteractionShape", cancellable = true)
	void stratum$getInteractionShape(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<VoxelShape> cir) {
		if (getInteractionShapeSkip) return;
		if (blockGetter instanceof LayeredWorld layered) {
			MixinReplacementMethods.stratum$iterate(layered, (layer) -> {
				var inter = layer.interceptInteract(blockPos, null);
				if (inter.getType() != InterceptType.PASSTHROUGH) {
					getInteractionShapeSkip = true;
					var s = inter.getState();
					if (s != null)
						cir.setReturnValue(s.getInteractionShape(blockGetter, blockPos));
					getInteractionShapeSkip = false;
					return true;
				}
				else return false;
			});
		}
	}

	@Inject(at = @At("HEAD"), method = "useWithoutItem", cancellable = true)
	void stratum$blockStateBase$useWithoutItem(Level level, Player player, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
		MixinReplacementMethods.stratum$iterate(level, (layer) -> {
			var inter = layer.interceptInteract(blockHitResult.getBlockPos(), null);
			if (inter.component1() == InterceptType.BLOCK) {
				cir.setReturnValue(InteractionResult.PASS);
				return true;
			}
			else return inter.component1() != InterceptType.PASSTHROUGH;
		});
	}

	@Inject(at = @At("HEAD"), method = "getDestroySpeed", cancellable = true)
	void stratum$blockStateBase$getDestroySpeed(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
		if (blockGetter instanceof LayeredWorld layered) {
			MixinReplacementMethods.stratum$iterate(layered, (layer) -> {
				var inter = layer.interceptBreak(blockPos, destroySpeed);
				if (inter.getType() != InterceptType.PASSTHROUGH) {
					cir.setReturnValue(inter.getSpeed());
					return true;
				}
				return false;
			});
		}
	}
}
