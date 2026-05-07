package dev.tairasoul.stratum.forwarded_mixins.client

import dev.tairasoul.stratum.layers.section.SectionSubdividedPos
import dev.tairasoul.stratum.quack.LayeredWorld

object `ClientChunkCache$StorageMixin` {
	@JvmStatic
	fun `stratum$hasOnlyAir`(original: Boolean, layeredWorld: LayeredWorld, subdivisions: List<SectionSubdividedPos>): Boolean {
		val layers = layeredWorld.`stratum$getLayers`()
		for (kv in layers.layers) {
			if (!kv.value.shouldRender) continue
			for (sub in subdivisions) {
				if (kv.value.contains(sub)) {
					return false
				}
			}
		}
		return original
	}
}