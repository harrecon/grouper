/**
 * Copyright 2014 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author mchyzer
 * $Id: XmlExportGroup.java 6216 2010-01-10 04:52:30Z mchyzer $
 */
package edu.internet2.middleware.grouper.xml.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.Dom4JReader;

import edu.internet2.middleware.grouper.attr.assign.AttributeAssignActionSet;
import edu.internet2.middleware.grouper.hibernate.AuditControl;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateHandlerBean;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.misc.GrouperVersion;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.xml.importXml.XmlImportMain;


/**
 *
 */
public class XmlExportAttributeAssignActionSet {

  /**
   * 
   */
  private static final String XML_EXPORT_ATTRIBUTE_ASSIGN_ACTION_SET_XPATH = "/grouperExport/attributeAssignActionSets/XmlExportAttributeAssignActionSet";

  /**
   * 
   */
  private static final String ATTRIBUTE_ASSIGN_ACTION_SETS_XPATH = "/grouperExport/attributeAssignActionSets";

  /** uuid */
  private String uuid;
  
  /** createTime */
  private String createTime;

  /** modifierTime */
  private String modifierTime;

  /** hibernateVersionNumber */
  private long hibernateVersionNumber;

  /** contextId */
  private String contextId;

  /** depth */
  private long depth;
  
  /**
   * depth
   * @return depth
   */
  public long getDepth() {
    return this.depth;
  }

  /**
   * depth
   * @param depth1
   */
  public void setDepth(long depth1) {
    this.depth = depth1;
  }

  /** ifHasAttributeAssignActionId */
  private String ifHasAttributeAssignActionId;
  
  /**
   * ifHasAttributeAssignActionId
   * @return ifHasAttributeAssignActionId
   */
  public String getIfHasAttributeAssignActionId() {
    return this.ifHasAttributeAssignActionId;
  }

  /**
   * ifHasAttributeAssignActionId
   * @param ifHasAttributeAssignActionId1
   */
  public void setIfHasAttributeAssignActionId(String ifHasAttributeAssignActionId1) {
    this.ifHasAttributeAssignActionId = ifHasAttributeAssignActionId1;
  }

  /**
   * thenHasAttributeAssignActionId
   */
  private String thenHasAttributeAssignActionId;
  
  /**
   * thenHasAttributeAssignActionId
   * @return thenHasAttributeAssignActionId
   */
  public String getThenHasAttributeAssignActionId() {
    return this.thenHasAttributeAssignActionId;
  }

  /**
   * thenHasAttributeAssignActionId
   * @param thenHasAttributeAssignActionId
   */
  public void setThenHasAttributeAssignActionId(String thenHasAttributeAssignActionId) {
    this.thenHasAttributeAssignActionId = thenHasAttributeAssignActionId;
  }

  /**
   * type
   */
  private String type;

  /**
   * logger 
   */
  private static final Log LOG = GrouperUtil.getLog(XmlExportAttributeAssignActionSet.class);
  
  /**
   * type
   * @return type
   */
  public String getType() {
    return this.type;
  }

  /**
   * type
   * @param type1
   */
  public void setType(String type1) {
    this.type = type1;
  }

  /**
   * 
   */
  public XmlExportAttributeAssignActionSet() {
    
  }

  /**
   * uuid
   * @return uuid
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * uuid
   * @param uuid1
   */
  public void setUuid(String uuid1) {
    this.uuid = uuid1;
  }

  /**
   * createTime
   * @return createTime
   */
  public String getCreateTime() {
    return this.createTime;
  }

  /**
   * createTime
   * @param createTime1
   */
  public void setCreateTime(String createTime1) {
    this.createTime = createTime1;
  }

  /**
   * modifierTime
   * @return modifierTime
   */
  public String getModifierTime() {
    return this.modifierTime;
  }

  /**
   * modifierTime
   * @param modifierTime1
   */
  public void setModifierTime(String modifierTime1) {
    this.modifierTime = modifierTime1;
  }

  /**
   * hibernateVersionNumber
   * @return hibernateVersionNumber
   */
  public long getHibernateVersionNumber() {
    return this.hibernateVersionNumber;
  }

