package dev.jason.gboardpatches.patches.gboard.features.numberhints

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
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
        findMutableMethodOrThrow(
            classType = SOFT_KEY_VIEW,
            name = "x",
            returnType = "V",
            parameterTypes = listOf(SOFT_KEY_DEF),
        ).applyHideNumberHintsHooks()
    }
}

internal fun MutableMethod.applyHideNumberHintsHooks() {
    val method = this
    val instructions = method.implementation?.instructions
        ?: error("Missing SoftKeyView.x implementation")
    check(method.implementation!!.registerCount == 18) {
        "Unsupported full-Gboard key renderer register layout"
    }
    val labelField = "$SOFT_KEY_DEF->g:[Ljava/lang/CharSequence;"
    val viewIdField = "$SOFT_KEY_DEF->h:[I"
    val currentKeyField = "$SOFT_KEY_VIEW->e:$SOFT_KEY_DEF"
    val refs = instructions.mapNotNull { (it as? ReferenceInstruction)?.reference }
    check(refs.any { (it as? FieldReference)?.toString() == currentKeyField }) {
        "Missing verified current key metadata access"
    }
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
    // x(p1) receives PREVIOUS metadata to clean up the reused view. The
    // labels just rendered belong to this.e, including on first render
    // when p1 is null. v0/v1 are dead at this sole return.
    // Replace the return location first so branches to that location run
    // the hook too. Inserting before RETURN_VOID leaves those branches
    // pointing past the new instructions.
    method.replaceInstruction(returns.single(), "move-object/from16 v0, p0")
    method.addInstructions(
        returns.single() + 1,
        """
            iget-object v1, v0, $currentKeyField
            ${RuntimeCallEmitter.invoke(RuntimeCallId.NUMBER_HINTS_RUNTIME_AFTER_KEY_UPDATE, "v0, v1")}
            return-void
        """.trimIndent(),
    )
    // /range is necessary: p0 is v16 in this method.
    method.addInstructions(
        0,
        RuntimeCallEmitter.invoke(RuntimeCallId.NUMBER_HINTS_RUNTIME_BEFORE_KEY_UPDATE, "p0 .. p0"),
    )
}
