package dev.tairasoul.stratum.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tairasoul.stratum.layers.section.SectionSubdividedPos;
import dev.tairasoul.stratum.quack.LayeredWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public class ClientChunkCache$StorageMixin {
	@Definition(id = "section", local = @Local(type = LevelChunkSection.class))
	@Definition(id = "hasOnlyAir", method = "Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")
	@Expression("section.hasOnlyAir()")
	@ModifyExpressionValue(
					method = "addEmptySections",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	boolean stratum$addEmptySections$hasOnlyAir(boolean original, @Local(type = int.class) int sectionIndex, @Local(type = LevelChunk.class, argsOnly = true) LevelChunk chunk) {
		var client = Minecraft.getInstance().level;
		if (client instanceof LayeredWorld layered) {
			int y = chunk.getSectionYFromSectionIndex(sectionIndex);
			SectionPos pos = SectionPos.of(chunk.getPos(), y);
			var range = SectionSubdividedPos.rangeFromSection(pos);
			return dev.tairasoul.stratum.forwarded_mixins.client.ClientChunkCache$StorageMixin.stratum$hasOnlyAir(original, layered, range);
		}
		return original;
	}

	@Definition(id = "section", local = @Local(type = LevelChunkSection.class))
	@Definition(id = "hasOnlyAir", method = "Lnet/minecraft/world/level/chunk/LevelChunkSection;hasOnlyAir()Z")
	@Expression("section.hasOnlyAir()")
	@ModifyExpressionValue(
					method = "refreshEmptySections",
					at = @At("MIXINEXTRAS:EXPRESSION")
	)
	boolean stratum$refreshEmptySections$hasOnlyAir(boolean original, @Local(type = int.class) int sectionIndex, @Local(type = LevelChunk.class, argsOnly = true) LevelChunk chunk) {
		var client = Minecraft.getInstance().level;
		if (client instanceof LayeredWorld layered) {
			int y = chunk.getSectionYFromSectionIndex(sectionIndex);
			SectionPos pos = SectionPos.of(chunk.getPos(), y);
			var range = SectionSubdividedPos.rangeFromSection(pos);
			return dev.tairasoul.stratum.forwarded_mixins.client.ClientChunkCache$StorageMixin.stratum$hasOnlyAir(original, layered, range);
		}
		return original;
	}
}
