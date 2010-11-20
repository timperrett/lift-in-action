package eu {
package getintheloop {
  
  import sbt._
  import org.apache.tools.ant.Project
  import org.apache.tools.ant.taskdefs.optional.Native2Ascii
  
  trait Native2AsciiPlugin extends MavenStyleScalaPaths {
    // description
    val Native2AsciiDescription = "Converts a file with native-encoded characters to one with Unicode-encoded characters."
    // command
    lazy val native2ascii = native2asciiAction
    // action
    protected def native2asciiAction = 
      task(native2asciiTask) describedAs(Native2AsciiDescription)
    // task executor
    private def native2asciiTask = {
      // make a new ant project
      var project = new Project
      project.setName("native2ascii")
      
      val task = new Native2Ascii
      task.setProject(project)
      
      task.setSrc(translationInputPath.asFile)
      task.setDest(translationOutputPath.asFile)
      task.setEncoding(encoding)
      task.setExt("."+translationOutputExtension)
      
      log.info("Encoding '." +translationInputExtension+ "' to '" +translationOutputExtension+ "' file(s) in " + translationOutputPath.relativePath)
      
      task.execute
      
      log.info("Translation complete.")
      
      None
    }
    
    /**
     * User configurable options
     */
    def encoding: String = "UTF-8"
    
    def translationInputExtension: String = "txt"
    def translationInputPath: Path = mainSourcePath / "i18n"
    
    def translationOutputExtension: String = "properties"
    def translationOutputPath: Path = mainResourcesPath
  }
  
}}