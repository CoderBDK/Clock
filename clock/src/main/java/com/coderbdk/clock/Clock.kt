package com.coderbdk.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.lang.Math.toRadians
import java.util.Calendar
import java.util.Date
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class Kata(time: Int) {
    var angle = 0f
        private set
    var degree = time
        private set

    val revDeg = toRadians(90.0).toFloat()

    init {
        update()
    }

    fun update() {
        degree = (degree + 6) % 360
        angle = (toRadians(degree.toDouble()) - revDeg).toFloat()
    }
}


abstract class ClockState(internal val timeInMillis: Long) {

    abstract val second: Kata
    abstract val minute: Kata
    abstract val hour: Kata
    internal abstract var ticking: Int
    internal abstract val clockInTickMillis: Long
    internal abstract fun createKata(time: Int): Kata
    internal abstract fun updateClockTime()

}

internal class ClockStateImpl(
    clockTimeInMillis: Long
) : ClockState(clockTimeInMillis) {

    private val calender: Calendar = Calendar.getInstance()

    override val second: Kata = createKata(calender.get(Calendar.SECOND) * 6)
    override val minute: Kata = createKata(calender.get(Calendar.MINUTE) * 6)
    override val hour: Kata = createKata(calender.get(Calendar.HOUR) * 30)
    override var ticking: Int by mutableIntStateOf(0)
    override val clockInTickMillis: Long
        get() {
            val totalSeconds = second.degree / 6
            val totalMinutes = minute.degree / 6
            val totalHours = hour.degree / 30
            return (totalHours * 3600000L) + (totalMinutes * 60000L) + (totalSeconds * 1000L)
        }

    init {
        calender.time = Date(timeInMillis)
    }

    override fun createKata(time: Int): Kata {
        return Kata(time)
    }

    override fun updateClockTime() {
        second.update()
        if (second.degree == 360) minute.update()
        if (second.degree == 360 && minute.degree % 60 == 0) hour.update()
        ticking = second.degree
    }

}


@Composable
fun rememberClockState(timeInMillis: Long = System.currentTimeMillis()): ClockState =
    remember { ClockStateImpl(clockTimeInMillis = timeInMillis) }


@Composable
fun Clock(
    state: ClockState,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val ticking by remember { derivedStateOf { state.ticking } }

    LaunchedEffect(state) {
        while (true) {
            state.updateClockTime()
            delay(1000)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        val width = size.width
        val height = size.height

        val radius = min(width, height) / 3
        val cx = width / 2f
        val cy = height / 2f

        // update canvas
        drawText(
            textMeasurer, "$ticking"
        )

        drawOutlineBorder(radius, cx, cy)
        drawSecondIndicatorBar(state.second, radius, cx, cy)
        drawMinuteIndicatorBar(state.minute, radius, cx, cy)
        drawHourIndicatorBar(state.hour, radius, cx, cy)

        drawClockBackground(state.second, radius, cx, cy)
        draw12Text(textMeasurer, state.second, radius, cx, cy)
        draw60Kata(state.second, radius, cx, cy)

        drawSecondKata(state.second, radius, cx, cy)
        drawMinuteKata(state.minute, radius, cx, cy)
        drawHourKata(state.hour, radius, cx, cy)

        drawCenterCircle(cx, cy)
        drawOverlay(radius, cx, cy)
    }
}

private fun DrawScope.drawOutlineBorder(radius: Float, cx: Float, cy: Float) {

    // draw outline border
    drawCircle(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF30FA07),
                Color(0xFF0AF775),
                Color(0xFF5EEED6),
            ),
            tileMode = TileMode.Repeated
        ),
        radius = radius + 148,
        center = Offset(cx, cy),
        style = Stroke(56f)
    )
}

private fun DrawScope.drawSecondIndicatorBar(second: Kata, radius: Float, cx: Float, cy: Float) {
    // second
    val x = cos(second.angle) * radius
    val y = sin(second.angle) * radius
    drawCircle(
        brush = Brush.linearGradient(
            listOf(
                Color(0xFFD53333),
                Color(0xF0EEA806),
                Color(0xFF31DD0E),
                Color(0xFF33D5B5),
                Color(0xF00634EE),
                Color(0xFF9F0EDD),
                Color(0xFFDD0E0E),
            ),
            start = Offset(cx, cy),
            end = Offset(cx + x, cy + y),
            tileMode = TileMode.Mirror
        ),
        radius = radius + 148,
        center = Offset(cx, cy),
        style = Stroke(48f)
    )

    val dw = radius + 180
    val dx = cos(second.angle) * dw
    val dy = sin(second.angle) * dw
    drawLine(
        color = Color.Black,
        start = Offset(cx, cy),
        end = Offset(cx + dx, cy + dy),
        strokeWidth = 16f,
    )
    drawLine(
        color = Color.White,
        start = Offset(cx, cy),
        end = Offset(cx + dx, cy + dy),
        strokeWidth = 14f,
    )
}

