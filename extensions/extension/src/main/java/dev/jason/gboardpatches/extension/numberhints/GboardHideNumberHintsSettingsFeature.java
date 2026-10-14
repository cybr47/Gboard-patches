package dev.jason.gboardpatches.extension.numberhints;

import android.content.Context;
import android.content.SharedPreferences;

import dev.jason.gboardpatches.extension.R;
import dev.jason.gboardpatches.extension.flagsettings.GboardBooleanFlagSettingsFeature;
import dev.jason.gboardpatches.extension.settings.GboardPatchesFeatureAvailability;
import dev.jason.gboardpatches.extension.settings.GboardSettingsText;

public final class GboardHideNumberHintsSettingsFeature extends GboardBooleanFlagSettingsFeature {
    public GboardHideNumberHintsSettingsFeature(Context context) {
        super(
                GboardPatchesFeatureAvailability.FEATURE_HIDE_NUMBER_HINTS,
                GboardSettingsText.get(context, R.string.gboard_patches_hide_number_hints_title),
                GboardSettingsText.get(context, R.string.gboard_patches_hide_number_hints_summary),
                GboardSettingsText.get(context, R.string.gboard_patches_hide_number_hints_title),
                GboardSettingsText.get(context, R.string.gboard_patches_header_badge),
                GboardSettingsText.get(context, R.string.gboard_patches_hide_number_hints_error_title),
                GboardSettingsText.get(context, R.string.gboard_patches_hide_number_hints_error_summary),
                GboardSettingsText.get(context, R.string.gboard_patches_hide_number_hints_section),
                null,
                new SettingsStore() {
                    @Override
                    public void ensureDefault(SharedPreferences preferences) {
                        GboardHideNumberHintsSettings.ensureDefault(preferences);
                    }

                    @Override
                    public boolean readEnabled(SharedPreferences preferences) {
                        return GboardHideNumberHintsSettings.readEnabled(preferences);
                    }

                    @Override
                    public boolean writeEnabled(SharedPreferences preferences, boolean enabled) {
                        return GboardHideNumberHintsSettings.writeEnabled(preferences, enabled);
                    }
                });
    }
}
