package ca.lukegrahamlandry.mimic;

import ca.lukegrahamlandry.mimic.entities.MimicEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class FabricGeoMimicEntity extends MimicEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public FabricGeoMimicEntity(EntityType<? extends MimicEntity> type, Level level) { super(type, level); }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<FabricGeoMimicEntity>("moveController", 0, state -> {
            var animation = animationPredicate(state.isMoving() ? 1f : 0f);
            RawAnimation raw = RawAnimation.begin();
            return state.setAndContinue(animation.getSecond() ? raw.thenLoop(animation.getFirst())
                    : raw.thenPlayAndHold(animation.getFirst()));
        }));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
