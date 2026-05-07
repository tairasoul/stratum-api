package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.quack.LayeredWorld

class DimensionLayers(val level: LayeredWorld) {
	val layers: HashMap<LayerFactory<*>, Layer> = HashMap()

	fun getOrCreate(key: LayerFactory<*>): Layer {
		val get = layers[key]
		if (get != null) {
			return get
		}
		else {
			val v = key.create(level)
			layers[key] = v
			return v
		}
	}

	fun <T: Layer> getOrCreateTyped(key: LayerFactory<T>): T {
		return getOrCreate(key as LayerFactory<*>) as T
	}
}