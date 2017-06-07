
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
inFile = odiInputStream.withReader { it.readLine() }
def mappingFileL = new File(inFile).collect {it};
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\simple_map_builder_def.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\CUTDOWN_sbci_source_stage_mapping.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\first_cut_mapper.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv')
//File mappingFile = new File(inFile)

//if (!mappingFile.exists()) {
//    println "Error: cannot find file: ${inFile}"
//	return
//}

//def mappingFileL = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv').collect {it};

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
	
	if (projCode.substring(0,1) == "*") {
		obj = setFolder(projCode.drop(1), objName)
		mappingC = obj()[0].getMappings()
    }
	else {
		mappingC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(objName, projCode);
	}

	return mappingC
    }
}



// Set expression 
def setExp(DatastoreComponent tgtDSC, OdiDataStore tgtTable, String propertyName, String expressionText) throws Exception { 
  DatastoreComponent.findAttributeForColumn(tgtDSC,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}

createExpression = {specLineL ->

	physTgtDesign = map.getPhysicalDesigns()
	physTgtDesign.each {
        tgtNodes = it.getTargetNodes()
	}
	
    //
	tgtNodes.each { 
		lc =  it.getLogicalComponent()
		
		// Bound datastore
		bds = lc.getBoundDataStore()

		setExp lc, bds, specLineL[2], specLineL[3]
		}
}		

iterateExpressions = {
                      nextLine = mappingFileL.pop()
                      specL = nextLine.tokenize(",")
		              mapC = getMappings(specL[0], specL[1])
                      // now have a list of mappings
                      mapC().each {
                                 map = it
		              		     println "About to create expression"
                      		     createExpression(specL)
                      }
}					  


// This is the main call to do the expression mapping
iterator = mappingFileL.size()
1.upto(iterator) {
    iterateExpressions()
}

tm.commit(txnStatus)
