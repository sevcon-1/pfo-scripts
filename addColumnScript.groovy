//Hacked by Dan Potter
// Set the tx up
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.model.OdiDataStore;
import oracle.odi.domain.model.OdiColumn ;

// FILE SPEC - Comma delimited
/*
MODEL_NAME DATASTORE_NAME COLUMN_NAME DATA_TYPE SCALE
ORCL_SBCILMS_STG STG_COUNTRY_CODES ODI_CHAR_COLUMN VARCHAR2 100
ORCL_SBCILMS_STG STG_COUNTRY_CODES ODI_NUMBER_COLUMN VARCHAR2 100
*/
//

// Z:\ALL\spec_files\column_specs.txt

// Sort out the spec file

println "Enter spec file name in tiny box at bottom of screen:" 
//inFile = "Z:\\ALL\\spec_files\\column_specs.txt"
inFile = odiInputStream.withReader { it.readLine() }
File specFile = new File(inFile)

if (!specFile.exists()) {
    println "Error: cannot find file: ${inFile}"
	return
}

specL = specFile.readLines()
//println specL

txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)

getDataStore = {model, table ->
    return {
    OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(table, model);
    }
}

getDataStores = {model
    return {
    OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(table, model);
    }
}
//Z:\SBCI\DEV\GroovyScripts\filespecs\new_columns_spec.csv
//popper = { elem -> return { elem.pop().tokenize(",")}}

//1.upto(specL.size()){
while (specL.size()) {
    // Seed the line variable
    colSpec = specL.pop().tokenize(",")
    //println "${colSpec[0]} belongs to ${colSpec[1]}"
    OdiDataStore ds = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(colSpec[1].trim(),colSpec[0].trim());
    assert ds
	//getDataStore colSpec[0],colSpec[1]
    try {

        if (ds) {
            OdiColumn col = new OdiColumn(ds, colSpec[2])
	    	if (col) {
	    	    println "Setting attributes"
	    	    col.setDataTypeCode(colSpec[3])
	    		col.setMandatory(false)
	    		println colSpec[4]
	    		int len = colSpec[4] as Integer
	    		col.setLength(colSpec[len])
	    		if (colSpec[5] != -1) {
 	    		    int scale = colSpec[5] as Integer
   	    		    col.setScale(scale)
	    		}
	    		
	    	tm.commit(txnStatus)
			txnStatus = tm.getTransaction(txnDef)
			}
        }
	}
	catch (Exception e) {
	    println "Error setting for line: ${colSpec}"
		println e
		//tm.rollback(txnStatus)
		txnStatus = tm.getTransaction(txnDef)
		println txnStatus

	}
}	


//tm.rollback(txnStatus)


