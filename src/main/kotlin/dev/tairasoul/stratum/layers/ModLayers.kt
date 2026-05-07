package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.Stratum
import net.minecraft.core.Registry
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

object ModLayers {
	val SIMPLE_OVERLAY_LAYER = register("simple-overlay-layer", SimpleOverlayLayerFactory())
	val AROUND_PLAYER_LAYER = register("around-player-layer", AroundPlayerLayerFactory())

	fun initialize() {}

	fun <T: Layer> register(name: String, factory: LayerFactory<T>): LayerFactory<T> {
		val layerKey =
			ResourceKey.create(Stratum.LAYER_REGISTRY.key(), Identifier.fromNamespaceAndPath(Stratum.MOD_ID, name))

		return Registry.register(Stratum.LAYER_REGISTRY, layerKey, factory as LayerFactory<*>) as LayerFactory<T>
	}
}