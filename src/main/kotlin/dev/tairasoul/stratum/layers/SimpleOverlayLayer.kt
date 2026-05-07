package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.quack.LayeredWorld

open class SimpleOverlayLayer(level: LayeredWorld) : Layer(level) {
	override fun shouldTickServer(): Boolean {
		return true
	}

	override fun tickServer() {
		sync()
	}

	override fun key(): LayerFactory<*> {
		return ModLayers.SIMPLE_OVERLAY_LAYER
	}
}