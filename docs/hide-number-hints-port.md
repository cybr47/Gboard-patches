# Hide Number Hints — full Gboard 18.0.3

This feature branch adds an optional visual setting to full Gboard
`18.0.3.954559732-release-arm64-v8a`, the same build admitted by Jason Wu's
upstream v3.12.0 patch bundle. Use the APKMirror **APKM split bundle**, which
includes the dictation feature split, when patching for voice typing.

## Installation

1. Import the `.mpp` from this branch's successful **Gboard Hide Number Hints CI**
   artifact into Morphe. Adding the upstream source or using its v3.12.0 release
   does not load this experimental feature branch.
2. Patch the original APKM and explicitly select **Hide Number Hints**, together
   with your other desired patches.
3. Install the patched result. Open **Gboard settings → Patches → Keys, Layout & Symbols
   → Hide Number Hints**, and enable the toggle (off by default).
4. Restart Gboard using the Patches settings restart action after changing it.

## Bugs fixed in the first implementation

- No settings dependency, manifest feature marker, settings entry, or stored
  preference existed. Selecting a patch in Morphe does not itself create a
  Gboard settings switch. Those are now wired through the upstream settings host.
- `SoftKeyView.x(SoftKeyDef)` takes the **previous** key metadata for cleanup.
  It draws the current metadata stored in `SoftKeyView.e`. The old hook passed
  `p1`, which is null on initial inflation or belongs to another key on reuse.
  The hook now reads `this.e` after stock rendering.
- Inserting instructions before the renderer's sole return does not redirect
  existing branches to them. The label-rendering loop jumps directly to that
  return, skipping the original after-hook. Replacing the return location with
  the first hook instruction preserves the incoming branch targets.
- Gboard maps label view ID `0` to its default main label (`0x7f0b0651`). The
  matcher now applies that same mapping instead of rejecting those keys.

## Behavior and composition

The extension only hides a visible digit TextView when the current metadata and
rendered views agree on the corresponding QWERTY letter/digit pair (`q/1` through
`p/0`, including uppercase). Before the next render it restores visibility so
recycled views work normally on other rows and layouts. It leaves letter text,
gravity, layout parameters, `SoftKeyDef.g/h`, and every `ActionDef` untouched.

The hook remains in the dedicated `x` renderer. It does not modify the `r` bind
method owned by the upstream SoftKey family composer, allowing the existing
up-flick uppercase, editing shortcut, top-row swipe, and spacebar patches to
compose with it. Input behavior configured by those patches remains in force.

## Validation

- Inspect the supplied APKM's `base.apk`: `SoftKeyView.x`, its call sites,
  metadata fields, and the default-label fallback.
- Run bytecode tests against a fixture assembled from the exact stock renderer,
  including a check that its loop exit executes the injected hook.
- Run Android view tests for all ten lowercase/uppercase pairs, zero-ID mapping,
  action/label preservation, recycled views, disabled state, and other layouts.
- Run the feature availability and settings toggle persistence tests.
- Build with `:patches:buildAndroid` and run the existing patch/extension suites.
- Apply the resulting `.mpp` to the original APKM, both alone and alongside the
  upstream SoftKey patches, and inspect the resulting DEX/manifest.

Actual Samsung display, typing, and long-press behavior still need device testing.
Check `q` through `p` long presses produce `1` through `0`, `?123` still works,
and disabling the switch restores the hints after restarting Gboard.
