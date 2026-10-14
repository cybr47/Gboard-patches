package dev.jason.gboardpatches.extension.numberhints;

import org.junit.Test;
import static org.junit.Assert.*;

public class GboardHideNumberHintsRuntimeTest {
    @Test
    public void resolvesGboardsZeroMainLabelIdAndRejectsAliases() {
        GboardHideNumberHintsRuntime.Candidate match = GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"q", "1"}, new int[] {0, 202});
        assertNotNull(match);
        assertEquals(GboardHideNumberHintsRuntime.DEFAULT_LABEL_VIEW_ID, match.letterViewId);
        assertEquals(202, match.digitViewId);
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"q", "1"}, new int[] {0, 0}));
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"q", "1"},
                new int[] {0, GboardHideNumberHintsRuntime.DEFAULT_LABEL_VIEW_ID}));
    }

    @Test
    public void matchesOnlyTheExpectedEnglishTopRowDigitPairs() {
        final String letters = "qwertyuiop";
        final String digits = "1234567890";
        for (int i = 0; i < letters.length(); i++) {
            String letter = String.valueOf(letters.charAt(i));
            String digit = String.valueOf(digits.charAt(i));
            GboardHideNumberHintsRuntime.Candidate candidate =
                    GboardHideNumberHintsRuntime.match(
                            new CharSequence[] { letter, digit },
                            new int[] { 101, 202 });
            assertNotNull("missing " + letter + "/" + digit, candidate);
            assertEquals(101, candidate.letterViewId);
            assertEquals(202, candidate.digitViewId);
            assertEquals(letters.charAt(i), candidate.letter);
            assertEquals(digits.charAt(i), candidate.digit);
        }
    }

    @Test
    public void supportsUppercaseButNeverTreatsOtherRowsAsNumberHints() {
        assertNotNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"Q", "1"}, new int[] {1, 2}));
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"a", "1"}, new int[] {1, 2}));
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"q", "2"}, new int[] {1, 2}));
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"1"}, new int[] {1}));
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"q", "1"}, new int[] {1, 1}));
        assertNull(GboardHideNumberHintsRuntime.match(
                new CharSequence[] {"q", "1"}, new int[] {1}));
        assertNull(GboardHideNumberHintsRuntime.match(null, null));
    }
}
