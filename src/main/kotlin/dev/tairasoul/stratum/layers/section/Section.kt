package dev.tairasoul.stratum.layers.section

import dev.tairasoul.stratum.Stratum
import dev.tairasoul.stratum.layers.LayerFactory
import dev.tairasoul.stratum.network.packets.SyncSectionS2C
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState
import java.util.concurrent.ConcurrentHashMap

class Section(val layer: LayerFactory<*>) {
	var states: ConcurrentHashMap<SectionRelativePos, BlockState> = ConcurrentHashMap()
	private val hashes: ConcurrentHashMap<ServerPlayer, Int> = ConcurrentHashMap()

	companion object {
		@JvmStatic
		val STREAM_CODEC = StreamCodec.of(::serialize, ::deserialize)

		private val layerCodec = ByteBufCodecs.fromCodec(Stratum.LAYER_REGISTRY.byNameCodec())

		private fun deserialize(buf: FriendlyByteBuf): Section {
			val layer: LayerFactory<*> = layerCodec.decode(buf)
			val len = buf.readInt()
			val map = ConcurrentHashMap<SectionRelativePos, BlockState>(len)
			for (i in 0 until len) {
				val pos = SectionRelativePos.STREAM_CODEC.decode(buf)
				val nbt = buf.readNbt()
				val dec = BlockState.CODEC.decode(Stratum.registry_ops, nbt)
				val r = dec.result().orElseThrow().first
				map[pos] = r
			}
			val sec = Section(layer)
			sec.states = map
			return sec
		}

		private fun serialize(buf: FriendlyByteBuf, section: Section) {
			layerCodec.encode(buf, section.layer)
			buf.writeInt(section.states.size)
			for (kv in ConcurrentHashMap(section.states)) {
				SectionRelativePos.STREAM_CODEC.encode(buf, kv.key)
				val enc = BlockState.CODEC.encodeStart(Stratum.registry_ops, kv.value)
				val tag = enc.result().orElseThrow()
				buf.writeNbt(tag)
			}
		}
	}

	fun update(states: ConcurrentHashMap<SectionRelativePos, BlockState>) {
		this.states = states
	}

	fun size(): Int {
		return states.size
	}

	fun set(pos: BlockPos, item: BlockState) {
		states[SectionRelativePos.from(pos)] = item
	}

	fun set(pos: SectionRelativePos, item: BlockState) {
		states[pos] = item
	}

	fun get(pos: BlockPos): BlockState? {
		return states[SectionRelativePos.from(pos)]
	}

	fun get(pos: SectionRelativePos): BlockState? {
		return states[pos]
	}

	fun contains(pos: BlockPos): Boolean {
		return get(pos) != null
	}

	fun contains(pos: SectionRelativePos): Boolean {
		return get(pos) != null
	}

	fun forget(player: ServerPlayer) {
		hashes.remove(player)
	}

	fun remove(pos: BlockPos) {
		states.remove(SectionRelativePos.from(pos))
	}

	fun remove(pos: SectionRelativePos) {
		states.remove(pos)
	}

	fun sendSync(pos: SectionSubdividedPos, player: ServerPlayer): Boolean {
		val lastHash = hashes[player]
		val currentHash = states.hashCode()
		val needsSync = lastHash == null || currentHash != lastHash
		if (needsSync) {
			hashes[player] = currentHash
			val sync = SyncSectionS2C(pos, this)
			ServerPlayNetworking.send(player, sync)
		}
		return needsSync
	}
}