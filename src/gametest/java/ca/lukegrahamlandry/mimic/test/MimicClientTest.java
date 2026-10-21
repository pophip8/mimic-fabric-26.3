package ca.lukegrahamlandry.mimic.test;

import ca.lukegrahamlandry.mimic.Constants;
import ca.lukegrahamlandry.mimic.ModMain;
import ca.lukegrahamlandry.mimic.client.CustomChestScreen;
import ca.lukegrahamlandry.mimic.FabricGeoMimicEntity;
import com.geckolib.cache.GeckoLibResources;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;

public class MimicClientTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (var world = context.worldBuilder().create()) {
            var connection = world.getConnection();
            var server = world.getServer();
            server.runCommand("fill -8 99 -8 8 100 8 minecraft:stone");
            server.runCommand("tp @a 0.5 101 0.5 0 10");
            server.runCommand("gamemode survival @a");
            UUID[] id = new UUID[1];
            int[] entityId = new int[1];
            server.runOnServer(game -> {
                var level = connection.getServerLevel();
                var mimic = ModMain.mimicEntityType.create(level, EntitySpawnReason.COMMAND);
                mimic.snapTo(0.5, 101, 3.5, 180, 0);
                mimic.setYBodyRot(180);
                mimic.setStealth(true);
                mimic.addItem(new ItemStack(Items.DIAMOND, 7));
                level.addFreshEntity(mimic);
                id[0] = mimic.getUUID();
                entityId[0] = mimic.getId();
            });
            connection.waitForChunksRender();
            connection.waitForClientboundEntityUpdates(ModMain.mimicEntityType);
            context.waitTicks(25);
            context.runOnClient(client -> {
                if (GeckoLibResources.getBakedModels().getModel(Constants.MODEL_LOC) == null)
                    throw new AssertionError("Mimic GeckoLib model was not loaded");
                if (GeckoLibResources.getBakedAnimations().getAnimation(Constants.ANIM_LOC,
                        new net.minecraft.resources.Identifier[0], "animation.mimic.idle.chest") == null)
                    throw new AssertionError("Mimic animations were not loaded");
            });
            context.takeScreenshot("mimic-wild-chest");
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                mimic.interact(connection.getServerPlayer(), InteractionHand.MAIN_HAND, Vec3.ZERO);
            });
            context.waitForScreen(CustomChestScreen.class);
            context.takeScreenshot("mimic-wild-inventory");
            context.waitTicks(45);
            context.waitForScreen(null);
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                if (!mimic.isAngry()) throw new AssertionError("Opening a wild Mimic did not trigger its ambush");
                var player = connection.getServerPlayer();
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModMain.mimicKey));
                mimic.interact(player, InteractionHand.MAIN_HAND, Vec3.ZERO);
                if (!mimic.isTamed() || mimic.getOwner() != player || !player.getMainHandItem().isEmpty())
                    throw new AssertionError("Taming or key consumption failed");
                mimic.snapToBlock(new net.minecraft.core.BlockPos(0, 101, 3), net.minecraft.core.Direction.NORTH);
            });
            connection.waitForClientboundEntityUpdates(ModMain.mimicEntityType);
            context.waitTicks(25);
            context.takeScreenshot("mimic-tamed-chest");
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                mimic.interact(connection.getServerPlayer(), InteractionHand.MAIN_HAND, Vec3.ZERO);
            });
            context.waitForScreen(CustomChestScreen.class);
            context.takeScreenshot("mimic-tamed-inventory");
            server.runOnServer(game -> connection.getServerPlayer().closeContainer());
            context.waitForScreen(null);
            context.getInput().holdShift();
            context.waitTicks(3);
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                var player = connection.getServerPlayer();
                if (!player.isShiftKeyDown()) throw new AssertionError("Sneak input did not reach the server");
                mimic.interact(player, InteractionHand.MAIN_HAND, Vec3.ZERO);
            });
            context.getInput().releaseShift();
            context.waitTicks(25);
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                if (mimic.isStealth()) throw new AssertionError("Sneak interaction did not make tamed Mimic stand");
            });
            context.runOnClient(client -> {
                var mimic = (FabricGeoMimicEntity) connection.getClientLevel().getEntity(entityId[0]);
                if (mimic.isStealth()) throw new AssertionError("Standing state did not reach the client");
                var controller = mimic.getAnimatableInstanceCache().getManagerForId(mimic.getId())
                        .getAnimationControllers().get("moveController");
                if (controller.getCurrentRawAnimation() == null)
                    throw new AssertionError("GeckoLib animation controller is not running");
                Constants.LOG.info("Standing Mimic animation: {}", controller.getCurrentRawAnimation());
                if (controller.getCurrentRawAnimation().equals(com.geckolib.animation.RawAnimation.begin()
                        .thenLoop("animation.mimic.idle.chest")))
                    throw new AssertionError("Standing Mimic is still playing its chest disguise animation");
            });
            context.takeScreenshot("mimic-tamed-standing");
            server.runCommand("tp @a 0.5 101 -6.5 0 10");
            context.waitTicks(100);
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                if (mimic.distanceToSqr(connection.getServerPlayer()) >= 36.0)
                    throw new AssertionError("Standing tamed Mimic did not follow its owner");
                var inventory = connection.getServerPlayer().getInventory();
                inventory.setItem(0, new ItemStack(ModMain.mimicKey));
                inventory.setItem(1, new ItemStack(ModMain.mimicLock));
                inventory.setItem(2, new ItemStack(ModMain.mimicHeart));
                inventory.setItem(3, new ItemStack(ModMain.mimicSpawnEgg));
            });
            connection.waitForClientboundPackets();
            context.waitTicks(3);
            context.takeScreenshot("mimic-items");
            context.runOnClient(client -> {
                if (!new ItemStack(ModMain.mimicKey).getHoverName().getString().equals("Mimic Key"))
                    throw new AssertionError("English item translation is missing");
                client.getLanguageManager().setSelected("fr_fr");
                client.options.languageCode = "fr_fr";
                client.reloadResourcePacks();
            });
            context.waitFor(client -> new ItemStack(ModMain.mimicKey).getHoverName().getString().equals("Clé de Mimic"), 600);
            context.waitFor(client -> client.gui.overlay() == null, 600);
            context.runOnClient(client -> {
                String[] keys = {"item.mimic.mimic_key", "item.mimic.mimic_lock", "item.mimic.fake_chest",
                        "item.mimic.mimic_spawn_egg", "entity.mimic.mimic", "block.mimic.mimic_lock", "mimic.no_open_standing"};
                String[] values = {"Clé de Mimic", "Cadenas de Mimic", "Cœur de Mimic", "Œuf d’apparition de Mimic",
                        "Mimic", "Marqueur d’apparition de Mimic", "Seuls les Mimics assis peuvent être ouverts."};
                for (int i = 0; i < keys.length; i++) {
                    String actual = net.minecraft.network.chat.Component.translatable(keys[i]).getString();
                    if (!actual.equals(values[i])) throw new AssertionError("French translation mismatch: " + keys[i] + " = " + actual);
                }
            });
            server.runOnServer(game -> {
                var mimic = (FabricGeoMimicEntity) connection.getServerLevel().getEntity(id[0]);
                mimic.setStealth(true);
                mimic.snapToBlock(new net.minecraft.core.BlockPos(0, 101, -4), net.minecraft.core.Direction.NORTH);
                mimic.interact(connection.getServerPlayer(), InteractionHand.MAIN_HAND, Vec3.ZERO);
            });
            context.waitForScreen(CustomChestScreen.class);
            context.waitTicks(2);
            context.takeScreenshot("mimic-inventaire-francais");
            server.runOnServer(game -> connection.getServerPlayer().closeContainer());
            context.waitForScreen(null);
        }
    }
}
