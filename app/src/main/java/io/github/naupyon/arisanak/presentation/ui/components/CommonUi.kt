package io.github.naupyon.arisanak.presentation.ui.components

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = android.content.ClipData.newPlainText("Arisanak Reminders", text)
    clipboard.setPrimaryClip(clip)
}

fun launchWhatsApp(context: Context, phone: String?, text: String) {
    copyToClipboard(context, text)
    Toast.makeText(context, "Salin laporan ke Clipboard", Toast.LENGTH_SHORT).show()
    val formattedPhone = phone?.replace("[^0-9]".toRegex(), "") ?: ""
    val uri = "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(text)}".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp tidak terinstal. Pesan telah disalin ke clipboard.", Toast.LENGTH_LONG).show()
    }
}

fun formatPhoneNumber(phone: String?): String? {
    if (phone.isNullOrBlank()) return null
    val digitsOnly = phone.replace("[^0-9]".toRegex(), "")
    if (digitsOnly.isEmpty()) return null

    return when {
        digitsOnly.startsWith("0") -> "+62 ${digitsOnly.substring(1)}"
        digitsOnly.startsWith("62") -> "+62 ${digitsOnly.substring(2)}"
        digitsOnly.startsWith("8") -> "+62 $digitsOnly"
        else -> "+$digitsOnly" // Fallback for other potential formats
    }
}

fun parseContactResult(context: Context, contactUri: Uri): Pair<String, String?>? {
    var name: String? = null
    var phone: String? = null
    val cr = context.contentResolver
    cr.query(contactUri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            if (nameIdx >= 0) name = cursor.getString(nameIdx)

            val idIdx = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            if (idIdx >= 0) {
                val id = cursor.getString(idIdx)
                val hasPhoneIdx = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                val hasPhone = if (hasPhoneIdx >= 0) cursor.getString(hasPhoneIdx) else "0"
                if (hasPhone == "1") {
                    cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )?.use { pCursor ->
                        if (pCursor.moveToFirst()) {
                            val numIdx = pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            if (numIdx >= 0) phone = pCursor.getString(numIdx)
                        }
                    }
                }
            }
        }
    }
    return name?.let { it to formatPhoneNumber(phone) }
}

fun formatEpochToDate(epoch: Long): String {
    val date = Date(epoch)
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(date)
}

@Composable
fun ConfettiCanvas(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ConfettiTransition")
    val driftY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ConfettiDrift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val random = Random(1337)
        for (i in 0 until 50) {
            val startX = random.nextFloat() * size.width
            val speedMult = 0.5f + random.nextFloat()
            val yPos = (driftY * speedMult) % size.height
            val color = Color(
                red = random.nextFloat(),
                green = random.nextFloat(),
                blue = random.nextFloat(),
                alpha = 0.8f
            )
            drawCircle(
                color = color,
                radius = 8.dp.toPx() * random.nextFloat(),
                center = Offset(startX, yPos)
            )
        }
    }
}
