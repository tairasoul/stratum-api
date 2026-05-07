package dev.tairasoul.stratum.network.packets

import dev.tairasoul.stratum.Stratum
import dev.tairasoul.stratum.layers.LayerFactory
import dev.tairasoul.stratum.layers.section.SectionSubdividedPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class SyncRemoveSectionS2C(val layer: LayerFactory<*>, val loc: SectionSubdividedPos) : CustomPacketPayload {
	companion object {
		@JvmStatic
		val IDENTIFIER: Identifier = Identifier.fromNamespaceAndPath(Stratum.MOD_ID, "sync-remove-section")
		@JvmStatic
		val TYPE: CustomPacketPayload.Type<SyncRemoveSectionS2C> = CustomPacketPayload.Type(IDENTIFIER);
		@JvmStatic
		val CODEC = StreamCodec.composite(
			ByteBufCodecs.fromCodec(Stratum.LAYER_REGISTRY.byNameCodec()),
			{ payload -> payload.layer },
			SectionSubdividedPos.STREAM_CODEC,
			{ payload -> payload.loc },
		) { layer, loc -> SyncRemoveSectionS2C(layer, loc) }
	}

	override fun type() = TYPE
}