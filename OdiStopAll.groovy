//Created by Dan P
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition; 
import oracle.odi.runtime.agent.RuntimeAgent;
import oracle.odi.core.OdiInstance;
import oracle.odi.core.security.Authentication ;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.MasterRepositoryDbInfo ;
import oracle.odi.core.config.WorkRepositoryDbInfo ;
import oracle.odi.core.config.PoolingAttributes;

import groovy.sql.Sql;

import org.apache.commons.cli.Option;

import java.util.Collection;
import java.io.*;

// These are to be passed as parameters
/*
    String workRep ="
    Url            ="
    Driver         ="
    Master_User    ="
    Master_Pass    ="
    Work_User      ="
    Odi_User       ="
    Odi_Pass       ="
	
	odi_operator = "ODI_REPO_OPERATOR"
	odiop_pass = "0Ofpo9Vk26G3gYX2EUre"
*/
/*
Args:
	0 - filename
*/

def cli = new CliBuilder()
cli.with {
    h longOpt: 'help', required: false, 'show usage information' 
	//x longOpt: 'exclusions', args: Option.UNLIMITED_VALUES, required: false, valueSeparator: ',' as char,'Exclude datatypes from conversion'
	//r longOpt: 'remapdatatype', args: Option.UNLIMITED_VALUES, required: false, valueSeparator: ',' as char,'Overwrites datatype mapping with custom value'
	w longOpt: 'workrep', args: 1, required: true, 'Schema name of work repo'
	u longOpt: 'url', args: 1, required: true, 'Master Repo Url'
	d longOpt: 'driver', args: 1, required: true, 'JDBC driver'
	m longOpt: 'master', args: 1, required: true, 'Schema name of master repo'
	p longOpt: 'masterpass', args: 1, required: true, 'Password for master repo'
	l longOpt: 'worklogin', args: 1, required: true, 'DB user for work repo'
	o longOpt: 'odiuser', args: 1, required: true, 'ODI user'
	s longOpt: 'odiuserpass', args: 1, required: true, 'ODI user password'
	r longOpt: 'odioperator', args: 1, required: true, 'ODI Repo user'
	z longOpt: 'odioperatorpass', args: 1, required: true, 'ODI Operator password'
}

def opt = cli.parse(args)
if (!opt) { println "No args passed"; return }
if (opt.h) { 
  cli.usage(); 
  return 
}

/* END ARG PROCESSING */


/* Assign args */
    workRep      =opt.w
    Url          =opt.u
    Driver       =opt.d
    Master_User  =opt.m
    Master_Pass  =opt.p
    Work_User    =opt.l
    Odi_User     =opt.o
    Odi_Pass     =opt.s

    odi_operator = opt.r
	odiop_pass = opt.z
	
	msgSummary = []
	banner = {msg->
	            msgSummary.add(msg)
                0.upto(0, {println " "})
                println "MSG#>>>>> ${msg}"
                0.upto(0, {println " "})
	}
    
	def now = new Date()
    banner("ODI Job Shutdown script: ${now.format("yyyy-MM-dd HH:mm:ss.SSS")}")
	
    banner.call("Connecting to Master and Work: ${opt.m} / ${opt.w}")
    MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(Url, Driver, Master_User,Master_Pass.toCharArray(), new PoolingAttributes());
    WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(workRep, new PoolingAttributes());

    banner.call("Authenticating OdiInstance")
    OdiInstance odiInstance=OdiInstance.createInstance(new OdiInstanceConfig(masterInfo,workInfo));
    
    Authentication auth = odiInstance.getSecurityManager().createAuthentication(Odi_User,Odi_Pass.toCharArray());
    odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
    
    banner.call("OdiInstance authenticated")
    
    banner.call("Connecting to Agent")
	RuntimeAgent agent = new RuntimeAgent(odiInstance, Odi_User, Odi_Pass.toCharArray())
	banner.call("Connected to runtime Agent")
	
    //sessInfo = agent.getSessionInformation()
    //println sessInfo
    
	banner.call("Fetching live sessions")
   
    banner.call("SQL Connection to db")
	def sql = Sql.newInstance (Url, odi_operator , odiop_pass)
	
	def sessL = []
	getLiveCount = {
	                return {
					        def liveSessions = []
					        sql.eachRow('SELECT SESS_NO FROM ndmisd_dwr1.vw_running_sessions') { row ->
                                //println "${row.SESS_NO}"
	                        	//v = row.SESS_NO as Long
	                            liveSessions.add(row.SESS_NO as Long)
                            }
							return liveSessions
					}
	}
	
	sessL = getLiveCount()
	banner("Number of live sessions is: ${sessL().size()}")
	 
    stop = oracle.odi.runtime.agent.invocation.StopType.valueOf('IMMEDIATE')

    sessL().each {
	            banner "Stopping session ${it}"
				agent.stopSession(it, stop)
	}
	
	banner("Number of live sessions is: ${sessL().size()}")
	
	//def con = new Sql(ds)

    sessL = []
	
    println "Message Summary"
	msgSummary.eachWithIndex{v,i -> println "${i}: ${v}"}
		
	banner.call(">>>> END OF SCRIPT <<<<")
	