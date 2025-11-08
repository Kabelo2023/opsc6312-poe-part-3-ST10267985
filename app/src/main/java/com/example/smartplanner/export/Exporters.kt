package com.example.smartplanner.export

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.smartplanner.ui.home.Task
import java.io.File
import java.time.format.DateTimeFormatter

object Exporters {
    fun shareIcs(ctx: Context, tasks: List<Task>) {
        val sb = StringBuilder().apply {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            tasks.forEach { t ->
                val d = t.dueDate.format(DateTimeFormatter.BASIC_ISO_DATE)
                appendLine("BEGIN:VEVENT")
                appendLine("SUMMARY:${t.title}")
                appendLine("DTSTART;VALUE=DATE:$d")
                appendLine("END:VEVENT")
            }
            appendLine("END:VCALENDAR")
        }
        val file = File(ctx.cacheDir, "syncup.ics").apply { writeText(sb.toString()) }
        shareFile(ctx, file, "text/calendar")
    }

    fun sharePdf(ctx: Context, tasks: List<Task>) {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val c = page.canvas
        val paint = android.graphics.Paint().apply { textSize = 14f }
        var y = 40f
        c.drawText("SyncUp – This Week", 40f, y, paint); y += 24f
        tasks.forEach { t -> c.drawText("- ${t.title} (${t.tag}) on ${t.dueDate}", 40f, y, paint); y += 18f }
        doc.finishPage(page)
        val file = File(ctx.cacheDir, "syncup.pdf")
        doc.writeTo(file.outputStream()); doc.close()
        shareFile(ctx, file, "application/pdf")
    }

    private fun shareFile(ctx: Context, file: File, mime: String) {
        val uri: Uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime; putExtra(Intent.EXTRA_STREAM, uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(intent, "Share"))
    }
}
