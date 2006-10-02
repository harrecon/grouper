/*
 Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
 Copyright 2004-2006 The University Of Bristol

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.internet2.middleware.grouper;
import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  java.io.*;
import  java.util.*;
import  org.apache.commons.logging.*;

/**
 * Utility class for exporting data from the Groups Registry in XML format.
 * <p>
 * This class can export all-or-port of a Groups Registry as a stem/group
 * hierarchy.  Alternatively, collections of {@link Stem}s, {@link Group}s,
 * {@link Subject}s or {@link Membership}s may be exported.
 * </p>
 * <p>
 * Exported stem and group data may be imported, as is, or with modifications,
 * into the same repository or into another repository. See {@link XmlImporter}.
 * </p>
 * <p><b>The API for this class will change in future Grouper releases.</b></p>
 * @author  Gary Brown.
 * @author  blair christensen.
 * @version $Id: XmlExporter.java,v 1.50 2006-10-02 18:35:16 blair Exp $
 * @since   1.0
 */
public class XmlExporter {

  // PRIVATE CLASS CONSTANTS //  
  private static final String CF          = "export.properties"; 
  private static final Log    LOG         = LogFactory.getLog(XmlExporter.class);
  private static final String RC_EFILE    = "export.file";
  private static final String RC_NAME     = "owner.name";
  private static final String RC_PARENT   = "mystery.parent";
  private static final String RC_RELATIVE = "mystery.relative";
  private static final String RC_SUBJ     = "subject.identifier";
  private static final String RC_UPROPS   = "properties.user";
  private static final String RC_UUID     = "owner.uuid";


  // PRIVATE INSTANCE VARIABLES //
  private GrouperSession  s;
  private GroupType       baseType          = null;
  private String          fromStem          = null;
  private boolean         includeParent;
  private boolean         isRelative;
  private Properties      options;
  private Subject         sysUser;
  private int             writeStemsCounter = 0; // TODO 20061002 ???
  private XmlWriter       xml               = null;


  // CONSTRUCTORS //

  /**
   * Export the Groups Registry to XML.
   * <p>
   * The export process is configured using the follow properties.
   * </p>
   * <table width="90%" border="1">
   * <tr>
   * <td>Key</td>
   * <td>Values</td>
   * <td>Default Value</td>
   * <td>Description</td>
   * </tr>
   * <tr>
   * <td>export.metadata</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true Group type and field information as well as Subject sources will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.data</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true data will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.privs.naming</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true naming privileges will be exported along with Stems.</td>
   * </tr>
   * <tr>
   * <td>export.privs.access</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true access privileges will be exported along with Groups.</td>
   * </tr>
   * <tr>
   * <td>export.privs.immediate-only</td>
   * <td>true/false</td>
   * <td>false</td>
   * <td>If true only directly granted privileges will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.group.members</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true group memberships are exported.</td>
   * </tr>
   * <tr>
   * <td>export.group.members.immediate-only</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true only immediate group memberships will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.group.lists</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true custom list attributes will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.group.lists.immediate-only</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true only immediate list members will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.group.internal-attributes</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true system-maintained Group attributes (eg. <tt>modifyDate</tt>) will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.group.custom-attributes</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true custom attributes will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.stem.internal-attributes</td>
   * <td>true/false</td>
   * <td>true</td>
   * <td>If true system-maintained Stem attributes (eg. <tt>modifyDate</tt>) will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.privs.for-parents</td>
   * <td>true/false</td>
   * <td>false</td>
   * <td>If true and only exporting a partial hierarchy then privileges for parent stems will be exported.</td>
   * </tr>
   * <tr>
   * <td>export.subject-attributes.source.&lt;source name&gt;.&lt;subject type&gt;</td>
   * <td>Space separated list of attribute names</td>
   * <td>?</td>
   * <td>Specifices any attributes that should be exported with a Subject given the specified Source and Subject Type.</td>
   * </tr>
   * <tr>
   * <td>export.subject-attributes.source.&lt;source name&gt;</td>
   * <td>Space separated list of attribute names</td>
   * <td>?</td>
   * <td>Specifies any attributes that should be exported with a Subject given the specified Source.</td>
   * </tr>
   * <tr>
   * <td>export.subject-attributes.type.&lt;subject type&gt;</td>
   * <td>Space separated list of attribute names</td>
   * <td>?</td>
   * <td>Specifies any attributes that should be exported with a Subject given the Subject Type.</td>
   * </tr>
   * </table>
   * @param   s           Perform export within this session.
   * @param   userOptions User-specified configuration parameters.
   * @since   1.1.0
   */
  public XmlExporter(GrouperSession s, Properties userOptions) {
    try {
      this.baseType = GroupTypeFinder.find("base"); // TODO ?
    }
    catch (SchemaException eS) {
      throw new GrouperRuntimeException(eS.getMessage(), eS);
    }
    try {
      this.options  = XmlUtils.getSystemProperties(LOG, CF);
    }
    catch (IOException eIO) {
      throw new GrouperRuntimeException(eIO.getMessage(), eIO);
    }
    this.options.putAll(userOptions); 
    this.s        = s;
    this.sysUser  = SubjectFinder.findRootSubject(); // TODO ?
  } // public XmlExporter(s, userOptions)

  
  // MAIN //
  /**
   * Export Groups Registry to XML output.
   * <p/>
   * @since   1.1.0
   */
  public static void main(String args[]) {
    if (XmlUtils.wantsHelp(args)) {
      System.out.println( _getUsage() );
      System.exit(0);
    }
    Properties rc = new Properties();
    try {
      rc = _getArgs(args);
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println();
      System.err.println( _getUsage() );
      System.exit(1);
    }
    XmlExporter exporter = null;
    try {
      exporter = new XmlExporter(
        GrouperSession.start(
          SubjectFinder.findByIdentifier( rc.getProperty(RC_SUBJ) )
        ),
        XmlUtils.getUserProperties(LOG, rc.getProperty(RC_UPROPS) )
      );
      _handleArgs(exporter, rc);
      LOG.debug("Finished export to [" + rc.getProperty(RC_EFILE) + "]");
    }
    catch (Exception e) {
      LOG.fatal("unable to export to xml: " + e.getMessage());
      System.exit(1);
    }
    finally {
      if (exporter != null) {
        try {
          exporter.s.stop();
        }
        catch (SessionException eS) {
          LOG.error(eS.getMessage());
        }
      }
    }
    System.exit(0);
  } // public static void main(args)


