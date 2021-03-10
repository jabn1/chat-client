import java.net._
import java.io._
import scala.io._
import scala.io.StdIn.readLine
import scala.concurrent.Future



val s = new Socket(InetAddress.getByName("localhost"), 5000)
lazy val in = new BufferedSource(s.getInputStream()).getLines()
val out = new PrintStream(s.getOutputStream())

var last = ""
last= null
var connected = false
var user = ""
val commands = Map(
    "login" -> "/ID $username",
    "getUsers" -> "/USERLIST",
    "chat" -> "/CHAT -u $username -m \"$message\"",
    "logout" -> "/CLOSE"
)

while(!connected){
    val input = readLine("Introduzca su usuario: ")
    connected = connect(input) == "Ok"
    if(connected){
        user = input
        println("Connectado exitosamente")
        getUsers()
        println("Connected users: " + in.next())
    }else{
        println("Intentelo nuevamente")
    }
}

implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
val future = Future {
    val result = getInput()
    result
}

while(true){
    if(connected){
        val input = readLine("Mensaje a Enviar (username:message) or (command): ") // username:asdlfjjsssf
        sendMessage(input)
    }
}

def getValue(x: Option[String]) = x match { case Some(s) => s case None => "" }

def getInput() : Boolean = {
    while(true){
        val next = in.next()
        if(next.slice(0,5) == "/CHAT"){
            println("\nUser message:" + next)
        }else if(last == "getUsers") {
            if (next != "Empty"){
                println("\nConnected users: " + next)
            }
        }else if(last == "logout"){
            if(next == "Ok"){
                System.exit(0)
            }
        }
    }
    return true
}

def connect(user: String) : String = {
    val connectCommand = getValue(commands.get("login")).replace("$username", user)
    out.println(connectCommand)
    out.flush()

    val response = in.next

    return response
}

def getUsers() = {
    val getUsersCommand = getValue(commands.get("getUsers"))
    out.println(getUsersCommand)
    out.flush()
    last = "getUsers"

}

def logout() = {
    val getUsersCommand = getValue(commands.get("logout"))
    out.println(getUsersCommand)
    out.flush()
    last = "logout"

}

def sendMessage(inputData: String) = {
    val data = inputData.split(":")
    if(data.size > 1 ){

        val username = data(0)
        val message = data(1)

        val chatCommand = getValue(commands.get("chat")).replace("$username", user).replace("$message", message)
        out.println(chatCommand)
        out.flush()
        last = "chat"
    }
    else{ //comandos
        val command = data(0)
        if(command == "userlist"){
            getUsers()
            last = "getUsers"
        }
        else if(command == "quit"){
            logout()
            last = "logout"
        }else{
            last = ""
            println("Invalid command")
        }
    }
}
s.close()
