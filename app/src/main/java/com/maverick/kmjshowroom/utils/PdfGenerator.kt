package com.maverick.kmjshowroom.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream

object PdfGenerator {

    fun generatePdf(context: Context, content: String): ByteArray {
        val pdf = PdfDocument()

        val titlePaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }

        val textPaint = Paint().apply {
            textSize = 12f
        }

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val lineHeight = 18f

        var y = margin + 20
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdf.startPage(pageInfo)
        var canvas = page.canvas

        val lines = content.split("\n")

        for (line in lines) {

            if (y > pageHeight - margin) {
                pdf.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdf.startPage(pageInfo)
                canvas = page.canvas
                y = margin + 20
            }

            val clean = line.trim()

            val words = clean.split(" ")
            var currentLine = ""

            for (word in words) {
                val check = "$currentLine $word"

                if (textPaint.measureText(check) > (pageWidth - margin * 2)) {
                    canvas.drawText(currentLine.trim(), margin, y, textPaint)
                    y += lineHeight
                    currentLine = word
                } else {
                    currentLine = check
                }
            }

            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine.trim(), margin, y, textPaint)
                y += lineHeight
            }
        }

        pdf.finishPage(page)

        val outputStream = ByteArrayOutputStream()
        pdf.writeTo(outputStream)
        pdf.close()

        return outputStream.toByteArray()
    }
}
