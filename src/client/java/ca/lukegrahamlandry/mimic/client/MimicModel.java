package ca.lukegrahamlandry.mimic.client;

import ca.lukegrahamlandry.mimic.Constants;
import ca.lukegrahamlandry.mimic.FabricGeoMimicEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;

public class MimicModel extends GeoModel<FabricGeoMimicEntity> {
    private static final DataTicket<Boolean> TAMED = DataTickets.create("mimic:tamed", Boolean.class);
    @Override
    public Identifier getModelResource(GeoRenderState state) { return Constants.MODEL_LOC; }
    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return state.getOrDefaultGeckolibData(TAMED, false) ? Constants.TAME_TEXTURE_LOC : Constants.EVIL_TEXTURE_LOC;
    }
    @Override
    public Identifier getAnimationResource(FabricGeoMimicEntity mimic) { return Constants.ANIM_LOC; }
    @Override
    public void addAdditionalStateData(FabricGeoMimicEntity mimic, Object related, GeoRenderState state) {
        state.addGeckolibData(TAMED, mimic.isTamed());
    }
}
