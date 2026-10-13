package dev.jason.gboardpatches.patches.gboard.features.numberhints

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import dev.jason.gboardpatches.patches.gboard.shared.findMutableMethodOrThrow
import dev.jason.gboardpatches.patches.gboard.shared.gboardPatchesExtensionCarrierPatch
import dev.jason.gboardpatches.patches.gboard.shared.isOpcode
import dev.jason.gboardpatches.patches.gboard.shared.returnInstructionIndices
import dev.jason.gboardpatches.patches.gboard.shared.runtimeabi.RuntimeCallEmitter
import dev.jason.gboardpatches.patches.gboard.shared.runtimeabi.RuntimeCallId
import dev.jason.gboardpatches.patches.shared.Constants.COMPATIBILITY_GBOARD

private const val SOFT_KEY_VIEW =
    "Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;"
private const val SOFT_KEY_DEF =
    "Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;"

/**
 * Hook the dedicated label renderer x(), NOT the composited SoftKeyView.r() bind.
 * Before and after hooks preserve the original keyboard action metadata.
 */
internal val gboardHideNumberHintsBytecodePatch = bytecodePatch(
    description = "Remove visible QWERTY number hints while retaining long-press actions.",
) {
    compatibleWith(COMPATIBILITY_GBOARD)
    dependsOn(gboardPatchesExtensionCarrierPatch)

    execute {
        val method = findMutableMethodOrThrow(
            classType = SOFT_KEY_VIEW,
            name = "x",
            returnType = "V",
            parameterTypes = listOf(SOFT_KEY_DEF),
        )
        val instructions = method.implementation?.instructions
            ?: error("Missing SoftKeyView.x implementation")
        check(method.implementation!!.registerCount == 18) {
            "Unsupported full-Gboard key renderer register layout"
        }
        val labelField = "$SOFT_KEY_DEF->g:[Ljava/lang/CharSequence;"
        val viewIdField = "$SOFT_KEY_DEF->h:[I"
        val refs = instructions.mapNotNull { (it as? ReferenceInstruction)?.reference }
        check(refs.any { (it as? FieldReference)?.toString() == labelField }) {
            "Missing verified key-label metadata access"
        }
        check(refs.any { (it as? FieldReference)?.toString() == viewIdField }) {
            "Missing verified label-view-ID metadata access"
        }
        check(refs.any { (it as? MethodReference)?.toString() ==
            "Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V" }) {
            "Missing expected TextView label renderer"
        }
        val returns = method.returnInstructionIndices().filter {
            instructions[it].isOpcode("RETURN_VOID")
        }
        check(returns.size == 1) {
            "Unsupported Gboard key renderer shape: ${returns.size} returns"
        }
        // /range is necessary: p0 and p1 are v16 and v17 in this method.
        method.addInstructions(
            returns.single(),
            RuntimeCallEmitter.invoke(RuntimeCallId.NUMBER_HINTS_RUNTIME_AFTER_KEY_UPDATE, "p0 .. p1"),
        )
        method.addInstructions(
            0,
            RuntimeCallEmitter.invoke(RuntimeCallId.NUMBER_HINTS_RUNTIME_BEFORE_KEY_UPDATE, "p0 .. p0"),
        )
    }
}
