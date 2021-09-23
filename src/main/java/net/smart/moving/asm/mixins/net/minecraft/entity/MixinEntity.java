package net.smart.moving.asm.mixins.net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.smart.moving.asm.interfaces.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity
{
    @Shadow
    protected abstract void setSize(float width, float height);

    @Override
    public void publicSetSize(float width, float height)
    {
        this.setSize(width, height);
    }
}