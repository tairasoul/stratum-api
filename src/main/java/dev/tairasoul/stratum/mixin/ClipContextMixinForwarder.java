package dev.tairasoul.stratum.mixin;

import dev.tairasoul.stratum.forwarded_mixins.ClipContextMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClipContext.class)
public abstract class ClipContextMixinForwarder {
//
//	@Shadow
//	public abstract VoxelShape getBlockShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos);
//
//	@Inject(at = @At("HEAD"), method = "getBlockShape", cancellable = true)
//	private void stratum$ClipContext$getBlockShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<VoxelShape> cir) {
//		ClipContextMixin.stratum$ClipContext$getBlockShape(blockState, blockGetter, blockPos, this::getBlockShape, cir);
//	}
}