private fun DrawScope.drawMinuteIndicatorBar(minute: Kata, radius: Float, cx: Float, cy: Float) {
    // minute
    val x = cos(minute.angle) * radius
    val y = sin(minute.angle) * radius
    drawCircle(
        color = Color.White,
        radius = radius + 124,
        center = Offset(cx, cy),
        style = Stroke(56f)
    )
    drawCircle(
        brush = Brush.linearGradient(
            listOf(
                Color(0xFFD53333),
                Color(0xF0EEA806),
                Color(0xFFDD0E0E),
            ),
            start = Offset(cx, cy),
            end = Offset(cx + x, cy + y),
            tileMode = TileMode.Mirror
        ),
        radius = radius + 124,
        center = Offset(cx, cy),
        style = Stroke(48f)
    )

    val dw = radius + 148
    val dx = cos(minute.angle) * dw
    val dy = sin(minute.angle) * dw
    drawLine(
        color = Color.Black,
        start = Offset(cx, cy),
        end = Offset(cx + dx, cy + dy),
        strokeWidth = 16f,
    )
    drawLine(
        color = Color.White,
        start = Offset(cx, cy),
        end = Offset(cx + dx, cy + dy),
        strokeWidth = 14f,
    )
}

private fun DrawScope.drawHourIndicatorBar(hour: Kata, radius: Float, cx: Float, cy: Float) {
    // hour
    val x = cos(hour.angle) * radius
    val y = sin(hour.angle) * radius
    drawCircle(
        color = Color.White,
        radius = radius + 102,
        center = Offset(cx, cy),
        style = Stroke(56f)
    )

    drawCircle(
        brush = Brush.linearGradient(
            listOf(
                Color(0xFF9F0EDD),
                Color(0xFFDD0E0E),
                Color(0xFF33D5BF),
                Color(0xFF0681EE),
                Color(0xFF392191),
            ),
            start = Offset(cx, cy),
            end = Offset(cx + x, cy + y),
            tileMode = TileMode.Mirror
        ),
        radius = radius + 102,
        center = Offset(cx, cy),
        style = Stroke(48f)
    )

    val dw = radius + 124
    val dx = cos(hour.angle) * dw
    val dy = sin(hour.angle) * dw
    drawLine(
        color = Color.Black,
        start = Offset(cx, cy),
        end = Offset(cx + dx, cy + dy),
        strokeWidth = 16f,
    )
    drawLine(
        color = Color.White,
        start = Offset(cx, cy),
        end = Offset(cx + dx, cy + dy),
        strokeWidth = 14f,
    )

}

private fun DrawScope.drawClockBackground(
    second: Kata,
    radius: Float,
    cx: Float,
    cy: Float
) {
    // clock background
    val x = cos(second.angle) * radius
    val y = sin(second.angle) * radius
    drawCircle(
        brush = Brush.linearGradient(
            listOf(
                Color(0xFFEC0F5A),
                Color(0xFF11E651),
                Color(0xFFECD506),
            ),
            start = Offset(cx, cy),
            end = Offset(cx + x, cy + y),
            tileMode = TileMode.Mirror
        ),
        radius = radius + 100,
        center = Offset(cx, cy),
        style = Stroke(8f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                Color(0xFF33D5BF),
                Color(0xFF0681EE),
                Color(0xFF392191),
            ),
        ),
        radius = radius + 100,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.draw12Text(
    textMeasurer: TextMeasurer,
    second: Kata,
    radius: Float,
    cx: Float,
    cy: Float
) {
    // draw text
    for (i in 1..12) {
        val angle = ((toRadians(i * (360 / 12) + 0.0)) - second.revDeg).toFloat()
        val tdx = cos(angle) * radius + cx
        val tdy = sin(angle) * radius + cy
        val text = textMeasurer.measure(
            "$i",
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                fontFamily = FontFamily.Cursive
            )
        )

        drawText(
            text,
            topLeft = Offset(tdx - text.size.width / 2f, tdy - text.size.height / 2f),
            brush = Brush.linearGradient(
                listOf(
                    Color.White,
                    Color(0xFFD53333),
                    Color(0xF0EEA806),
                    Color(0xFFDD0E0E),
                ),
            ),
        )
    }
}

