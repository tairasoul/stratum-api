package dev.tairasoul.stratum.client.quack;

import net.minecraft.world.level.chunk.LevelChunk;

public interface ClientChunkCacheAccessor {
	void stratum$refreshEmptySections(final LevelChunk chunk);
}
