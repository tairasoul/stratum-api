package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.quack.LayeredWorld

open class SimpleOverlayLayerFactory : LayerFactory<SimpleOverlayLayer> {
	override fun create(level: LayeredWorld): SimpleOverlayLayer {
		return SimpleOverlayLayer(level)
	}

}