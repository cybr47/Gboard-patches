package dev.jason.gboardpatches.extension.numberhints;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Full Gboard 18.0.3 visual tweak. SoftKeyDef.g contains display labels;
 * SoftKeyDef.h contains corresponding view IDs. ActionDef remains untouched.
 */
@SuppressWarnings("unused")
public final class GboardHideNumberHintsRuntime {
    private static final String SOFT_KEY_DEF =
            "com.google.android.libraries.inputmethod.metadata.SoftKeyDef";
    private static final String TOP_ROW = "qwertyuiop";
    private static final String DIGITS = "1234567890";
    private static final Map<ViewGroup, AlteredKey> ALTERED = new WeakHashMap<>();

    private GboardHideNumberHintsRuntime() { }

    /** Restore reused key views BEFORE Gboard updates their labels. */
    public static void beforeKeyUpdate(Object candidate) {
        if (!(candidate instanceof ViewGroup root)) return;
        try {
            final AlteredKey previous;
            synchronized (ALTERED) {
                previous = ALTERED.remove(root);
            }
            if (previous != null) previous.restore();
        } catch (Throwable ignored) {
            // Visual-only change must not interrupt input.
        }
    }

    /** Only hide a matching digit TextView after the original label render. */
    public static void afterKeyUpdate(Object candidate, Object metadata) {
        if (!(candidate instanceof ViewGroup root) || metadata == null) return;
        try {
            if (!SOFT_KEY_DEF.equals(metadata.getClass().getName())) return;
            Field labelsField = metadata.getClass().getDeclaredField("g");
            Field viewIdsField = metadata.getClass().getDeclaredField("h");
            if (labelsField.getType() != CharSequence[].class ||
                    viewIdsField.getType() != int[].class) return;
            labelsField.setAccessible(true);
            viewIdsField.setAccessible(true);
            CharSequence[] labels = (CharSequence[]) labelsField.get(metadata);
            int[] ids = (int[]) viewIdsField.get(metadata);
            Candidate match = match(labels, ids);
            if (match == null) return;

            View letter = root.findViewById(match.letterViewId);
            View digit = root.findViewById(match.digitViewId);
            if (!(letter instanceof TextView main) ||
                    !(digit instanceof TextView hint) || letter == digit) return;
            if (!equalsIgnoreCaseSingle(main.getText(), match.letter) ||
                    !equalsSingle(hint.getText(), match.digit)) return;
            if (hint.getVisibility() != View.VISIBLE) return;

            AlteredKey altered = new AlteredKey(main, hint);
            hint.setVisibility(View.GONE);
            main.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = main.getLayoutParams();
            if (main.getParent() == root && params instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams centered = new FrameLayout.LayoutParams(
                        (FrameLayout.LayoutParams) params);
                centered.gravity = Gravity.CENTER;
                main.setLayoutParams(centered);
            }
            synchronized (ALTERED) {
                ALTERED.put(root, altered);
            }
        } catch (Throwable ignored) {
            // Unknown layouts stay unchanged.
        }
    }

    /** Require both the English top-row letter and its exact number hint. */
    static Candidate match(CharSequence[] labels, int[] ids) {
        if (labels == null || ids == null || labels.length != ids.length) return null;
        for (int letterIndex = 0; letterIndex < labels.length; letterIndex++) {
            CharSequence label = labels[letterIndex];
            if (label == null || label.length() != 1) continue;
            char c = Character.toLowerCase(label.charAt(0));
            int topRowIndex = TOP_ROW.indexOf(c);
            if (topRowIndex < 0 || ids[letterIndex] == 0) continue;
            char expected = DIGITS.charAt(topRowIndex);
            for (int hintIndex = 0; hintIndex < labels.length; hintIndex++) {
                if (hintIndex == letterIndex || ids[hintIndex] == 0 ||
                        ids[hintIndex] == ids[letterIndex]) continue;
                if (equalsSingle(labels[hintIndex], expected)) {
                    return new Candidate(ids[letterIndex], ids[hintIndex], c, expected);
                }
            }
        }
        return null;
    }

    private static boolean equalsSingle(CharSequence text, char value) {
        return text != null && text.length() == 1 && text.charAt(0) == value;
    }

    private static boolean equalsIgnoreCaseSingle(CharSequence text, char value) {
        return text != null && text.length() == 1 &&
                Character.toLowerCase(text.charAt(0)) == value;
    }

    static final class Candidate {
        final int letterViewId;
        final int digitViewId;
        final char letter;
        final char digit;

        Candidate(int letterViewId, int digitViewId, char letter, char digit) {
            this.letterViewId = letterViewId;
            this.digitViewId = digitViewId;
            this.letter = letter;
            this.digit = digit;
        }
    }

    private static final class AlteredKey {
        final WeakReference<TextView> main;
        final WeakReference<TextView> hint;
        final int previousVisibility;
        final int previousGravity;
        final ViewGroup.LayoutParams previousLayoutParams;

        AlteredKey(TextView main, TextView hint) {
            this.main = new WeakReference<>(main);
            this.hint = new WeakReference<>(hint);
            previousVisibility = hint.getVisibility();
            previousGravity = main.getGravity();
            ViewGroup.LayoutParams params = main.getLayoutParams();
            previousLayoutParams = params instanceof FrameLayout.LayoutParams
                    ? new FrameLayout.LayoutParams((FrameLayout.LayoutParams) params) : null;
        }

        void restore() {
            TextView priorMain = main.get();
            TextView priorHint = hint.get();
            if (priorHint != null) priorHint.setVisibility(previousVisibility);
            if (priorMain == null) return;
            priorMain.setGravity(previousGravity);
            if (previousLayoutParams != null && priorMain.getParent() instanceof FrameLayout) {
                priorMain.setLayoutParams(new FrameLayout.LayoutParams(
                        (FrameLayout.LayoutParams) previousLayoutParams));
            }
        }
    }
}
