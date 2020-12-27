package api.player.asm.mixins.net.minecraft.entity;

import api.player.asm.interfaces.IEntityAccessor;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntityAccessor
{
    @Shadow
    protected boolean firstUpdate;

    @Override
    public boolean getFirstUpdate()
    {
        return this.firstUpdate;
    }
}
