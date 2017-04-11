//Hacked by Dan P
// Built on original idea by David Allen

import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;

import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiInterface;
import oracle.odi.domain.project.interfaces.DataSet;
import oracle.odi.domain.model.OdiDataStore;
import oracle.odi.domain.topology.OdiContext;

import oracle.odi.domain.project.finder.IOdiIKMFinder;
import oracle.odi.domain.project.finder.IOdiLKMFinder;
import oracle.odi.domain.project.finder.IOdiCKMFinder;


// Must be Run while attached to repository
//assert odiInstance.isClosed()
println "Enter mapping spec file name in tiny box at bottom of screen:" 
inFile = odiInputStream.withReader { it.readLine() }
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\simple_map_builder_def.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\CUTDOWN_sbci_source_stage_mapping.txt')
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\first_cut_mapper.txt')
File mappingFile = new File(inFile)

if (!mappingFile.exists()) {
    println "Error: cannot find file: ${inFile}"
	return
}	

// Set the tx up
txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)


getDataStore = {model, table ->
    return {
    OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(table, model);
    }
}

def sectionStatus = { i -> return { i } }

def sectionL = {section -> 
    return {
            def copyL = lfs.grep {it == section}
    }
}

def createExp(DatastoreComponent tgtDSC, OdiDataStore tgtTable, String propertyName, String expressionText) throws Exception { 
  DatastoreComponent.findAttributeForColumn(tgtDSC,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}

def setMsgStatus = {i -> 
                return {
				    //println (i) 
					def r
				    if (i=="E") { return r = "Error" }
                    if (i=="W") { return r = "Warning" }
				    if (i=="M") { return r = "Message" }
                                    
				} 
}

def printSectionMsg = {status, sectionid, ident -> 
                        
                          def s = setMsgStatus(status)
						  println ("${s()} returned in section: ${sectionid} for identifier: ${ident}")
}


Map srcDsMap = [:]
joinList = []
sameMap = 1
goFlag = sectionStatus(1)
mappingFile.eachLine{line ->
    
	// Hold control variable to manage flow 

	
    lt = line.tokenize("\t")
    //println "${lt[0]}"
    
    //get mapping line info
    //s = getLineMatch(lt[0], "header")
    //if (r) {
    
    if (lt[0] == 'END') {

        //println ("in END")

		//Commit prev transaction and start a new one
		tm.commit(txnStatus)
		txnStatus = tm.getTransaction(txnDef)
		goFlag = sectionStatus(1)
    
    }
    
    
    if (lt[0] == "header") {

      //println "value of r is ${r}"
      // Extract mapping, project, folder etc and build mapping
      // Check number of elements is OK
      assert lt.size() == 4 : "Incorrect number of elements in header line"
      
      pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
      ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
      
      //println(lt[1])
      project = pf.findByCode(lt[1])
      
      //println(lt[2])
      folderColl = ff.findByName(lt[2], lt[1])
      
      //Check only one folder exists
      assert folderColl.size() == 1// : folderColl.size()
      
      OdiFolder folder = null
      folder = folderColl.iterator().next()
      
      try {
		  printSectionMsg "M", lt[0], "creating mapping ${lt[3]}"
	      map = new Mapping(lt[3], folder)
		  mapCrOK = true
		  tme.persist(map)
	  }
      catch (Exception e) {
	      goFlag = sectionStatus(0)
		  //println "Caught Exception"
	      //err = e.printStackTrace()
		  e.printStackTrace()
		  printSectionMsg("E", "MAPPING", lt[3])
		  //println ("Error creating mapping ${lt[3]}")
	  }
	  println "Continuing"
      
    }
    
    // Only proceed if the mapping has been created
	if (goFlag()) {
		//println ("past goFlag")
        if (lt[0] == "source") {
          
		    try {
                srcDs = getDataStore lt[1], lt[2]
                
                //println (srcDs().getName())
                srcDatastoreC  = (DatastoreComponent) map.createComponent("DATASTORE",srcDs(), false);
                
                //Add to a #map for future use
                srcDsMap[lt[3]] = srcDatastoreC
                outconn = srcDatastoreC.getOutputPoint()
                
                //bon = srcDatastoreC.getBoundObjectName()
                //println("bound object name is: ${bon}")
          	}
            catch (Exception e) {
	            goFlag = sectionStatus(0)
	            printSectionMsg("E", lt[0], lt[3]) 
				//println ("Error creating mapping ${lt[3]}")
	        }

        }
        
        if (lt[0] == "target") {
          
            try {
			    tgtDs = getDataStore lt[1], lt[2]
                
                tgtDatastoreC  = (DatastoreComponent) map.createComponent("DATASTORE",tgtDs(), false)
                tgtDatastoreC.setIntegrationType(lt[4])
                
                //println (tgtDs().getName())
                //objMap["source"] = srcDs
                
                // This has to be the last connector placed on the mapping
	            // Works here only for 1:1 mappings
                outconn.connectTo(tgtDatastoreC)
			}
            catch (Exception e) {
	            goFlag = sectionStatus(0)
	            printSectionMsg("E", lt[0], lt[3]) 

	        }
        }
        
        
        if (lt[0] == "join") {
          
          //Get all joins into a list and process after the fact? That way final connector can be added...
          fromDs = srcDsMap[lt[1]]
          toDs = srcDsMap[lt[2]]
          joinCond = lt[3]
          
          println "joining from ${fromDs} to ${toDs}"
          
          srcJoin = JoinComponent.joinSources("JOIN_DATA", fromDs, toDs, joinCond)
          joinList.push(srcJoin)
          
          //srcJoin.connectReferencedSourcesToJoin()
          
          //joinOutConn = srcJoin.getOutputPoint()
          
          //joinOutConn.connectTo(tgtDatastoreC)
          
          
        }
        
        if (lt[0] == "mapping") {
          
            try {
			//println "Length of this mapping line is: ${lt.size()}"
		        createExp(tgtDatastoreC, tgtDs(), lt[2], lt[1])
			}
			catch (Exception e) {
			    goFlag = sectionStatus(0)
	            printSectionMsg("E", lt[0], "mapping ${tgtDs}: ${lt[2]} to ${lt[1]}") 
			}
		  
          
        }
        
        if (lt[0] == "physical") {
            
			def ikm
			def lkm
		
            physTgtDesign = map.getPhysicalDesigns()
            
            // Tee up KMs
            // Convention is that the "physical" line must have entries in position 1 and 2. The word DEFAULT takes the place of a default choice
		    if (lt[1] != "DEFAULT") {
		        lkm = ((IOdiIKMFinder)tme.getFinder(OdiIKM.class)).findGlobalByName(lt[1]);
		    }
		    
                    if (lt[2] != "DEFAULT") {
		        ikm = ((IOdiIKMFinder)tme.getFinder(OdiIKM.class)).findGlobalByName(lt[2]);
		    }
            
            physTgtDesign.each {
                apNodes = it.getAllAPNodes()
                tgtNodes = it.getTargetNodes()
                apNodes.each { 
                               println it.getLKMName()
                }
                tgtNodes.each{
                              //println it.getIKMName()
                              if (ikm) {it.setIKM(ikm)}
                }
            }
        }
    }
        

} // End Mapping File Read

//println joinList.size()

    tm.commit(txnStatus)

//println (tgtDs().getName())
//println (srcDs().getName())

//println "${srcDsMap.keySet()}"
//srcDsMap.each{k, v -> println "${k} is the key for ${v}"}





