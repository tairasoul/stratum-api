package dev.tairasoul.stratum.client.network.packets

import dev.tairasoul.stratum.network.packets.SyncRemoveSectionS2C
import dev.tairasoul.stratum.quack.LayeredWorld
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft

object SyncRemoveSectionS2CHandler {
	@JvmStatic
	fun handle(
		payload: SyncRemoveSectionS2C,
		context: ClientPlayNetworking.Context
	) {
		val level = context.player().level()
		val layers = (level as LayeredWorld).`stratum$getLayers`()
		val instance = Minecraft.getInstance()
		val levelRenderer = instance.levelRenderer
		val layer = layers.getOrCreate(payload.layer)
		val section = payload.loc.toSectionPos()
		layer.remove(payload.loc)
		levelRenderer.setSectionDirtyWithNeighbors(section.x, section.y, section.z)
	}
}