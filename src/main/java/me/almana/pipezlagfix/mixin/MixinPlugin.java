package me.almana.pipezlagfix.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private boolean enabled = true;

    @Override
    public void onLoad(String mixinPackage) {
        // Simple manual config reading because Forge config isn't loaded yet
        Path configPath = Paths.get("config", "pipezlagfix-common.toml");
        if (Files.exists(configPath)) {
            try {
                List<String> lines = Files.readAllLines(configPath);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("enabled")) {
                        // enabled = false
                        if (trimmed.contains("false")) {
                            enabled = false;
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return enabled;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