  // PUBLIC INSTANCE METHODS //

  /**
   * Exports data for the entire repository
   * <p/>
   * @param   writer    Write XML here.
   * @throws  GrouperException
   * @since   1.1.0
   */
  public void export(Writer writer)
    throws  GrouperException
  {
    LOG.info("starting export from root stem");
    this.xml = new XmlWriter(writer);
    try {
      this._export(StemFinder.findRootStem(s), true, false);
    }
    catch (CompositeNotFoundException eCNF) {
      throw new GrouperException(eCNF.getMessage(), eCNF);
    }
    catch (GroupNotFoundException eGNF)     {
      throw new GrouperException(eGNF.getMessage(), eGNF);
    }
    catch (IOException eIO)                 {
      throw new GrouperException(eIO.getMessage(), eIO);
    }
    catch (MemberNotFoundException eMNF)    {
      throw new GrouperException(eMNF.getMessage(), eMNF);
    }
    catch (SchemaException eS)              {
      throw new GrouperException(eS.getMessage(), eS);
    }
    catch (StemNotFoundException eNSNF)     {
      throw new GrouperException(eNSNF.getMessage(), eNSNF);
    }
    catch (SubjectNotFoundException eSNF)   {
      throw new GrouperException(eSNF.getMessage(), eSNF);
    }
    LOG.debug("export complete");
  } // public void export()

  /**
   * Export a Collection of stems, groups, members, subjects or memberships
   * <p/> 
   * @param   items
   * @param   info    allows you to indicate how the Collection was generated
   * @throws  Exception
   * @since   1.1.0
   */
  public synchronized void export(Collection items, String info) 
    throws  Exception 
  {
    LOG.debug("Start export of Collection:" + info);

    this.fromStem         = "_Z";
    Date    before        = _writeHeader();
    int     counter       = 0;

    if (_optionTrue("export.data")) {
      Iterator itemsIterator = items.iterator();
      this.xml.puts("<dataList>");
      Object obj;
      while (itemsIterator.hasNext())       {
        obj = itemsIterator.next();
        counter++;
        if      (obj instanceof Group)      {
          this._writeFullGroup( (Group) obj);
        } 
        else if (obj instanceof Stem)       {
          Stem stem = (Stem) obj;
          this._writeBasicStemHeader(stem);
          this._writeInternalAttributes(stem);
          this._writeStemPrivs(stem);
          this._writeBasicStemFooter(stem);
        } 
        else if (obj instanceof Subject)    {
          if (counter == 1) {
            this.xml.puts("<exportOnly/>");
          }
          this._writeSubject( (Subject) obj);
        } 
        else if (obj instanceof Member)     {
          if (counter == 1) {
            this.xml.puts("<exportOnly/>");
          }
          this._writeSubject( ((Member) obj).getSubject());
        } 
        else if (obj instanceof Membership) {
          if (counter == 1) {
            this.xml.puts("<exportOnly/>");
          }
          this._writeMembership( (Membership) obj);
        } 
        else {
          LOG.error("Don't know about exporting " + obj);
        }
        this.xml.puts();
      }
      this.xml.puts("</dataList>");
    }
    this.xml.puts("<exportComments><![CDATA[");
    this.xml.puts(info);
    this.xml.puts("]]></exportComments>");
    this._writeFooter(before);
    LOG.debug("Finished export of Collection:" + info);
  } // public synchronized void export(items, info)

  /**
   * Export a single group
   * <p/>
   * @param   group
   * @param   relative  determines whether to export parent stems
   * @throws  Exception
   * @since   1.1.0
   */
  public void export(Group group, boolean relative) 
    throws  Exception 
  {
    LOG.debug("Start export of Group " + group.getName());
    this._export(group, relative, false);
    LOG.debug("Finished export of Group " + group.getName());
  } // public void export( group, relative)

  /**
   * Exports part of the repository
   * <p/> 
   * @param   stem          where to export from
   * @param   relative      determines whether to export parent stems
   * @param   includeParent should 'stem' be included or just the children
   * @throws  Exception
   * @since   1.1.0
   */
  public void export(Stem stem, boolean relative, boolean includeParent) 
    throws  Exception 
  {
    LOG.debug("Start export of Stem " + stem.getName());
    _export(stem, relative, includeParent);
    LOG.debug("Finished export of Stem " + stem.getName());
  } // public void export(stem, relative, includeParent)


  // PROTECTED CLASS METHODS //

  // @since   1.0
  protected static boolean hasImmediatePrivilege(
    Subject subject, Group group, String privilege
  ) 
  {
    Iterator  privIterator  = null;
    Set       privs         = null;

    privs = group.getPrivs(subject);
    AccessPrivilege aPriv;
    privIterator = privs.iterator();
    while (privIterator.hasNext()) {
      aPriv = (AccessPrivilege) privIterator.next();
      if (
          aPriv.getName().equals(privilege)
          && aPriv.getOwner().equals(subject)
      ) 
      {
        return true;
      }
    }
    return false;

  } // protected static boolean hasImmediatePrivilege(subject, group, privilege)

  // @since   1.0
  protected static boolean hasImmediatePrivilege(Subject subject, Owner o, String privilege) 
  {
    if (o instanceof Group) {
      return hasImmediatePrivilege(subject, (Group) o, privilege);
    }
    return hasImmediatePrivilege(subject, (Stem) o, privilege);
  } // protected static boolean hasImmediatePrivilege(subject, o, privilege)

  // @since   1.0
  protected static boolean hasImmediatePrivilege(
    Subject subject, Stem stem, String privilege
  ) 
  {
    Iterator  privIterator  = null;
    Set       privs         = null;

    privs = stem.getPrivs(subject);
    NamingPrivilege nPriv;
    privIterator = privs.iterator();
    while (privIterator.hasNext()) {
      nPriv = (NamingPrivilege) privIterator.next();
      if (
          nPriv.getName().equals(privilege)
          && nPriv.getOwner().equals(subject)
      ) 
      {
        return true;
      }
    }
    return false;
  } // protected static boolean hasImmediatePrivilege(subject, stem, privilege)


