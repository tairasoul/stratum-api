package dev.tairasoul.stratum.mixin.client;

import dev.tairasoul.stratum.client.quack.ClientChunkCacheAccessor;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientChunkCache.class)
public class ClientChunkCacheMixin implements ClientChunkCacheAccessor {

	@Shadow
	volatile ClientChunkCache.Storage storage;

	@Override
	public void stratum$refreshEmptySections(LevelChunk chunk) {
		storage.refreshEmptySections(chunk);
	}
}
