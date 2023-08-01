package com.teampotato.mcretector.mixin;

import com.teampotato.mcretector.MCretector;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MixinPlugin implements IMixinConfigPlugin {
    public static final Logger LOGGER = LogManager.getLogger(MCretector.MOD_ID);

    private static final List<String> MCREATOR_MODS = new ArrayList<>();

    public MixinPlugin() {
        LOGGER.info("MCreator mods detection starts.");
        FMLLoader.getLoadingModList().getModFiles().forEach(iModFileInfo -> {
            String jarFilePath = iModFileInfo.getFile().getFilePath().normalize().toString();
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    if (entry.isDirectory() && (entryName.contains("mcreator") || entryName.contains("procedures"))) {
                        MCREATOR_MODS.add(jarFilePath);
                        LOGGER.fatal("Possible MCreator mod found: " + jarFilePath);
                    }
                }
            } catch (IOException e) {
                LOGGER.fatal("Failed to check " + jarFilePath + ", try to restart your computer!");
                Minecraft.getInstance().stop();
            }
        });
        if (!MCREATOR_MODS.isEmpty() && FMLLoader.getDist().isClient()) {
            LOGGER.fatal("Possible MCreator mod(s) found. Check latest log for details.");
            Minecraft.getInstance().stop();
            return;
        }
        LOGGER.info("MCreator mods detection ends.");
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
