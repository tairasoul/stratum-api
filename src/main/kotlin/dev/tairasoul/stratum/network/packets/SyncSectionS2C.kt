package dev.tairasoul.stratum.network.packets

import dev.tairasoul.stratum.Stratum
import dev.tairasoul.stratum.layers.LayerFactory
import dev.tairasoul.stratum.layers.section.Section
import dev.tairasoul.stratum.layers.section.SectionRelativePos
import dev.tairasoul.stratum.layers.section.SectionSubdividedPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.BlockState
import java.util.concurrent.ConcurrentHashMap

data class SyncSectionS2C(val loc: SectionSubdividedPos, val section: Section) : CustomPacketPayload {
	companion object {
		@JvmStatic
		val IDENTIFIER: Identifier = Identifier.fromNamespaceAndPath(Stratum.MOD_ID, "sync-section")
		@JvmStatic
		val TYPE: CustomPacketPayload.Type<SyncSectionS2C> = CustomPacketPayload.Type(IDENTIFIER)
		@JvmStatic
		val CODEC = StreamCodec.composite(
			SectionSubdividedPos.STREAM_CODEC,
			{ payload -> payload.loc },
			Section.STREAM_CODEC,
			{ payload -> payload.section }
		) {
			loc, sec ->
			SyncSectionS2C(loc, sec)
		}
	}
	override fun type() = TYPE
}
