package ca.lukegrahamlandry.mimic.events;

import ca.lukegrahamlandry.mimic.ModMain;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import java.util.ArrayList;
import java.util.List;

public class TickHandler {
    public static final List<MimicSpawnData> spawns = new ArrayList<>();

    public static void onServerTick() {
        var iterator = spawns.iterator();
        while (iterator.hasNext()) {
            MimicSpawnData spawn = iterator.next();
            if (--spawn.time <= 0) {
                iterator.remove();
                if (spawn.world instanceof ServerLevel server && server.hasChunkAt(spawn.pos)
                        && server.getBlockState(spawn.pos).is(Blocks.CHEST)) {
                    var mimic = ModMain.mimicEntityType.create(server, EntitySpawnReason.TRIGGERED);
                    if (mimic != null) {
                        mimic.snapToBlock(spawn.pos, null);
                        if (server.addFreshEntity(mimic)) mimic.consumeChest(spawn.pos);
                    }
                }
            }
        }
    }

    public static class MimicSpawnData {
        public final BlockPos pos;
        public int time = 40;
        public final Level world;
        public MimicSpawnData(Level level, BlockPos position) {
            this.pos = position.immutable();
            this.world = level;
        }
    }
}
