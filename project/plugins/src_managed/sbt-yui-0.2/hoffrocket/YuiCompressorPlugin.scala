package hoffrocket
import sbt._

import com.yahoo.platform.yui.compressor.CssCompressor
import com.yahoo.platform.yui.compressor.JavaScriptCompressor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import org.mozilla.javascript.ErrorReporter
import org.mozilla.javascript.EvaluatorException

trait YuiCompressorPlugin extends DefaultWebProject{
  
  def yuiSourceFilter = "*.css" | "*.js"
  def yuiSources = descendents(webappPath, yuiSourceFilter)
  def yuiTemp = outputPath / "yui-temp"
  
  def yuiEncoding = "UTF-8"
  def yuiNomunge = false
  def yuiJswarn = false
  def yuiPreserveAllSemiColons = false
  def yuiDisableOptimizations = false
  def lineBreaks = -1
  
  def yuiProducts:Iterable[Path] = for (path <- yuiSources.get) yield Path.fromFile(yuiTemp.toString + path.toString.substring(webappPath.toString.length))
  
  lazy val yuicompressor = yuicompressorAction 
  
  def yuicompressorAction = fileTask(yuiProducts from yuiSources) {
    val errorReporter = new YuiErrorReporter()
    for(file <- yuiSources.get){
        val inFile = new File(file.toString)
        val in = new InputStreamReader(new FileInputStream(inFile), yuiEncoding)
		try {
	        val fileName = file.toString.substring(webappPath.toString.length)

	        val outFile = new File( yuiTemp.toString + fileName)

	        if (!outFile.exists || outFile.lastModified <  inFile.lastModified){
            
	            log.info("Compressing: " + fileName)
	            val extension = fileName.substring(fileName.lastIndexOf("."))
            
	            if (!outFile.getParentFile.exists && !outFile.getParentFile.mkdirs) {
	                throw new Exception( "Cannot create resource output directory: " + outFile.getParentFile() )
	            }
	            val out = new OutputStreamWriter(new FileOutputStream(outFile), yuiEncoding)
	            try {
	                if (".js".equalsIgnoreCase(extension)) {
	                    val compressor = new JavaScriptCompressor(in, errorReporter)

	                    compressor.compress(out, lineBreaks, !yuiNomunge, yuiJswarn, yuiPreserveAllSemiColons, yuiDisableOptimizations)
	                } else if (".css".equalsIgnoreCase(extension)) {
	                    val compressor = new CssCompressor(in)
	                    compressor.compress(out, lineBreaks)
	                }
	            } finally {
	                out.close
	            }
	        }
		} finally {
			in.close
		}
        
    }
    None
  } describedAs("Minify JS and CSS files with yui compressor") 
  
  class YuiErrorReporter extends ErrorReporter {
        def logit(level:Level.Value, message:String, sourceName:String, line:Int, lineSource:String, lineOffset:Int) = 
            log.log(level, "%s in %s:%d,%d at %s".format(message, sourceName,line,lineOffset, lineSource))
            
        def error(message:String, sourceName:String, line:Int, lineSource:String, lineOffset:Int) {
            logit(Level.Error, message, sourceName, line,lineSource,lineOffset)
        }

        def runtimeError(message:String, sourceName:String, line:Int, lineSource:String, lineOffset:Int):EvaluatorException = {
            error(message, sourceName, line,lineSource,lineOffset)
            new EvaluatorException(message, sourceName, line, lineSource, lineOffset)
        }

        def warning(message:String, sourceName:String, line:Int, lineSource:String, lineOffset:Int) {
            logit(Level.Warn, message, sourceName, line,lineSource,lineOffset)
        }
      
  }
  
  override def prepareWebappAction = super.prepareWebappAction dependsOn(yuicompressor) 
  override def extraWebappFiles = (yuiTemp ##) ** "*"
  override def webappResources = super.webappResources --- yuiSources
  override def watchPaths = super.watchPaths +++ yuiSources
}