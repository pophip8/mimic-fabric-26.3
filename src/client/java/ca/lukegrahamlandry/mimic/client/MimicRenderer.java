package ca.lukegrahamlandry.mimic.client;

import ca.lukegrahamlandry.mimic.FabricGeoMimicEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;

public class MimicRenderer extends GeoEntityRenderer<FabricGeoMimicEntity, LivingEntityRenderState> {
    public MimicRenderer(EntityRendererProvider.Context context) { super(context, new MimicModel()); }
}
