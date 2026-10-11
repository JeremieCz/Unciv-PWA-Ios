package com.unciv.app.web

import com.badlogic.gdx.graphics.Pixmap
import com.unciv.ui.components.fonts.FontFamilyData
import com.unciv.ui.components.fonts.FontImplementation
import com.unciv.ui.components.fonts.FontMetricsCommon
import com.unciv.ui.components.fonts.Fonts
import org.teavm.jso.browser.Window
import org.teavm.jso.canvas.CanvasRenderingContext2D
import org.teavm.jso.canvas.TextMetrics
import org.teavm.jso.dom.html.HTMLCanvasElement

class WebFont : FontImplementation {
    private val canvas = Window.current().document.createElement("canvas") as HTMLCanvasElement
    private val context = canvas.getContext("2d") as CanvasRenderingContext2D
    private var fontSize = Fonts.ORIGINAL_FONT_SIZE.toInt()
    private var family = "sans-serif"
    private var metrics = FontMetricsCommon(
        ascent = fontSize * 0.8f,
        descent = fontSize * 0.2f,
        height = fontSize.toFloat(),
        leading = 0f
    )

    override fun setFontFamily(fontFamilyData: FontFamilyData, size: Int) {
        fontSize = size
        family = fontFamilyData.invariantName
            .replace(Regex("[^\\p{L}\\p{N} ,_-]"), "")
            .ifBlank { "sans-serif" }
        context.font = "${fontSize}px \"$family\""
        val textMetrics = context.measureText("M")
        val ascent = textMetrics.actualBoundingBoxAscent.toFloat().takeIf { it > 0f } ?: fontSize * 0.8f
        val descent = textMetrics.actualBoundingBoxDescent.toFloat().takeIf { it > 0f } ?: fontSize * 0.2f
        val height = (textMetrics.fontBoundingBoxAscent + textMetrics.fontBoundingBoxDescent)
            .toFloat().takeIf { it > 0f } ?: fontSize.toFloat()
        metrics = FontMetricsCommon(ascent, descent, height, (height - ascent - descent).coerceAtLeast(0f))
    }

    override fun getFontSize() = fontSize

    override fun getCharPixmap(symbolString: String): Pixmap {
        context.font = "${fontSize}px \"$family\""
        val textMetrics: TextMetrics = context.measureText(symbolString)
        val width = textMetrics.width.toInt().coerceAtLeast(1)
        val height = metrics.height.toInt().coerceAtLeast(fontSize)
        canvas.width = width
        canvas.height = height
        context.font = "${fontSize}px \"$family\""
        context.fillStyle = "#ffffff"
        context.textBaseline = "alphabetic"
        context.fillText(symbolString, 0.0, metrics.ascent.toDouble())

        val imageData = context.getImageData(0.0, 0.0, width.toDouble(), height.toDouble()).data
        val argb = IntArray(width * height)
        for (pixel in argb.indices) {
            val offset = pixel * 4
            val red = imageData.get(offset).toInt()
            val green = imageData.get(offset + 1).toInt()
            val blue = imageData.get(offset + 2).toInt()
            val alpha = imageData.get(offset + 3).toInt()
            argb[pixel] = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
        }
        return Fonts.pixmapFromArgb(width, height, argb)
    }

    override fun getSystemFonts() = sequenceOf(
        FontFamilyData("Sans-serif", "sans-serif"),
        FontFamilyData("Serif", "serif"),
        FontFamilyData("Monospace", "monospace")
    )

    override fun getMetrics() = metrics
}
