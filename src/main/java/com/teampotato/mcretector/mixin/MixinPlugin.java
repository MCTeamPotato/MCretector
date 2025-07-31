package com.teampotato.mcretector.mixin;

import com.teampotato.mcretector.MCretector;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IConfigurable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MixinPlugin implements IMixinConfigPlugin {
    public static final Logger LOGGER = LogManager.getLogger(MCretector.MOD_ID);

    private static final Set<String> MCREATOR_MODS = new HashSet<>();
    private static final Set<String> MODS_FAILED_TO_DETECT = new HashSet<>();

    public MixinPlugin() {
        LOGGER.info("MCreator mods detection starts.");
        List<ModFileInfo> modFileInfoList = processDetection();
        if (!MCREATOR_MODS.isEmpty()) {
            LOGGER.fatal("Possible MCreator mod(s) found:");
            MCREATOR_MODS.forEach(LOGGER::fatal);
            LOGGER.info("MCreator mods detection ends.");
        } else {
            LOGGER.info("MCreator mods detection ends. No possible MCreator mods found");
        }
        if (!MODS_FAILED_TO_DETECT.isEmpty()) {
            if (MODS_FAILED_TO_DETECT.size() == modFileInfoList.size()) {
                LOGGER.fatal("MCretector failed to detect files because your mods folder are locked, try to restart your computer!");
                return;
            }
            LOGGER.warn("Mods failed to detect:");
            MODS_FAILED_TO_DETECT.forEach(LOGGER::warn);
            LOGGER.warn("There are many reasons for this. If the mod is JIJ (Jar In Jar, mods included in other mods' files as their libraries), or the mod is a Language Provider (such as Kotlin For Forge, they're exceptions from normal mods that will keep loading and be locked during the game), detection will fail to process");
            LOGGER.warn("But these kinds of mods will never be made of MCreator under normal circumstances.");
        }
    }

    @NotNull
    private static List<ModFileInfo> processDetection() {
        List<ModFileInfo> modFileInfoList = FMLLoader.getLoadingModList().getModFiles();
        modFileInfoList.forEach(iModFileInfo -> {
            String jarFilePath = iModFileInfo.getFile().getFilePath().normalize().toString();
            IConfigurable config = iModFileInfo.getConfig();
            Consumer<Object> consumer = value -> {
                if (value instanceof String && (((String) value).contains("mcreator") || ((String)value).contains("MCreator"))) MCREATOR_MODS.add(jarFilePath);
            };
            config.getConfigElement("credits").ifPresent(consumer);
            config.getConfigElement("displayURL").ifPresent(consumer);
            config.getConfigElement("authors").ifPresent(consumer);
            config.getConfigElement("license").ifPresent(value -> {
                if (value instanceof String && value.equals("Not specified")) MCREATOR_MODS.add(jarFilePath);
            });
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    if (entry.isDirectory() && (entryName.contains("mcreator") || entryName.contains("procedures"))) {
                        MCREATOR_MODS.add(jarFilePath);
                    }
                }
            } catch (IOException exception) {
                String modID = iModFileInfo.getFile().getFileName();
                MODS_FAILED_TO_DETECT.add(modID);
            }
        });
        return modFileInfoList;
    }

    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
