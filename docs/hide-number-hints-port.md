# Hide Number Hints — full Gboard 18.0.3 port

Status: **research / not yet implemented or validated**. This is a feature-branch design note, not a released Morphe patch.

## Target and user-visible behavior

- Full Gboard `18.0.3.954559732-release-arm64-v8a` (APKMirror APKM split bundle).
- Hide only the small `1 2 3 4 5 6 7 8 9 0` secondary labels above the QWERTY letters.
- Preserve normal QWERTY character entry, long-press number actions, numeric keyboard, passwords, and secondary symbol/other-language layouts.
- Recenter the letter labels if their layout currently reserves a slot for the hint.
- Eventually expose a user-controlled setting, default **off**, rather than silently changing unrelated keyboard layouts.

## Verified upstream integration points

1. Target bindings already admit Gboard version `18.0.3`.
2. `gboard-version-bindings.json` identifies the full-Gboard SoftKey bind method as `SoftKeyView.r(SoftKeyDef, long): boolean`.
3. `GboardSoftKeyFamilyComposer.kt` owns edits to this method so the English up-flick, top-row swipe, spacebar logo, custom symbols and other SoftKey patches can coexist.
4. The English QWERTY implementation reads `SoftKeyDef.g: CharSequence[]` to resolve the main letter label. Avoid mutating this field when hiding hint labels.
5. Feature settings are implemented in `extensions/extension` and assembled in `GboardKeyboardLayoutSettingsGroupFeature.java`.

## Why a straight port is unsafe

A different project's old Hide Number Hints hook targeted `SoftKeyView.q(...)` in its own Gboard variant. Our full-Gboard 18.0.3 binding identifies `r(...)`, so copying its fingerprint as-is would not work. That project's implementation was later reverted entirely. Its assumption that the secondary label is `SoftKeyDef.h` must be confirmed independently against **this** APK, not copied as fact.

A second standalone patch that modifies `SoftKeyView.r` without coordinating with `GboardSoftKeyFamilyComposer` can also make existing patch composition fail its exact-stock verification.

## Required before implementation

- Obtain the exact `.apkm` bundle and inspect `base.apk` / relevant splits, including Gboard's `SoftKeyDef` fields, key-rendering methods, and layout resources.
- Establish whether the number hints are drawn from metadata, view children or canvas; identify the **render-only** modification that leaves actions intact.
- Add an opt-in feature to the SoftKey composer (if the bind method is the correct hook) and the extension settings UI, plus focused tests. Never modify long-press action definitions.
- Build an `.mpp` using the repository's Gradle tasks and confirm patch application against the supplied target. Test actual typing and long-press behavior on device before release.
- Keep the proof-of-concept on this feature branch; do not merge into `main` or publish a release before functional checks.

## Suggested functional test matrix

- Latin English QWERTY lower/upper case, portrait/landscape.
- `Q W E R T Y U I O P` number hints hidden, letters visually centered.
- Long-press Q through P still yields 1 through 0.
- `?123` and standalone numeric row still work.
- Existing English QWERTY Up-Flick Uppercase, Long-Press Editing Shortcuts and Swipeable Custom Top Row patches still build together.
- Disable the new setting and confirm original layout is restored.
