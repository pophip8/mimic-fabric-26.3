package ca.lukegrahamlandry.mimic.test;

import ca.lukegrahamlandry.mimic.ModMain;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.gamerules.GameRules;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Creates a persistent singleplayer playground using Minecraft's world creation and saving APIs. */
public class MimicPlaygroundCreator implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        Path save;
        try (var world = context.worldBuilder().adjustSettings(settings -> {
            settings.setName("Laboratoire Mimic 26.3");
            settings.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
            settings.setAllowCommands(true);
            settings.getGameRules().set(GameRules.KEEP_INVENTORY, true, null);
        }).create()) {
            save = world.getWorldSave().getSaveDirectory();
            var server = world.getServer();
            var connection = world.getConnection();
            server.runCommand("fill -20 63 -16 20 64 24 minecraft:smooth_stone");
            server.runCommand("fill -19 64 -15 19 64 23 minecraft:white_concrete");
            server.runCommand("fill -20 65 -16 20 65 -16 minecraft:oak_fence");
            server.runCommand("fill -20 65 24 20 65 24 minecraft:oak_fence");
            server.runCommand("fill -20 65 -16 -20 65 24 minecraft:oak_fence");
            server.runCommand("fill 20 65 -16 20 65 24 minecraft:oak_fence");
            server.runCommand("fill -11 64 7 -9 64 9 minecraft:red_concrete");
            server.runCommand("fill -1 64 7 1 64 9 minecraft:yellow_concrete");
            server.runCommand("fill 9 64 7 11 64 9 minecraft:light_blue_concrete");
            server.runCommand("setworldspawn 0 65 0 0");
            server.runCommand("defaultgamemode creative");
            server.runCommand("gamemode creative @a");
            server.runCommand("tp @a 0.5 65 0.5 0 10");
            server.runCommand("time set noon");
            server.runOnServer(game -> {
                var level = connection.getServerLevel();
                for (int x : new int[]{-10, 0, 10}) {
                    var mimic = ModMain.mimicEntityType.create(level, EntitySpawnReason.COMMAND);
                    mimic.snapToBlock(new BlockPos(x, 65, 8), Direction.NORTH);
                    mimic.setStealth(true);
                    mimic.setPersistenceRequired();
                    mimic.addItem(new ItemStack(Items.DIAMOND, 7));
                    mimic.addItem(new ItemStack(Items.GOLD_INGOT, 12));
                    if (x == 10) mimic.setLocked(true);
                    level.addFreshEntity(mimic);
                    var chest = chest(level, new BlockPos(x, 65, 18));
                    chest.setItem(0, new ItemStack(Items.DIAMOND, 7));
                    chest.setItem(1, new ItemStack(Items.GOLD_INGOT, 12));
                }
                var supplies = chest(level, new BlockPos(0, 65, -3));
                supplies.setItem(0, new ItemStack(ModMain.mimicKey, 64));
                supplies.setItem(1, new ItemStack(ModMain.mimicLock, 64));
                supplies.setItem(2, new ItemStack(ModMain.mimicHeart, 64));
                supplies.setItem(3, new ItemStack(ModMain.mimicSpawnEgg, 64));
                supplies.setItem(4, new ItemStack(Items.IRON_AXE));
                supplies.setItem(5, new ItemStack(Items.GOLDEN_APPLE, 64));
                supplies.setItem(6, new ItemStack(Items.CHEST, 64));
                supplies.setItem(7, new ItemStack(Items.SHIELD));
                supplies.setItem(8, new ItemStack(Items.DIAMOND_CHESTPLATE));
                supplies.setItem(9, new ItemStack(Items.NETHERITE_SCRAP, 64));
                supplies.setItem(10, new ItemStack(Items.GOLD_INGOT, 64));
                supplies.setItem(11, new ItemStack(Items.IRON_CHAIN, 64));
                supplies.setChanged();
                sign(level, new BlockPos(-10, 65, 5), "1 - Sauvage", "Wild Mimic", "Clic droit", "Right click");
                sign(level, new BlockPos(0, 65, 5), "2 - Apprivoiser", "Tame with key", "Cle / Mimic Key", "Puis MAJ-clic");
                sign(level, new BlockPos(10, 65, 5), "3 - Verrouille", "Locked Mimic", "Prendre le butin", "Take some loot");
                sign(level, new BlockPos(0, 65, 15), "4 - Coeur", "Heart on chest", "MAJ + clic droit", "Sneak + use");
                sign(level, new BlockPos(-2, 65, -3), "Materiel de test", "Test supplies", "FR / EN : langue", "Options > Langue");
                var inventory = connection.getServerPlayer().getInventory();
                inventory.setItem(0, new ItemStack(ModMain.mimicKey, 64));
                inventory.setItem(1, new ItemStack(ModMain.mimicLock, 64));
                inventory.setItem(2, new ItemStack(ModMain.mimicHeart, 64));
                inventory.setItem(3, new ItemStack(ModMain.mimicSpawnEgg, 64));
                inventory.setItem(4, new ItemStack(Items.IRON_AXE));
                inventory.setItem(5, new ItemStack(Items.CHEST, 64));
                inventory.setItem(6, new ItemStack(Items.GOLDEN_APPLE, 64));
            });
            connection.waitForChunksRender();
            context.waitTicks(30);
            context.takeScreenshot("laboratoire-mimic-pret");
            server.runCommand("save-all flush");
        }
        try {
            Path marker = Path.of(System.getProperty("mimic.playground.marker"));
            Files.createDirectories(marker.getParent());
            Files.writeString(marker, save.toString());
        } catch (java.io.IOException exception) {
            throw new RuntimeException("Could not record playground save directory", exception);
        }
    }

    private static ChestBlockEntity chest(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState());
        return (ChestBlockEntity) level.getBlockEntity(pos);
    }

    private static void sign(ServerLevel level, BlockPos pos, String... lines) {
        level.setBlockAndUpdate(pos, Blocks.BIRCH_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, 8));
        List<Component> components = List.of(Component.literal(lines[0]), Component.literal(lines[1]),
                Component.literal(lines[2]), Component.literal(lines[3]));
        var text = new SignText(components, components, DyeColor.BLACK, false);
        var sign = (SignBlockEntity) level.getBlockEntity(pos);
        sign.setText(text, SignTextSlot.FRONT);
        sign.setText(text, SignTextSlot.BACK);
        sign.setWaxed(true);
        sign.setChanged();
    }
}