  /**
   * hibernateVersionNumber
   * @param hibernateVersionNumber1
   */
  public void setHibernateVersionNumber(long hibernateVersionNumber1) {
    this.hibernateVersionNumber = hibernateVersionNumber1;
  }

  /**
   * contextId
   * @return contextId
   */
  public String getContextId() {
    return this.contextId;
  }

  /**
   * contextId
   * @param contextId1
   */
  public void setContextId(String contextId1) {
    this.contextId = contextId1;
  }
  
  /**
   * convert to attributeAssignActionSet
   * @return the attributeAssignActionSet
   */
  public AttributeAssignActionSet toAttributeAssignActionSet() {
    AttributeAssignActionSet attributeAssignActionSet = new AttributeAssignActionSet();
    
    attributeAssignActionSet.setContextId(this.contextId);
    attributeAssignActionSet.setCreatedOnDb(GrouperUtil.dateLongValue(this.createTime));
    attributeAssignActionSet.setDepth((int)this.depth);
    attributeAssignActionSet.setHibernateVersionNumber(this.hibernateVersionNumber);
    attributeAssignActionSet.setIfHasAttrAssignActionId(this.ifHasAttributeAssignActionId);
    attributeAssignActionSet.setLastUpdatedDb(GrouperUtil.dateLongValue(this.modifierTime));
    attributeAssignActionSet.setThenHasAttrAssignActionId(this.thenHasAttributeAssignActionId);
    attributeAssignActionSet.setTypeDb(this.type);
    attributeAssignActionSet.setId(this.uuid);
    
    return attributeAssignActionSet;
  }

  /**
   * @param exportVersion
   * @return the xml string
   */
  public String toXml(GrouperVersion exportVersion) {
    StringWriter stringWriter = new StringWriter();
    this.toXml(exportVersion, stringWriter);
    return stringWriter.toString();
  }

