package dev.jason.gboardpatches.extension.numberhints;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.libraries.inputmethod.metadata.SoftKeyDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import dev.jason.gboardpatches.extension.settings.GboardPatchesSettings;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public final class GboardHideNumberHintsViewTest {
    private static final int HINT_ID = 0x7f0b0652;
    private Context context;
    private SharedPreferences preferences;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        preferences = GboardPatchesSettings.preferences(context);
        preferences.edit().clear().commit();
    }

    @Test
    public void enabledHidesAllTenRenderedHintsIncludingDefaultLabelId() {
        assertTrue(GboardHideNumberHintsSettings.writeEnabled(preferences, true));
        String letters = "qwertyuiop";
        String digits = "1234567890";
        for (int i = 0; i < letters.length(); i++) {
            for (boolean uppercase : new boolean[] { false, true }) {
                String letter = String.valueOf(letters.charAt(i));
                if (uppercase) letter = letter.toUpperCase(java.util.Locale.ROOT);
                String digit = String.valueOf(digits.charAt(i));
                FrameLayout root = key(letter, digit);
                TextView main = root.findViewById(GboardHideNumberHintsRuntime.DEFAULT_LABEL_VIEW_ID);
                TextView hint = root.findViewById(HINT_ID);
                Object tap = new Object();
                Object longPress = new Object();
                SoftKeyDef current = metadata(letter, digit, tap, longPress);
                FrameLayout.LayoutParams layout = (FrameLayout.LayoutParams) main.getLayoutParams();

                GboardHideNumberHintsRuntime.beforeKeyUpdate(root);
                GboardHideNumberHintsRuntime.afterKeyUpdate(root, current);

                assertEquals(View.GONE, hint.getVisibility());
                assertEquals(View.VISIBLE, main.getVisibility());
                assertEquals(letter, main.getText().toString());
                assertEquals(Gravity.TOP | Gravity.CENTER_HORIZONTAL, main.getGravity());
                assertSame(layout, main.getLayoutParams());
                assertSame(tap, current.f[0]);
                assertSame(longPress, current.f[1]);
                assertArrayEquals(new CharSequence[] { letter, digit }, current.g);
                assertArrayEquals(new int[] { 0, HINT_ID }, current.h);
                GboardHideNumberHintsRuntime.beforeKeyUpdate(root);
                assertEquals(View.VISIBLE, hint.getVisibility());
            }
        }
    }

    @Test
    public void recycledViewRestoresBeforeStockWritesTheNextKey() {
        GboardHideNumberHintsSettings.writeEnabled(preferences, true);
        FrameLayout root = key("q", "1");
        TextView hint = root.findViewById(HINT_ID);
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("q", "1"));
        assertEquals(View.GONE, hint.getVisibility());

        GboardHideNumberHintsRuntime.beforeKeyUpdate(root);
        assertEquals(View.VISIBLE, hint.getVisibility());
        TextView main = root.findViewById(GboardHideNumberHintsRuntime.DEFAULT_LABEL_VIEW_ID);
        main.setText("a");
        hint.setText("@");
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("a", "@"));
        assertEquals(View.VISIBLE, hint.getVisibility());
    }

    @Test
    public void defaultsAndDisabledSettingKeepHintsAndDisableRestoresOnNextRender() {
        FrameLayout root = key("q", "1");
        TextView hint = root.findViewById(HINT_ID);
        assertFalse(GboardHideNumberHintsSettings.readEnabled(preferences));
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("q", "1"));
        assertEquals(View.VISIBLE, hint.getVisibility());

        GboardHideNumberHintsSettings.writeEnabled(preferences, true);
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("q", "1"));
        assertEquals(View.GONE, hint.getVisibility());
        GboardHideNumberHintsSettings.writeEnabled(preferences, false);
        GboardHideNumberHintsRuntime.beforeKeyUpdate(root);
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("q", "1"));
        assertEquals(View.VISIBLE, hint.getVisibility());
    }

    @Test
    public void staleMetadataOtherRowsAndMissingViewsStayUnchanged() {
        GboardHideNumberHintsSettings.writeEnabled(preferences, true);
        FrameLayout root = key("w", "2");
        TextView hint = root.findViewById(HINT_ID);
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("q", "1"));
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, null);
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, new Object());
        assertEquals(View.VISIBLE, hint.getVisibility());
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("w", "2"));
        assertEquals(View.GONE, hint.getVisibility());
        GboardHideNumberHintsRuntime.beforeKeyUpdate(root);

        for (String[] labels : new String[][] { {"a", "1"}, {"1", "!"}, {"q", "2"}, {"й", "1"} }) {
            FrameLayout other = key(labels[0], labels[1]);
            GboardHideNumberHintsRuntime.afterKeyUpdate(other, metadata(labels[0], labels[1]));
            assertEquals(View.VISIBLE, other.findViewById(HINT_ID).getVisibility());
        }
        root.removeView(hint);
        GboardHideNumberHintsRuntime.afterKeyUpdate(root, metadata("w", "2"));
        assertEquals(View.VISIBLE, root.getChildAt(0).getVisibility());
    }

    private SoftKeyDef metadata(String letter, String digit, Object... actions) {
        return new SoftKeyDef(new CharSequence[] { letter, digit },
                new int[] { 0, HINT_ID }, actions);
    }

    private FrameLayout key(String letter, String digit) {
        FrameLayout root = new FrameLayout(context);
        TextView main = new TextView(context);
        main.setId(GboardHideNumberHintsRuntime.DEFAULT_LABEL_VIEW_ID);
        main.setText(letter);
        main.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        root.addView(main, new FrameLayout.LayoutParams(100, 100));
        TextView hint = new TextView(context);
        hint.setId(HINT_ID);
        hint.setText(digit);
        root.addView(hint, new FrameLayout.LayoutParams(20, 20));
        return root;
    }
}