  // PROTECTED INSTANCE METHODS //

  // @since   1.1.0
  protected Properties getOptions() {
    return (Properties) this.options.clone();
  } // protected Properties getOptions()


  // PRIVATE CLASS METHODS //

  // @since   1.1.0
  private static Properties _getArgs(String args[])
    throws  IllegalArgumentException,
            IllegalStateException
  {
    Properties rc = new Properties();

    String  arg;
    int     inputPos  = 0;
    int     pos       = 0;

    while (pos < args.length) {
      arg = args[pos];
      if (arg.startsWith("-")) {
        if (arg.equals("-id")) {
          if (rc.getProperty(RC_NAME) != null) {
            throw new IllegalArgumentException(XmlUtils.E_NAME_AND_UUID);
          }
          rc.setProperty(RC_UUID, args[pos + 1]);
          pos += 2;
          continue;
        } 
        else if (arg.equals("-name")) {
          if (rc.getProperty(RC_UUID) != null) {
            throw new IllegalArgumentException(XmlUtils.E_NAME_AND_UUID);
          }
          rc.setProperty(RC_NAME, args[pos + 1]);
          pos += 2;
          continue;
        } 
        else if (arg.equals("-relative")) {
          rc.setProperty(RC_RELATIVE, "true");
          pos++;
          continue;
        } 
        else if (arg.equalsIgnoreCase("-includeparent")) {
          rc.setProperty(RC_PARENT, "true");
          pos++;
          continue;
        } else {
          throw new IllegalArgumentException(XmlUtils.E_UNKNOWN_OPTION + arg);
        }
      }
      switch (inputPos) {
      case 0:
        rc.setProperty(RC_SUBJ, arg);
        break;
      case 1:
        rc.setProperty(RC_EFILE, arg);
        break;
      case 2:
        rc.setProperty(RC_UPROPS, arg);
        break;
      case 3:
        throw new IllegalArgumentException("Too many arguments - " + arg);
      }
      pos++;
      inputPos++;
    }
    if (inputPos < 1) {
      throw new IllegalStateException("Too few arguments");
    }
    return rc;
  } // private static Properties _getArgs(args)
    
  // @since   1.1.0
  private static Set _getListFieldsForGroup(Group g)
    throws  SchemaException 
  {
    Set       lists   = new LinkedHashSet();
    Field     defList = Group.getDefaultList();
    Field     f;
    GroupType type;
    Iterator  iterF;
    Iterator  iter    = g.getTypes().iterator();
    while (iter.hasNext()) {
      type  = (GroupType) iter.next();
      iterF = type.getFields().iterator();
      while (iterF.hasNext()) { 
        f = (Field) iterF.next();
        if (
              f.getType().equals(FieldType.LIST)
          &&  !f.equals(defList)
          &&  g.canReadField(f)
        )
        {
          lists.add(f.getName());
        }
      }
    }
    return lists;
  } // private static Set _getListFieldsForGroup(group)
  
  // @since   1.1.0
  private static String _getUsage() {
    return  "Usage:"                                                                + GrouperConfig.NL
            + "args: -h,            Prints this message"                            + GrouperConfig.NL
            + "args: subjectIdentifier [(-id <id>] | [-name <name>)] [-relative]"   + GrouperConfig.NL
            + "      [-includeParent] fileName [properties]"                        + GrouperConfig.NL
            +                                                                         GrouperConfig.NL
            + "  subjectIdentifier, Identifies a Subject 'who' will create a"       + GrouperConfig.NL
            + "                     GrouperSession"                                 + GrouperConfig.NL
            + "  -id,               The UUID of a Group or Stem to export"          + GrouperConfig.NL
            + "  -name,             The name of a Group or Stem to export"          + GrouperConfig.NL
            + "  -relative,         If id or name specified do not export parent"   + GrouperConfig.NL
            + "                     Stems"                                          + GrouperConfig.NL
            + "  -includeParent,    If id or name identifies a Stem export this"    + GrouperConfig.NL
            + "                     stem and child Stems or Groups"                 + GrouperConfig.NL
            + "  filename,          The file where exported data will be written."  + GrouperConfig.NL
            + "                     Will overwrite existing files"                  + GrouperConfig.NL
            + "  properties,        The name of a standard Java properties file "   + GrouperConfig.NL
            + "                     which configures the export. Check Javadoc for" + GrouperConfig.NL
            + "                     a list of properties. If 'properties' is not "  + GrouperConfig.NL
            + "                     specified, XmlExporter will look for "          + GrouperConfig.NL
            + "                     'export.properties' in the working directory. " + GrouperConfig.NL
            + "                     If this file does not exist XmlExporter will "  + GrouperConfig.NL
            + "                     look on the classpath. If 'properties' is not " + GrouperConfig.NL
            + "                     specified and 'export.properties' cannot be "   + GrouperConfig.NL
            + "                     found, the export will fail."                   + GrouperConfig.NL
            ;
  } // private static String _getUsage()

  // @since   1.1.0
  private static void _handleArgs(XmlExporter exporter, Properties rc) 
    throws  Exception
  {
    if (rc.getProperty(RC_UUID) == null && rc.getProperty(RC_NAME) == null) {
      exporter.export( new PrintWriter( new FileWriter( rc.getProperty(RC_EFILE) ) ) );
    } 
    else {
      Group group = null;
      Stem  stem  = null;
      if (rc.getProperty(RC_UUID) != null) {
        String uuid = rc.getProperty(RC_UUID);
        try {
          group = GroupFinder.findByUuid(exporter.s, uuid);
          LOG.debug("Found group with uuid [" + uuid + "]");
        } 
        catch (GroupNotFoundException eGNF) {
          // Look for stem instead
          try {
            stem = StemFinder.findByUuid(exporter.s, uuid);
            LOG.debug("Found stem with uuid [" + uuid + "]");
          } 
          catch (StemNotFoundException eNSNF) {
            throw new IllegalArgumentException(
              "Could not find group or stem with uuid [" + uuid + "]"
            );
          }
        }
      } 
      else {
        String name = rc.getProperty(RC_NAME);
        try {
          group = GroupFinder.findByName(exporter.s, name);
          LOG.debug("Found group with name [" + name + "]");
        } 
        catch (GroupNotFoundException eGNF) {
          // Look for stem instead
          try {
            stem = StemFinder.findByName(exporter.s, name);
            LOG.debug("Found stem with name [" + name + "]");
          } catch (StemNotFoundException eNSNF) {
            // No group or stem
            throw new IllegalArgumentException(
              "Could not find group or stem with name [" + name + "]"
            );
          }
        }
      }
      if (group != null) {
        exporter.export(
          group,
          Boolean.getBoolean(rc.getProperty(RC_RELATIVE))
        );
      } 
      else {
        exporter.export(
          stem,   
          Boolean.getBoolean(rc.getProperty(RC_RELATIVE)),
          Boolean.getBoolean(rc.getProperty(RC_PARENT))
        );
      }
    }
  } // private static void _handleArgs(exporter, rc)


