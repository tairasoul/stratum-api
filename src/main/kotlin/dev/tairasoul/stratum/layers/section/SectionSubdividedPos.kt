package dev.tairasoul.stratum.layers.section

import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.ChunkPos

data class SectionSubdividedPos(val x: Int, val y: Int, val z: Int) {
	companion object {
		@JvmStatic
		val STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, SectionSubdividedPos::y,
			ByteBufCodecs.INT, SectionSubdividedPos::x,
			ByteBufCodecs.INT, SectionSubdividedPos::z
		) { y, x, z -> SectionSubdividedPos(x, y, z) }

		@JvmStatic
		fun from(pos: BlockPos): SectionSubdividedPos {
			val x = Math.floorDiv(pos.x, 4)
			val y = Math.floorDiv(pos.y, 4)
			val z = Math.floorDiv(pos.z, 4)
			return SectionSubdividedPos(x, y, z)
		}

		@JvmStatic
		fun rangeFromSection(sectionPos: SectionPos): List<SectionSubdividedPos> {
			val list = ArrayList<SectionSubdividedPos>()
			val startX = sectionPos.x * 4
			val startY = sectionPos.y * 4
			val startZ = sectionPos.z * 4
			for (x in 0 until 4) {
				for (y in 0 until 4) {
					for (z in 0 until 4) {
						list.add(SectionSubdividedPos(x + startX, y + startY, z + startZ))
					}
				}
			}
			return list
		}

		@JvmStatic
		fun asLong(x: Int, y: Int, z: Int): Long {
			val x = Math.floorDiv(x, 4)
			val y = Math.floorDiv(y, 4)
			val z = Math.floorDiv(z, 4)
			val yMask = 0b11111111111
			val maskedY = (y and yMask).toLong()
			val horizMask = 0b111111111111111111111111111
			val maskedX = (x and horizMask).toLong()
			val maskedZ = (z and horizMask).toLong()
			return (maskedY shl 54) or (maskedX shl 27) or (maskedZ)
		}

		@JvmStatic
		fun fromLong(l: Long): SectionSubdividedPos {
			val yMask = 0b11111111111L
			val horizMask = 0b111111111111111111111111111L
			val yBits = (l and (yMask shl 54)) shr 54
			val xBits = (l and (horizMask shl 27)) shr 27
			val zBits = (l and horizMask)
			return SectionSubdividedPos(xBits.toInt(), yBits.toInt(), zBits.toInt())
		}
	}

	fun toOriginBlockPos(): BlockPos {
		val x = this.x * 4
		val y = this.y * 4
		val z = this.z * 4
		return BlockPos(x, y, z)
	}

	fun toChunkPos(): ChunkPos {
		val originX = Math.floorDiv(x, 4)
		val originZ = Math.floorDiv(z, 4)
		return ChunkPos(originX, originZ)
	}

	fun toSectionPos(): SectionPos {
		val originX = Math.floorDiv(x, 4)
		val originY = Math.floorDiv(y, 4)
		val originZ = Math.floorDiv(z, 4)
		return SectionPos.of(originX, originY, originZ)
	}

	fun asLong(): Long {
		return asLong(x, y, z)
	}
}
