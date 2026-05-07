package dev.tairasoul.stratum.layers.intercept

import net.minecraft.world.level.block.state.BlockState

data class InterceptedState(val type: InterceptType, val state: BlockState?)