  // PRIVATE INSTANCE METHODS //

  // @since   1.1.0
  private synchronized void _export(Owner o, boolean relative, boolean includeParent)
    throws  CompositeNotFoundException,
            GrouperException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            StemNotFoundException,
            SubjectNotFoundException
  {
    LOG.debug("Relative export="     + relative);
    LOG.debug("Include parent stem=" + includeParent);
    this.isRelative         = relative;
    this.includeParent      = includeParent;
    this.writeStemsCounter  = 0;
    Date    before          = _writeHeader();
    if (!relative) {
      fromStem = null;
    }
    if (relative) {
      Stem dummyStem = null;
      if      (includeParent || o instanceof Group) {
        if (o instanceof Group) {
          dummyStem = ( (Group) o ).getParentStem();
        } 
        else {
          dummyStem = ( (Stem) o ).getParentStem();
        }
      } 
      else if (!includeParent) {
        dummyStem = (Stem) o;
      }
      fromStem = dummyStem.getName() + ":";
    }

    if (this._optionTrue("export.data")) {
      this._exportData(o);
    } 
    else {
      LOG.debug("export.data=false, so no data exported");
    }
    this._writeExportParams(o);
    _writeFooter(before);
  } // private synchronized void _export(o, relative, includeParent)

  // @since   1.1.0
  private void _exportData(Owner o) 
    throws  CompositeNotFoundException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            StemNotFoundException,
            SubjectNotFoundException
  {
    this.xml.indent();
    this.xml.puts("<data>");
    // TODO 20061002 refactor out to own method
    Stack stems = null;
    if (!isRelative) {
      stems = this._getParentStems(o);
    } 
    else {
      stems = new Stack();
      if (o instanceof Group) {
        stems.push( (Group) o);
        if (includeParent) {
          stems.push( ( (Group) o ).getParentStem());
        }
      } 
      else {
        stems.push( (Stem) o);
      }
    }
    this._writeStems(stems);
    this.xml.puts("</data>");
    this.xml.undent();
    LOG.debug("Finished repository data as XML");
  } // private void _exportData(o)

  // @since   1.0
  private String _fixGroupName(String name) {
    if (fromStem != null && name.startsWith(fromStem)) {
      name = name.replaceAll("^" + fromStem, XmlUtils.SPECIAL_STAR);
    }
    return name;
  } // private String _fixGroupName(name)

  // @since   1.0
  private Iterator _getExportAttributes(Subject subj) {
    String source = subj.getSource().getId();
    String type   = subj.getType().getName();
    String key    = "export.subject-attributes.source." + source + "." + type;
    String value  = options.getProperty(key);
    if (XmlUtils.isEmpty(value)) {
      key   = "export.subject-attributes.source." + source;
      value = options.getProperty(key);
    }
    if (XmlUtils.isEmpty(value)) {
      key   = "export.subject-attributes.type." + type;
      value = options.getProperty(key);
    }
    if (XmlUtils.isEmpty(value)) {
      return null;
    }
    if (XmlUtils.SPECIAL_STAR.equals(value)) {
      return subj.getAttributes().keySet().iterator();
    }
    StringTokenizer st  = new StringTokenizer(value);
    Set             res = new LinkedHashSet();
    while (st.hasMoreTokens()) {
      res.add(st.nextToken());
    }
    return res.iterator();
  } // private Iterator _getExportAttributes(subj)

  // @since   1.1.0
  private String _fixXmlAttribute(String value) {
    value = value.replaceAll("'", "&apos;");
    value = value.replaceAll("<", "&lt;");
    value = value.replaceAll(">", "&gt;");
    return value;
  } // private String _fixXmlAttribute(value)

  // @since   1.1.0
  private Stack _getParentStems(Owner o) 
  {
    Stem  startStem = null;
    Stack stems     = new Stack();
    if (o instanceof Group) {
      Group group = (Group) o;
      stems.push(group);
      startStem = group.getParentStem();
    } 
    else {
      startStem = (Stem) o;
    }
    stems.push(startStem);
    Stem parent = startStem;
    do {
      try {
        parent = parent.getParentStem();
        if (XmlUtils.isEmpty(parent.getExtension())) {
          parent = null;
        } 
        else {
          stems.push(parent);
        }
      } 
      catch (StemNotFoundException e) {
        parent = null;
      }
    } 
    while (parent != null);
    return stems;
  } // private Stack _getParentStems(o)

  // @since   1.1.0
  private boolean _optionTrue(String key) {
    if (XmlUtils.isEmpty(key)) {
      options.setProperty(key, "false");
      return false;
    }
    return "true".equals(options.getProperty(key));
  } // private boolean _optionTrue(key)

  // @since   1.1.0
  private void _writeBasicStemFooter(Stem stem) 
    throws  IOException
  {
    this.xml.puts("</stem>");
    this.xml.puts("<!--/" + stem.getName() + "-->");
    this.xml.puts();
  } // private void _writeBasicStemFooter(stem)

  // @since   1.1.0
  private void _writeBasicStemHeader(Stem stem) 
    throws  IOException
  {
    this.xml.indent();
    this.xml.puts();
    this.xml.puts( this.xml.comment( U.q( stem.getName() ) ) );
    this.xml.puts("<stem extension="  + U.q( this._fixXmlAttribute(stem.getExtension()) )         );
    this.xml.indent();
    this.xml.puts("displayExtension=" + U.q( this._fixXmlAttribute(stem.getDisplayExtension()) )  );
    this.xml.puts("name="             + U.q( this._fixXmlAttribute(stem.getName()) )              );
    this.xml.puts("displayName="      + U.q( this._fixXmlAttribute(stem.getDisplayName()) )       );
    this.xml.puts("id="               + U.q( this._fixXmlAttribute(stem.getUuid()) )              );
    this.xml.undent();
    this.xml.puts(">");
    this.xml.indent();
    this.xml.puts("<description>" + this._fixXmlAttribute(stem.getDescription()) + "</description>");
    this.xml.undent();
    // Don't fully undent
  } // private void _writeBasicStemHeader(stem)

