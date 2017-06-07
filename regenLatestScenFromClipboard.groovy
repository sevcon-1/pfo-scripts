//Hacked by Dan Potter
//2014-09-11

// Regenerates the latest version of scenarios copied from the Designer to
// the clipboard - Note: you must use ctrl+c to copy NOT  mouse action

import java.awt.datatransfer.*;
import  java.awt.*;
import oracle.odi.generation.support.OdiScenarioGeneratorImpl;
import oracle.odi.generation.IOdiScenarioGenerator;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition; 
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.MasterRepositoryDbInfo 
import oracle.odi.core.config.WorkRepositoryDbInfo 
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.domain.project.finder.IOdiInterfaceFinder;
import oracle.odi.domain.project.OdiInterface;
import oracle.odi.core.security.Authentication 


//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

//StringSelection str = new StringSelection( "This is some text" );
//clipboard.setContents( str, null );

/* -- get system clipboard */

Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

/* -- get clipboard context */

Transferable data = clipboard.getContents(null);

/* -- is context string type ? */

boolean bIsText = ( ( data != null ) && ( data.isDataFlavorSupported( DataFlavor.stringFlavor ) ) );

/* -- if yes, translate context to string type and write it */

if ( bIsText ) {

  try {

    txnDef = new DefaultTransactionDefinition(); 
    tm = odiInstance.getTransactionManager() ;
    txnStatus = tm.getTransaction(txnDef);

    String s = (String)data.getTransferData( DataFlavor.stringFlavor );

    //println( s );
    
    def l = s.split('\n').collect{it.replaceAll('\r','')}
    
    IOdiScenarioGenerator gene = new OdiScenarioGeneratorImpl (odiInstance);
    
    String sc;
 
    for(i in 0 .. l.size()-1) {
        sc = l.get(i);
        sTag = (sc =~ /(.\w*)\s.*/) [ 0 ] [ 1 ]
        println "Regenerating: "+sTag
        gene.regenerateLatestScenario(sTag);
//        println l.get(i);
             
    }
    
    tm.commit(txnStatus);



  } catch (UnsupportedFlavorException ex) {
    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
  } catch (IOException ex) {
    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
  }
}

