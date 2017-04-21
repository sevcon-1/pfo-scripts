
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;

import oracle.odi.domain.project.OdiProject;
//import oracle.odi.domain.project.finder.IOdiProjectFinder;
//import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.mapping.finder.IMappingFinder
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
File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv')
//File mappingFile = new File(inFile)

if (!mappingFile.exists()) {
    println "Error: cannot find file: ${inFile}"
	return
}	

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

getMapping = {projCode, mapName ->
    return {
	// This just returns a single mapping based on the folder and the mapping name
	mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(mapName, projCode);
	 
    }
}

getMappings = {projCode ->
    return {
	// This just returns a single mapping based on the folder and the mapping name
	mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(projCode);
	 
    }
}


// Set expression 
def createExp(DatastoreComponent tgtDSC, OdiDataStore tgtTable, String propertyName, String expressionText) throws Exception { 
  DatastoreComponent.findAttributeForColumn(tgtDSC,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}


mappingFile.each {
    specL = it.tokenize(",")
	
	
	//This bit to make the mapping file generic or mapping specific
	//mapC = specL[1] == "ALL" ? getMappings(specL[0]) : getMapping(specL[0], specL[1])
	// get mapping based on project and name
	mapC = getMapping(specL[0], specL[1])
    //popper idea?
	map = mapC()[0]
	// get physical nodes - to drive target datastore
	physTgtDesign = map.getPhysicalDesigns()
	physTgtDesign.each {
        tgtNodes = it.getTargetNodes()
		
	}
    //
	tgtNodes.each { 
		lc =  it.getLogicalComponent()
		
		// Bound datastore
		bds = lc.getBoundDataStore()
		
		println "Mapping ${specL[2]} for ${map.getName()}"
		createExp lc, bds, specL[2], specL[3]

		//println lc.getClass()

		}
    
	
}

tm.commit(txnStatus)
