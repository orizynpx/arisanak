package io.github.naupyon.arisanak.presentation.ui.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import io.github.naupyon.arisanak.domain.model.Interval
import io.github.naupyon.arisanak.presentation.viewmodel.GroupUiState
import io.github.naupyon.arisanak.presentation.viewmodel.PaymentState
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExportUtils {

    fun generateGroupRecapPdf(
        outputStream: OutputStream,
        groupState: GroupUiState,
        intervals: List<Interval>
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
            color = Color.BLACK
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 12f
            color = Color.BLACK
        }
        val textPaint = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 10f
            color = Color.BLACK
        }
        val tableHeaderPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 9f
            color = Color.BLACK
        }
        val tableRowPaint = Paint().apply {
            typeface = Typeface.DEFAULT
            textSize = 9f
            color = Color.BLACK
        }

        var yPos = 40f
        val margin = 40f
        val pageWidth = 595f
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", locale)

        // Title
        canvas.drawText("Rekap Arisan: ${groupState.group.name}", margin, yPos, titlePaint)
        yPos += 25f

        // Date
        canvas.drawText("Tanggal Cetak: ${sdf.format(Date())}", margin, yPos, textPaint)
        yPos += 30f

        // Summary Section
        canvas.drawText("Ringkasan Kas", margin, yPos, headerPaint)
        yPos += 20f
        canvas.drawText("Total Pot: Rp ${String.format(locale, "%,.0f", groupState.targetPot)}", margin, yPos, textPaint)
        yPos += 15f
        canvas.drawText("Terkumpul: Rp ${String.format(locale, "%,.0f", groupState.collectedAmount)}", margin, yPos, textPaint)
        yPos += 15f
        canvas.drawText("Sisa: Rp ${String.format(locale, "%,.0f", groupState.targetPot - groupState.collectedAmount)}", margin, yPos, textPaint)
        yPos += 30f

        // Payment Status Table
        canvas.drawText("Status Pembayaran (Putaran #${groupState.activeInterval?.sequenceNumber ?: 1})", margin, yPos, headerPaint)
        yPos += 20f

        val col1 = margin
        val col2 = 250f
        val col3 = 350f
        val col4 = 450f

        // Table Header
        canvas.drawText("Nama Anggota", col1, yPos, tableHeaderPaint)
        canvas.drawText("Status", col2, yPos, tableHeaderPaint)
        canvas.drawText("Terbayar", col3, yPos, tableHeaderPaint)
        canvas.drawText("Sisa", col4, yPos, tableHeaderPaint)
        yPos += 5f
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, paint)
        yPos += 15f

        groupState.members.forEach { memberState ->
            canvas.drawText(memberState.member.displayName, col1, yPos, tableRowPaint)
            val statusStr = when {
                memberState.state == PaymentState.PAID -> "Lunas"
                memberState.state == PaymentState.PARTIAL -> "Sebagian"
                memberState.state == PaymentState.DITALANGI_PAID -> "Lunas (Ditalangi)"
                memberState.state.name.startsWith("DITALANGI") -> "Ditalangi"
                else -> "Belum Bayar"
            }
            canvas.drawText(statusStr, col2, yPos, tableRowPaint)
            canvas.drawText(String.format(locale, "%,.0f", memberState.amountPaid), col3, yPos, tableRowPaint)
            canvas.drawText(String.format(locale, "%,.0f", memberState.sisa), col4, yPos, tableRowPaint)
            yPos += 15f
        }

        yPos += 20f

        // Roll History Table
        canvas.drawText("Riwayat Kocokan", margin, yPos, headerPaint)
        yPos += 20f

        canvas.drawText("Putaran", col1, yPos, tableHeaderPaint)
        canvas.drawText("Pemenang", col2, yPos, tableHeaderPaint)
        canvas.drawText("Tanggal", col3, yPos, tableHeaderPaint)
        yPos += 5f
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, paint)
        yPos += 15f

        val completedIntervals = intervals.filter { it.winnerMemberId != null }.sortedByDescending { it.sequenceNumber }
        val dateSdf = SimpleDateFormat("dd/MM/yyyy", locale)

        completedIntervals.forEach { interval ->
            val winner = groupState.members.find { it.member.id == interval.winnerMemberId }
            canvas.drawText("#${interval.sequenceNumber}", col1, yPos, tableRowPaint)
            canvas.drawText(winner?.member?.displayName ?: "Unknown", col2, yPos, tableRowPaint)
            canvas.drawText(dateSdf.format(Date(interval.startDate)), col3, yPos, tableRowPaint)
            yPos += 15f
        }

        pdfDocument.finishPage(page)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
    }
}
