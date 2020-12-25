package protocol.switchboard

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SwitchBoardSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    fun connect(endpoint: String, port: Int) {
        print("SB Connecting to $endpoint:$port...")
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedReader(InputStreamReader(socket.inputStream))
        println("Done!")
    }

    fun sendMessage(message: String, sendNewLine: Boolean = true) {
        print("SB >> ")
        writer.write(message)
        if (sendNewLine) {
            writer.write("\r\n")
        }
        writer.flush()
        println(message)
    }

    fun readMessage(): String {
        val response = reader.readLine()
        println("SB << $response")
        return response
    }

    fun readRaw(length: Int): String {
        print("SB << ")
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
        print("SB Closing connection...")
        writer.close()
        reader.close()
        socket.close()
        println("Done!")
    }
}