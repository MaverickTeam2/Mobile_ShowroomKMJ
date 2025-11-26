package com.maverick.kmjshowroom.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    fun generatePdf(context: Context, content: String, fileName: String): File {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        // Setup paints
        paint.color = Color.BLACK
        paint.textSize = 12f

        titlePaint.color = Color.BLACK
        titlePaint.textSize = 16f
        titlePaint.isFakeBoldText = true

        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points
        val margin = 40f
        val lineHeight = 18f

        val lines = content.split("\n")
        var currentPage = 1
        var yPosition = margin + 30f

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = page.canvas

        for (line in lines) {
            if (yPosition > pageHeight - margin) {
                pdfDocument.finishPage(page)
                currentPage++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = margin + 30f
            }

            when {
                line.contains("╔") || line.contains("╚") || line.contains("║") -> {
                    // Header styling
                    titlePaint.textSize = 14f
                    canvas.drawText(line.replace("╔", "").replace("╚", "").replace("╗", "").replace("╝", "").replace("║", "").replace("═", "").trim(), margin, yPosition, titlePaint)
                }
                line.contains("═") || line.contains("─") -> {
                    // Draw separator line
                    paint.strokeWidth = 1f
                    canvas.drawLine(margin, yPosition - 5f, pageWidth - margin, yPosition - 5f, paint)
                }
                else -> {
                    // Normal text
                    canvas.drawText(line, margin, yPosition, paint)
                }
            }

            yPosition += lineHeight
        }

        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()

        return file
    }
}