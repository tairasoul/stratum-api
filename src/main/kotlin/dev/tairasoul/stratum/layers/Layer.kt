package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.layers.intercept.BreakInterceptState
import dev.tairasoul.stratum.layers.intercept.InterceptType
import dev.tairasoul.stratum.layers.intercept.InterceptedState
import dev.tairasoul.stratum.layers.section.Section
import dev.tairasoul.stratum.layers.section.SectionMap
import dev.tairasoul.stratum.layers.section.SectionRelativePos
import dev.tairasoul.stratum.layers.section.SectionSubdividedPos
import dev.tairasoul.stratum.quack.LayeredWorld
import dev.tairasoul.stratum.saved.DimensionTrackedBlocks
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import java.util.concurrent.ConcurrentHashMap

abstract class Layer(val level: LayeredWorld) {
	val blocks = SectionMap(key())

	abstract fun key(): LayerFactory<*>

	open fun sync() {
		if (level is ServerLevel) {
			for (player in PlayerLookup.world(level)) {
				blocks.sendSync(player)
			}
		}
	}

	protected fun getTrackedBlocks(): DimensionTrackedBlocks {
		if (level is ServerLevel) {
			return level.dataStorage.computeIfAbsent(DimensionTrackedBlocks.TYPE)
		}
		return DimensionTrackedBlocks()
	}

	open val shouldRender = true

	open fun interceptRender(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, this.get(pos))
	}

	open fun interceptCollide(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, this.get(pos))
	}

	open fun interceptSwim(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, this.get(pos))
	}

	open fun interceptDrown(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, this.get(pos))
	}

	open fun interceptLight(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, this.get(pos))
	}

	open fun interceptEffects(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, this.get(pos))
	}

	open fun interceptBreak(pos: BlockPos, originalSpeed: Float): BreakInterceptState {
		return BreakInterceptState(InterceptType.MODIFY, -1f)
	}

	open fun interceptInteract(pos: BlockPos, state: BlockState?): InterceptedState {
		return InterceptedState(InterceptType.MODIFY, null)
	}

	open fun set(pos: BlockPos, state: BlockState) {
		blocks.set(pos, state)
		if (level is Level) {
			if (!level.isClientSide) {
				val s = getTrackedBlocks()
				s.track(pos)
			}
			val le = level.lightEngine
			le.checkBlock(pos)
		}
	}

	open fun remove(pos: BlockPos) {
		blocks.remove(pos)
		if (level is Level) {
			if (!level.isClientSide) {
				val s = getTrackedBlocks()
				s.untrack(pos)
			}
			val le = level.lightEngine
			le.checkBlock(pos)
		}
	}

	open fun remove(pos: SectionSubdividedPos) {
		blocks.remove(pos)
		if (level is Level) {
			val origin = pos.toOriginBlockPos()
			if (!level.isClientSide) {
				val s = getTrackedBlocks()
				val le = level.lightEngine
				for (x in -1..4) {
					for (y in -1..4) {
						for (z in -1..4) {
							val pos = origin.offset(x, y, z)
							le.checkBlock(pos)
							s.untrack(pos)
						}
					}
				}
			}
			else {
				val origin = pos.toOriginBlockPos()
				val le = level.lightEngine
				for (x in -1..4) {
					for (y in -1..4) {
						for (z in -1..4) {
							val pos = origin.offset(x, y, z)
							le.checkBlock(pos)
						}
					}
				}
			}
		}
	}

	open fun update(pos: SectionSubdividedPos, section: Section) {
		blocks.update(pos, section)
		if (level is Level) {
			if (!level.isClientSide) {
				val le = level.lightEngine
				val origin = pos.toOriginBlockPos()
				val s = getTrackedBlocks()
				for (x in 0..3) {
					for (y in 0..3) {
						for (z in 0..3) {
							val pos = origin.offset(x, y, z)
							s.untrack(pos)
						}
					}
				}
				for (block in section.states.keys) {
					val offset = origin.offset(block.x.toInt(), block.y.toInt(), block.z.toInt())
					le.checkBlock(offset)
					s.track(offset)
				}
			}
			else {
				val le = level.lightEngine
				val origin = pos.toOriginBlockPos()
				for (block in section.states.keys) {
					val offset = origin.offset(block.x.toInt(), block.y.toInt(), block.z.toInt())
					le.checkBlock(offset)
				}
			}
		}
	}

	open fun update(pos: SectionSubdividedPos, states: ConcurrentHashMap<SectionRelativePos, BlockState>) {
		blocks.update(pos, states)
	}

	open fun get(pos: BlockPos): BlockState? {
		return blocks.get(pos)
	}

	open fun get(pos: SectionSubdividedPos): Section? {
		return blocks.get(pos)
	}

	open fun contains(pos: BlockPos): Boolean {
		return blocks.contains(pos)
	}

	open fun contains(pos: SectionSubdividedPos): Boolean {
		return blocks.contains(pos)
	}

	open fun shouldTickServer(): Boolean {
		return false
	}

	open fun tickServer() {

	}

	open fun shouldTickClient(): Boolean {
		return false
	}

	open fun tickClient() {

	}
}