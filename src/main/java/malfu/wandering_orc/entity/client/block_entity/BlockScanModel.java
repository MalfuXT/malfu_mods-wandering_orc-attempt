package malfu.wandering_orc.entity.client.block_entity;

import malfu.wandering_orc.WanderingOrc;
import malfu.wandering_orc.entity.block_entity.BlockScanEntity;
import malfu.wandering_orc.entity.custom.MinotaurEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BlockScanModel extends DefaultedEntityGeoModel<BlockScanEntity> {
    public BlockScanModel() {
        super(new Identifier(WanderingOrc.MOD_ID, "block_scan_entity"));
    }
}
