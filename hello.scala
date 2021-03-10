import java.net._
import java.io._
import scala.io._
import scala.io.StdIn.readLine

val s = new Socket(InetAddress.getByName("localhost"), 5000)
lazy val in = new BufferedSource(s.getInputStream()).getLines()
val out = new PrintStream(s.getOutputStream())

var connected = false
var user = ""
val commands = Map(
    "login" -> "/ID $username",
    "getUsers" -> "/USERLIST",
    "chat" -> "/CHAT -u $username -m \"$message\"",
    "logout" -> "/CLOSE"
)

while(true){
    while(!connected){
        val input = readLine("Introduzca su usuario: ")
        connected = connect(input) == "Ok"
        if(connected){
            user = input
            println("Connectado exitosamente")
            getUsers()
        }else{
            println("Intentelo nuevamente")
        }
    }

    if(connected){
        val input = readLine("Mensaje a Enviar (username:message): ") // username:asdlfjjsssf
        sendMessage(input)

    }
}
// while(true){

// }
def getValue(x: Option[String]) = x match { case Some(s) => s case None => "" }

def connect(user: String) : String = {
    val connectCommand = getValue(commands.get("login")).replace("$username", user)
    out.println(connectCommand)
    out.flush()

    val response = in.next()

    return response
}

def getUsers() : Boolean = {
    val getUsersCommand = getValue(commands.get("getUsers"))
    out.println(getUsersCommand)
    out.flush()

    val response = in.next()

    if (response != "Empty"){
        println("Connected users: " + response)
    }

    return (response != "Empty")
}

def sendMessage(inputData: String) : String = {
    val data = inputData.split(":")
    if(data.size > 1 ){

        val username = data(0)
        val message = data(1)

        val chatCommand = getValue(commands.get("chat")).replace("$username", user).replace("$message", message)
        out.println(chatCommand)
        out.flush()


    }

    val response = in.next()
    return response
}
s.close()
