package dev.jason.gboardpatches.patches.gboard.features.numberhints

import app.morphe.patcher.patch.resourcePatch
import dev.jason.gboardpatches.patches.gboard.features.featureflags.applyFeatureMarker
import dev.jason.gboardpatches.patches.shared.Constants.COMPATIBILITY_GBOARD

internal val gboardHideNumberHintsFeatureMarkerPatch = resourcePatch(
    description = "Register Hide Number Hints in the Gboard Patches settings UI.",
) {
    compatibleWith(COMPATIBILITY_GBOARD)

    finalize {
        applyFeatureMarker("dev.jason.gboardpatches.feature.hide_number_hints")
    }
}
