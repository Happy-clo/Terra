package com.dfsek.terra.config;

import com.dfsek.tectonic.loading.TypeRegistry;
import com.dfsek.terra.api.TerraPlugin;
import com.dfsek.terra.api.addon.TerraAddon;
import com.dfsek.terra.api.block.BlockType;
import com.dfsek.terra.api.math.GridSpawn;
import com.dfsek.terra.api.tectonic.LoaderRegistrar;
import com.dfsek.terra.api.util.ProbabilityCollection;
import com.dfsek.terra.api.util.Range;
import com.dfsek.terra.api.util.collections.MaterialSet;
import com.dfsek.terra.api.util.seeded.SourceSeeded;
import com.dfsek.terra.api.util.seeded.StageSeeded;
import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.api.world.biome.generation.pipeline.BiomeSource;
import com.dfsek.terra.addons.biome.pipeline.stages.ExpanderStage;
import com.dfsek.terra.addons.biome.pipeline.stages.MutatorStage;
import com.dfsek.terra.api.world.biome.provider.ImageBiomeProvider;
import com.dfsek.terra.api.world.palette.holder.PaletteHolder;
import com.dfsek.terra.api.world.palette.holder.PaletteLayerHolder;
import com.dfsek.terra.api.world.palette.slant.SlantHolder;
import com.dfsek.terra.carving.CarverPalette;
import com.dfsek.terra.config.loaders.LinkedHashMapLoader;
import com.dfsek.terra.config.loaders.MaterialSetLoader;
import com.dfsek.terra.config.loaders.ProbabilityCollectionLoader;
import com.dfsek.terra.config.loaders.RangeLoader;
import com.dfsek.terra.config.loaders.config.FloraLayerLoader;
import com.dfsek.terra.config.loaders.config.GridSpawnLoader;
import com.dfsek.terra.config.loaders.config.OreConfigLoader;
import com.dfsek.terra.config.loaders.config.OreHolderLoader;
import com.dfsek.terra.config.loaders.config.TreeLayerLoader;
import com.dfsek.terra.config.loaders.config.biome.BiomeProviderBuilderLoader;
import com.dfsek.terra.config.loaders.config.biome.SourceBuilderLoader;
import com.dfsek.terra.config.loaders.config.biome.StageBuilderLoader;
import com.dfsek.terra.config.loaders.config.biome.templates.source.NoiseSourceTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.expander.ExpanderStageTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.BorderListMutatorTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.BorderMutatorTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.ReplaceListMutatorTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.ReplaceMutatorTemplate;
import com.dfsek.terra.addons.biome.pipeline.config.stage.mutator.SmoothMutatorTemplate;
import com.dfsek.terra.config.loaders.config.function.FunctionTemplate;
import com.dfsek.terra.addons.palette.palette.CarverPaletteLoader;
import com.dfsek.terra.addons.palette.palette.PaletteHolderLoader;
import com.dfsek.terra.addons.palette.palette.PaletteLayerLoader;
import com.dfsek.terra.addons.palette.palette.slant.SlantHolderLoader;
import com.dfsek.terra.addons.flora.flora.FloraLayer;
import com.dfsek.terra.addons.flora.flora.TerraFlora;
import com.dfsek.terra.addons.ore.ores.OreConfig;
import com.dfsek.terra.addons.ore.ores.OreHolder;
import com.dfsek.terra.addons.tree.tree.TreeLayer;

import java.util.LinkedHashMap;

public class GenericLoaders implements LoaderRegistrar {
    private final TerraPlugin main;

    public GenericLoaders(TerraPlugin main) {
        this.main = main;
    }

    @Override
    public void register(TypeRegistry registry) {
        registry.registerLoader(ProbabilityCollection.class, new ProbabilityCollectionLoader())
                .registerLoader(Range.class, new RangeLoader())
                .registerLoader(GridSpawn.class, new GridSpawnLoader())
                .registerLoader(PaletteHolder.class, new PaletteHolderLoader())
                .registerLoader(PaletteLayerHolder.class, new PaletteLayerLoader())
                .registerLoader(SlantHolder.class, new SlantHolderLoader())
                .registerLoader(FloraLayer.class, new FloraLayerLoader())
                .registerLoader(OreConfig.class, new OreConfigLoader())
                .registerLoader(TreeLayer.class, new TreeLayerLoader())
                .registerLoader(MaterialSet.class, new MaterialSetLoader())
                .registerLoader(OreHolder.class, new OreHolderLoader())
                .registerLoader(ReplaceMutatorTemplate.class, ReplaceMutatorTemplate::new)
                .registerLoader(ExpanderStageTemplate.class, ExpanderStageTemplate::new)
                .registerLoader(SmoothMutatorTemplate.class, SmoothMutatorTemplate::new)
                .registerLoader(ReplaceListMutatorTemplate.class, ReplaceListMutatorTemplate::new)
                .registerLoader(BorderMutatorTemplate.class, BorderMutatorTemplate::new)
                .registerLoader(BorderListMutatorTemplate.class, BorderListMutatorTemplate::new)
                .registerLoader(NoiseSourceTemplate.class, NoiseSourceTemplate::new)
                .registerLoader(FunctionTemplate.class, FunctionTemplate::new)
                .registerLoader(LinkedHashMap.class, new LinkedHashMapLoader())
                .registerLoader(CarverPalette.class, new CarverPaletteLoader())
                .registerLoader(SourceSeeded.class, new SourceBuilderLoader())
                .registerLoader(StageSeeded.class, new StageBuilderLoader())
                .registerLoader(BiomeProvider.BiomeProviderBuilder.class, new BiomeProviderBuilderLoader())
                .registerLoader(BiomeProvider.Type.class, (t, object, cf) -> BiomeProvider.Type.valueOf((String) object))
                .registerLoader(BiomeSource.Type.class, (t, object, cf) -> BiomeSource.Type.valueOf((String) object))
                .registerLoader(ImageBiomeProvider.Align.class, (t, object, cf) -> ImageBiomeProvider.Align.valueOf((String) object))
                .registerLoader(ExpanderStage.Type.class, (t, object, cf) -> ExpanderStage.Type.valueOf((String) object))
                .registerLoader(MutatorStage.Type.class, (t, object, cf) -> MutatorStage.Type.valueOf((String) object))
                .registerLoader(TerraFlora.Search.class, (t, o, l) -> TerraFlora.Search.valueOf(o.toString()));

        if(main != null) {
            registry.registerLoader(TerraAddon.class, main.getAddons())
                    .registerLoader(BlockType.class, (t, object, cf) -> main.getWorldHandle().createBlockData((String) object).getBlockType());
        }
    }
}
