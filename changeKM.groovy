
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

import oracle.odi.domain.project.finder.IOdiIKMFinder;
import oracle.odi.domain.project.finder.IOdiLKMFinder;
import oracle.odi.domain.project.finder.IOdiCKMFinder;



// Must be Run while attached to repository
//assert !odiInstance.isClosed()

println "Enter KM spec file name in tiny box at bottom of screen:" 
//inFile = odiInputStream.withReader { it.readLine() }
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv')
//File mappingFile = new File(inFile)

//if (!mappingFile.exists()) {
//    println "Error: cannot find file: ${inFile}"
//	return
//}
def mappingFileL = new File('Z:\\SBCI\\DEV\\GroovyScripts\\filespecs\\changekm.csv').collect {it};

// Map datastore columns on target
txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)

       
getMapping = {projCode, mapName ->
    return {
	// This just returns a single mapping based on the folder and the mapping name
	mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(mapName, projCode);
   }
}
loopSpec = {
            nextLine = mappingFileL.pop()
            specL = nextLine.tokenize(",")
			mapC = getMapping(specL[0], specL[2])
			println "Entries in mapC: ${mapC()}"
            // now have a list of mappings
            }
            
    def ikm
    def lkm
	def ckm
    
    while (mappingFileL.size() > 0) {
	
        //loopSpec()
		nextLine = mappingFileL.pop()
		
		if (nextLine[0]=="!") { continue; } 
		
        specL = nextLine.tokenize(",")
		//mapC = getMapping(specL[0], specL[2])
		mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(specL[2], specL[0]);
		if (!mapC) {println "WARNING: No mapping found for ${specL[0]}.${specL[2]}"}
		sleep(500)
        //nextLine has been popualted in the closure
		//see what sort of km we are changing and get that KM
		if (specL[3] == "LKM") {

            lkm = ((IOdiLKMFinder)tme.getFinder(OdiLKM.class)).findGlobalByName(specL[4]);
			if (!lkm) {println "WARNING: Cannot find LKM: ${specL[4]}"}
        }
                 
        if (specL[3] == "IKM") {
            ikm = ((IOdiIKMFinder)tme.getFinder(OdiIKM.class)).findGlobalByName(specL[4]);
            if (!ikm) {println "WARNING: Cannot find IKM: ${specL[4]}"}
		}

        if (specL[3] == "CKM") {
            ckm = ((IOdiCKMFinder)tme.getFinder(OdiCKM.class)).findGlobalByName(specL[4]);
			assert ckm
			if (!ckm) {println "WARNING: Cannot find CKM: ${specL[4]}"}
        }

		
		mapC.each {
		    map = it
			println "Processing mapping: ${map.getName()}"
		    //take that get the physical design for the mapping
		    physTgtDesign = map.getPhysicalDesigns()
		    
		    physTgtDesign.each {
		    	
            	if (lkm) {
				    println "Changing LKM for ${map.getName()} -> ${lkm}"
            		apNodes = it.getAllAPNodes()
            		apNodes.each { it.setLKM(lkm) }
            	}
            
            	if (ikm || ckm) {
			        println "Changing CKM for ${map.getName()} -> ${ckm}"
            		tgtNodes = it.getTargetNodes()
            		tgtNodes.each { 
					
					ikm ? it.setIKM(ikm) : it.setCheckKM(ckm)
					
					}
            	    	
            	}
		}
		// Reset
		ikm=''
		lkm=''
		ckm=''
		if (ikm) {println "IKM still has a value" }
		if (lkm) {println "LKM still has a value" }
		if (ckm) {println "LKM still has a value" }

        }
	}

tm.commit(txnStatus)