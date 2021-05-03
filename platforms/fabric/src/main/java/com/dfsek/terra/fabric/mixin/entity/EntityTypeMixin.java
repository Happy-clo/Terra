package com.dfsek.terra.fabric.mixin.entity;

import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
@Implements(@Interface(iface = com.dfsek.terra.api.platform.entity.EntityType.class, prefix = "vw$"))
public abstract class EntityTypeMixin {
    public Object vw$getHandle() {
        return this;
    }
}
