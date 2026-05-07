package dev.tairasoul.stratum.client.network.packets

import dev.tairasoul.stratum.Stratum
import dev.tairasoul.stratum.client.quack.ClientChunkCacheAccessor
import dev.tairasoul.stratum.network.packets.SyncSectionS2C
import dev.tairasoul.stratum.quack.LayeredWorld
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft

object SyncSectionS2CHandler {
	@JvmStatic
	fun handle(
		payload: SyncSectionS2C,
		context: ClientPlayNetworking.Context
	) {
		val level = context.player().level()
		val layers = (level as LayeredWorld).`stratum$getLayers`()
		val instance = Minecraft.getInstance()
		val levelRenderer = instance.levelRenderer
		val layer = layers.getOrCreate(payload.section.layer)
		val section = payload.loc.toSectionPos()
		layer.update(payload.loc, payload.section)
		val cache = level.chunkSource;
		if (cache is ClientChunkCacheAccessor) {
			val levelChunk = level.getChunk(section.x, section.z);
			cache.`stratum$refreshEmptySections`(levelChunk)
		}
		levelRenderer.setSectionDirtyWithNeighbors(section.x, section.y, section.z)
	}
}