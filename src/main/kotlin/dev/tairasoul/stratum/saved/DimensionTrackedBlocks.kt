package dev.tairasoul.stratum.saved

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType

class DimensionTrackedBlocks : SavedData {
	companion object {
		val CODEC =  Codec.list(BlockPos.CODEC).xmap(
			{
				val asSet = HashSet<BlockPos>()
				it.forEach { v ->
					asSet.add(v)
				}
				DimensionTrackedBlocks(asSet)
			},
			{
				val asList = ArrayList<BlockPos>()
				it.tracked.forEach { v ->
					asList.add(v)
				}
				asList
			}
		)

		val TYPE = SavedDataType<DimensionTrackedBlocks>(
			"stratum_tracked_blocks",
			::DimensionTrackedBlocks,
			CODEC,
			DataFixTypes.LEVEL
		)

		fun get(level: Level): DimensionTrackedBlocks? {
			if (level is ServerLevel) {
				return level.dataStorage.computeIfAbsent(TYPE)
			}
			return null
		}
	}

	private val tracked: MutableSet<BlockPos>
	var firstCheck = false

	constructor() {
		tracked = HashSet()
	}

	constructor(tracked: MutableSet<BlockPos>) {
		this.tracked = tracked
		firstCheck = true
	}

	fun track(pos: BlockPos) {
		tracked.add(pos)
		setDirty()
	}

	fun untrack( pos: BlockPos) {
		tracked.remove(pos)
		setDirty()
	}

	fun removeAll() {
		tracked.clear()
		setDirty()
	}

	fun getTracked(): MutableSet<BlockPos> {
		return tracked
	}
}