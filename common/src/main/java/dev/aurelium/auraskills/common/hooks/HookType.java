package dev.aurelium.auraskills.common.hooks;

public interface HookType {

    Class<? extends Hook> getHookClass();

    String getPluginName();

}