private fun DrawScope.draw60Kata(second: Kata, radius: Float, cx: Float, cy: Float) {
    // draw 60 kata
    val x = cos(second.angle) * radius
    val y = sin(second.angle) * radius
    for (i in 1..60) {
        val angle = ((toRadians(i * (360 / 60) + 0.0)) - second.revDeg).toFloat()
        val tdx = cos(angle) * (radius + 64)
        val tdy = sin(angle) * (radius + 64)

        val mdx = cos(angle) * (radius * 2 + radius / 2.05f)
        val mdy = sin(angle) * (radius * 2 + radius / 2.05f)

        // if(i % 5 == 0)continue
        drawLine(
            brush = Brush.linearGradient(
                listOf(
                    Color(0xFFEC0F5A),
                    Color(0xFF11E651),
                    Color(0xFFECD506),
                ),
                start = Offset(cx, cy),
                end = Offset(cx + x, cy + y),
                tileMode = TileMode.Mirror
            ),
            start = Offset(cx + tdx, cy + tdy),
            end = Offset(cx + (mdx - tdx), cy + (mdy - tdy)),
            strokeWidth = if (i % 5 == 0) 16f else 4f,
        )
    }

    drawCircle(
        color = Color.Red,
        radius = 10f,
        center = Offset(cx, cy)
    )
}


private fun DrawScope.drawSecondKata(
    second: Kata,
    radius: Float,
    cx: Float,
    cy: Float
) {
    // seconder kata
    val kataW = radius - 50
    val x = cos(second.angle) * kataW
    val y = sin(second.angle) * kataW

    drawLine(
        color = Color.Black,
        start = Offset(cx, cy),
        end = Offset(cx + x, cy + y),
        strokeWidth = 10f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = Color.White,
        start = Offset(cx, cy),
        end = Offset(cx + x, cy + y),
        strokeWidth = 8f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = Color.Black,
        radius = 12f,
        center = Offset(cx + x, cy + y),
    )
    drawCircle(
        color = Color.White,
        radius = 10f,
        center = Offset(cx + x, cy + y),
    )

}

private fun DrawScope.drawMinuteKata(minute: Kata, radius: Float, cx: Float, cy: Float) {
    // minuter kata
    val kataW = radius - 100
    val x = cos(minute.angle) * kataW
    val y = sin(minute.angle) * kataW

    drawLine(
        color = Color.Black,
        start = Offset(cx, cy),
        end = Offset(cx + x, cy + y),
        strokeWidth = 14f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = Color.White,
        start = Offset(cx, cy),
        end = Offset(cx + x, cy + y),
        strokeWidth = 12f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = Color.Black,
        radius = 12f,
        center = Offset(cx + x, cy + y),
    )
    drawCircle(
        color = Color.White,
        radius = 10f,
        center = Offset(cx + x, cy + y),
    )
}

private fun DrawScope.drawHourKata(hour: Kata, radius: Float, cx: Float, cy: Float) {
    // hourer kata
    val kataW = radius - 150
    val x = cos(hour.angle) * kataW
    val y = sin(hour.angle) * kataW
    drawLine(
        color = Color.Black,
        start = Offset(cx, cy),
        end = Offset(cx + x, cy + y),
        strokeWidth = 18f,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = Color.White,
        start = Offset(cx, cy),
        end = Offset(cx + x, cy + y),
        strokeWidth = 16f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = Color.Black,
        radius = 12f,
        center = Offset(cx + x, cy + y),
    )
    drawCircle(
        color = Color.White,
        radius = 10f,
        center = Offset(cx + x, cy + y),
    )
}

private fun DrawScope.drawCenterCircle(cx: Float, cy: Float) {
    // center circle
    drawCircle(
        color = Color.Black,
        radius = 30f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color.White,
        radius = 28f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color.Black,
        radius = 26f,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color.White,
        radius = 24f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawOverlay(radius: Float, cx: Float, cy: Float) {
    // draw overlay
    drawCircle(
        color = Color(0x29169477),
        radius = radius + 164,
        center = Offset(cx, cy),
        style = Stroke(64f)
    )
}

private fun rotateX(kata: Kata, radius: Float, cx: Float): Float {
    return (cos(kata.angle) * radius) + cx
}

private fun rotateY(kata: Kata, radius: Float, cy: Float): Float {
    return (sin(kata.angle) * radius) + cy
}
