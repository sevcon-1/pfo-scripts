
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;

import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.OdiInterface;
import oracle.odi.domain.model.OdiDataStore;
import oracle.odi.domain.project.finder.IOdiKMFinder;
import oracle.odi.domain.project.finder.IOdiIKMFinder;
import oracle.odi.domain.project.finder.IOdiLKMFinder;
import oracle.odi.domain.project.finder.IOdiCKMFinder;

import oracle.odi.domain.project.OdiIKM;
import oracle.odi.domain.project.OdiLKM;
import oracle.odi.domain.project.OdiCKM;

import oracle.odi.domain.adapter.project.IOptionValue;

// Must be Run while attached to repository
//assert !odiInstance.isClosed()

println "Enter spec file name in tiny box at bottom of screen:" 
inFile = odiInputStream.withReader { it.readLine() }
//File mappingFile = new File('Z:\\SBCI\\DEV\\tmp\\filespecs\\add_mapping.csv')
//File mappingFile = new File(inFile)

def mappingFileL = new File(inFile).collect {it};

if (!mappingFileL.size()) {
    println "Error: cannot find content for file: ${inFile}"
	return
}
//def mappingFileL = new File('Z:\\SBCI\\DEV\\GroovyScripts\\filespecs\\column_attributes.csv').collect {it};
//def mappingFileL = new File('Z:\\SBCI\\DEV\\GroovyScripts\\filespecs\\column_attributes.csv').collect {it};

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
            
    while (mappingFileL.size() > 0) {

		nextLine = mappingFileL.pop()
        specL = nextLine.tokenize(",")
		//mapC = getMapping(specL[0], specL[2])
		mapC = ((IMappingFinder)tme.getFinder(Mapping.class)).findByName(specL[2], specL[0]);
		//sleep(500)
		mapC.each {
		    map = it
		    //take that get the physical design for the mapping
		    physTgtDesign = map.getPhysicalDesigns()
		    
		    physTgtDesign.each {
            
			    tgtNodes = it.getTargetNodes()
                
			    tgtNodes.each {
				
				    lc =  it.getLogicalComponent()

					//assert lc.getClass() == "String"
                
				    attr = lc.findOutputAttribute(specL[3]) 
					//assert attr.getClass() == "String"
					
           	        //Unique Keys - Unset all the existing keys then set those specified
					if (specL[4]=="UK") {
					    attrL = lc.getAttributes()
				        attrL.each { it.setKeyIndicator(false) }
		                
						if (attr) {
						    println "Setting attribute ${specL[3]} on ${map.getName()} as ${specL[4]}"
						    attr.setKeyIndicator(true);
						}
						else {
						    //println "${specL[3]} does not exist"
						    println "WARNING - attribute ${specL[3]} does not exist for ${map.getName()}"
						}
			        }
					if (specL[4]=="TGT") {
					
    					println "Setting attribute ${specL[3]} on ${map.getName()} as ${specL[4]}"
						hint = MapExpression.ExecuteOnLocation.valueOf("TARGET")
				        attr.setExecuteOnHint(hint) 

					}
					
					// Set specific KM Options
					// IKM
					String value
					if (specL[4]=="IKMOPT") {
					
    				    OdiIKM projectIKM = (OdiIKM) ((IOdiKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiIKM.class)).findGlobalByName(specL[5]).toArray()[0];

						physTgtDesign = map.getPhysicalDesigns()

						physTgtDesign.each {
                            
            		        physNodes = it.getPhysicalNodes()
							physNodes.each {
								node = it
					            OdiIKM ikm = (OdiIKM) node.getIKM();
    	                        if (ikm != null && ikm == projectIKM) {
								    //value = specL[3]
                                    IOptionValue odiMapOption = node.getIKMOptionValue(specL[3]);
                                    if (odiMapOption != null) {
							            println "Setting option ${specL[3]} for mapping ${map.getName()} on node ${node.getName()}"
                                        odiMapOption.setValue(specL[6]);
                                    }
                                }
					        }
            	    	
            	        }
					}
				}
           	}
		}
    }
	

tm.commit(txnStatus)