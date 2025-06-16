package com.github.catomon.moewpaper.utils

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.DesktopWindow
import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference
import java.awt.Desktop
import java.io.File

fun DesktopWindow.toggleMinimized() {
    if (DesktopUtils.MyUser32.INSTANCE.IsIconic(hwnd)) {
        User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_RESTORE)
        User32.INSTANCE.SetForegroundWindow(hwnd)
    } else {
        val minimized = User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_MINIMIZE)

        if (!minimized) {
            User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_RESTORE)
            User32.INSTANCE.SetForegroundWindow(hwnd)
        }
    }
}

object DesktopUtils {
    interface MyUser32 : User32 {
        fun IsIconic(hWnd: HWND): Boolean

        companion object {
            val INSTANCE: MyUser32 = Native.load("user32", MyUser32::class.java)
        }
    }

    fun getWindows(): List<DesktopWindow> {
        return WindowUtils.getAllWindows(true).filter {
          (  it.title.isNotBlank() && it.title !in listOf(
                "Microsoft Text Input Application",
                "Program Manager",
                "KonaHomescreen"
            )) || MyUser32.INSTANCE.IsIconic(it.hwnd)
        }.sortedBy { it.title }.also { println(it.joinToString("\n") { it.title }) }
    }

    fun getIcon(window: DesktopWindow): String? {
        return SystemIconUtils.extractIconFromFileAndCache(window.filePath)
    }

    fun closeWindow(hwnd: HWND) {
//        User32Util.destroyWindow(hwnd)
        User32.INSTANCE.PostMessage(hwnd, WinUser.WM_CLOSE, null, null)
    }

    fun bringToForeground(hwnd: HWND) {
        User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_RESTORE)
        User32.INSTANCE.SetForegroundWindow(hwnd)
    }

    fun minimize(hwnd: HWND) {
        User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_MINIMIZE)
    }

    fun isMinimized(hwnd: HWND) = User32.INSTANCE.IsWindowVisible(hwnd)

    fun getWindowProcessId(hwnd: HWND): Int {
        val processIdRef = IntByReference()
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, processIdRef)

        val pid = processIdRef.value

        return pid
    }

    fun closeWindowsOfProcess(processId: Int) {
        val windows = mutableListOf<HWND>()

        WindowUtils.getAllWindows(false).forEach { dWindow ->
            val hwnd = dWindow?.hwnd

            if (hwnd != null) {
                if (getWindowProcessId(hwnd) == processId) {
                    windows += hwnd
                }
            }
        }

        windows.forEach {
            closeWindow(it)
        }
    }

    fun openFile(file: File): Boolean {
        val desktop = Desktop.getDesktop()
        try {
            desktop.open(file)
            return true
        } catch (e: Exception) {
            println("error opening a file: ${file.path}")
            return false
        }
    }

    fun killProcess(pid: Long) {
        val process = ProcessHandle.of(pid).orElse(null)

        if (process != null) {
            process.destroy()
            println("Process terminated gracefully.")

            if (process.isAlive) {
                process.destroyForcibly()
                println("Process terminated forcibly.")
            }
        } else {
            println("Process not found.")
        }
    }

    @Throws(Exception::class)
    @JvmStatic
    fun bringMainWindowToForeground(processId: Long) {
        var mainHwnd: HWND? = null

        User32.INSTANCE.EnumWindows(object : WinUser.WNDENUMPROC {
            override fun callback(hwnd: HWND?, arg: Pointer?): Boolean {
                if (hwnd == null) return true

                val processIdRef = IntByReference()
                User32.INSTANCE.GetWindowThreadProcessId(hwnd, processIdRef)
                val pid = processIdRef

                if (pid.value.toLong() == processId) {
                    val titleLength =
                        User32.INSTANCE.GetWindowTextLength(hwnd)

                    if (titleLength > 0) {
                        mainHwnd = hwnd
                        return false
                    }
                }
                return true
            }
        }, null)

        val hwnd = mainHwnd
        if (hwnd != null) {
            User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_RESTORE)
            val result = User32.INSTANCE.SetForegroundWindow(hwnd)
            if (result) {
                println("Main window brought to foreground successfully.")
            } else {
                println("Failed to bring main window to foreground.")
            }
        } else {
            println("Main window not found for process ID $processId.")
        }
    }
}