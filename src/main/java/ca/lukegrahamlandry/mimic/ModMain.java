package ca.lukegrahamlandry.mimic;

import ca.lukegrahamlandry.mimic.blocks.SingleMimicSpawner;
import ca.lukegrahamlandry.mimic.client.MimicContainer;
import ca.lukegrahamlandry.mimic.entities.MimicEntity;
import ca.lukegrahamlandry.mimic.events.TickHandler;
import ca.lukegrahamlandry.mimic.items.FakeChestItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.function.Function;

public class ModMain implements ModInitializer {
    public static EntityType<FabricGeoMimicEntity> mimicEntityType;
    public static MenuType<MimicContainer> evilMimicMenuType;
    public static MenuType<MimicContainer> tameMimicMenuType;
    public static Item mimicKey, mimicLock, mimicHeart, mimicSpawnEgg;

    @Override
    public void onInitialize() {
        mimicEntityType = Registry.register(BuiltInRegistries.ENTITY_TYPE, Constants.MIMIC_ENTITY_ID,
                EntityType.Builder.of(FabricGeoMimicEntity::new, MobCategory.MISC).sized(1f, 1f)
                        .build(ResourceKey.create(Registries.ENTITY_TYPE, Constants.MIMIC_ENTITY_ID)));
        FabricDefaultAttributeRegistry.register(mimicEntityType, MimicEntity.createMobAttributes());
        mimicKey = item(Constants.MIMIC_KEY_ID, Item::new);
        mimicLock = item(Constants.MIMIC_LOCK_ID, Item::new);
        mimicHeart = item(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fake_chest"), FakeChestItem::new);
        mimicSpawnEgg = item(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mimic_spawn_egg"),
                props -> new SpawnEggItem(props.spawnEgg(mimicEntityType)));
        Registry.register(BuiltInRegistries.BLOCK, Constants.SINGLE_MIMIC_SPAWN_BLOCK_ID,
                new SingleMimicSpawner(BlockBehaviour.Properties.of()
                        .setId(ResourceKey.create(Registries.BLOCK, Constants.SINGLE_MIMIC_SPAWN_BLOCK_ID))
                        .noCollision().noOcclusion().strength(-1.0f, 3600000.0f)));
        evilMimicMenuType = Registry.register(BuiltInRegistries.MENU, Constants.EVIL_MIMIC_CONTAINER, new MenuType<MimicContainer>(
                (id, inventory) -> MimicContainer.create(Constants.EVIL_MIMIC_CONTAINER, id, inventory), FeatureFlags.DEFAULT_FLAGS));
        tameMimicMenuType = Registry.register(BuiltInRegistries.MENU, Constants.TAME_MIMIC_CONTAINER, new MenuType<MimicContainer>(
                (id, inventory) -> MimicContainer.create(Constants.TAME_MIMIC_CONTAINER, id, inventory), FeatureFlags.DEFAULT_FLAGS));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(mimicSpawnEgg));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(mimicKey); entries.accept(mimicLock); entries.accept(mimicHeart);
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> TickHandler.onServerTick());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> TickHandler.spawns.clear());
    }

    private static Item item(Identifier id, Function<Item.Properties, Item> factory) {
        return Registry.register(BuiltInRegistries.ITEM, id,
                factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }
}