  // @since   1.1.0
  private void _writeComposite(Composite comp) 
    throws  GroupNotFoundException,
            IOException
  {
    this.xml.puts("<composite>");
    _writeGroupRef(comp.getLeftGroup());
    this.xml.puts();
    this.xml.puts(
      "<compositeType>" + comp.getType().toString() + "</compositeType>"
    );
    this.xml.puts();
    _writeGroupRef(comp.getRightGroup());
    this.xml.puts("</composite>");
  } // private void _writeComposite(comp)

  // @since   1.1.0
  private void _writeExportParams(Owner o)
    throws  IOException
  {
    LOG.debug("Writing export params to XML");
    this.xml.puts("<exportParams>");
    this.xml.indent();
    String s = "<node type='";
    if (o instanceof Group) {
      s += "group'>";
    }
    else {
      s += "stem'>";
    }
    this.xml.puts( s + o.getName() + "</node>" );
    this.xml.puts("<relative>" + isRelative + "</relative>");
    if (o instanceof Stem) {
      this.xml.puts("<includeParent>" + includeParent + "</includeParent>");
    }
    this.xml.undent();
    this.xml.puts("</exportParams>");
  } // private void _writeExportParams(o)

  // @since   1.1.0
  private void _writeFieldMetaData(Field f) 
    throws  IOException
  {
    this.xml.indent();
    this.xml.puts("<field name="  + U.q( this._fixXmlAttribute(f.getName()))  );
    this.xml.indent();
    this.xml.puts("required="     + U.q( f.getRequired() )                    );
    this.xml.puts("type="         + U.q( f.getType().toString() )             );
    this.xml.puts("readPriv="     + U.q( f.getReadPriv().toString() )         );
    this.xml.puts("writePriv="    + U.q( f.getWritePriv().toString() )        );
    this.xml.undent();
    this.xml.puts("/>");
    this.xml.undent();
  } // private void _writeFieldMetaData(f)

  // @since   1.1.0
  private synchronized void _writeFooter(Date before)
    throws  IOException
  {
    LOG.debug("Writing XML Footer");
    Date    now       = new Date();
    long    duration  = (now.getTime() - before.getTime()) / 1000;
    this.xml.puts();
    this.xml.puts("<exportInfo>");
    this.xml.puts("<start>" + before + "</start>");
    this.xml.puts("<end>" + now + "</end>");
    this.xml.puts("<duration>" + duration + "</duration>");
    _writeOptions();
    this.xml.puts("</exportInfo>");
    this.xml.puts("</registry>");
    this.xml.close();
  } // private synchronized _writeFooter(before)

  // @since 1.1.0
  private void _writeFullGroup(Group group) 
    throws  CompositeNotFoundException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            SubjectNotFoundException
  {
    LOG.debug("Writing group " + group.getName() + " to XML");
    this.xml.puts();
    // This was in a try/catch that ignored `Exception`.
    this.xml.puts("<!--" + group.getName() + "-->");
    this.xml.puts(
      "<group extension='" + this._fixXmlAttribute(group.getExtension()) + "'"
    );
    this.xml.put("       ");
    this.xml.puts(
      "displayExtension='" + this._fixXmlAttribute(group.getDisplayExtension()) + "'"
    );
    this.xml.put("      ");
    this.xml.puts("name='" + this._fixXmlAttribute(group.getName()) + "'");
    this.xml.put("      ");
    this.xml.puts(
      "displayName='" + this._fixXmlAttribute(group.getDisplayName()) + "'"
    );
    this.xml.put("      ");
    this.xml.puts("id='" + this._fixXmlAttribute(group.getUuid()) + "'>");

    this.xml.puts(
      "<description>" + this._fixXmlAttribute(group.getDescription()) + "</description>"
    );

    if (_optionTrue("export.group.internal-attributes")) {
      this._writeInternalAttributes(group);
    }
    if (_optionTrue("export.group.custom-attributes")) {
      Set       types     = group.getTypes();
      types.remove(this.baseType);
      if (!types.isEmpty()) {
        this.xml.puts("  <groupTypes>");
        Iterator  typesIterator = types.iterator();
        GroupType groupType;
        while (typesIterator.hasNext()) {
          groupType = (GroupType) typesIterator.next();
          this._writeGroupType(group, groupType);
        }
        this.xml.puts("  </groupTypes>");
      }
    }

    List listFields = new ArrayList();
    if (_optionTrue("export.group.lists")) {
      listFields.addAll( _getListFieldsForGroup(group) );
    }
    if (_optionTrue("export.group.members")) {
      listFields.add(0, "members");
    }

    for (int i = 0; i < listFields.size(); i++) {
      LOG.debug("Writing list members for " + group.getName() + ": field=" + listFields.get(i));
      _writeListField(
        group, FieldFinder.find((String) listFields .get(i))
      );
    }
    if (_optionTrue("export.privs.access")) {
      _writePrivileges("admin" , group.getAdmins()   , group);
      _writePrivileges("update", group.getUpdaters() , group);
      _writePrivileges("read"  , group.getReaders()  , group);
      _writePrivileges("view"  , group.getViewers()  , group);
      _writePrivileges("optin" , group.getOptins()   , group);
      _writePrivileges("optout", group.getOptouts()  , group);
    }
    this.xml.puts();
    this.xml.puts("</group>");
    // This was in a try/catch that ignored `Exception`.
    this.xml.puts("<!--/" + group.getName() + "-->");
    this.xml.puts();
    LOG.debug("Finished writing group " + group.getName() + " to XML");
  } // private void _writeFullGroup(group)

