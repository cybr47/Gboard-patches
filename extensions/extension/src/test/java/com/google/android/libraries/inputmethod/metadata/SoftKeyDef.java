package com.google.android.libraries.inputmethod.metadata;

/** Test fixture with the display/action field shapes verified in full Gboard 18.0.3. */
public final class SoftKeyDef {
    public final Object[] f;
    public final CharSequence[] g;
    public final int[] h;

    public SoftKeyDef(CharSequence[] labels, int[] viewIds, Object... actions) {
        f = actions;
        g = labels;
        h = viewIds;
    }
}
