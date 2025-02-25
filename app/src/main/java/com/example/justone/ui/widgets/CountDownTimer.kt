package com.example.justone.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.justone.ui.theme.Green400
import com.example.justone.ui.theme.Red400
import com.example.justone.ui.theme.Yellow400

private const val TIMER_ANGLE = 360f

@Composable
fun CountDownTimer(timer: Int, timerWidth: Int, onCountDownFinished: () -> Unit = {}) {
    val haptic = LocalHapticFeedback.current
    val unitWidth = timerWidth / (10 + 2).toFloat()
    val radius = (unitWidth * 8) / 2
    val canvasSize = with(LocalDensity.current) { timerWidth.toDp() }

    val animateArcFloat = remember { Animatable(1f) }

    var countDownTimer by remember { mutableStateOf(timer) }
    val leftTime by animateIntAsState(
        targetValue = countDownTimer,
        animationSpec = tween(durationMillis = timer * 1000, easing = LinearEasing),
        finishedListener = {
            onCountDownFinished()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    )

    Box(modifier = Modifier.size(canvasSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBackgroundRing(radius, unitWidth)
            drawForegroundArc(animateArcFloat, radius, unitWidth)
        }
        CountDownNumber(leftTime)
    }

    LaunchedEffect(animateArcFloat) {
        animateArcFloat.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = timer * 1000, easing = LinearEasing)
        )
    }

    LaunchedEffect(countDownTimer) {
        countDownTimer = 0
    }
}

private fun DrawScope.drawForegroundArc(
    animateArcFloat: Animatable<Float, AnimationVector1D>,
    radius: Float,
    unitWidth: Float
) {
    drawArc(
        brush = Brush.sweepGradient(
            0.0f to Red400,
            0.4f to Yellow400,
            1.0f to Green400
        ),
        startAngle = TIMER_ANGLE,
        sweepAngle = TIMER_ANGLE * animateArcFloat.value,
        size = Size(radius * 2, radius * 2),
        topLeft = Offset(unitWidth * 2, unitWidth * 2),
        useCenter = false,
        style = Stroke(unitWidth)
    )
}

private fun DrawScope.drawBackgroundRing(
    radius: Float,
    unitWidth: Float
) {
    drawArc(
        color = Color.LightGray,
        startAngle = TIMER_ANGLE,
        sweepAngle = TIMER_ANGLE,
        size = Size(radius * 2, radius * 2),
        topLeft = Offset(unitWidth * 2, unitWidth * 2),
        useCenter = false,
        style = Stroke(unitWidth)
    )
}
