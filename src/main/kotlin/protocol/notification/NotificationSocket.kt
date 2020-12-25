package protocol.notification

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class NotificationSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    fun connect(endpoint: String = "35.185.200.209", port: Int = 1863) {
        print("Connecting to $endpoint:$port...")
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedReader(InputStreamReader(socket.inputStream))
        println("Done!")
    }

    fun sendMessage(message: String) {
        print(">> ")
        writer.write("$message\r\n")
        writer.flush()
        println(message)
    }

    fun readMessage(): String {
        val response = reader.readLine()
        println("<< $response")
        return response
    }

    fun readRaw(length: Int): String {
        print("<< ")
        var output = ""
        var remaining = length
        while (remaining > 0) {
            val buffer = CharArray(remaining)
            remaining -= reader.read(buffer)
            output += String(buffer)
        }
        println(output)
        return output
    }

    fun close() {
        print("Closing connection...")
        writer.close()
        reader.close()
        socket.close()
        println("Done!")
    }
}