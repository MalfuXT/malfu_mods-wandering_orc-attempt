package malfu.wandering_orc.entity.armor;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.item.custom.WarriorArmArmorItem;
import malfu.wandering_orc.item.custom.WarriorArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarriorArmArmorRenderer extends GeoArmorRenderer<WarriorArmArmorItem> {
    public WarriorArmArmorRenderer() {
        super(new DefaultedItemGeoModel<WarriorArmArmorItem>(new Identifier(WanderingOrc.MOD_ID, "armor/warrior_armor"))
                .withAltTexture(new Identifier(WanderingOrc.MOD_ID, "armor/warrior_arm_armor")));
    }
}
