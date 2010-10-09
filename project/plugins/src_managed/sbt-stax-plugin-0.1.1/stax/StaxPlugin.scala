package stax

import sbt._
import com.staxnet.appserver.config.{AppConfig,AppConfigHelper}
import com.staxnet.appserver.utils.ZipHelper
import com.staxnet.appserver.utils.ZipHelper.ZipEntryHandler
import net.stax.api.{HashWriteProgress,StaxClient}
import java.io.{BufferedReader,File,FileInputStream,FileOutputStream,IOException,InputStream,InputStreamReader,PrintStream}
import java.util.zip.{ZipEntry,ZipOutputStream}

trait StaxPlugin extends DefaultWebProject {
  def staxApplicationId: String = ""
  def staxUsername: String = ""
  def staxPassword: String = ""
  def staxServer: String = "api.stax.net"
  /**
   * Deploy
   */
  
   // description
   val StaxDeployDescription = "Deploy your WAR to stax.net with the stax-deploy"
   // command
   lazy val staxDeploy = staxDeployAction
   // action
   protected def staxDeployAction = 
     task(staxDeployTask) dependsOn(`package`) describedAs(StaxDeployDescription)

   
   private def staxDeployTask = {
     
     // println(warPath)
     val username = 
       prompt("Stax Username:", () => staxUsername.isEmpty) getOrElse staxUsername
     val password = 
       prompt("Stax Password:", () => staxPassword.isEmpty) getOrElse staxPassword
     val appId = 
       prompt("Stax Application ID:", () => staxApplicationId.isEmpty) getOrElse staxApplicationId
     
     val apiUrl = "http://%s/api".format(staxServer)
     
     
     if(warPath.exists){
       // val appConfig = getAppConfig(this.warFile, ApplicationHelper.getEnvironmentList(this.environment, new String[0]), new String[] { "deploy" });
       val appConfig = new AppConfig()
       val defaultAppDomain = username
       val appIdParts = appId.split("/")
       var targetAppId = ""
       val domain = if(appIdParts.length > 1){
         appIdParts(0)
       } else if(!defaultAppDomain.isEmpty){
         targetAppId = defaultAppDomain + "/" + appId
         defaultAppDomain
       } else {
         log.error("Default app domain could not be determined, appid needs to be fully-qualified")
       }
       
       val environment = appConfig.getAppliedEnvironments().toArray.toList.mkString(",")       
       
       log.info("Deploying application %s (environment: %s)".format(appId,environment))
       
       val client = new StaxClient(apiUrl, username, password, "xml", "0.1");
       client.applicationDeployWar(targetAppId, 
         environment, null, 
         warPath.asFile.getAbsolutePath(), 
         null, new HashWriteProgress());
       
     } else {
       log.error("No WAR file exists to deploy to Stax")
     }
     None
   }
  
  /**
   * Utils
   */
  private def trim(s: Option[String]) = s.getOrElse("")
  private def prompt(withText: String, conditional: () => Boolean): Option[String] = 
    if(conditional()){
      Some(trim(SimpleReader.readLine("\n"+withText)))
    } else { None }
    
  
  
}
