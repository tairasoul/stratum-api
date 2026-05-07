package dev.tairasoul.stratum.mixin;

import dev.tairasoul.stratum.forwarded_mixins.MixinReplacementMethods;
import dev.tairasoul.stratum.layers.DimensionLayers;
import dev.tairasoul.stratum.layers.Layer;
import dev.tairasoul.stratum.quack.LayeredWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin implements LayeredWorld {
	@Shadow
	@Final
	private boolean isClientSide;
	@Unique
	private final DimensionLayers stratum$layers = new DimensionLayers(this);
	@Unique
	private final DimensionLayers stratum$clientLayers = new DimensionLayers(this);

	@Override
	public DimensionLayers stratum$getLayers() {
		if (this.isClientSide)
			return stratum$clientLayers;
		else
			return stratum$layers;
	}
}
