package com.coderbdk.clock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coderbdk.clock.ui.theme.ClockTheme
import kotlin.math.cos
import kotlin.math.sin
import java.lang.Math.toRadians
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClockUI(false)
                }
            }
        }
    }
}

private class Kata(time: Int) {
    var angle = 0f
    var degree = 0
    var revDeg = toRadians(90.0).toFloat()

    init {
        degree = time
        update()
    }

    fun update() {
        degree += 6
        angle = (toRadians(degree + 0.0) - revDeg).toFloat()
        if (degree > 360) {
            degree = 0
        }
    }
}

@Composable
fun ClockUI(isPreview: Boolean) {

    val calender = Calendar.getInstance()
    calender.time = Date(System.currentTimeMillis())

    val textMeasure = rememberTextMeasurer()
    val second = Kata(calender.get(Calendar.SECOND) * 6)
    val minute = Kata(calender.get(Calendar.MINUTE) * 6)
    val hour = Kata(calender.get(Calendar.HOUR) * 30)

    var isStart by remember {
        mutableStateOf(false)
    }
    if (!isPreview) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                isStart = !isStart
                handler.postDelayed(this, 100)
            }

        }
        handler.post(runnable)
    }

    Canvas(
        modifier = Modifier
            .background(Color.Black)
            .clickable {
                isStart = !isStart
            }
            .fillMaxSize()
    ) {

        val width = size.width
        val height = size.height

        val radius = (width / 2f) - 200
        val cx = width / 2f
        val cy = height / 2f

        var x = 0f
        var y = 0f

        val time = String.format(
            "$isStart-> %02d:%02d:%02d",
            (hour.degree / 30),
            (minute.degree / 6),
            (second.degree / 6)
        )

        drawText(
            textMeasure.measure("$width, $height, $time"),
            color = Color.White
        )


        // second
        x = cos(second.angle) * radius
        y = sin(second.angle) * radius

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

        var dw = radius + 180
        var dx = cos(second.angle) * dw
        var dy = sin(second.angle) * dw
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
        // minute
        x = cos(minute.angle) * radius
        y = sin(minute.angle) * radius
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

        dw = radius + 148
        dx = cos(minute.angle) * dw
        dy = sin(minute.angle) * dw
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
        // hour
        x = cos(hour.angle) * radius
        y = sin(hour.angle) * radius
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

        dw = radius + 124
        dx = cos(hour.angle) * dw
        dy = sin(hour.angle) * dw
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

        // clock background
        x = cos(second.angle) * radius
        y = sin(second.angle) * radius
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
        drawLine(
            color = Color.Cyan,
            start = Offset(0f, cy),
            end = Offset(width, cy)
        )
        drawLine(
            color = Color.Cyan,
            start = Offset(cx, 0f),
            end = Offset(cx, height)
        )

        for (i in 1..12) {
            val angle = ((toRadians(i * (360 / 12) + 0.0)) - second.revDeg).toFloat()
            val tdx = cos(angle) * radius + cx
            val tdy = sin(angle) * radius + cy
            val text = textMeasure.measure(
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
        // draw 60 kata
        x = cos(second.angle) * radius
        y = sin(second.angle) * radius
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

        // seconder kata
        var kataW = radius - 50
        x = cos(second.angle) * kataW
        y = sin(second.angle) * kataW

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

        // minuter kata
        kataW = radius - 100
        x = cos(minute.angle) * kataW
        y = sin(minute.angle) * kataW

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
        // hourer kata
        kataW = radius - 150
        x = cos(hour.angle) * kataW
        y = sin(hour.angle) * kataW
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

        // draw overlay
        drawCircle(
            color = Color(0x29169477),
            radius = radius + 164,
            center = Offset(cx, cy),
            style = Stroke(64f)
        )

        second.update()
        if (second.degree == 360) minute.update()
        if (second.degree == 360 && minute.degree % 60 == 0) hour.update()

    }
}

private fun rotateX(kata: Kata, radius: Float, cx: Float): Float {
    return (cos(kata.angle) * radius) + cx
}

private fun rotateY(kata: Kata, radius: Float, cy: Float): Float {
    return (sin(kata.angle) * radius) + cy
}

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    ClockTheme {
        Column(Modifier.size(500.dp, 500.dp)) {
            ClockUI(true)
        }
    }
}