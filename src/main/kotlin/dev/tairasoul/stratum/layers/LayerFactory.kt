package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.quack.LayeredWorld

interface LayerFactory<T> where T : Layer {
	fun create(level: LayeredWorld): T
}