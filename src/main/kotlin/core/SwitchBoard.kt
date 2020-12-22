package core

import protocol.NotificationTransport
import protocol.NotificationTransportManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

object SwitchBoard {
    private val transport: NotificationTransport = NotificationTransportManager.transport

    suspend fun transfer(passport: String) {
        val result = transport.sendXfr()
        print("Connecting to ${result.address}:${result.port}...")
        val socket = Socket(result.address, result.port)
        println("Connected!")
        val writer = PrintWriter(socket.outputStream)
        val reader = BufferedReader(InputStreamReader(socket.inputStream))
        println(">> USR 1 orfeo.ciano@gmail.com ${result.auth}")
        writer.write("USR 1 orfeo.ciano@gmail.com ${result.auth}\r\n")
        writer.flush()
        val authResult = reader.readLine()
        println("<< $authResult")
        println(">> CAL 2 orfeo18@hotmail.it")
        writer.write("CAL 2 orfeo18@hotmail.it\r\n")
        writer.flush()
        val inviteResult = reader.readLine()
        println("<< $inviteResult")
        val joinResult = reader.readLine()
        println("<< $joinResult")
        writer.write("MSG 3 N 127\r\n" +
                "MIME-Version: 1.0\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "X-MMS-IM-Format: FN=MS%20Sans%20Serif; EF=; CO=0; CS=0; PF=0\r\n\r\n" +
                "Hi.")
        writer.flush()
    }

}