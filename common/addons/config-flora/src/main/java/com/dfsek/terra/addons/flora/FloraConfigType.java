package com.dfsek.terra.addons.flora;

import java.util.function.Supplier;

import com.dfsek.terra.api.TerraPlugin;
import com.dfsek.terra.api.config.ConfigFactory;
import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.api.config.ConfigType;
import com.dfsek.terra.api.registry.OpenRegistry;
import com.dfsek.terra.api.structure.Structure;
import com.dfsek.terra.api.util.reflection.TypeKey;
import com.dfsek.terra.api.world.Flora;


public class FloraConfigType implements ConfigType<FloraTemplate, Structure> {
    public static final TypeKey<Structure> FLORA_TYPE_TOKEN = new TypeKey<>() {
    };
    private final FloraFactory factory = new FloraFactory();
    
    @Override
    public Supplier<OpenRegistry<Structure>> registrySupplier(ConfigPack pack) {
        return pack.getRegistryFactory()::create;
    }
    
    @Override
    public FloraTemplate getTemplate(ConfigPack pack, TerraPlugin main) {
        return new FloraTemplate();
    }
    
    @Override
    public ConfigFactory<FloraTemplate, Structure> getFactory() {
        return factory;
    }
    
    @Override
    public TypeKey<Structure> getTypeKey() {
        return FLORA_TYPE_TOKEN;
    }
}
