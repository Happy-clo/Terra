package com.dfsek.terra;

import com.dfsek.tectonic.loading.TypeRegistry;
import com.dfsek.terra.biome.grid.master.TerraBiomeGrid;
import com.dfsek.terra.biome.palette.PaletteHolder;
import com.dfsek.terra.biome.palette.PaletteLayer;
import com.dfsek.terra.carving.CarverPalette;
import com.dfsek.terra.command.TerraCommand;
import com.dfsek.terra.command.structure.LocateCommand;
import com.dfsek.terra.config.base.PluginConfig;
import com.dfsek.terra.config.base.WorldConfig;
import com.dfsek.terra.config.lang.LangUtil;
import com.dfsek.terra.config.loaders.ImageLoaderLoader;
import com.dfsek.terra.config.loaders.MaterialSetLoader;
import com.dfsek.terra.config.loaders.ProbabilityCollectionLoader;
import com.dfsek.terra.config.loaders.RangeLoader;
import com.dfsek.terra.config.loaders.config.FloraLayerLoader;
import com.dfsek.terra.config.loaders.config.GridSpawnLoader;
import com.dfsek.terra.config.loaders.config.NoiseBuilderLoader;
import com.dfsek.terra.config.loaders.config.OreConfigLoader;
import com.dfsek.terra.config.loaders.config.OreHolderLoader;
import com.dfsek.terra.config.loaders.config.StructureFeatureLoader;
import com.dfsek.terra.config.loaders.config.TreeLayerLoader;
import com.dfsek.terra.config.loaders.palette.CarverPaletteLoader;
import com.dfsek.terra.config.loaders.palette.PaletteHolderLoader;
import com.dfsek.terra.config.loaders.palette.PaletteLayerLoader;
import com.dfsek.terra.debug.Debug;
import com.dfsek.terra.generation.TerraChunkGenerator;
import com.dfsek.terra.generation.config.NoiseBuilder;
import com.dfsek.terra.generation.items.flora.FloraLayer;
import com.dfsek.terra.generation.items.flora.TerraFlora;
import com.dfsek.terra.generation.items.ores.Ore;
import com.dfsek.terra.generation.items.ores.OreConfig;
import com.dfsek.terra.generation.items.ores.OreHolder;
import com.dfsek.terra.generation.items.tree.TreeLayer;
import com.dfsek.terra.image.ImageLoader;
import com.dfsek.terra.listeners.EventListener;
import com.dfsek.terra.listeners.SpigotListener;
import com.dfsek.terra.procgen.GridSpawn;
import com.dfsek.terra.registry.ConfigRegistry;
import com.dfsek.terra.structure.features.Feature;
import com.dfsek.terra.util.MaterialSet;
import com.dfsek.terra.util.PaperUtil;
import com.dfsek.terra.util.StructureTypeEnum;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.polydev.gaea.GaeaPlugin;
import org.polydev.gaea.generation.GaeaChunkGenerator;
import org.polydev.gaea.lang.Language;
import org.polydev.gaea.math.ProbabilityCollection;
import org.polydev.gaea.math.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Terra extends GaeaPlugin {
    private final Map<String, TerraChunkGenerator> generatorMap = new HashMap<>();
    private final ConfigRegistry registry = new ConfigRegistry();

    @Override
    public void onDisable() {
        TerraChunkGenerator.saveAll();
    }

    @Override
    public void onEnable() {
        Debug.setLogger(getLogger()); // Set debug logger.


        saveDefaultConfig();

        Metrics metrics = new Metrics(this, 9017); // Set up bStats.
        metrics.addCustomChart(new Metrics.SingleLineChart("worlds", TerraWorld::numWorlds)); // World number chart.

        PluginConfig.load(this); // Load master config.yml
        LangUtil.load(PluginConfig.getLanguage(), this); // Load language.

        TerraWorld.invalidate(); // Clear/set up world cache.
        registry.loadAll(this); // Load all config packs.

        PluginCommand c = Objects.requireNonNull(getCommand("terra"));
        TerraCommand command = new TerraCommand(this); // Set up main Terra command.
        c.setExecutor(command);
        c.setTabCompleter(command);

        LocateCommand locate = new LocateCommand(command, false);
        PluginCommand locatePl = Objects.requireNonNull(getCommand("locate"));
        locatePl.setExecutor(locate); // Override locate command. Once Paper accepts StructureLocateEvent this will be unneeded on Paper implementations.
        locatePl.setTabCompleter(locate);


        long save = PluginConfig.getDataSaveInterval();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, TerraChunkGenerator::saveAll, save, save); // Schedule population data saving

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this); // Register master event listener
        Bukkit.getPluginManager().registerEvents(new SpigotListener(this), this); // Register Spigot event listener, once Paper accepts StructureLocateEvent PR Spigot and Paper events will be separate.

        PaperUtil.checkPaper(this);
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return generatorMap.computeIfAbsent(worldName, name -> {
            WorldConfig c = new WorldConfig(worldName, id, this);
            TerraWorld.loadWorld(c);
            return new TerraChunkGenerator(c.getConfig(), this);
        });
    }

    @Override
    public boolean isDebug() {
        return PluginConfig.isDebug();
    }

    @Override
    public Class<? extends GaeaChunkGenerator> getGeneratorClass() {
        return TerraChunkGenerator.class;
    }

    @Override
    public Language getLanguage() {
        return LangUtil.getLanguage();
    }

    public void registerAllLoaders(TypeRegistry registry) {
        registry.registerLoader(ProbabilityCollection.class, new ProbabilityCollectionLoader())
                .registerLoader(Range.class, new RangeLoader())
                .registerLoader(CarverPalette.class, new CarverPaletteLoader())
                .registerLoader(GridSpawn.class, new GridSpawnLoader())
                .registerLoader(PaletteHolder.class, new PaletteHolderLoader())
                .registerLoader(PaletteLayer.class, new PaletteLayerLoader())
                .registerLoader(Biome.class, (t, o, l) -> Biome.valueOf((String) o))
                .registerLoader(BlockData.class, (t, o, l) -> Bukkit.createBlockData((String) o))
                .registerLoader(Material.class, (t, o, l) -> Material.matchMaterial((String) o))
                .registerLoader(FloraLayer.class, new FloraLayerLoader())
                .registerLoader(Ore.Type.class, (t, o, l) -> Ore.Type.valueOf((String) o))
                .registerLoader(OreConfig.class, new OreConfigLoader())
                .registerLoader(NoiseBuilder.class, new NoiseBuilderLoader())
                .registerLoader(TreeLayer.class, new TreeLayerLoader(this))
                .registerLoader(MaterialSet.class, new MaterialSetLoader())
                .registerLoader(OreHolder.class, new OreHolderLoader())
                .registerLoader(Feature.class, new StructureFeatureLoader())
                .registerLoader(ImageLoader.class, new ImageLoaderLoader())
                .registerLoader(EntityType.class, (t, o, l) -> EntityType.valueOf((String) o))
                .registerLoader(TerraBiomeGrid.Type.class, (t, o, l) -> TerraBiomeGrid.Type.valueOf((String) o))
                .registerLoader(StructureTypeEnum.class, (t, o, l) -> StructureTypeEnum.valueOf((String) o))
                .registerLoader(ImageLoader.Channel.class, (t, o, l) -> ImageLoader.Channel.valueOf((String) o))
                .registerLoader(ImageLoader.Align.class, (t, o, l) -> ImageLoader.Align.valueOf((String) o))
                .registerLoader(TerraFlora.Search.class, (t, o, l) -> TerraFlora.Search.valueOf((String) o));
    }

    public ConfigRegistry getRegistry() {
        return registry;
    }
}