  /**
   * @param exportVersion 
   * @param writer
   */
  public void toXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, Writer writer) {
    XStream xStream = XmlExportUtils.xstream();
  
    CompactWriter compactWriter = new CompactWriter(writer);
    
    xStream.marshal(this, compactWriter);
  
  }

  /**
   * parse the xml file for groups
   * @param xmlImportMain
   */
  public static void processXmlSecondPass(final XmlImportMain xmlImportMain) {
    xmlImportMain.getReader().addHandler( ATTRIBUTE_ASSIGN_ACTION_SETS_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
            }
            public void onEnd(ElementPath path) {
                // process a ROW element
                Element row = path.getCurrent();
  
                // prune the tree
                row.detach();
            }
        }
    );
  
    xmlImportMain.getReader().addHandler( XML_EXPORT_ATTRIBUTE_ASSIGN_ACTION_SET_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
                // do nothing here...    
            }
            public void onEnd(ElementPath path) {
  
              Element row = null;
              try {
                // process a ROW element
                row = path.getCurrent();
  
                // prune the tree
                row.detach();
  
                XmlExportAttributeAssignActionSet xmlExportAttributeAssignActionSetFromFile = (XmlExportAttributeAssignActionSet)xmlImportMain.getXstream().unmarshal(new Dom4JReader(row));
                
                AttributeAssignActionSet attributeAssignActionSet = xmlExportAttributeAssignActionSetFromFile.toAttributeAssignActionSet();
                
                XmlExportUtils.syncImportable(attributeAssignActionSet, xmlImportMain);
                
                xmlImportMain.incrementCurrentCount();
              } catch (RuntimeException re) {
                LOG.error("Problem importing attributeAssignActionSet: " + XmlExportUtils.toString(row), re);
                throw re;
              }
            }
        }
    );
  
  }

  /**
   * get db count
   * @param xmlExportMain 
   * @return db count
   */
  public static long dbCount(XmlExportMain xmlExportMain) {
    long result = HibernateSession.byHqlStatic().createQuery("select count(theAttributeAssignActionSet) " 
        + exportFromOnQuery(xmlExportMain, false)).uniqueResult(Long.class);
    return result;
  }
  
  /**
   * get the query from the FROM clause on to the end for export
   * @param xmlExportMain
   * @param includeOrderBy 
   * @return the export query
   */
  private static String exportFromOnQuery(XmlExportMain xmlExportMain, boolean includeOrderBy) {
    //select all members in order
    StringBuilder queryBuilder = new StringBuilder();
    if (!xmlExportMain.filterStemsOrObjects()) {
      queryBuilder.append(" from AttributeAssignActionSet as theAttributeAssignActionSet " +
      		" where theAttributeAssignActionSet.typeDb = 'immediate' ");
    } else {
      queryBuilder.append(
          " from AttributeAssignActionSet as theAttributeAssignActionSet " +
          " where theAttributeAssignActionSet.typeDb = 'immediate' " +
          " and exists " +
          " ( select theAttributeDef from AttributeDef as theAttributeDef, " +
          " AttributeAssignAction theAttributeAssignAction " +
          " where theAttributeAssignAction.id = theAttributeAssignActionSet.ifHasAttrAssignActionId " +
          " and theAttributeAssignAction.attributeDefId = theAttributeDef.id and ( ");
      xmlExportMain.appendHqlStemLikeOrObjectEquals(queryBuilder, "theAttributeDef", "nameDb", false);
      queryBuilder.append(" ) ) " +
          " and exists " +
          " ( select theAttributeDef from AttributeDef as theAttributeDef, " +
          " AttributeAssignAction theAttributeAssignAction " +
          " where theAttributeAssignAction.id = theAttributeAssignActionSet.thenHasAttrAssignActionId " +
          " and theAttributeAssignAction.attributeDefId = theAttributeDef.id and ( ");
      xmlExportMain.appendHqlStemLikeOrObjectEquals(queryBuilder, "theAttributeDef", "nameDb", false);
      queryBuilder.append(" ) ) ");
    }
    if (includeOrderBy) {
      queryBuilder.append(" order by theAttributeAssignActionSet.id");
    }
    return queryBuilder.toString();
  }

  /**
   * 
   * @param writer
   * @param xmlExportMain
   */
  public static void exportAttributeAssignActionSetsGsh(final Writer writer, final XmlExportMain xmlExportMain) {
    //get the members
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
      
      public Object callback(HibernateHandlerBean hibernateHandlerBean)
          throws GrouperDAOException {

        Session session = hibernateHandlerBean.getHibernateSession().getSession();

        //select all members in order
        Query query = session.createQuery(
            "select distinct "
            + " ( select theAttributeDef.nameDb from AttributeDef theAttributeDef, AttributeAssignAction theAttributeAssignAction where theAttributeAssignAction.id = theAttributeAssignActionSet.ifHasAttrAssignActionId and theAttributeDef.id = theAttributeAssignAction.attributeDefId ), "
            + " ( select theAttributeAssignAction.nameDb from AttributeAssignAction as theAttributeAssignAction where theAttributeAssignAction.id = theAttributeAssignActionSet.ifHasAttrAssignActionId ), "
            + " ( select theAttributeAssignAction.nameDb from AttributeAssignAction as theAttributeAssignAction where theAttributeAssignAction.id = theAttributeAssignActionSet.thenHasAttrAssignActionId ), "
            + " theAttributeAssignActionSet "
            + exportFromOnQuery(xmlExportMain, false));

//        //select all action sets (immediate is depth = 1)
//        Query query = session.createQuery(
//            "select distinct  " + exportFromOnQuery(xmlExportMain, true));

        final GrouperVersion grouperVersion = new GrouperVersion(GrouperVersion.grouperContainerVersionOrMavenVersion());

        //this is an efficient low-memory way to iterate through a resultset
        ScrollableResults results = null;
        try {
          results = query.scroll();
          while(results.next()) {
            final String nameOfAttributeDef = (String)results.get(0);
            final String ifHasActionName = (String)results.get(1);
            final String thenHasActionName = (String)results.get(2);
            final AttributeAssignActionSet attributeAssignActionSet = (AttributeAssignActionSet)results.get(3);
            final XmlExportAttributeAssignActionSet xmlExportAttributeAssignActionSet = attributeAssignActionSet.xmlToExportAttributeAssignActionSet(grouperVersion);

            //writer.write("" + subjectId + ", " + sourceId + ", " + listName + ", " + groupName 
            //    + ", " + stemName + ", " + nameOfAttributeDef 
            //    + ", " + enabledTime + ", " + disabledTime  + "\n");
            
            HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_NEW, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
              
              public Object callback(HibernateHandlerBean hibernateHandlerBean)
                  throws GrouperDAOException {
                try {
                  xmlExportAttributeAssignActionSet.toGsh(grouperVersion, writer, nameOfAttributeDef, ifHasActionName, thenHasActionName);
                } catch (IOException ioe) {
                  throw new RuntimeException("Problem exporting action hierarchy to gsh: " + nameOfAttributeDef + ", " + ifHasActionName + ", " + thenHasActionName, ioe);
                }
                return null;
              }
            });
            xmlExportMain.incrementRecordCount();
          }
        } finally {
          HibUtils.closeQuietly(results);
        }
        
        return null;
      }
    });
  }

  /**
   * @param exportVersion 
   * @param writer
   * @param nameOfAttributeDef
   * @param ifHasActionName 
   * @param thenHasActionName 
   * @throws IOException 
   */
  public void toGsh(
      @SuppressWarnings("unused") GrouperVersion exportVersion, Writer writer, 
      String nameOfAttributeDef, String ifHasActionName, String thenHasActionName) throws IOException {
    
    //readWrite = permissionDef.getAttributeDefActionDelegate().findAction("readWrite", true);
    //admin = permissionDef.getAttributeDefActionDelegate().findAction("admin", true);
    // 
    //readWrite.getAttributeAssignActionSetDelegate().addToAttributeAssignActionSet(read);
    
    if (nameOfAttributeDef == null) {
      throw new RuntimeException("Why is nameOfAttributeDef null?");
    }

    writer.write("attributeDef = AttributeDefFinder.findByName(\""
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(nameOfAttributeDef) + "\", false);\n");

    writer.write("if (attributeDef != null) { ");

    writer.write("AttributeAssignAction ifHasAction = attributeDef.getAttributeDefActionDelegate().findAction(\"" + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(ifHasActionName) + "\", false); ");

    writer.write("if (ifHasAction != null) { ");

    writer.write("AttributeAssignAction thenHasAction = attributeDef.getAttributeDefActionDelegate().findAction(\"" + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(thenHasActionName) + "\", false); ");

    writer.write("if (thenHasAction != null) { ");

    writer.write(" boolean changed = ifHasAction.getAttributeAssignActionSetDelegate().addToAttributeAssignActionSet(thenHasAction); gshTotalObjectCount++; "
        + "if (changed) { gshTotalChangeCount++; System.out.println(\"Made change for action inheritance for attributeDef: " 
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(nameOfAttributeDef) + ", if has action: " + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(ifHasActionName) 
        + " then has action: " + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(thenHasActionName) + "\"); } ");

    writer.write(" } else { System.out.println(\"ERROR: cant find thenHasAction: '" 
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(thenHasActionName) + "', attributeDefName: '" 
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(nameOfAttributeDef) + "'\"); gshTotalErrorCount++; }");

    writer.write(" } else { System.out.println(\"ERROR: cant find ifHasAction: '" 
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(ifHasActionName) + "', attributeDefName: '" 
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(nameOfAttributeDef) + "'\"); gshTotalErrorCount++; }");
    
    writer.write(" } else { System.out.println(\"ERROR: cant find attributeDef: '" 
        + GrouperUtil.escapeDoubleQuotesSlashesAndNewlinesForString(nameOfAttributeDef) + "'\"); gshTotalErrorCount++; }\n");

  }

  /**
   * 
   * @param writer
   * @param xmlExportMain 
   */
  public static void exportAttributeAssignActionSets(final Writer writer, final XmlExportMain xmlExportMain) {
    //get the members
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
      
      public Object callback(HibernateHandlerBean hibernateHandlerBean)
          throws GrouperDAOException {
  
        Session session = hibernateHandlerBean.getHibernateSession().getSession();
  
        //select all action sets (immediate is depth = 1)
        Query query = session.createQuery(
            "select distinct theAttributeAssignActionSet " + exportFromOnQuery(xmlExportMain, true));
  
        GrouperVersion grouperVersion = new GrouperVersion(GrouperVersion.grouperContainerVersionOrMavenVersion());
        try {
          writer.write("  <attributeAssignActionSets>\n");
  
          //this is an efficient low-memory way to iterate through a resultset
          ScrollableResults results = null;
          try {
            results = query.scroll();
            while(results.next()) {
              Object object = results.get(0);
              final AttributeAssignActionSet attributeAssignActionSet = (AttributeAssignActionSet)object;
              
              //comments to dereference the foreign keys
              if (xmlExportMain.isIncludeComments()) {
                HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_NEW, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
                  
                  public Object callback(HibernateHandlerBean hibernateHandlerBean)
                      throws GrouperDAOException {
                    try {
                      writer.write("\n    <!-- ");
                      XmlExportUtils.toStringAttributeAssignActionSet(writer, attributeAssignActionSet, false);
                      writer.write(" -->\n");
                      return null;
                    } catch (IOException ioe) {
                      throw new RuntimeException(ioe);
                    }
                  }
                });
              }
              
              XmlExportAttributeAssignActionSet xmlExportAttributeAssignActionSet = attributeAssignActionSet.xmlToExportAttributeAssignActionSet(grouperVersion);
              writer.write("    ");
              xmlExportAttributeAssignActionSet.toXml(grouperVersion, writer);
              writer.write("\n");
              xmlExportMain.incrementRecordCount();
            }
          } finally {
            HibUtils.closeQuietly(results);
          }
          
          
          if (xmlExportMain.isIncludeComments()) {
            writer.write("\n");
          }
          
          //end the attribute assign action set element 
          writer.write("  </attributeAssignActionSets>\n");
        } catch (IOException ioe) {
          throw new RuntimeException("Problem with streaming attributeAssignActionSets", ioe);
        }
        return null;
      }
    });
  }

  /**
   * take a reader (e.g. dom reader) and convert to an xml export group
   * @param exportVersion
   * @param hierarchicalStreamReader
   * @return the bean
   */
  public static XmlExportAttributeAssignActionSet fromXml(@SuppressWarnings("unused") GrouperVersion exportVersion, 
      HierarchicalStreamReader hierarchicalStreamReader) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeAssignActionSet xmlExportAttributeAssignActionSet = (XmlExportAttributeAssignActionSet)xStream.unmarshal(hierarchicalStreamReader);
  
    return xmlExportAttributeAssignActionSet;
  }

  /**
   * 
   * @param exportVersion
   * @param xml
   * @return the object from xml
   */
  public static XmlExportAttributeAssignActionSet fromXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, String xml) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportAttributeAssignActionSet xmlExportAttributeAssignActionSet = (XmlExportAttributeAssignActionSet)xStream.fromXML(xml);
  
    return xmlExportAttributeAssignActionSet;
  }

  /**
   * parse the xml file for attributeAssignActionSets
   * @param xmlImportMain
   */
  public static void processXmlFirstPass(final XmlImportMain xmlImportMain) {
    xmlImportMain.getReader().addHandler( ATTRIBUTE_ASSIGN_ACTION_SETS_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
            }
            public void onEnd(ElementPath path) {
                // process a ROW element
                Element row = path.getCurrent();

                // prune the tree
                row.detach();
            }
        }
    );

    xmlImportMain.getReader().addHandler( XML_EXPORT_ATTRIBUTE_ASSIGN_ACTION_SET_XPATH, 
        new ElementHandler() {
            public void onStart(ElementPath path) {
                // do nothing here...    
            }
            public void onEnd(ElementPath path) {
                // process a ROW element
                Element row = path.getCurrent();

                // prune the tree
                row.detach();

                xmlImportMain.incrementTotalImportFileCount();
            }
        }
    );
 
  }

  
}
