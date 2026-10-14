package dev.jason.gboardpatches.patches.gboard.features.numberhints

import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.builder.BuilderOffsetInstruction
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class GboardHideNumberHintsBytecodePatchTest {
    @Test
    fun `stock renderer loop exits execute the hook using current metadata`() {
        val method = stockRenderer()
        val implementation = method.implementation!!
        val originalFields = implementation.instructions.mapNotNull {
            ((it as? ReferenceInstruction)?.reference as? com.android.tools.smali.dexlib2.iface.reference.FieldReference)
                ?.toString()
        }
        val exits = implementation.instructions.filterIsInstance<BuilderOffsetInstruction>()
            .filter { it.target.location.instruction!!.opcode.name == "RETURN_VOID" }
        assertTrue("Fixture must exercise a jump to the renderer return", exits.isNotEmpty())

        method.applyHideNumberHintsHooks()

        // The old add-before-return hook was skipped by these exact stock branches.
        exits.forEach { exit ->
            assertEquals("MOVE_OBJECT_FROM16", exit.target.location.instruction!!.opcode.name)
        }
        val patched = implementation.instructions
        assertEquals(18, implementation.registerCount)
        assertEquals(1, patched.count { it.opcode.name == "RETURN_VOID" })
        assertEquals("INVOKE_STATIC_RANGE", patched.first().opcode.name)
        val afterIndex = patched.indexOfFirst {
            (it as? ReferenceInstruction)?.reference?.toString()?.contains("->afterKeyUpdate(") == true
        }
        assertTrue(afterIndex >= 2)
        val readCurrent = patched[afterIndex - 1] as ReferenceInstruction
        assertEquals(
            "Lcom/google/android/libraries/inputmethod/widgets/SoftKeyView;->e:" +
                "Lcom/google/android/libraries/inputmethod/metadata/SoftKeyDef;",
            readCurrent.reference.toString(),
        )
        assertEquals(1, (readCurrent as TwoRegisterInstruction).registerA)
        assertEquals(0, readCurrent.registerB)
        val call = patched[afterIndex] as FiveRegisterInstruction
        assertEquals(2, call.registerCount)
        assertEquals(0, call.registerC)
        assertEquals(1, call.registerD)
        assertEquals("RETURN_VOID", patched[afterIndex + 1].opcode.name)
        // Every stock field access remains; only a read of this.e is added.
        assertEquals(originalFields + readCurrent.reference.toString(), patched.mapNotNull {
            ((it as? ReferenceInstruction)?.reference as? com.android.tools.smali.dexlib2.iface.reference.FieldReference)
                ?.toString()
        })
    }

    private fun stockRenderer(): MutableMethod {
        val bytes = javaClass.getResourceAsStream(
            "/gboard/18.0.3/number-hints/stock-renderer.dex",
        )!!.use { it.readBytes() }
        assertEquals(
            "09d760da76783cf8650a2b9ccd0de497c2534a2972ae1d2f265c9b0fa82f6061",
            MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) },
        )
        val dex = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), bytes.inputStream().buffered())
        return MutableMethod(dex.classes.single().methods.single { it.name == "x" })
    }
}
