package malfu.wandering_orc.entity.armor;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.item.custom.WarriorDiamondArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarriorDiamondArmorRenderer extends GeoArmorRenderer<WarriorDiamondArmorItem> {
    public WarriorDiamondArmorRenderer() {
        super(new DefaultedItemGeoModel<WarriorDiamondArmorItem>(new Identifier(WanderingOrc.MOD_ID, "armor/warrior_armor"))
                .withAltTexture(new Identifier(WanderingOrc.MOD_ID, "armor/warrior_diamond_armor")));
    }
}
