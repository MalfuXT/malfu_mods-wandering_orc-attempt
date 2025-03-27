package malfu.wandering_orc.entity.armor;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.custom.OrcArcherEntity;
import malfu.wandering_orc.item.custom.WarriorArmorItem;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WarriorArmorRenderer extends GeoArmorRenderer<WarriorArmorItem> {
    public WarriorArmorRenderer() {
        super(new DefaultedItemGeoModel<WarriorArmorItem>(new Identifier(WanderingOrc.MOD_ID, "armor/warrior_armor"))
                .withAltTexture(new Identifier(WanderingOrc.MOD_ID, "armor/warrior_armor")));
    }
}
