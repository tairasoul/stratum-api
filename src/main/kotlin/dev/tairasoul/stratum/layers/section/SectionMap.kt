package dev.tairasoul.stratum.layers.section

import dev.tairasoul.stratum.layers.LayerFactory
import dev.tairasoul.stratum.network.packets.SyncRemoveSectionS2C
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.iterator

class SectionMap(val layer: LayerFactory<*>) {
	private val sections: ConcurrentHashMap<SectionSubdividedPos, Section> = ConcurrentHashMap()
	private val knowsOf: ConcurrentHashMap<ServerPlayer, MutableSet<SectionSubdividedPos>> = ConcurrentHashMap()

	fun set(pos: BlockPos, item: BlockState) {
		val sec = SectionSubdividedPos.from(pos)
		set(sec, SectionRelativePos.from(pos), item)
	}

	fun set(pos: SectionSubdividedPos, block: SectionRelativePos, item: BlockState) {
		val section = sections[pos]
		if (section != null) {
			section.set(block, item)
		}
		else {
			val section = Section(layer)
			section.set(block, item)
			sections[pos] = section
		}
	}

	fun get(pos: BlockPos): BlockState? {
		val sec = SectionSubdividedPos.from(pos)
		return get(sec, SectionRelativePos.from(pos))
	}

	fun get(pos: SectionSubdividedPos, block: SectionRelativePos): BlockState? {
		val section = sections[pos]
		if (section != null) {
			return section.get(block)
		}
		return null
	}

	fun get(pos: SectionSubdividedPos): Section? {
		return sections[pos]
	}

	fun remove(pos: BlockPos) {
		val sec = SectionSubdividedPos.from(pos)
		remove(sec, SectionRelativePos.from(pos))
	}

	fun remove(pos: SectionSubdividedPos, block: SectionRelativePos) {
		val section = sections[pos]
		if (section != null) {
			section.remove(block)
			if (section.size() == 0) {
				sections.remove(pos)
			}
		}
	}

	fun remove(pos: SectionSubdividedPos) {
		sections.remove(pos)
	}

	fun update(pos: SectionSubdividedPos, section: Section) {
		sections[pos] = section
	}

	fun update(pos: SectionSubdividedPos, states: ConcurrentHashMap<SectionRelativePos, BlockState>) {
		val section = sections[pos]
		if (section != null) {
			section.update(states)
		}
		else {
			val sec = Section(layer)
			sec.update(states)
			sections[pos] = sec
		}
	}

	fun contains(pos: BlockPos): Boolean {
		val sec = SectionSubdividedPos.from(pos)
		val section = sections[sec]
		if (section != null) {
			return section.contains(pos)
		}
		return false
	}

	fun contains(pos: SectionSubdividedPos): Boolean {
		return sections.containsKey(pos)
	}

	fun forget(player: ServerPlayer) {
		knowsOf.remove(player)
	}

	fun sendSync(player: ServerPlayer) {
		val knowsOf = knowsOf[player]
		if (knowsOf != null) {
			knowsOf.removeIf {
				if (!sections.containsKey(it) || !player.chunkTrackingView.contains(it.toChunkPos())) {
					val remove = SyncRemoveSectionS2C(layer, it)
					ServerPlayNetworking.send(player, remove)
					true
				}
				else false
			}
			for (section in sections) {
				if (player.chunkTrackingView.contains(section.key.toChunkPos())) {
					if (section.value.sendSync(section.key, player))
						knowsOf.add(section.key)
				}
			}
		}
		else {
			val knowsOf = Collections.newSetFromMap<SectionSubdividedPos>(ConcurrentHashMap())
			for (section in sections) {
				if (player.chunkTrackingView.contains(section.key.toChunkPos())) {
					if (section.value.sendSync(section.key, player))
						knowsOf.add(section.key)
				}
			}
			this.knowsOf[player] = knowsOf
		}
	}
}