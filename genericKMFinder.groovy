//Created by DI Studio

import oracle.odi.domain.project.finder.IOdiKMFinder;

import oracle.odi.domain.project.finder.IOdiKMFinder;
import oracle.odi.domain.project.finder.IOdiIKMFinder;
import oracle.odi.domain.project.finder.IOdiLKMFinder;
import oracle.odi.domain.project.finder.IOdiCKMFinder;

import oracle.odi.domain.project.OdiIKM;
import oracle.odi.domain.project.OdiLKM;
import oracle.odi.domain.project.OdiCKM;

//km = "GLOBAL.IKM NTMA Oracle Control Append"
km = "IKM NTMA Oracle Control Append"
km = "CKM SBCI Oracle"

//finder = new IOdiKMFinder();

//kmC = ((IOdiKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiIKM.class)).findGlobalByName(km)
kmC = ((IOdiKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiKM.class)).findGlobalByName(km)
//println kmC.size()
kmC.each{println it}
//OdiIKM projectIKM = (OdiIKM) ((IOdiKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiIKM.class)).findGlobalByName(km); //.toArray()[0];
//println projectIKM.getClass()
/*
if (GLOBAL) {
	    OdiIKM projectIKM = (OdiIKM) ((IOdiKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiIKM.class)).findGlobalByName(specL[5]).toArray()[0];
} else {
	    OdiIKM projectIKM = (OdiIKM) ((IOdiKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiIKM.class)).findGlobalByName(specL[5]).toArray()[0];		
}
*/
