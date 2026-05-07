package dev.tairasoul.stratum.network

import dev.tairasoul.stratum.network.packets.SyncRemoveSectionS2C
import dev.tairasoul.stratum.network.packets.SyncSectionS2C
import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object Packets {
	fun register() {
		registerByte(SyncRemoveSectionS2C.TYPE, SyncRemoveSectionS2C.CODEC)
		registerFriendly(SyncSectionS2C.TYPE, SyncSectionS2C.CODEC)
	}

	private fun <T> registerByte(type: CustomPacketPayload.Type<T>, codec: StreamCodec<ByteBuf, T>) where T : CustomPacketPayload {
		PayloadTypeRegistry.playS2C().register(type, codec)
	}

	private fun <T> registerFriendly(type: CustomPacketPayload.Type<T>, codec: StreamCodec<FriendlyByteBuf, T>) where T : CustomPacketPayload {
		PayloadTypeRegistry.playS2C().register(type, codec)
	}
}