  // @since   1.1.0
  private void _writeFullStem(Stem stem) 
    throws  CompositeNotFoundException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            StemNotFoundException,
            SubjectNotFoundException
  {
    LOG.debug("Writing Stem " + stem.getName() + " to XML");
    _writeBasicStemHeader(stem);
    this._writeInternalAttributes(stem);
    _writeStemPrivs(stem);
    _writeStemBody(stem);
    _writeBasicStemFooter(stem);
    LOG.debug("Finished writing Stem " + stem.getName() + " to XML");
  } // private void _writeFullStem(stem)

  // @since   1.1.0
  private void _writeGroupRef(Group group) 
    throws  IOException
  {
    _writeGroupRef(group, false);
  } // private void _writeGroupRef(group)

  // @since   1.1.0
  private void _writeGroupRef(Group group, boolean writeAbsoluteName) 
    throws  IOException
  {
    this.xml.puts("<groupRef id='" + group.getUuid() + "'");
    String name = group.getName();
    if (!writeAbsoluteName) {
      name = this._fixGroupName(name);
    }
    this.xml.puts("        name='" + name + "'");
    this.xml.puts(" displayName='" + group.getDisplayName() + "'/>");
  } // private void _writeGroupRef(group, writeAbsoluteName)

  // @since   1.1.0
  private void _writeGroupType(Group group, GroupType groupType)
    throws  IOException,
            SchemaException
  {
    this.xml.puts(
      "<groupType name='" + this._fixXmlAttribute(groupType.getName()) + "'>"
    );
    Field     field;
    Set       fields          = groupType.getFields();
    Iterator  fieldsIterator  = fields.iterator();
    String    value;
    while (fieldsIterator.hasNext()) {
      field = (Field) fieldsIterator.next();
      if (field.getType().equals(FieldType.LIST)) {
        continue;
      }
      if (!group.canReadField(field)) {
        continue;
      }
      try {
        value = this._fixXmlAttribute(group.getAttribute(field.getName()));
        if (
            !XmlUtils.isEmpty(value)
            && ":description:extension:displayExtension:"
                .indexOf(":" + field.getName() + ":") == -1
        ) 
        {
          this.xml.puts(
            "<attribute name='"
            + this._fixXmlAttribute(field.getName()) + "'>" + value
            + "</attribute>"
          );
        }
      }
      catch (AttributeNotFoundException eANF) {
        LOG.error(eANF.getMessage());
      }
    }
    this.xml.puts("</groupType>");
  } // private void _writeGroupType(group, groupType)

  // @since   1.1.0
  private void _writeInternalAttributes(Group g) 
    throws  IOException,
            SubjectNotFoundException
  {
    this.xml.indent();
    this.xml.puts("<internalAttributes>");
    this._writeInternalAttribute( "parentStem"    , g.getParentStem().getName() );
    this._writeInternalAttribute( "createSource"  , g.getCreateSource()         );
    this._writeInternalAttribute( "createSubject" , g.getCreateSubject()        );
    this._writeInternalAttribute( "createTime"    , g.getCreateTime()           );
    this._writeInternalAttribute( "modifySource"  , g.getModifySource()         );
    this._writeInternalAttribute( "modifySubject" , g.getModifySubject()        );
    this._writeInternalAttribute( "modifyTime"    , g.getModifyTime()           );
    this.xml.puts("</internalAttributes>");
    this.xml.undent();
  } // private void _writeInternalAttributes(g)

  // @since   1.1.0
  private void _writeGroupTypesMetaData()
    throws  IOException 
  {
    Set types = GroupTypeFinder.findAll();
    if (types.isEmpty()) {
      return;
    }
    this.xml.indent();
    this.xml.puts("<groupTypesMetaData>");
    Iterator  itF;
    GroupType gt;
    Iterator  itGT = types.iterator();
    while (itGT.hasNext()) {
      gt = (GroupType) itGT.next();
      this.xml.indent();
      this.xml.puts("<groupTypeDef name=" + U.q( this._fixXmlAttribute(gt.getName()) ) + ">");
      itF = gt.getFields().iterator();
      while (itF.hasNext()) {
        this._writeFieldMetaData( (Field) itF.next() );
      }
      this.xml.puts("</groupTypeDef>");
      this.xml.undent();
    }
    this.xml.puts("</groupTypesMetaData>");
    this.xml.undent();
  } // private void _writeGroupTypesMetaData()

