package dev.jason.gboardpatches.extension.numberhints;

import android.content.SharedPreferences;

import dev.jason.gboardpatches.extension.flagsettings.GboardBooleanFlagSettings;

public final class GboardHideNumberHintsSettings {
    public static final String PREF_KEY_ENABLED = "pref_hide_number_hints_enabled";
    public static final boolean DEFAULT_ENABLED = false;

    private GboardHideNumberHintsSettings() { }

    public static boolean readEnabled(SharedPreferences preferences) {
        try {
            return GboardBooleanFlagSettings.readEnabled(
                    preferences, PREF_KEY_ENABLED, DEFAULT_ENABLED);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void ensureDefault(SharedPreferences preferences) {
        GboardBooleanFlagSettings.ensureDefault(preferences, PREF_KEY_ENABLED, DEFAULT_ENABLED);
    }

    public static boolean writeEnabled(SharedPreferences preferences, boolean enabled) {
        return GboardBooleanFlagSettings.writeEnabled(preferences, PREF_KEY_ENABLED, enabled);
    }
}
