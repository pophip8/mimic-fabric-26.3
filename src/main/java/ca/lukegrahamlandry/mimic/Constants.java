package ca.lukegrahamlandry.mimic;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "mimic";
	public static final String MOD_NAME = "Mimic";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final Identifier MIMIC_ENTITY_ID = Identifier.fromNamespaceAndPath(MOD_ID, "mimic");
	public static final Identifier MIMIC_KEY_ID = Identifier.fromNamespaceAndPath(MOD_ID, "mimic_key");
	public static final Identifier MIMIC_LOCK_ID = Identifier.fromNamespaceAndPath(MOD_ID, "mimic_lock");
	public static final Identifier SINGLE_MIMIC_SPAWN_BLOCK_ID = Identifier.fromNamespaceAndPath(MOD_ID, "mimic_lock");
    public static final Identifier TAME_MIMIC_CONTAINER = Identifier.fromNamespaceAndPath(MOD_ID, "tame_mimic");
	public static final Identifier EVIL_MIMIC_CONTAINER = Identifier.fromNamespaceAndPath(MOD_ID, "evil_mimic");

	public static final Identifier MODEL_LOC = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mimic");
	public static final Identifier TAME_TEXTURE_LOC = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/tamed_mimic.png");
	public static final Identifier EVIL_TEXTURE_LOC = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/entity/evil_mimic.png");
	public static final Identifier ANIM_LOC = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mimic");

	public static Block getMimicSpawnBlock() {
		return BuiltInRegistries.BLOCK.getValue(SINGLE_MIMIC_SPAWN_BLOCK_ID);
	}
}