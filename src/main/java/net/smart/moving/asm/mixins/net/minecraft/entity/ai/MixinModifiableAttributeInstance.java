package net.smart.moving.asm.mixins.net.minecraft.entity.ai;

import net.smart.moving.asm.interfaces.IModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModifiableAttributeInstance.class)
public abstract class MixinModifiableAttributeInstance implements IModifiableAttributeInstance
{
    @Shadow
    private double cachedValue;

    @Override
    public void setCachedValue(float cachedValue)
    {
        this.cachedValue = cachedValue;
    }
}