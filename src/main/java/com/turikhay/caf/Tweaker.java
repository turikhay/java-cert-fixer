package com.turikhay.caf;

import com.turikhay.caf.util.Logger;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.launchwrapper.LogWrapper;

import java.io.File;
import java.util.List;

/**
 * Tweaker for modern Minecraft versions
 * @see com.turikhay.caf.Tweaker2 modified Tweaker
 */
public class Tweaker implements ITweaker {
    static {
        CAFixer.fix(new Logger() {
            @Override
            public void logMessage(String message) {
                LogWrapper.info(message);
            }

            @Override
            public void logError(String message, Throwable error) {
                LogWrapper.severe(message, error);
            }
        });
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
