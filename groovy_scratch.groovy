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

// String Replacement
//s = 'IKM#This_is_the_IKM'
s = 'IKM#DEFAULT'

println s.substring(s.indexOf('#')+1).replaceAll('_',' ')

// String replacement and a closure within a closure
//test call for this : groovy scratch first IKM#DEFAULT IKM#NOT_DEFAULT
def setPhysicalLine = {
                       return {
					           def km = {i-> return {args[i].substring(args[i].indexOf('#')+1).replaceAll('_',' ')}}
							   lkm = km(1)
							   ikm = km(2)
                               def l = "physical\t${lkm()}\t${ikm()}"
					 }
}

p = setPhysicalLine()
println p()

// Simple Case statement
println args[0]
switch (args[0]) {
    case "STAGING": 
        srcIdx = 1; break;
        tgtIdx = 2; break;
    case "FINAL": 
        srcIdx = 2; break;
        tgtIdx = 3; break;
	default: println "Error: Cannot determine source and target"; return;
}
println "After the Case"

// Case in a closure returning list 
/*
println args[0]
def c = { stage ->
    return {
	    def idxL = []
        switch (stage) {
            case stage: 
                idxL[0] = 1; 
                idxL[1] = 2; break;
            case stage: 
                idxL[0] = 2; 
                idxL[1] = 3; break;
        	default: println "Error: Cannot determine source and target"; return;
        }
	    return idxL
    }
}
s = c(args[0])
println s()
//println "After the Case"
*/