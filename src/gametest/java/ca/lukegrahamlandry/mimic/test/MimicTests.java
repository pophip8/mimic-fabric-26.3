package ca.lukegrahamlandry.mimic.test;

import ca.lukegrahamlandry.mimic.ModMain;
import ca.lukegrahamlandry.mimic.Constants;
import ca.lukegrahamlandry.mimic.FabricGeoMimicEntity;
import ca.lukegrahamlandry.mimic.events.TickHandler;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;

public class MimicTests {
    private static void check(GameTestHelper helper, boolean condition, String message) {
        if (!condition) throw helper.assertionException(message);
    }

    @GameTest
    public void keysLocksAndAxeDamage(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var tame = helper.spawn(ModMain.mimicEntityType, new BlockPos(2, 2, 2));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(ModMain.mimicKey));
        tame.interact(player, net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.phys.Vec3.ZERO);
        check(helper, tame.isTamed() && tame.getOwner() == player, "Key did not set tame owner");
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        tame.addAdditionalSaveData(output);
        var restored = ModMain.mimicEntityType.create(helper.getLevel(), EntitySpawnReason.LOAD);
        restored.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING,
                helper.getLevel().registryAccess(), output.buildResult()));
        check(helper, restored.isTamed() && restored.getOwner() == player, "Tame owner was not saved");
        var locked = helper.spawn(ModMain.mimicEntityType, new BlockPos(4, 2, 2));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(ModMain.mimicLock));
        locked.interact(player, net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.phys.Vec3.ZERO);
        locked.setAngry(true);
        check(helper, locked.isLocked() && !locked.isAngry(), "Locked Mimic became hostile");
        var victim = helper.spawn(ModMain.mimicEntityType, new BlockPos(6, 2, 2));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        victim.hurtServer(helper.getLevel(), helper.getLevel().damageSources().playerAttack(player), 4f);
        check(helper, victim.getHealth() == 52f, "Axe damage was not doubled");
        helper.succeed();
    }

    @GameTest
    public void worldgenMixinsAndSpawner(GameTestHelper helper) throws Exception {
        // Force all template/feature targets through Mixin's transformer, even in the flat test world.
        Class.forName("net.minecraft.world.level.levelgen.structure.structures.EndCityPieces$EndCityPiece");
        Class.forName("net.minecraft.world.level.levelgen.structure.structures.IglooPieces$IglooPiece");
        Class.forName("net.minecraft.world.level.levelgen.structure.StructurePiece");
        Class.forName("net.minecraft.world.level.levelgen.feature.Feature");
        BlockPos pos = helper.absolutePos(new BlockPos(2, 3, 2));
        helper.getLevel().setBlockAndUpdate(pos.below(2), Blocks.STONE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(pos.below(), Blocks.CHEST.defaultBlockState());
        ((ChestBlockEntity) helper.getLevel().getBlockEntity(pos.below())).setItem(0, new ItemStack(Items.DIAMOND, 3));
        var block = (ca.lukegrahamlandry.mimic.blocks.SingleMimicSpawner) Constants.getMimicSpawnBlock();
        helper.getLevel().setBlockAndUpdate(pos, block.defaultBlockState());
        block.tick(block.defaultBlockState(), helper.getLevel(), pos, net.minecraft.util.RandomSource.create(0));
        check(helper, helper.getLevel().getBlockState(pos).isAir(), "Temporary spawner was not removed");
        check(helper, helper.getLevel().getBlockState(pos.below()).isAir(), "Spawner did not consume the chest");
        helper.succeed();
    }

    @GameTest(maxTicks = 100)
    public void attackAndDeathDropInventory(GameTestHelper helper) {
        var mimic = helper.spawn(ModMain.mimicEntityType, new BlockPos(3, 3, 3));
        mimic.setNoGravity(true);
        mimic.setNoAi(true);
        var target = helper.spawn(net.minecraft.world.entity.EntityTypes.COW, new BlockPos(3, 3, 4));
        target.setNoAi(true);
        target.setNoGravity(true);
        mimic.setTarget(target);
        mimic.setAngry(true);
        var attack = new ca.lukegrahamlandry.mimic.goals.MimicAttackGoal(mimic);
        attack.start();
        for (int tick = 1; tick < 20; tick++) helper.runAfterDelay(tick, attack::tick);
        helper.runAfterDelay(20, () -> {
            check(helper, target.getHealth() < target.getMaxHealth() || !target.isAlive(), "Attack goal did not damage its target");
            mimic.addItem(new ItemStack(Items.DIAMOND, 7));
            mimic.hurtServer(helper.getLevel(), helper.getLevel().damageSources().genericKill(), Float.MAX_VALUE);
            var items = helper.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                    mimic.getBoundingBox().inflate(3));
            check(helper, items.stream().filter(item -> item.getItem().is(Items.DIAMOND))
                    .mapToInt(item -> item.getItem().getCount()).sum() == 7, "Death did not drop the exact stored inventory");
            helper.succeed();
        });
    }

    @GameTest
    public void eatsChestWithoutLosingItems(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState());
        var chest = (ChestBlockEntity) helper.getLevel().getBlockEntity(pos);
        chest.setItem(0, new ItemStack(Items.DIAMOND, 7));
        var mimic = helper.spawn(ModMain.mimicEntityType, new BlockPos(2, 3, 2));
        mimic.consumeChest(pos);
        check(helper, helper.getLevel().getBlockState(pos).isAir(), "Chest was not consumed");
        check(helper, mimic.countItem(Items.DIAMOND) == 7, "Chest inventory was not transferred exactly");
        check(helper, mimic.isStealth(), "Mimic should disguise itself after eating");
        helper.succeed();
    }

    @GameTest
    public void inventoryAndStateSurviveSave(GameTestHelper helper) {
        var mimic = helper.spawn(ModMain.mimicEntityType, new BlockPos(2, 2, 2));
        mimic.addItem(new ItemStack(Items.EMERALD, 19));
        mimic.setLocked(true);
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        mimic.addAdditionalSaveData(output);
        var restored = ModMain.mimicEntityType.create(helper.getLevel(), EntitySpawnReason.LOAD);
        restored.readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING,
                helper.getLevel().registryAccess(), output.buildResult()));
        check(helper, restored.countItem(Items.EMERALD) == 19, "Saved inventory was lost");
        check(helper, restored.isLocked() && restored.isStealth() && !restored.isAngry(), "Saved state was lost");
        helper.succeed();
    }

    @GameTest(maxTicks = 100)
    public void heartConvertsChestAfterDelay(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(3, 2, 3));
        helper.getLevel().setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(pos, Blocks.CHEST.defaultBlockState());
        ((ChestBlockEntity) helper.getLevel().getBlockEntity(pos)).setItem(0, new ItemStack(Items.GOLD_INGOT, 11));
        TickHandler.spawns.add(new TickHandler.MimicSpawnData(helper.getLevel(), pos));
        helper.runAfterDelay(45, () -> {
            check(helper, helper.getLevel().getBlockState(pos).isAir(), "Delayed chest conversion failed");
            var mimics = helper.getLevel().getEntitiesOfClass(FabricGeoMimicEntity.class,
                    new net.minecraft.world.phys.AABB(pos));
            check(helper, mimics.stream().anyMatch(m -> m.countItem(Items.GOLD_INGOT) == 11),
                    "Converted Mimic lost chest items");
            helper.succeed();
        });
    }

    @GameTest
    public void lootAndRecipesLoad(GameTestHelper helper) {
        var loot = helper.getLevel().getServer().reloadableRegistries().getLootTable(
                ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "default_mimic_loot")));
        check(helper, loot != net.minecraft.world.level.storage.loot.LootTable.EMPTY, "Mimic loot table is missing");
        for (String name : new String[]{"mimic_key", "mimic_lock", "fake_chest"}) {
            check(helper, helper.getLevel().getServer().getRecipeManager().byKey(
                    ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name))).isPresent(),
                    "Recipe is missing: " + name);
        }
        helper.succeed();
    }
}
