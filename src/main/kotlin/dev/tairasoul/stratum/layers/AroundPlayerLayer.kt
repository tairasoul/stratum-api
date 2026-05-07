package dev.tairasoul.stratum.layers

import dev.tairasoul.stratum.quack.LayeredWorld
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.iterator

open class AroundPlayerLayer(level: LayeredWorld) : Layer(level) {
	val tracked = ConcurrentHashMap<ServerPlayer, MutableSet<BlockPos>>()

	fun addPlayer(player: ServerPlayer) {
		if (tracked[player] == null) {
			tracked[player] = ConcurrentHashMap.newKeySet()
		}
	}

	fun removePlayer(player: ServerPlayer) {
		if (tracked[player] != null) {
			for (block in tracked[player]!!) {
				remove(block)
			}
			tracked.remove(player)
			sync()
		}
	}

	override fun shouldTickServer(): Boolean {
		return tracked.isNotEmpty()
	}

	override fun tickServer() {
		val RADIUS = 10
		for (player in tracked) {
			val playerPos = if (player.key.isCrouching) player.key.blockPosition().subtract(Vec3i(0, 2, 0)) else player.key.blockPosition().subtract(Vec3i(0, 1, 0))
			val cubicStart = playerPos.offset(RADIUS, RADIUS, RADIUS)
			val cubicEnd = playerPos.offset(-RADIUS, -RADIUS, -RADIUS)
			val around = HashSet<BlockPos>(RADIUS*RADIUS*RADIUS)
			for (pos in BlockPos.betweenClosedStream(cubicStart, cubicEnd)) {
				around.add(pos.immutable())
			}
			player.value.removeIf { block ->
				if (!around.contains(block) || block.distSqr(playerPos) > RADIUS*RADIUS) {
					remove(block)
					true
				}
				else false
			}
			for (block in around) {
				if (block.distSqr(playerPos) > RADIUS*RADIUS) continue
				val block = block.immutable()
				val gotten = get(block)
				if (gotten == null || !gotten.isAir) {
					set(block, Blocks.AIR.defaultBlockState())
					player.value.add(block)
				}
			}
			val forward = playerPos.offset(1, 0, 0)
			val forwardLeft = forward.offset(0, 0, -1)
			val forwardRight = forward.offset(0, 0, 1)
			val left = playerPos.offset(0, 0, -1)
			val right = playerPos.offset(0, 0, 1)
			val back = playerPos.offset(-1, 0, 0)
			val backLeft = back.offset(0, 0, -1)
			val backRight = back.offset(0, 0, 1)
			val platform = listOf(forward, forwardLeft, forwardRight, left, playerPos, right, back, backLeft, backRight)
			for (coord in platform) {
				val g = get(coord)
				if (g == null || g != Blocks.WHITE_CONCRETE.defaultBlockState()) {
					set(coord, Blocks.WHITE_CONCRETE.defaultBlockState())
					player.value.add(coord)
				}
			}
		}
		sync()
	}

	override fun key(): LayerFactory<*> {
		return ModLayers.AROUND_PLAYER_LAYER
	}
}