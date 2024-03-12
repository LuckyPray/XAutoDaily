package me.teble.xposed.autodaily.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import me.teble.xposed.autodaily.ui.scene.MainScreen


@Composable
fun XAutoDailyApp(circuit: Circuit) {
    val backStack = rememberSaveableBackStack(root = MainScreen)
    val navigator = rememberCircuitNavigator(backStack)

    CircuitCompositionLocals(circuit) {
        ContentWithOverlays {
            NavigableCircuitContent(
                navigator = navigator, backStack = backStack,
                decoration = GestureNavigationDecoration(
                    circuit.defaultNavDecoration,
                    onBackInvoked = navigator::pop
                )
            )
        }

    }

}