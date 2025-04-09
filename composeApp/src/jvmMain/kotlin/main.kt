import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.catomon.moewpaper.App
import com.github.catomon.moewpaper.di.appModule
import com.github.catomon.moewpaper.ui.MoeViewModel
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinUser
import moe_wallpaper.composeapp.generated.resources.Res
import moe_wallpaper.composeapp.generated.resources.ic_cyclone
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener

fun main() = application {

    startKoin {
        modules(appModule)
    }

    val maxSize = Toolkit.getDefaultToolkit().screenSize

    val windowState = rememberWindowState(
        width = maxSize.width.dp,
        height = maxSize.height.dp,
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center)
    )

    val viewModel: MoeViewModel = get(MoeViewModel::class.java)

    Window(
        title = "Moe Wallpaper",
        state = windowState,
        onCloseRequest = ::exitApplication,
        resizable = false,
        undecorated = true,
//        onKeyEvent = { keyEvent ->
//            println(keyEvent.key)
//            when {
//                keyEvent.key == Key.AltLeft && keyEvent.type == KeyEventType.KeyDown -> {
//                    viewModel.showItemNames.value = true
//                    println("sada" +  viewModel.showItemNames.value)
//                    true
//                }
//                keyEvent.key == Key.AltLeft && keyEvent.type == KeyEventType.KeyUp -> {
//                    viewModel.showItemNames.value = false
//                    println("sada" +  viewModel.showItemNames.value)
//                    true
//                }
//                else -> false
//            }
//        }
    ) {

        window.focusableWindowState = false

        var usableBounds =
            remember { GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds }
        var bottomPadding by remember { mutableStateOf(0) }

        fun updatePadding() {
            usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
            bottomPadding = (window.height - usableBounds.height).let { if (it < 0) 0 else it }
            if (bottomPadding > 0)
                windowState.position = WindowPosition(
                    getScreenCenterPosition(windowState.size).x.dp,
                    getScreenCenterPosition(windowState.size).y.dp + 20.dp
                )
            else
                windowState.position = WindowPosition(Alignment.Center)
        }

        window.addMouseListener(object : MouseListener {
            override fun mouseClicked(p0: MouseEvent?) {}
            override fun mousePressed(p0: MouseEvent?) {}
            override fun mouseReleased(p0: MouseEvent?) {}

            override fun mouseEntered(p0: MouseEvent?) {
                updatePadding()
            }

            override fun mouseExited(p0: MouseEvent?) {

            }
        })

        window.addWindowListener(object : WindowListener {
            override fun windowOpened(p0: WindowEvent?) {
                updatePadding()
            }

            override fun windowClosing(p0: WindowEvent?) {}
            override fun windowClosed(p0: WindowEvent?) {}
            override fun windowIconified(p0: WindowEvent?) {}
            override fun windowDeiconified(p0: WindowEvent?) {}
            override fun windowActivated(p0: WindowEvent?) {}
            override fun windowDeactivated(p0: WindowEvent?) {}
        })

        DevelopmentEntryPoint {
            App(state = viewModel, modifier = Modifier.padding(bottom = bottomPadding.dp), exitApplication = this@application::exitApplication)
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}

fun getScreenCenterPosition(windowSize: DpSize): IntOffset {
    val screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
    val screenWidth = screenBounds.width
    val screenHeight = screenBounds.height

    val x = (screenWidth - windowSize.width.value.toInt()) / 2
    val y = (screenHeight - windowSize.height.value.toInt()) / 2

    return IntOffset(x, y)
}