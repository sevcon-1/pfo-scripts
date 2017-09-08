// Rittman Mead
// Author: Rita Santos
//
// class odi parameters
//
// loads the ODI information from a configuration file


class OdiParams {

    ConfigObject config_file; //object that access the file
    String full_path;
    String config_filename;
    
    // ODI parameters
    String url;
    String driver;
    String master_user;
    String master_pass;
	String work_user;
	String work_pass;
    String workrep;
    String odi_user;
    String odi_pass;

    // constructor
    def loadParams (path,filename){ 
      full_path = path
	  config_filename = filename
	  config_file = new ConfigSlurper().parse(new File(path + "\\" + filename).toURL())
      
	  url = config_file.Url
	  driver = config_file.Driver
	  master_user = config_file.Master_User
	  master_pass = config_file.Master_Pass
	  work_user = config_file.Work_User
	  work_pass = config_file.Work_Pass
	  workrep = config_file.WorkRep
	  odi_user = config_file.Odi_User
	  odi_pass = config_file.Odi_Pass
    }
    
	// get methods
    def get_config_filename(){
      return config_filename
    }
    
    def get_full_path(){
      return full_path
    }
    
    def get_url(){
      return url
    }
	
    def get_driver(){
      return driver
    }
	
    def get_master_user(){
      return master_user
    }
    
    def get_master_pass(){
      return master_pass
    }
 
    def get_work_user(){
      return work_user
    }
    
    def get_work_pass(){
      return work_pass
    }
 
    def get_workrep(){
      return workrep
    }
    
    def get_odi_user(){
      return odi_user
    }
    
    def get_odi_pass(){
      return odi_pass
    }
}