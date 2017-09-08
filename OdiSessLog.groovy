//Created by DJRP
import oracle.odi.core.OdiInstance
import oracle.odi.core.config.OdiInstanceConfig
import oracle.odi.core.config.MasterRepositoryDbInfo 
import oracle.odi.core.config.WorkRepositoryDbInfo 
import oracle.odi.core.security.Authentication  
import oracle.odi.core.config.PoolingAttributes 
import oracle.odi.domain.runtime.session.finder.IOdiSessionFinder 
import oracle.odi.domain.runtime.session.OdiSession
import java.util.Collection 
import java.io.*

class SessLog {

/* ----------------------------------------------------------------------------------------- 
Simple sample code to list all executions of the last version of a scenario,
along with detailed steps information
----------------------------------------------------------------------------------------- */

/* update the following parameters to match your environment 
=> */
//def session_id = 97014 /*Scenario Name*/
/* <=
End of the update section */

    OdiSession odiSession
    Collection OdiChildrenList
    OdiInstance odiInstance

def FindSession(odiInstance, session_id) {
    
    this.odiInstance = odiInstance
    odiSession = ((IOdiSessionFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiSession.class)).findBySessionId(session_id)
    if (!odiSession) {return}
    OdiChildrenList = ((IOdiSessionFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiSession.class)).findChildSessions(odiSession)

}

def getOdiSessionValue() {
    return odiSession
}

def makeMap (odiSession) {
    
    def logMap = [:]
    
    def errMsg = null
    if (this.odiSession.getErrorMessage()  != null) {
        def from = 0
        def to = null
        def len = this.odiSession.getErrorMessage().toString().length()
        if (len > 4000) {to = 2000} else { to = len}
            errMsg = this.odiSession.getErrorMessage().toString().substring(from, to)
        }

    logMap = [name :         this.odiSession.getName(), 
              error :        errMsg ,
              start :        this.odiSession.getStartTime(), 
              end :          this.odiSession.getEndTime(), 
              parent :       this.odiSession.getParentSessionId(),
              status :       this.odiSession.getStatus(),
              master_agent : this.odiSession.getMasterAgentName()
              ]
  
    
}

// This method returns only the top level log.
def getLog(odiInstance) {
    def pSession = makeMap(odiSession)
    //def pSession = [name :         odiSession.getName(), 
    //                error :        odiSession.getErrorMessage(), 
    //                start :        odiSession.getStartTime(), 
    //                end :          odiSession.getEndTime(), 
    //                parent :       odiSession.getParentSessionId(),
    //                status :       odiSession.getStatus(),
    //                master_agent : odiSession.getMasterAgentName()
    //                ]
}

def getLog() {

    def sessName = odiSession.getName().toString()

    def parSessMap = [:]
    def sessMap = [:]
    
    //assert OdiChildrenList.class.simpleName == null
    this.OdiChildrenList = getOffspring()
    
    def from = 0
    for (i in this.OdiChildrenList) {
        
        def to = null
        def errMsg = i.getErrorMessage().toString()
        
        if (errMsg != null) {
            def len = i.getErrorMessage().toString().length()
            if (len > 4000) {to = 2000} else { to = len}
            errMsg = i.getErrorMessage().toString().substring(from, to)
        }
        
        //if (errMsg == null) { assert errMsg != null }
        
        sessMap [i.getSessionId().toInteger()] = [name : i.getName(), 
                                                  error : errMsg, 
                                                  start : i.getStartTime(), 
                                                  end : i.getEndTime(), 
                                                  parent : i.getParentSessionId(),
                                                  status : i.getStatus(),
                                                  master_agent : i.getMasterAgentName(),
                                                  duration : i.getDuration()
                                                  ]
    }

    parSessMap [odiSession.getName().toString()] = sessMap
    
    return parSessMap
    
}

def getOffspring() {

    def OdiChildrenClone = OdiChildrenList.clone()

    def keepLooking = true
    while (keepLooking) {
    
    for (i in OdiChildrenClone) {
    
        def v = ( ((IOdiSessionFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiSession.class)).findChildSessions(i) )

//assert v == null
        
        if (v == null) {
            println "v is null"
        }
        
        for (j in v) {

            if (!(j in OdiChildrenList)){
              
              println "j is not in the Clone list. J is ${j}"
              OdiChildrenList.add(j)
              println "List added to"
              OdiChildrenList.each { println "OdiChildrenList is now worth ${OdiChildrenList}" }
            }
        }
        //OdiChildrenClone.each {println "clone entries within For Loop $it"}
    }
    
      //Compare the lists and go again?
      if (!(OdiChildrenList == OdiChildrenClone)) {
        //wipe the clone and re-clone
          OdiChildrenClone = OdiChildrenList.clone()
      }
      else {
        keepLooking = false
      }
      
      //assert OdiChildrenClone.size() == 7
      
    }
    
    return OdiChildrenList
}

}