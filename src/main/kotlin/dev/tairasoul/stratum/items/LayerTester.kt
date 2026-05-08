package dev.tairasoul.stratum.items

import dev.tairasoul.stratum.layers.ModLayers
import dev.tairasoul.stratum.quack.LayeredWorld
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks

class LayerTester(properties: Properties) : Item(properties) {
	override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResult {
		if (player is ServerPlayer && level is ServerLevel) {
			val layers = (level as LayeredWorld).`stratum$getLayers`()
			val layer = layers.getOrCreateTyped(ModLayers.AROUND_PLAYER_LAYER)
			if (layer.tracked.containsKey(player)) {
				layer.removePlayer(player)
			}
			else {
				layer.addPlayer(player)
			}
			return InteractionResult.SUCCESS_SERVER
		}
		return InteractionResult.PASS
	}

//	override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResult {
//		if (level.isClientSide) return InteractionResult.PASS
//		if (!player.isCrouching) {
//			val layers = (player.level() as LayeredWorld).`stratum$getLayers`()
//			val overlay = layers.getOrCreateTyped(ModLayers.SIMPLE_OVERLAY_LAYER)
//			val start = player.blockPosition()
//			val RADIUS = 8
//			for (x in -RADIUS..RADIUS) {
//				for (y in -RADIUS..RADIUS) {
//					for (z in -RADIUS..RADIUS) {
//						val offset = start.offset(x, y, z)
//						overlay.remove(offset)
//					}
//				}
//			}
//		}
//		return InteractionResult.FAIL
//	}

//	override fun useOn(useOnContext: UseOnContext): InteractionResult {
//		if (useOnContext.level.isClientSide) return InteractionResult.PASS
//		val block = useOnContext.clickedPos
//		val layers = (useOnContext.level as LayeredWorld).`stratum$getLayers`()
//		val overlay = layers.getOrCreateTyped(ModLayers.SIMPLE_OVERLAY_LAYER)
//		overlay.set(block, Blocks.WATER.defaultBlockState())
//		return InteractionResult.SUCCESS_SERVER
//	}

//	override fun useOn(useOnContext: UseOnContext): InteractionResult {
//		if (useOnContext.level.isClientSide) return InteractionResult.PASS
//		val block = useOnContext.clickedPos
//		val layers = (useOnContext.level as LayeredWorld).`stratum$getLayers`()
//		val layer = layers.getOrCreateTyped(ModLayers.SINKHOLE_LAYER)
//		return InteractionResult.SUCCESS_SERVER
//	}
}