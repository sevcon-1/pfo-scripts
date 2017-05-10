
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;

import oracle.odi.domain.project.OdiProject;
//import oracle.odi.domain.project.finder.IOdiProjectFinder;
//import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
//import oracle.odi.domain.topology.finder.IOdiContextFinder;
//import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiInterface;
//import oracle.odi.domain.project.interfaces.DataSet;
import oracle.odi.domain.model.OdiDataStore;
//import oracle.odi.domain.topology.OdiContext;

//import oracle.odi.domain.project.finder.IOdiIKMFinder;
//import oracle.odi.domain.project.finder.IOdiLKMFinder;
//import oracle.odi.domain.project.finder.IOdiCKMFinder;



// Must be Run while attached to repository
//assert !odiInstance.isClosed()

println "Enter mapping spec file name in tiny box at bottom of screen:" 
//inFile = odiInputStream.withReader { it.readLine() }
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\simple_map_builder_def.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\CUTDOWN_sbci_source_stage_mapping.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\first_cut_mapper.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv')
//File mappingFile = new File(inFile)

//if (!mappingFile.exists()) {
//    println "Error: cannot find file: ${inFile}"
//	return
//}
def mappingFileL = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv').collect {it};

// Map datastore columns on target
txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)


// Get datastore

getDataStore = {model, table ->
    return {
    OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(table, model);
    }
}


def folder
setFolder = {projCode, folName ->
             return {
                 folderC = ((IOdiFolderFinder)tme.getFinder(OdiFolder.class)).findByName(folName, projCode);
				 assert folderC.size() == 1 : "Error - ambiguous folder name specified"
				 return folderC
				 }
}

// The original
getMapping = {projCode, mapName ->
    return {
	// This just returns a single mapping based on the folder and the mapping name
	mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(mapName, projCode);
   }
}

// objName parameter can relate to either a folder or a mapping name
getMappings = {projCode, objName ->
	return {
	
	//if (projCode.substring(0,1) == "*") {pcode = projCode}
    //projCode.substring(0,1) == "*" ? ((pcode = projCode.drop(1)) && (obj = setFolder(pcode, objName))) : ((pcode = projCode) && (obj = objName))
	if (projCode.substring(0,1) == "*") {
	    //pcode = projCode.drop(1)
		obj = setFolder(projCode.drop(1), objName)
		//assert obj().getClass() == "X"
		//mappingC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(setFolder(projCode.drop(1), objName)[0], projCode.drop(1));
		//mappingC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(projCode.drop(1), obj()[0]);
		mappingC = obj()[0].getMappings()
//		mappingC.each {println "Mapping is: ${it}" }
		//mappingC.each {println it.getClass() }
		//m = obj()[0]
		//println m.getName()
		//assert 1==0
    }
	else {
		mappingC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(objName, projCode);
	}

	//def obj = 
	//mappingC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(obj, pcode);
	return mappingC
	
	//if (projCode.substring(0,1)=="*") {
	//    mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(projCode.substring(1), objName);	
	//}
	//else {
	//    mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(projCode);	
	//}

	 
    }
}



// Set expression 
def createExp(DatastoreComponent tgtDSC, OdiDataStore tgtTable, String propertyName, String expressionText) throws Exception { 
  DatastoreComponent.findAttributeForColumn(tgtDSC,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}

// get first line to check if this is a wildcard change
mappingFileL[0].substring(0,1) == "*" ? (wildcardInd = true) : (wildcardInd = false)

createExpression = {specLineL ->

	physTgtDesign = map.getPhysicalDesigns()
	physTgtDesign.each {
        tgtNodes = it.getTargetNodes()
	}
	
	//assert mapC().size() == 1
	
    //
	tgtNodes.each { 
		lc =  it.getLogicalComponent()
		
		// Bound datastore
		bds = lc.getBoundDataStore()
		
		println "Mapping ${specL[2]} for ${map.getName()}"
		//createExp lc, bds, specL[2], specL[3]
		createExp lc, bds, specLineL[2], specLineL[3]
		

		//println lc.getClass()

		}
}		

iterateExpressions = {
                      nextLine = mappingFileL.pop()
                      specL = nextLine.tokenize(",")
                      println specL
		              mapC = getMappings(specL[0], specL[1]) //THIS IS THE ORIGINAL LINE
                      println "Size of Mapc is ${mapC().size()}"
                      // now have a list of mappings
                      mapC().each {
                                 map = it
		              		     println "About to create expression"
                      		     createExpression(specL)
                      }
}					  



iterator = mappingFileL.size()
if (wildcardInd) {
    1.upto(iterator) {
		iterateExpressions()
		// Now set the expressions
//		mapC = getMappings(specL[0], specL[1]) //THIS IS THE ORIGINAL LINE
//
//        // now have a list of mappings
//        mapC.each {
//                   map = it
//				   println "About to create expression"
//        		     //createExpression()
//        }
	}
	
	             
}
else {
    mappingFileL.each {
	                    nextLine = mappingFileL.pop()
		                specL = nextLine.tokenize(",")
	                    mapC = getMappings(specL[0], specL[1]) //THIS IS THE ORIGINAL LINE
			            mapC.each {
                            map = it
				            println "About to create expression"
        		            //createExpression()
                        }
	}
					   
}


//mappingFile.each {
//    specL = it.tokenize(",")
//	
//	//This bit to make the mapping file generic or mapping specific
//	//specL[1] == "*" ? getMappings(specL[0]) specL[1] : getMapping(specL[0], specL[1])
//	// get mapping based on project and name
//	mapC = getMappings(specL[0], specL[1]) //THIS IS THE ORIGINAL LINE
//    println "Mapping list has ${mapC().size()} entries"
//	
//	//popper idea?
//	//map = mapC()[0]
//	map = mapC().pop()
//	println map.getClass()
//	// get physical nodes - to drive target datastore
//	physTgtDesign = map.getPhysicalDesigns()
//	physTgtDesign.each {
//        tgtNodes = it.getTargetNodes()
//	}
//	
//	//assert mapC().size() == 1
//	
//    //
//	tgtNodes.each { 
//		lc =  it.getLogicalComponent()
//		
//		// Bound datastore
//		bds = lc.getBoundDataStore()
//		
//		println "Mapping ${specL[2]} for ${map.getName()}"
//		//createExp lc, bds, specL[2], specL[3]
//
//		//println lc.getClass()
//
//		}
//    
//	
//}

tm.commit(txnStatus)
