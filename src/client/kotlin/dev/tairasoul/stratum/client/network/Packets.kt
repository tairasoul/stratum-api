package dev.tairasoul.stratum.client.network

import dev.tairasoul.stratum.client.network.packets.SyncRemoveSectionS2CHandler
import dev.tairasoul.stratum.client.network.packets.SyncSectionS2CHandler
import dev.tairasoul.stratum.network.packets.SyncRemoveSectionS2C
import dev.tairasoul.stratum.network.packets.SyncSectionS2C
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object Packets {
	fun register() {
		register(SyncRemoveSectionS2C.TYPE, SyncRemoveSectionS2CHandler::handle)
		register(SyncSectionS2C.TYPE, SyncSectionS2CHandler::handle)
	}

	private fun <T> register(id: CustomPacketPayload.Type<T>, handler: ClientPlayNetworking.PlayPayloadHandler<T>) where T: CustomPacketPayload {
		ClientPlayNetworking.registerGlobalReceiver(id, handler)
	}
}