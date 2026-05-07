package dev.tairasoul.stratum.items

import dev.tairasoul.stratum.Stratum
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item


object ModItems {
	val LAYER_TESTER = register("layer-tester", { i -> LayerTester(i)}, Item.Properties())

	fun initialize() {}

	fun <T : Item> register(name: String, itemFactory: (Item.Properties) -> T, settings: Item.Properties): T {
		val itemKey: ResourceKey<Item> =
			ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Stratum.MOD_ID, name))

		val item: T = itemFactory(settings.setId(itemKey))

		Registry.register(BuiltInRegistries.ITEM, itemKey, item)

		return item
	}
}