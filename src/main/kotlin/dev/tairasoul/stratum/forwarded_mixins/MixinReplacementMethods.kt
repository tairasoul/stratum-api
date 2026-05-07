package dev.tairasoul.stratum.forwarded_mixins

import dev.tairasoul.stratum.layers.Layer
import dev.tairasoul.stratum.quack.LayeredWorld
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import kotlin.collections.iterator

object MixinReplacementMethods {
	@JvmStatic
	fun `stratum$iterate`(level: LayeredWorld, call: (Layer) -> Boolean) {
		val layers = level.`stratum$getLayers`()
		for (kv in layers.layers) {
			if (!call(kv.value)) continue
			return
		}
	}

	@JvmStatic
	fun `stratum$iterate`(level: Level, call: (Layer) -> Boolean) {
		if (level is LayeredWorld) {
			val layers = level.`stratum$getLayers`()
			for (kv in layers.layers) {
				if (!call(kv.value)) continue
				return
			}
		}
	}

	@JvmStatic
	fun `stratum$getBlockState`(pos: BlockPos, level: Level, call: (Layer) -> Boolean): BlockState? {
		if (level is LayeredWorld) {
			val layers = level.`stratum$getLayers`()
			for (kv in layers.layers) {
				if (!call(kv.value)) continue
				val block = kv.value.get(pos) ?: continue
				return block
			}
		}
		return null
	}

	@JvmStatic
	fun `stratum$replaceBlockState`(original: BlockState, pos: BlockPos, level: Level,  call: (Layer) -> Boolean): BlockState {
		if (level is LayeredWorld) {
			val layers = level.`stratum$getLayers`()
			for (kv in layers.layers) {
				if (!call(kv.value)) continue
				val block = kv.value.get(pos) ?: continue
				return block
			}
		}
		return original
	}

	@JvmStatic
	fun `stratum$replaceBlock`(original: Block, pos: BlockPos, level: Level, call: (Layer) -> Boolean): Block {
		if (level is LayeredWorld) {
			val layers = level.`stratum$getLayers`()
			for (kv in layers.layers) {
				if (!call(kv.value)) continue
				val block = kv.value.get(pos) ?: continue
				return block.block
			}
		}
		return original
	}

	@JvmStatic
	fun `stratum$replaceFluid`(original: FluidState, pos: BlockPos, level: Level, call: (Layer) -> Boolean): FluidState {
		if (level is LayeredWorld) {
			val layers = level.`stratum$getLayers`()
			for (kv in layers.layers) {
				if (!call(kv.value)) continue
				val block = kv.value.get(pos) ?: continue
				return block.fluidState
			}
		}
		return original
	}
}