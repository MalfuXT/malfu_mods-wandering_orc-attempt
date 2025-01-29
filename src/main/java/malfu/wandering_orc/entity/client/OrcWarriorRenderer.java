package malfu.wandering_orc.entity.client;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcWarriorEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcWarriorRenderer extends GeoEntityRenderer<OrcWarriorEntity> {
    public OrcWarriorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcWarriorModel());
    }

    @Override
    public Identifier getTexture(OrcWarriorEntity animatable) {
        return new Identifier(WanderingOrc.MOD_ID,"textures/entity/orc_warrior.png");
    }
}
