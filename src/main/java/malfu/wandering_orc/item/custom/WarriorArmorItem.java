package malfu.wandering_orc.item.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import malfu.wandering_orc.entity.armor.WarriorArmorRenderer;
import malfu.wandering_orc.item.ModItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class WarriorArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Supplier<Object> renderProvider;

    public WarriorArmorItem(ArmorMaterial armorMaterial, Type type, Settings properties) {
        super(armorMaterial, type, properties);
        if (FabricLoader.getInstance().isModLoaded("fabric")) {
            try {
                this.renderProvider = GeoItem.makeRenderer(this);
            } catch (NoSuchMethodError e) {
                this.renderProvider = () -> null;  // Fallback in case method is missing
            }
        } else {
            this.renderProvider = () -> null;  // No renderer for non-Fabric environments
        }
    }

    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            public @NotNull BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if (this.renderer == null) {
                    this.renderer = new WarriorArmorRenderer();
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, (state) -> {
            state.getController().setAnimation(DefaultAnimations.IDLE);
            Entity entity = (Entity)state.getData(DataTickets.ENTITY);
            if (entity instanceof ArmorStandEntity) {
                return PlayState.CONTINUE;
            } else {
                Set<Item> wornArmor = new ObjectOpenHashSet<>();

                for(ItemStack stack : entity.getArmorItems()) {
                    if (stack.isEmpty()) {
                        return PlayState.STOP;
                    }

                    wornArmor.add(stack.getItem());
                }

                boolean isFullSet = wornArmor.containsAll(ObjectArrayList.of
                        (ModItems.WARRIOR_HELMET, ModItems.WARRIOR_CHESTPLATE,
                                ModItems.WARRIOR_LEGGINGS, ModItems.WARRIOR_BOOTS));
                return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
            }
        }));
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

