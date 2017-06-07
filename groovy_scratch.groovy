//Created by DI Studio
//Closure Example
/*
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
*/
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
/*
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
*/
//println lfs.grep{ it == "e" }
/*
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
*/
// String Replacement
/*
//s = 'IKM#This_is_the_IKM'
s = 'IKM#DEFAULT'

println s.substring(s.indexOf('#')+1).replaceAll('_',' ')
*/

/*
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
*/

// Simple Case statement
/*
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
*/

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

// Ternary Operator
/*
def getLineMatch = {section, matcher ->
  r = (section.toLowerCase().matches(matcher)) ? 1 : 0
  return r
}
*/
/* 
// Get dir path - uses lastIndexOf and substring 
p = args[0]
File f = new File (p)
assert f.exists()
idx = args[0].lastIndexOf('\\')
// Check a dir has been passed in
assert idx != -1
println "Index value is: ${idx}"
//println p.substring(0, args[0].lastIndexOf('\\'))
*/

//Ternary Operator setting a file name
/*
def outPath
idx = args[0].lastIndexOf('\\')
if (idx != -1) {
    outPath = args[0].substring(0, idx)
	println "Out path is: ${outPath}"
	//return
}
now = new Date()
fileTs = now.format("yMMddkmms")
//Variable = If this is true ? Variable is set to this value : Else set to this value
//outPath ? outFname = "mapping_output_${fileTs}.txt" : outFname = "set_To_final_value"
outFname =  outPath ? "mapping_output_${fileTs}.txt" : "set_To_final_value"
println "Out file is called: ${outFname}"
*/

/*
//Getting some timings
ms = {return {System.currentTimeMillis()} }
ns = {return {System.nanoTime()} }
1.upto(100) {
    t = ms()
	println t()
    sleep(1000)
}
// Sorting out args
//args.each {println it}
*/
/*
// Tokenize maps ??
Map pMap = [:]
s = 'this=that'
l = s.tokenize("=")
pMap[l[0]] = l[1]
pMap.each{k, v -> println "${k} is worth ${v}"}
*/
/*
//  INPUT BOILERPLATE
println "Enter mapping spec file name in tiny box at bottom of screen:" 
inFile = odiInputStream.withReader { it.readLine() }
*/

/*
// Popper Closure
l =  [1,2,3,4,5,6,7,8,9]

//def popper = {elem -> return {elem.pop()}}
def popper = {return {l.pop()}}
a = popper(l)

//l.each {println a}
//a = l.pop()
//println a
//println popper(l)
//while (popper(l)) {println popper()}
1.upto(l.size()){
    println (a())
}
//1.upto() { println a() }
*/

/*
// Closure iteration test
def idx = { 
    println "In the closure"
	return {
        println "In the return closure"
		def j
		switch (v) {
		    case "STAGING":
			    println "Evaluting Staging";
				j = 1;
				break;
		    case "FINAL":
			    println "Evaluting FINAL";
				j = 2;
                break;				
			default: println "Evaluting DEFAULT"; j = -1; 
			
		return j;
		}
	}
}
//i = 0
v = args[0]
i = idx()

1.upto(10) {

    //println "mod ${it} = ${it%2} "
	println "Value of i is: ${i()}"
	m = (it%2)
	if (m==1) { v = "STAGING" }  else { v = "FINAL"}
	
}
*/

// Splitting a string with the letter as a delimiter
// Not sure why it works
//s = "ABDE"
//
//s.split(/\W*/).each { 
//
//    println "it has a value of ${it}"
//    switch(it) {
//        case "D":
//            println ("Value is D")
//            break;
//        default:
//            println "Illegal value"
//            break;
//    }
//}
*/