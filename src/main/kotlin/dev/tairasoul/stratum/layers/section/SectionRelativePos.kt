package dev.tairasoul.stratum.layers.section

import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class SectionRelativePos(val x: Byte, val y: Byte, val z: Byte) {
	companion object {
		@JvmStatic
		val STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE, SectionRelativePos::y,
			ByteBufCodecs.BYTE, SectionRelativePos::x,
			ByteBufCodecs.BYTE, SectionRelativePos::z,
		) { y, x, z -> SectionRelativePos(x, y, z) }
		@JvmStatic
		fun from(pos: BlockPos): SectionRelativePos {
			val mask = 3
			val relativeX = pos.x and mask
			val relativeY = pos.y and mask
			val relativeZ = pos.z and mask
			return SectionRelativePos(relativeX.toByte(), relativeY.toByte(), relativeZ.toByte())
		}
	}

	fun toBlockPos(subdividedPos: SectionSubdividedPos): BlockPos {
		val origin = subdividedPos.toOriginBlockPos()
		val x = origin.x + this.x
		val y = origin.y + this.y
		val z = origin.z + this.z
		return BlockPos(x, y, z)
	}
}