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

File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\groovyBuilder\\simple_map_builder_def.txt')
// Stick mapping in a list

// Set the tx up
txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)


getDataStore = {model, table ->
    return {
    //OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(table, model);
    OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(table, model);
    }
}

def getLineMatch = {section, matcher ->
  r = (section.toLowerCase().matches(matcher)) ? 1 : 0
  return r
}

def sectionL = {section -> 
    return {
            def copyL = lfs.grep {it == section}
    }
}

def createExp(DatastoreComponent tgtDSC, OdiDataStore tgtTable, String propertyName, String expressionText) throws Exception { 
  DatastoreComponent.findAttributeForColumn(tgtDSC,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}

Map srcDsMap = [:]
mappingFile.eachLine{line ->
    lt = line.tokenize("\t")
    //println "${lt[0]}"
    
    //get mapping line info
    //s = getLineMatch(lt[0], "header")
    //if (r) {
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
	  
	  map = new Mapping(lt[3], folder)
      tme.persist(map)
      
    }
    
	
    if (lt[0] == "source") {
      
      srcDs = getDataStore lt[1], lt[2]
      
      //println (srcDs().getName())
	  srcDatastoreC  = (DatastoreComponent) map.createComponent("DATASTORE",srcDs(), false);
      
	  //Add to a #map for future use
	  srcDsMap[lt[3]] = srcDatastoreC
	  outconn = srcDatastoreC.getOutputPoint()
	  
	  //bon = srcDatastoreC.getBoundObjectName()
	  //println("bound object name is: ${bon}")
    }
    
	if (lt[0] == "join") {
	  
	  fromDs = srcDsMap[lt[1]]
	  toDs = srcDsMap[lt[2]]
	  joinCond = lt[3]
	  
	  println "joining from ${fromDs} to ${toDs}"
	  
	  srcJoin = JoinComponent.joinSources("JOIN_DATA", fromDs, toDs, joinCond)
	  
	  
	}
	
    if (lt[0] == "target") {
      
      tgtDs = getDataStore lt[1], lt[2]
      
	  tgtDatastoreC  = (DatastoreComponent) map.createComponent("DATASTORE",tgtDs(), false)
	  tgtDatastoreC.setIntegrationType(lt[4])
	  
      //println (tgtDs().getName())
      //objMap["source"] = srcDs
	  
	  outconn.connectTo(tgtDatastoreC)
    }
	
    if (lt[0] == "mapping") {
	  
	  createExp(tgtDatastoreC, tgtDs(), lt[1], lt[2])
	  
    }

    if (lt[0] == "physical") {
	  
	  physTgtDesign = map.getPhysicalDesigns()
      
	  // Tee up KMs
	  ikm = ((IOdiIKMFinder)tme.getFinder(OdiIKM.class)).findGlobalByName(lt[1]);
	  
	  physTgtDesign.each {
	      apNodes = it.getAllAPNodes()
		  tgtNodes = it.getTargetNodes()
		  apNodes.each { 
		                 println it.getLKMName()
		  }
          tgtNodes.each{
                        println it.getIKMName()
						it.setIKM(ikm)
          }
	  }

	
	}
	
	
}

	tm.commit(txnStatus)

//println (tgtDs().getName())
//println (srcDs().getName())

//println "${srcDsMap.keySet()}"
//srcDsMap.each{k, v -> println "${k} is the key for ${v}"}





