import groovy.transform.Field
// Example Call
// groovy generate_mapping_file.groovy SBCI_MIS LMS MSSQL_SBCI_LOANS_PORTAL ORCL_SBCILMS_STG CONTROL_APPEND STAGING LKM#This_is_the_lkm IKM#This_is_the_ikm Z:\SBCI\DEV\tmp\groovyBuilder\mapping_spec.txt
//
// Args:
//	0 PROJECT_NAME
//  1 ODI_FOLDER
//  2 SOURCE_MODEL
//  3 TARGET_MODEL
//  4 INTEGRATION_TYPE
//  5 Layer that will be the target i.e. STAGING or FINAL (LMS)
//  6 LKM
//  7 IKM
//  8 File path

// Check correct number of args supplied
@Field final int argsSize = 9

//assert args.size() == 9 : "Incorrect number of arguments supplied (${args.size()})\r\nCorrect usage is: OdiMappingBuilder <url> <driver> <schama> <pwd> <workrep> <odiuser> <odipwd> <project> <folder> <iname> <control_file>\r\n\r\n"

if (args.size() != argsSize) {
    println "Error: Incorrect number of arguments supplied (${args.size()} instead of ${argsSize})\r\nCorrect usage is: OdiMappingBuilder <url> <driver> <schama> <pwd> <workrep> <odiuser> <odipwd> <project> <folder> <iname> <control_file>\r\n\r\n"
	return
	}
//File specFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\mapping_spec.txt')
File specFile = new File(args[8])
if (!specFile.exists()) { println "Error: Cannot find mapping file ${args[8]}"; return; }

List outL = []

List contentsL = []

contentsL = specFile.readLines()

def extractOnNext = 0
grabMappings = 0

def setPhysicalLine = {
                       return {
					           def km = {i-> return {args[i].substring(args[i].indexOf('#')+1).replaceAll('_',' ')}}
							   lkm = km(6)
							   ikm = km(7)
                               def l = "physical\t${lkm()}\t${ikm()}"
					 }
}

p = setPhysicalLine()
//println p()

/*
def stageIdxL = { stage ->
    return {
	    def idxL = []
        switch (stage) {
            case "STAGING": 
                idxL[0] = 1; 
                idxL[1] = 2; break;
            case "FINAL": 
                idxL[0] = 2; 
                idxL[1] = 3; break;
        	default: println "Error: Cannot determine source and target"; return;
        }
	    return idxL
    }
}
*/
//stgIdxsL = c(args[5])

def sourceTargetL = { stage ->
    return {
	    def tabL = []
        switch (stage) {
            case "STAGING": 
                tabL[0] = tableM['source']; 
                tabL[1] = tableM['staging']; break;
            case "FINAL": 
                tabL[0] = tableM['staging']; 
                tabL[1] = tableM['target']; break;
			default: println "Error: Cannot determine source and target"; return;
        }
	    return tabL
    }
}

//println s()
//println "After the Case"

//Mapping to hold source and target tables
tableM = [source : '', staging : '', target : '', journal : '']

firstPass = true

contentsL.eachWithIndex {fLine, idx ->
    
	if (extractOnNext) {
	    extractOnNext = false; 
	    grabMappings = true
	}
	
    def l = fLine.tokenize("\t")
	
    if (l[0] == "Source Table") { 
	  //If here then this is a new mapping section so reset any variables
	  startExtract = false
	  grabMappings = false
	  
	  //Push ending onto out List if not first time through
	  if (!firstPass) { 
					   //println p(); 
					   outL.push(p())
					   outL.push("END")
	  }
	  firstPass = false
	  
	  sourceTab = l[1]
	  tableM['source'] = l[1]
	  
	  //println "Source bit"

	}

    if (l[0] == "Staging Table") { 

	  stagingTab = l[1]
	  tableM['staging'] = l[1]
	  //println "Stg bit"
	  
	}

    if (l[0] == "Target Table") { 

	  targetTab = l[1]
	  tableM['target'] = l[1]
	  //println "Tgt bit"
	  
	}

    if (l[0] == "Journal Table") { 
      //println "Section Started for ${l[1]}"
	  journTab = l[1]	  
	  tableM['journal'] = l[1]
	  //println "Journ bit"
	}

    if (l[0] == "Data Dictionary Id") {
	    //println "Column Listing Section!"
		//println "Dict bit"
		extractOnNext = 1
		
        //println header
		//println sourceLine
		//println targetLine
		
		if (args[5] == 'STAGING') {
           stab = sourceTab
		   ttab = stagingTab
		}
		
		if (args[5] == 'FINAL') {
		   stab = stagingTab
		   ttab = targetTab
		}
	    
		//Set header here
	    header = "header\t${args[0]}\t${args[1]}"
        // add target Mapping default name to header line
	    header = header + "\tMAP_${ttab}"

		sourceTabL = sourceTargetL(args[5])
		//sourceLine = "source\t${args[2]}\t$stab\t$stab"
	    //targetLine = "target\t${args[3]}\t$ttab\t$ttab\t${args[4]}"	  
		sourceLine = "source\t${args[2]}\t${sourceTabL()[0]}\t${sourceTabL()[0]}"
	    targetLine = "target\t${args[3]}\t${sourceTabL()[1]}\t${sourceTabL()[1]}\t${args[4]}"	  
		
		outL.push(header)
		outL.push(sourceLine)
		outL.push(targetLine)
		
    }
	
    if (grabMappings) {
	    
		if (l[2] && l[2].length() > 1) {
		    //mapLine = "mapping\t${sourceTab}.${l[2].toUpperCase()}\t${l[3]}"
			mapLine = "mapping\t${sourceTabL()[0]}.${l[2].toUpperCase()}\t${l[3]}"
			//println mapLine
			outL.push(mapLine)
		}

	}
	
}

outL.push(p())
outL.push("END")
outL.each {println it}
