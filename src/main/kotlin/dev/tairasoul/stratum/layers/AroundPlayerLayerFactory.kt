package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.quack.LayeredWorld

open class AroundPlayerLayerFactory : LayerFactory<AroundPlayerLayer> {
	override fun create(level: LayeredWorld): AroundPlayerLayer {
		return AroundPlayerLayer(level)
	}
}