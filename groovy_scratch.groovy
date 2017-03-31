//Created by DI Studio
def createGreeter = { name ->
  return {
    def day = new Date().getDay()
    if (day == 0 || day == 6) {
      //println "Nice Weekend, $name"
      msg = "Nice Weekend, ${name}"
    } else {
      //println "Hello, $name"
      msg = "Nice Weekend, ${name}"
      
    }
  }
}
/*
def greetWorld = createGreeter("World")
greetWorld()
def greetMe = createGreeter("Me")
def greetYou = createGreeter("You")
greetMe()
println (msg)
greetMe()
greetYou()
println (msg)
*/

l=[]
l = ['a','b','c','d','d','a','a','a','a']
//l.each{ println it }
//def getLineMatch = {->  println ("in the closure") }

lfs=[]
s = "a b c d e f e e e e e e e e e e"
lfs=s.tokenize()
//lfs.each{println (it)}

println lfs

cutl = {section -> 
    return {
            def copyL = lfs.grep {it == section}
    }
}

newL = cutl("e")
println newL().getClass()
println newL()

aL = cutl("a")
println aL().getClass()
println newL()
println aL()

//println lfs.grep{ it == "e" }

// Catch an error from a closure
def clos = {p ->
  if (p==1) {
    //try {force_error()} catch (Exception e) {      e.printStackTrace() }
	force_error()
    
  }
  if (p==2) {
    int one = 2
    println "good def 1"
  }
  if (p==3) {
    int one = 3
    println "good def 2"
  }

  
}

l = [1, 2,3]

l.each {
    try {
        clos(it)      
    }
    catch (Exception e) {
      println "In the catch"
	  e.printStackTrace()
      
    }

}



