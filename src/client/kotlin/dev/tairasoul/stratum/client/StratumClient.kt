package dev.tairasoul.stratum.client

import dev.tairasoul.stratum.Stratum.Companion.access
import dev.tairasoul.stratum.Stratum.Companion.registry_ops
import dev.tairasoul.stratum.client.network.Packets
import dev.tairasoul.stratum.quack.LayeredWorld
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps

class StratumClient : ClientModInitializer {
	override fun onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register { listener, sender, minecraft ->
			access = listener.level.registryAccess()
			registry_ops = RegistryOps.create(NbtOps.INSTANCE, access)
		}
		Packets.register()
		ClientTickEvents.START_WORLD_TICK.register {
			if (it is LayeredWorld) {
				val layers = it.`stratum$getLayers`()
				for (kv in layers.layers) {
					if (kv.value.shouldTickClient()) {
						kv.value.tickClient()
					}
				}
			}
		}
	}
}