  // @since   1.1.0
  private synchronized Date _writeHeader()
    throws  GrouperException,
            IOException 
  {
    LOG.debug("Writing XML header");
    Date    before  = new Date();
    this.xml.puts("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    this.xml.puts("<registry>");
    if (_optionTrue("export.metadata")) {
      this._writeMetaData();
    }
    return before;
  } // private synchronized Date _writeHeader()

  // @since   1.1.0
  private void _writeInternalAttribute(String attr, Date d)
    throws  IOException
  {
    this.xml.indent();
    this.xml.puts(
        "<internalAttribute name=" + U.q(attr) + ">" 
      + Long.toString( d.getTime() ) 
      + "</internalAttribute> " 
      + this.xml.comment( d.toString() )
    ); 
    this.xml.undent();
  } // private void _writeInternalAttribute(attr, d)

  // @since   1.1.0
  private void _writeInternalAttribute(String attr, String val)
    throws  IOException
  {
    this.xml.indent();
    this.xml.puts(
        "<internalAttribute name=" + U.q(attr) + ">" 
      + this._fixXmlAttribute(val) + "</internalAttribute>"
    ); 
    this.xml.undent();
  } // private void _writeInternalAttribute(attr, val)

  // @since   1.1.0
  private void _writeInternalAttribute(String attr, Subject subj) 
    throws  IOException
  {
    String  idAttr  = "id";
    String  id      = subj.getId();
    if (subj.getType().getName().equals("group")) {
      idAttr  = "identifier";
      id      = this._fixGroupName(subj.getName());
    }
    String  txt   = 
        "<subject " + idAttr + "=" + U.q( this._fixXmlAttribute(id) )
      + " type="    + U.q( this._fixXmlAttribute(subj.getType().getName()) )
      + " source="  + U.q( this._fixXmlAttribute(subj.getSource().getId()) );
    if (idAttr.equals("identifier")) {
      txt += " id=" + U.q( subj.getId() );
    }
    txt += "/>";
    this.xml.indent();
    this.xml.puts("<internalAttribute name=" + U.q(attr) + ">");
    this.xml.indent();
    this.xml.puts(txt);
    this.xml.undent();
    this.xml.puts("</internalAttribute>");
    this.xml.undent();
  } // private void _writeInternalAttribute(attr, subj, comment)

  // @since   1.1.0
  private void _writeInternalAttributes(Stem ns)
    throws  IOException,
            StemNotFoundException,
            SubjectNotFoundException
  {
    if (!this._optionTrue("export.stem.internal-attributes")) {
      return;
    }
    this.xml.indent();
    this.xml.puts("<internalAttributes>");
    this._writeInternalAttribute( "parentStem"    , ns.getParentStem().getName()  );
    this._writeInternalAttribute( "createSource"  , ns.getCreateSource()          );
    this._writeInternalAttribute( "createSubject" , ns.getCreateSubject()         );
    this._writeInternalAttribute( "createTime"    , ns.getCreateTime()            );
    this._writeInternalAttribute( "modifySource"  , ns.getModifySource()          );
    this._writeInternalAttribute( "modifySubject" , ns.getModifySubject()         );
    this._writeInternalAttribute( "modifyTime"    , ns.getModifyTime()            );
    this.xml.puts("</internalAttributes>");
    this.xml.undent();
  } // private void _writeInternalAttributes(stem)

  // @since   1.1.0
  private void _writeListField(Group group, Field field) 
    throws  CompositeNotFoundException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            SubjectNotFoundException
  {
    if (!group.canReadField(field)) {
      LOG.debug(
        "No read privilege. List [" + field.getName() + "] for ["
        + group.getName() + "] ignored"
      );
      return;
    }
    boolean isComposite = false;
    Set     membersSet = null;
    if ("members".equals(field.getName()) && group.hasComposite()) {
      isComposite = true;
      membersSet  = new HashSet();
    } 
    else {
      membersSet = group.getImmediateMemberships(field);
    }
    Collection members = new ArrayList();
    members.addAll(membersSet);
    if (
      (
        "members".equals(field.getName()) && !_optionTrue("export.group.members.immediate-only")
      )
      || 
      (
        !"members".equals(field.getName()) && !_optionTrue("export.group.lists.immediate-only")
      )
    ) 
    {
      members.addAll(group.getEffectiveMemberships(field));
      if ("members".equals(field.getName()) && group.hasComposite()) {
        members.addAll(group.getCompositeMemberships());
      }
    }

    if (members.isEmpty() && !isComposite) {
      return;
    }
    this.xml.puts();
    this.xml.puts(
      "<list field='" + this._fixXmlAttribute(field.getName())
      + "'  groupType='"
      + this._fixXmlAttribute(field.getGroupType().getName()) + "'>"
    );
    if (isComposite) {
      Composite composite = CompositeFinder.findAsOwner(group);
      _writeComposite(composite);
    }
    _writeMembers(members, group, field);
    this.xml.puts(
      "</list> <!--/field=" + this._fixXmlAttribute(field.getName()) + "-->"
    );
  } // private void _writeListField(group, field)

  // @since   1.1.0
  private void _writeMembers(Collection members, Group group, Field field)
    throws  IOException,
            MemberNotFoundException,
            SubjectNotFoundException
  {
    boolean     isImmediate;
    Iterator    it = members.iterator();
    Membership  member;
    Subject     subj;
    while (it.hasNext()) {
      isImmediate = false;
      member      = (Membership) it.next();
      try {
        isImmediate = member.getViaGroup() == null;
      }   
      catch (GroupNotFoundException e) {
        if (!group.hasComposite()) {
          isImmediate = true;
        }
      }
      subj = member.getMember().getSubject();
      this._writeSubject(subj, " immediate='" + isImmediate + "' ");
    }
  } // private void _writeMembers(members, group, field)

  // @since   1.1.0
  private void _writeMembership(Membership membership) 
    throws  GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SubjectNotFoundException
  {
    boolean isImmediate = true;
    // How do composites fit in here?
    if (membership.getMship_type().equals(MembershipType.E)) {
      isImmediate = false;
    }

    this.xml.puts("<membership>");
    this.xml.puts("<depth>" + membership.getDepth() + "</depth>");
    this.xml.puts("<listName>" + membership.getList().getName() + "</listName>");
    this.xml.puts("<immediate>" + isImmediate + "</immediate>");
    _writeGroupRef(membership.getGroup(), true);
    this._writeSubject(membership.getMember().getSubject());
    this.xml.puts("</membership>");
  } // private void _writeMembership(membership)

  // @since   1.1.0
  private void _writeMetaData()
    throws  GrouperException,
            IOException 
  {
    this.xml.indent();
    this.xml.puts("<metadata>");
    this._writeGroupTypesMetaData();
    this.xml.puts();
    this._writeSubjectSourcesMetaData();
    this.xml.puts("</metadata>");
    this.xml.undent();
  } // private void _writeMetaData()

  // @since   1.1.0
  private void _writeOptions() 
    throws  IOException
  {
    LOG.debug("Writing export options as XML");
    this.xml.puts("<options>");
    List      orderedList     = new ArrayList(options.keySet());
    Collections.sort(orderedList);
    Iterator  optionsIterator = orderedList.iterator();

    String key;
    while (optionsIterator.hasNext()) {
      key = (String) optionsIterator.next();
      this.xml.puts(
        "<option key='" + key + "'>"
        + options.getProperty(key) + "</option>"
      );
    }
    this.xml.puts("</options>");
  } // private void _writeOptions()

  // @since   1.1.0
  private void _writePrivileges(String privilege, Set subjects, Owner o)
    throws  IOException,
            MemberNotFoundException
  {
    if (subjects.size() == 1) {
      subjects.remove(sysUser);
    }
    if (subjects.isEmpty()) {
      LOG.debug("No privilegees with [" + privilege + "] for " + o.getName());
      return;
    }

    LOG.debug("Writing privilegees with [" + privilege + "] for " + o.getName());
    this.xml.puts();

    this.xml.puts("<privileges type='" + privilege + "'>");
    Iterator  subjIterator  = subjects.iterator();
    Subject   subject;
    boolean   isImmediate   = false;

    while (subjIterator.hasNext()) {
      subject     = (Subject) subjIterator.next();
      isImmediate = hasImmediatePrivilege(subject, o, privilege);
      if (
        (!"GrouperSystem".equals(subject.getId()))
        && 
        (isImmediate || !_optionTrue("export.privs.immediate-only"))) 
      {
        this._writeSubject(subject, " immediate='" + isImmediate + "' ");
      }
    }
    this.xml.puts("</privileges> <!--/privilege=" + privilege + "-->");

  } // private void _writePrivileges(privilege, subjects, o)

  // @since   1.1.0
  private void _writeStemBody(Stem stem) 
    throws  CompositeNotFoundException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            StemNotFoundException,
            SubjectNotFoundException
  {
    Stem      childStem;
    Set       stems         = stem.getChildStems();
    Iterator  stemsIterator = stems.iterator();
    while (stemsIterator.hasNext()) {
      childStem = (Stem) stemsIterator.next();
      _writeFullStem(childStem);
    }

    Set       groups          = stem.getChildGroups();
    Iterator  groupsIterator  = groups.iterator();
    Group childGroup;
    while (groupsIterator.hasNext()) {
      childGroup = (Group) groupsIterator.next();
      _writeFullGroup(childGroup);
    }
  } // private void _writeStemBody(stem)

  // @since   1.1.0
  private void _writeStemPrivs(Stem stem) 
    throws  IOException,
            MemberNotFoundException
  {
    if (
        _optionTrue("export.privs.naming") 
       ) 
    {
      LOG.debug("Writing STEM privilegees for " + stem.getName());
      _writePrivileges("stem", stem.getStemmers(), stem);
      _writePrivileges("create", stem.getStemmers(), stem);
      LOG.debug("Writing CREATE privilegees for " + stem.getName());
    } 
    else {
      LOG.debug("Skipping naming privs for " + stem.getName());
    }
  } // private void _writeStemPrivs(stem)

  // @since   1.1.0
  private void _writeStems(Stack stems) 
    throws  CompositeNotFoundException,
            GroupNotFoundException,
            IOException,
            MemberNotFoundException,
            SchemaException,
            StemNotFoundException,
            SubjectNotFoundException
  {
    this.writeStemsCounter++;
    Object obj = stems.pop();
    if (obj instanceof Group) {
      this._writeFullGroup( (Group) obj );
      return;
    }

    Stem stem = (Stem) obj;

    if (stems.isEmpty()) {
      if (includeParent || this.writeStemsCounter > 1) {
        this._writeFullStem(stem);
      } 
      else {
        this._writeStemBody(stem);
      }
      return;
    } 
    else {
      this._writeBasicStemHeader(stem);
      if(this._optionTrue("export.privs.for-parents")) {
      	this._writeStemPrivs(stem);
      }
      this._writeStems(stems);
      this._writeBasicStemFooter(stem);
    }
  } // private void _writeStems(stems)

  // @since   1.1.0
  private void _writeSubject(Subject subj) 
    throws  IOException 
  {
    this._writeSubject(subj, GrouperConfig.EMPTY_STRING);
  } // private void _writeSubject(subj)

  // @since   1.1.0
  private void _writeSubject(Subject subj, String immediate) 
    throws  IOException 
  {
    String attrName = "id";
    String id       = null;
    if ("group".equals(subj.getType().getName())) {
      attrName  = "identifier";
      id        = this._fixGroupName(subj.getName());
    } else {
      id = subj.getId();
    }
    this.xml.put(
      "<subject " + attrName + "='" + this._fixXmlAttribute(id)
      + "' type='" + this._fixXmlAttribute(subj.getType().getName())
      + "' source='" + this._fixXmlAttribute(subj.getSource().getId())
      + "'" + immediate
    );
    if ("group".equals(subj.getType().getName())) {
      this.xml.put(" id='" + subj.getId() + "'");
    }
    Iterator exportAttrs = _getExportAttributes(subj);
    if (XmlUtils.isEmpty(exportAttrs)) {
      this.xml.puts("/>");
      return;
    }
    this.xml.puts(">");
    String    attr;
    Iterator  attrIt;
    String    attrValue;
    Set       values;
    while (exportAttrs.hasNext()) {
      attr = (String) exportAttrs.next();
      values = subj.getAttributeValues(attr);
      this.xml.puts("<subjectAttribute name='" + attr + "'>");
      attrIt = values.iterator();
      while (attrIt.hasNext()) {
        attrValue = (String) attrIt.next();
        this.xml.puts("<value>" + attrValue + "</value>");
      }
      this.xml.puts("</subjectAttribute>");
    }
    this.xml.puts("</subject>");

  } // private void _writeSubject(subj, immediate)

  // @since   1.1.0
  private void _writeSubjectSourceMetaData(Source sa) 
    throws  IOException
  {
    this.xml.indent();
    this.xml.puts("<source id=" + U.q( this._fixXmlAttribute(sa.getId()) )  );
    this.xml.indent();
    this.xml.puts("name="       + U.q( sa.getName() )                       );
    this.xml.puts("class="      + U.q( sa.getClass().getName() )            );
    this.xml.undent();
    this.xml.puts(">");
    Iterator it = sa.getSubjectTypes().iterator();
    while (it.hasNext()) {
      this._writeSubjectSourceTypesMetaData( (SubjectType) it.next() );
    }
    this.xml.puts("</source>");
    this.xml.undent();
  } // private void _writeSubjectSourceMetaData(sa)

  // @since   1.1.0
  private void _writeSubjectSourcesMetaData()
    throws  GrouperException,
            IOException 
  {
    this.xml.indent();
    this.xml.puts("<subjectSourceMetaData>");
    try {
      Iterator it = SourceManager.getInstance().getSources().iterator();
      while (it.hasNext()) {
        this._writeSubjectSourceMetaData( (Source) it.next() );
      }
    }
    catch (Exception e) {
      throw new GrouperException(e.getMessage(), e);
    }
    finally {
      this.xml.puts("</subjectSourceMetaData>");
      this.xml.undent();
    }
  } // private void _writeSubjectSourcesMetaData()

  // @since   1.1.0
  private void _writeSubjectSourceTypesMetaData(SubjectType st) 
    throws  IOException
  {
    this.xml.indent();
    this.xml.puts("<subjectType name=" + U.q( st.getName() ) + "/>");
    this.xml.undent();
  } // private void _writeSubjectSourceTypesMetaData(st)
  
} // public class XmlExporter

