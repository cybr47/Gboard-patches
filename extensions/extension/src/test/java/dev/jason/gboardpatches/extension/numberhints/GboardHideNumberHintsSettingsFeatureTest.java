package dev.jason.gboardpatches.extension.numberhints;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import dev.jason.gboardpatches.extension.keyboard.GboardKeyboardLayoutSettingsGroupFeature;
import dev.jason.gboardpatches.extension.settings.GboardPatchesFeatureAvailability;
import dev.jason.gboardpatches.extension.settings.GboardPatchesSettings;
import dev.jason.gboardpatches.extension.settings.GboardPatchesSettingsContract;
import dev.jason.gboardpatches.extension.settings.GboardPatchesSettingsTestHost;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public final class GboardHideNumberHintsSettingsFeatureTest {
    @Test
    public void selectedPatchShowsWorkingToggleInKeyboardLayoutGroup() {
        Context context = RuntimeEnvironment.getApplication();
        SharedPreferences preferences = GboardPatchesSettings.preferences(context);
        preferences.edit().clear().commit();
        Bundle original = context.getApplicationInfo().metaData;
        context.getApplicationInfo().metaData = new Bundle();
        try {
            GboardHideNumberHintsSettingsFeature feature = new GboardHideNumberHintsSettingsFeature(context);
            GboardPatchesSettingsTestHost host = new GboardPatchesSettingsTestHost(context);
            assertFalse(feature.isAvailable(context));
            assertFalse(new GboardKeyboardLayoutSettingsGroupFeature(context).buildScreen(host)
                    .getRows().stream().anyMatch(row -> "Hide Number Hints".contentEquals(row.getTitle())));

            context.getApplicationInfo().metaData.putBoolean(
                    GboardPatchesFeatureAvailability.FEATURE_HIDE_NUMBER_HINTS, true);
            assertTrue(feature.isAvailable(context));
            assertTrue(new GboardKeyboardLayoutSettingsGroupFeature(context).buildScreen(host)
                    .getRows().stream().anyMatch(row -> "Hide Number Hints".contentEquals(row.getTitle())));

            GboardPatchesSettingsContract.ToggleRow row = (GboardPatchesSettingsContract.ToggleRow)
                    feature.buildScreen(host).getRows().get(0);
            assertFalse(row.isChecked());
            row.getToggleAction().accept(true);
            assertTrue(GboardHideNumberHintsSettings.readEnabled(preferences));
            row = (GboardPatchesSettingsContract.ToggleRow) feature.buildScreen(host).getRows().get(0);
            assertTrue(row.isChecked());
            row.getToggleAction().accept(false);
            assertFalse(GboardHideNumberHintsSettings.readEnabled(preferences));
        } finally {
            context.getApplicationInfo().metaData = original;
        }
    }

    @Test
    public void corruptOrMissingPreferenceDefaultsToDisabled() {
        SharedPreferences preferences = GboardPatchesSettings.preferences(RuntimeEnvironment.getApplication());
        preferences.edit().clear().commit();
        GboardHideNumberHintsSettings.ensureDefault(preferences);
        assertFalse(GboardHideNumberHintsSettings.readEnabled(preferences));
        preferences.edit().putInt(GboardHideNumberHintsSettings.PREF_KEY_ENABLED, 1).commit();
        assertFalse(GboardHideNumberHintsSettings.readEnabled(preferences));
        assertFalse(GboardHideNumberHintsSettings.readEnabled(null));
    }
}
