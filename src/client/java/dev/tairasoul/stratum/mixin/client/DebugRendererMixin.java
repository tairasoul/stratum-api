package dev.tairasoul.stratum.mixin.client;

import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
	@Shadow
	@Final
	private List<DebugRenderer.SimpleDebugRenderer> renderers;

	@Inject(at = @At("RETURN"), method = "refreshRendererList")
	void stratum$debugRenderer$refreshRendererList(CallbackInfo ci) {
//		renderers.add(new LayeredLightDebugRenderer(Minecraft.getInstance()));
	}
}
