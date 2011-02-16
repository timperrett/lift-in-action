package sample {
package lib {

import scala.xml.{Text, NodeSeq}
import net.liftweb.common.{Box,Full,Empty,Failure}
import net.liftweb.util.NamedPF
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml,RewriteRequest,RewriteResponse,ParsePath}
import net.liftweb.sitemap.Loc
import net.liftweb.mapper.{OrderBy,Ascending,By}
import net.liftweb.textile.TextileParser
import net.liftweb.textile.TextileParser.WikiURLInfo
import sample.model.WikiEntry

/**
 * A wiki location
 *
 * @param page - the name of the page
 * @param edit - are we viewing or editing the page?
 */
case class WikiLoc(page: String, edit: Boolean) {

  /**
   * Get the underly database record for this page. 
   * When requested, the page will go to the database and look
   * for an entry who's name matches that passed as the
   * paramerter to the WikiLoc instance.
   */
  lazy val record: WikiEntry =
    WikiEntry.find(By(WikiEntry.name, page)) openOr WikiEntry.create.name(page)
}

/**
 * The WikiStuff object that provides menu, URL rewriting,
 * and snippet support for the page that displays wiki contents
 */
object Wiki extends Loc[WikiLoc] {
  object AllLoc extends WikiLoc("all", false)

  // the name of the page
  def name = "wiki"

  // the default parameters (used for generating the menu listing)
  def defaultValue = Full(WikiLoc("HomePage", false))
  
  def params = Nil
  
  /**
   * Is the current page an "edit" or "view"? 
   * This confusses most newbies, as its not always clear 
   * where these values come from.
   * As this Loc defines its own RewritePF, it added extra S.params 
   * to the incoming request, which is what this requestValue is accessing. 
   */ 
  def currentEdit = requestValue.is.map(_.edit) openOr false

  /**
   * Check for page-specific snippets and do appropriate dispatching
   */
  override val snippets: SnippetTest = {
    case ("wiki", Full(AllLoc)) => showAll _
    case ("wiki", Full(wp @ WikiLoc(_ , true))) => editRecord(wp.record) _
    case ("wiki", Full(wp @ WikiLoc(_ , false))) if !wp.record.saved_? => editRecord(wp.record) _
    case ("wiki", Full(wp: WikiLoc)) => displayRecord(wp.record) _
  }
  
  /**
   * Generate a link based on the current page
   */
  val link = new Loc.Link[WikiLoc](List("wiki"), false){
    override def createLink(in: WikiLoc) = 
      if(in.edit) Full(Text("/wiki/edit/"+urlEncode(in.page)))
      else Full(Text("/wiki/"+urlEncode(in.page)))
  }
  
  /**
   * What's the text of the link?
   */
  val text = new Loc.LinkText(calcLinkText _)
  def calcLinkText(in: WikiLoc): NodeSeq = 
    if(in.edit) Text("Wiki edit "+in.page) else Text("Wiki "+in.page)

  /**
   * Rewrite the request so that we a) get friendly URLs and
   * b) get the appropriate page and context (edit or view) params
   * in order to pass them to the WikiLoc 
   */
  override val rewrite: LocRewrite = Full(NamedPF("Wiki Rewrite"){
    case RewriteRequest(ParsePath("wiki" :: "edit" :: page :: Nil, _, _,_),_, _) =>
      (RewriteResponse("wiki" :: Nil), WikiLoc(page, true))
    case RewriteRequest(ParsePath("wiki" :: page :: Nil, _, _,_),_,_) =>
      (RewriteResponse("wiki" :: Nil), WikiLoc(page, false))
  })
  
  /**
   * Show all the pages that are part of the wiki
   */
  def showAll(in: NodeSeq): NodeSeq = WikiEntry.findAll(OrderBy(WikiEntry.name, Ascending)).flatMap(entry =>
    <div><a href={url(entry.name)}>{entry.name}</a></div>)
  
  
  /**
   * Make the URL to be accessed based up the page in the WikiLoc. 
   * The createLink method already ensures that spaces etc are encoded
   */
  def url(page: String) = createLink(WikiLoc(page, false))
  
  /**
   * The render bind for actually displaying the wiki contents. This will 
   * look just like what you are familiar with for creating snippets
   */
  def editRecord(r: WikiEntry)(xhtml: NodeSeq): NodeSeq = {
    val isNew = !r.saved_?
    val pageName = r.name.is
    <form action={url(pageName)} method="post">{
      bind("e", chooseTemplate("display","edit", xhtml),
      "message" -> (if(isNew) Text("Create Entry named '"+pageName+"'") 
          else Text("Edit entry named '"+pageName+"'")),
      "cancel" -> <a href={url(pageName)}>Cancel</a>,
      "textarea" -> r.entry.toForm,
      "submit" -> SHtml.submit(isNew ? "Add" | "Save", () => r.save)
    )}</form>
  }
  
  /** 
   * Pretty much the same as the editRecord method, but obviously
   * minus the err, editing.
   */
  def displayRecord(entry: WikiEntry)(xhtml: NodeSeq): NodeSeq = 
    bind("v",chooseTemplate("display","view", xhtml),
      "content" -> TextileParser.toHtml(entry.entry, textileWriter),
      "edit" -> <a href={createLink(WikiLoc(entry.name, true))}>Edit</a>
    )
    
  private val textileWriter = Some((info: WikiURLInfo) =>
    info match {
      case WikiURLInfo(page, _) => (stringUrl(page), Text(page), None)
    })
  
  def stringUrl(page: String) = url(page).map(_.text) getOrElse ""
  
}


}}
