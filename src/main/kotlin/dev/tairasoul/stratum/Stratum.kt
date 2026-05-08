package dev.tairasoul.stratum

import dev.tairasoul.stratum.items.ModItems
import dev.tairasoul.stratum.layers.LayerFactory
import dev.tairasoul.stratum.layers.ModLayers
import dev.tairasoul.stratum.network.Packets
import dev.tairasoul.stratum.quack.LayeredWorld
import dev.tairasoul.stratum.saved.DimensionTrackedBlocks
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import org.slf4j.LoggerFactory
import kotlin.collections.iterator

class Stratum : ModInitializer {
	companion object {
		const val MOD_ID = "stratum-api"

		@JvmStatic
		val LAYER_REGISTRY: Registry<LayerFactory<*>> = FabricRegistryBuilder
			.createSimple(ResourceKey.createRegistryKey<LayerFactory<*>>(Identifier.fromNamespaceAndPath(MOD_ID, "layer-registry")))
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister()

		@JvmStatic
		lateinit var registry_ops: RegistryOps<Tag>
		@JvmStatic
		lateinit var access: RegistryAccess
	}

	override fun onInitialize() {
		ModLayers.initialize()
		if (FabricLoader.getInstance().isDevelopmentEnvironment)
			ModItems.initialize()
		Packets.register()
		ServerLifecycleEvents.SERVER_STARTED.register {
			val registry = it.registryAccess()
			access = registry
			registry_ops = RegistryOps.create(NbtOps.INSTANCE, access)
		}
		ServerTickEvents.START_WORLD_TICK.register {
			val data = DimensionTrackedBlocks.get(it)
			if (data != null && data.firstCheck) {
				val le = it.lightEngine
				data.firstCheck = false
				val tracked = data.getTracked()
				for (block in tracked) {
					it.getChunk(block)
					le.checkBlock(block)
				}
				tracked.clear()
			}

			if (it is LayeredWorld) {
				val layers = it.`stratum$getLayers`()
				for (kv in layers.layers) {
					if (kv.value.shouldTickServer()) {
						kv.value.tickServer()
					}
				}
			}
		}
	}
}