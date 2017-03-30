
// Example Call
// generate_mapping_file.groovy SBCI_MIS LMS MSSQL_SBCI_LOANS_PORTAL ORCL_SBCILMS_STG CONTROL_APPEND
//
// Args:
//	0 PROJECT_NAME
//  1 ODI_FOLDER
//  2 SOURCE_MODEL
//  3 TARGET_MODEL
//  4 INTEGRATION_TYPE



File specFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\mapping_spec.txt')

List outL = []

List contentsL = []

contentsL = specFile.readLines()

def extractOnNext = 0
grabMappings = 0

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
	  if (!firstPass) { outL.push("END") }
	  firstPass = false
	  
	  //Set header here
	  header = "header\t${args[0]}\t${args[1]}"
	  
	  sourceTab = l[1]
	  sourceLine = "source\t${args[2]}\t$sourceTab\t$sourceTab"
	  
	  //println "Source bit"

	}

    if (l[0] == "Staging Table") { 

	  stagingT = l[1]
	  //println "Stg bit"
	  
	}

    if (l[0] == "Target Table") { 

	  targetTab = l[1]
	  targetLine = "target\t${args[3]}\t$targetTab\t$targetTab\t${args[4]}"	  

	  // add target Mapping default name to header line
	  header = header + "\tMAP_${targetTab}"

	  //println "Tgt bit"
	  
	}

    if (l[0] == "Journal Table") { 
      //println "Section Started for ${l[1]}"
	  journT = l[1]	  
	  //println "Journ bit"
	}

    if (l[0] == "Data Dictionary Id") {
	    //println "Column Listing Section!"
		//println "Dict bit"
		extractOnNext = 1
		
        //println header
		//println sourceLine
		//println targetLine
		
		outL.push(header)
		outL.push(sourceLine)
		outL.push(targetLine)
		
    }
	
    if (grabMappings) {
	    
		if (l[2] && l[2].length() > 1) {
		    mapLine = "mapping\t${sourceTab}.${l[2].toUpperCase()}\t${l[3]}"
			//println mapLine
			outL.push(mapLine)
		}

	}

}

outL.push("END")
outL.each {println it}
