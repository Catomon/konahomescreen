import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.catomon.moewpaper.App
import com.github.catomon.moewpaper.di.appModule
import com.github.catomon.moewpaper.ui.MoeViewModel
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.skiko.GraphicsApi
import org.jetbrains.skiko.SkikoProperties
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get
import rogga.echoMsg
import rogga.echoWarn
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import javax.swing.JOptionPane
import kotlin.concurrent.thread

fun main() {
    setDefaultExceptionHandler()

    setRenderApi()

    application {
        setComposeExceptionHandler()

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
            title = "KonaHomescreen",
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
                if (bottomPadding > 0) {
                    val winSize = windowState.size
                    windowState.position = WindowPosition(
                        getWndScrCenterPos(
                            winSize.width.value.toInt(),
                            winSize.height.value.toInt()
                        ).x.dp,
                        getWndScrCenterPos(
                            winSize.width.value.toInt(),
                            winSize.height.value.toInt()
                        ).y.dp + 20.dp
                    )
                } else {
                    windowState.position = WindowPosition(Alignment.Center)
                }
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
                App(
                    viewModel = viewModel,
                    padding = PaddingValues(bottom = bottomPadding.dp),
                    exitApplication = this@application::exitApplication
                )
            }
        }
    }
}

fun getWndScrCenterPos(windowWidth: Int, windowHeight: Int): IntOffset {
    val screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
    val screenWidth = screenBounds.width
    val screenHeight = screenBounds.height

    val x = (screenWidth - windowWidth) / 2
    val y = (screenHeight - windowHeight) / 2

    return IntOffset(x, y)
}

private fun setDefaultExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        JOptionPane.showMessageDialog(
            null,
            e.stackTraceToString(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
}

private fun setRenderApi() {
    try {
        val renderApi = GraphicsApi.DIRECT3D.name
        System.setProperty("skiko.renderApi", renderApi)
        echoMsg { "skiko.renderApi = $renderApi" }
    } catch (e: Exception) {
        echoWarn { "Could not set desired render api." }
        e.printStackTrace()
    }
}

fun ApplicationScope.setComposeExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        e.printStackTrace()

        try {
            File("last_error.txt").writeText(e.stackTraceToString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        thread {
            application {
                Window(
                    onCloseRequest = ::exitApplication,
                    state = rememberWindowState(width = 300.dp, height = 250.dp),
                    visible = true,
                    title = "Error",
                ) {
                    val clipboard = LocalClipboardManager.current

                    Box(contentAlignment = Alignment.Center) {
                        SelectionContainer {
                            Text(
                                e.stackTraceToString(), Modifier.fillMaxSize().verticalScroll(
                                    rememberScrollState()
                                )
                            )
                        }
                        Button({
                            clipboard.setText(AnnotatedString(e.stackTraceToString()))
                        }, Modifier.align(Alignment.BottomCenter)) {
                            Text("Copy")
                        }
                    }
                }
            }
        }

        exitApplication()
    }
}