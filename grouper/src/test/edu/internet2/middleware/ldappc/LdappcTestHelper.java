/*
 * Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.internet2.middleware.ldappc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import junit.framework.Assert;

import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.ldif.LdifEntry;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.opensaml.util.resource.PropertyReplacementResourceFilter;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.util.DatatypeHelper;
import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.XMLUnmarshaller;
import org.openspml.v2.util.xml.UnknownSpml2TypeException;
import org.slf4j.Logger;

import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.ldappc.exception.LdappcException;
import edu.internet2.middleware.ldappc.util.IgnoreRequestIDDifferenceListener;
import edu.internet2.middleware.ldappc.util.LdapUtil;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.bean.LdapAttributes;
import edu.vt.middleware.ldap.bean.LdapEntry;
import edu.vt.middleware.ldap.bean.LdapResult;
import edu.vt.middleware.ldap.ldif.LdifResult;

public class LdappcTestHelper {

  private static final Logger LOG = GrouperUtil.getLogger(LdappcTestHelper.class);

  /**
   * Rewrite the given string containing macros of the form ${key} with the properties
   * from the given property file.
   * 
   * @param ldif
   * @param propertiesFile
   * @return
   * @throws IOException
   * @throws ResourceException
   */
  public static String applyFilter(String ldif, File propertiesFile) throws IOException,
      ResourceException {
    if (propertiesFile == null) {
      return ldif;
    }

    PropertyReplacementResourceFilter filter = new PropertyReplacementResourceFilter(
        propertiesFile);
    return DatatypeHelper.inputstreamToString(filter
        .applyFilter(new ByteArrayInputStream(ldif.getBytes())), null);
  }

  /**
   * Get a map with keys ldap DNs and values ldif entries. Ignores objectclass=top.
   * 
   * @param ldifEntries
   * @return
   * @throws NamingException
   */
  public static Map<LdapDN, LdifEntry> buildLdapEntryMap(List<LdifEntry> ldifEntries,
      boolean caseSensitiveAttributeNames) throws NamingException {

    Map<LdapDN, LdifEntry> map = new HashMap<LdapDN, LdifEntry>();

    for (LdifEntry ldifEntry : ldifEntries) {
      if (ldifEntry.isEntry()) {
        Entry entry = ldifEntry.getEntry();
        if (entry.contains("objectclass", "top")) {
          entry.remove("objectclass", "top");
        }
        if (!caseSensitiveAttributeNames) {
          Iterator<EntryAttribute> iterator = entry.iterator();
          while (iterator.hasNext()) {
            EntryAttribute entryAttribute = iterator.next();
            entryAttribute.setId(entryAttribute.getId().toLowerCase()); // redundant ?
          }
        }
      }
      map.put(ldifEntry.getDn(), ldifEntry);
    }

    return map;
  }

  /**
   * Get a map with keys objectclass names and values the names of the attributes that are
   * present for each object of the given objectclass.
   * 
   * @param ldif
   * @return
   * @throws NamingException
   */
  public static Map<String, Collection<String>> buildObjectlassAttributeMap(
      Collection<LdifEntry> ldifEntries) throws NamingException {
    Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();

    for (LdifEntry ldifEntry : ldifEntries) {
      Set<String> objectclasses = new HashSet<String>();
      Set<String> attributeIds = new HashSet<String>();

      if (!ldifEntry.isEntry()) {
        LOG.trace("Unable to parse LdifEntry as an Entry {}", ldifEntry);
        return null;
      }
      Entry entry = ldifEntry.getEntry();

      Iterator<EntryAttribute> iterator = entry.iterator();
      while (iterator.hasNext()) {
        EntryAttribute entryAttribute = iterator.next();
        String entryAttributeId = entryAttribute.getId();
        if (entryAttributeId.equalsIgnoreCase("objectclass")) {
          Iterator<Value<?>> values = entryAttribute.getAll();
          while (values.hasNext()) {
            Value<?> value = values.next();
            if (value.getString().equals("top")) {
              continue;
            }
            objectclasses.add(value.getString());
          }
        }
        attributeIds.add(entryAttributeId);
      }
      for (String objectclass : objectclasses) {
        map.put(objectclass, attributeIds);
      }
    }
    return map;
  }

  /**
   * see {@link #buildObjectlassAttributeMap(Collection)}
   * 
   * @param ldif
   * @return
   * @throws NamingException
   */
  public static Map<String, Collection<String>> buildObjectlassAttributeMap(String ldif)
      throws NamingException {
    LdifReader reader = new LdifReader();
    return buildObjectlassAttributeMap(reader.parseLdif(ldif));
  }

  /**
   * Destroy everything under the given base.
   * 
   * @param baseDn
   * @param ldap
   * @throws NamingException
   */
  public static void deleteChildren(String baseDn, Ldap ldap) throws NamingException {
    List<String> toDelete = getChildDNs(baseDn, ldap);
    for (String dn : toDelete) {
      LOG.info("delete '{}'", dn);
      ldap.delete(dn);
    }
  }

  /**
   * Destroy everything under the given base.
   * 
   * @param base
   * @param ldapContext
   * @throws Exception
   */
  public static void deleteChildren(String base, LdapContext ldapContext)
      throws Exception {
    List<String> toDelete = getChildDNs(base, ldapContext);
    for (String dn : toDelete) {
      LOG.info("delete " + dn);
      ldapContext.destroySubcontext(dn);
    }
  }

  /**
   * Return a list of child DNs under the given DN, in (reverse) order suitable for
   * deletion.
   * 
   * @param name
   *          the top level DN
   * @return
   * @throws NamingException
   */
  public static List<String> getChildDNs(String name, Ldap ldap) throws NamingException {
    ArrayList<String> tree = new ArrayList<String>();

    Iterator<SearchResult> searchResults = ldap.searchAttributes(name,
        new BasicAttributes("objectclass", null), new String[] {});
    LdapResult ldapResult = new LdapResult(searchResults);
    for (LdapEntry ldapEntry : ldapResult.getEntries()) {
      tree.addAll(getChildDNs(ldapEntry.getDn(), ldap));
      tree.add(ldapEntry.getDn());
    }

    return tree;
  }

  /**
   * Return a list of child DNs under the given DN, in (reverse) order suitable for
   * deletion.
   * 
   * @param name
   *          the top level DN
   * @return
   * @throws NamingException
   */
  public static List<String> getChildDNs(String name, LdapContext ldapContext)
      throws NamingException {
    ArrayList<String> tree = new ArrayList<String>();

    SearchControls ctrls = new SearchControls();
    ctrls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
    ctrls.setReturningAttributes(new String[] {});

    NamingEnumeration<SearchResult> results = ldapContext.search(name, "objectclass=*",
        ctrls);

    while (results.hasMore()) {
      SearchResult result = results.next();
      tree.addAll(getChildDNs(result.getNameInNamespace(), ldapContext));
      tree.add(result.getNameInNamespace());
    }

    return tree;
  }

  /**
   * Return an LDIF representation of the entire DIT.
   * 
   * @return
   * @throws NamingException
   */
  public static String getCurrentLdif(String baseDn, Ldap ldap) throws NamingException {

    StringBuffer ldif = new StringBuffer();

    List<String> currentDns = getChildDNs(baseDn, ldap);

    for (String currentDn : currentDns) {
      Attributes attributes = ldap.getAttributes(currentDn);
      LdapEntry ldapEntry = new LdapEntry();
      ldapEntry.setDn(currentDn);
      ldapEntry.setLdapAttributes(new LdapAttributes(attributes));
      LdifResult ldifResult = new LdifResult(ldapEntry);
      ldif.append(ldifResult.toLdif());
    }

    LOG.debug("current ldif {}\n{}", ldap.getLdapConfig().getLdapUrl(), ldif.toString());
    return ldif.toString();
  }

  /**
   * see {@link #getCurrentLdif(String, String[], LdapContext)}
   * 
   * @param baseDn
   * @param ldapContext
   * @return
   * @throws NamingException
   */
  public static String getCurrentLdif(String baseDn, LdapContext ldapContext)
      throws NamingException {
    return getCurrentLdif(baseDn, null, ldapContext);
  }

  /**
   * Return an LDIF representation of the entire DIT.
   * 
   * @return
   * @throws NamingException
   */
  public static String getCurrentLdif(String baseDn, String[] attrIds,
      LdapContext ldapContext) throws NamingException {

    StringBuffer ldif = new StringBuffer();

    List<String> currentDns = getChildDNs(baseDn, ldapContext);

    for (String currentDn : currentDns) {
      ldif.append("dn: " + currentDn + "\n");
      Attributes attributes = ldapContext.getAttributes(currentDn, attrIds);
      ldif.append(LdapUtil.getLdif(attributes));
      ldif.append("\n");
    }

    return ldif.toString();
  }

  /**
   * Return the file from the given object's classloader.
   * 
   * @param object
   *          the parent
   * @param fileName
   *          the file name
   * @return the File
   */
  public static File getFile(Object object, String fileName) {
    try {
      URL url = object.getClass().getResource(fileName);
      if (url == null) {
        throw new LdappcException("File not found : " + fileName);
      }
      return new File(url.toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new LdappcException("An error occurred : " + e.getMessage());
    }
  }

  public static void loadLdif(File file, Ldap ldap) throws NamingException {

    LdifReader ldifReader = new LdifReader(file);
    for (LdifEntry entry : ldifReader) {
      Attributes attributes = new BasicAttributes(true);
      for (EntryAttribute entryAttribute : entry.getEntry()) {
        BasicAttribute attribute = new BasicAttribute(entryAttribute.getId());
        Iterator<Value<?>> values = entryAttribute.getAll();
        while (values.hasNext()) {
          attribute.add(values.next().get());
        }
        attributes.put(attribute);
      }
      LOG.debug("creating '" + entry.getDn().toString() + " " + attributes);
      ldap.create(entry.getDn().toString(), attributes);
    }
  }

  public static void loadLdif(File file, LdapContext ldapContext) throws Exception {

    LdifReader ldifReader = new LdifReader(file);
    for (LdifEntry entry : ldifReader) {
      Attributes attributes = new BasicAttributes(true);
      for (EntryAttribute entryAttribute : entry.getEntry()) {
        BasicAttribute attribute = new BasicAttribute(entryAttribute.getId());
        Iterator<Value<?>> values = entryAttribute.getAll();
        while (values.hasNext()) {
          attribute.add(values.next().get());
        }
        attributes.put(attribute);
      }
      LOG.debug("creating '" + entry.getDn().toString() + " " + attributes);
      ldapContext.createSubcontext(entry.getDn().toString(), attributes);
    }
  }

  public static void loadLdif(File ldifFile, File replacementPropertiesFile,
      LdapContext ldapContext) throws Exception {

    PropertyReplacementResourceFilter prf = new PropertyReplacementResourceFilter(
        replacementPropertiesFile);

    LdifReader ldifReader = new LdifReader(prf.applyFilter(new FileInputStream(ldifFile)));
    for (LdifEntry entry : ldifReader) {
      Attributes attributes = new BasicAttributes(true);
      for (EntryAttribute entryAttribute : entry.getEntry()) {
        BasicAttribute attribute = new BasicAttribute(entryAttribute.getId());
        Iterator<Value<?>> values = entryAttribute.getAll();
        while (values.hasNext()) {
          attribute.add(values.next().get());
        }
        attributes.put(attribute);
      }
      LOG.debug("creating '" + entry.getDn().toString() + " " + attributes);
      ldapContext.createSubcontext(entry.getDn().toString(), attributes);
    }
  }

  /**
   * Normalize values as DNs for every attribute of the given Entry which matches a given
   * attribute name. Probably this method should use the ApacheDS Normalization.
   * 
   * @param entry
   * @param attributeNames
   * @throws NamingException
   */
  public static void normalizeDNValues(Entry entry, Collection<String> dnAttributeNames)
      throws NamingException {
    Iterator<EntryAttribute> iterator = entry.iterator();
    while (iterator.hasNext()) {
      Set<String> toAdd = new HashSet<String>();
      Set<String> toRemove = new HashSet<String>();
      EntryAttribute entryAttribute = iterator.next();
      if (dnAttributeNames.contains(entryAttribute.getId())) {
        Iterator<Value<?>> valueIterator = entryAttribute.getAll();
        while (valueIterator.hasNext()) {
          Value<?> value = valueIterator.next();
          String oldValue = value.getString();
          String newValue = new LdapDN(value.get().toString()).toNormName();
          if (!oldValue.equals(newValue)) {
            toRemove.add(value.getString());
            toAdd.add(new LdapDN(value.get().toString()).toNormName());
          }
        }
      }
      if (!toAdd.isEmpty()) {
        entryAttribute.add(toAdd.toArray(new String[] {}));
      }
      if (!toRemove.isEmpty()) {
        entryAttribute.remove(toRemove.toArray(new String[] {}));
      }
    }
  }

  /**
   * see {@link #normalizeDNValues(Entry, Collection)}
   * 
   * @param ldifEntries
   * @param dnAttributeNames
   * @throws NamingException
   */
  public static void normalizeDNValues(Collection<LdifEntry> ldifEntries,
      Collection<String> dnAttributeNames) throws NamingException {
    for (LdifEntry ldifEntry : ldifEntries) {
      if (ldifEntry.isEntry()) {
        normalizeDNValues(ldifEntry.getEntry(), dnAttributeNames);
      }
    }
  }

  /**
   * Remove from the entry any attribute which is not contained in the given collection of
   * attribute names.
   * 
   * @param entry
   * @param attributeNamesToKeep
   * @throws NamingException
   */
  public static void purgeAttributes(Entry entry, Collection<String> attributeNamesToKeep)
      throws NamingException {
    if (attributeNamesToKeep == null) {
      return;
    }

    Set<String> attrNames = new HashSet<String>();
    for (String attributeName : attributeNamesToKeep) {
      attrNames.add(attributeName.toLowerCase());
    }

    List<EntryAttribute> entryAttributesToRemove = new ArrayList<EntryAttribute>();
    Iterator<EntryAttribute> iterator = entry.iterator();
    while (iterator.hasNext()) {
      EntryAttribute entryAttribute = iterator.next();
      if (!attrNames.contains(entryAttribute.getId().toLowerCase())) {
        entryAttributesToRemove.add(entryAttribute);
      }
    }
    for (EntryAttribute entryAttributeToRemove : entryAttributesToRemove) {
      entry.remove(entryAttributeToRemove);
    }
  }

  /**
   * Remove attributes from the entry which are not in the supplied map.
   * 
   * see {@link #purgeAttributes(Entry, Collection)}.
   * 
   * see {@link #buildObjectlassAttributeMap(BufferedReader)}
   * 
   * @param ldifEntries
   * @param objectclassAttributeMap
   * @throws NamingException
   */
  public static void purgeAttributes(Collection<LdifEntry> ldifEntries,
      Map<String, Collection<String>> objectclassAttributeMap) throws NamingException {
    if (objectclassAttributeMap == null) {
      return;
    }
    for (LdifEntry ldifEntry : ldifEntries) {
      if (ldifEntry.isEntry()) {
        Set<String> attributeNamesToKeep = new HashSet<String>();
        for (String objectclass : objectclassAttributeMap.keySet()) {
          if (ldifEntry.getEntry().hasObjectClass(objectclass)) {
            attributeNamesToKeep.addAll(objectclassAttributeMap.get(objectclass));
          }
        }
        purgeAttributes(ldifEntry.getEntry(), attributeNamesToKeep);
      }
    }
  }

  /**
   * Return the contents of the given file as a string.
   * 
   * @param file
   * @return
   */
  public static String readFile(File file) {

    StringBuffer buffer = new StringBuffer();

    try {
      BufferedReader in = new BufferedReader(new FileReader(file));
      String str;
      while ((str = in.readLine()) != null) {
        buffer.append(str + System.getProperty("line.separator"));
      }
      in.close();
    } catch (IOException e) {
      Assert.fail("An error occurred : " + e.getMessage());
    }
    return buffer.toString();
  }

  public static Object readSpml(XMLUnmarshaller u, File file) {
    try {
      String xml = readFile(file);
      return u.unmarshall(xml);
    } catch (UnknownSpml2TypeException e) {
      e.printStackTrace();
      Assert.fail("An error occurred : " + e.getMessage());
    }
    return null;
  }

  public static void verifyLdif(String correctLdif, String currentLdif)
      throws NamingException, FileNotFoundException, IOException, ResourceException {
    verifyLdif(correctLdif, currentLdif, null);
  }

  public static void verifyLdif(String correctLdif, String currentLdif,
      File propertiesFile) throws NamingException, FileNotFoundException, IOException,
      ResourceException {
    verifyLdif(correctLdif, currentLdif, propertiesFile, null);
  }

  public static void verifyLdif(String correctLdif, File propertiesFile,
      Collection<String> normalizeDnAttributes, String base, LdapContext ldapContext)
      throws IOException, ResourceException, NamingException {

    // replace macros
    String filteredCorrectLdif = LdappcTestHelper
        .applyFilter(correctLdif, propertiesFile);

    // get attribute ids to request
    Map<String, Collection<String>> map = LdappcTestHelper
        .buildObjectlassAttributeMap(filteredCorrectLdif);
    Set<String> attrIds = new HashSet<String>();
    for (Collection<String> values : map.values()) {
      attrIds.addAll(values);
    }

    // get current ldif using requested attribute ids
    String currentLdif = LdappcTestHelper.getCurrentLdif(base, attrIds
        .toArray(new String[] {}), ldapContext);

    // verify ldif
    LdappcTestHelper.verifyLdif(correctLdif, currentLdif, propertiesFile,
        normalizeDnAttributes);
  }

  public static void verifyLdif(String correctLdif, String currentLdif,
      File propertiesFile, Collection<String> normalizeDnAttributes)
      throws NamingException, FileNotFoundException, IOException, ResourceException {
    InputStream correct = new ByteArrayInputStream(correctLdif.getBytes());
    InputStream current = new ByteArrayInputStream(currentLdif.getBytes());
    verifyLdif(correct, current, propertiesFile, normalizeDnAttributes);
  }

  public static void verifyLdif(File correctFile, File currentFile, File propertiesFile)
      throws FileNotFoundException, IOException, ResourceException, NamingException {
    verifyLdif(correctFile, currentFile, propertiesFile, null);
  }

  public static void verifyLdif(File correctFile, File currentFile, File propertiesFile,
      Collection<String> normalizeDnAttributes) throws FileNotFoundException,
      IOException, ResourceException, NamingException {
    InputStream correct = new FileInputStream(correctFile);
    InputStream current = new FileInputStream(currentFile);
    verifyLdif(correct, current, propertiesFile, normalizeDnAttributes);
  }

  public static void verifyLdif(InputStream correct, InputStream current,
      File propertiesFile, Collection<String> normalizeDnAttributes)
      throws FileNotFoundException, IOException, ResourceException, NamingException {
    String correctLdif;
    String currentLdif;
    if (propertiesFile != null) {
      // replace macros
      PropertyReplacementResourceFilter filter = new PropertyReplacementResourceFilter(
          propertiesFile);
      correctLdif = DatatypeHelper.inputstreamToString(filter.applyFilter(correct), null);
      currentLdif = DatatypeHelper.inputstreamToString(filter.applyFilter(current), null);
    } else {
      correctLdif = DatatypeHelper.inputstreamToString(correct, null);
      currentLdif = DatatypeHelper.inputstreamToString(current, null);
    }

    // the ApacheDS reader
    LdifReader reader = new LdifReader();

    // build map of entries from string ldif
    Map<LdapDN, LdifEntry> correctMap = buildLdapEntryMap(reader.parseLdif(correctLdif),
        false);
    Map<LdapDN, LdifEntry> currentMap = buildLdapEntryMap(reader.parseLdif(currentLdif),
        false);

    // only compare attributes that exist in the correct file
    Map<String, Collection<String>> objectClassAttributeMap = buildObjectlassAttributeMap(correctMap
        .values());
    if (objectClassAttributeMap != null) {
      purgeAttributes(correctMap.values(), objectClassAttributeMap);
      purgeAttributes(currentMap.values(), objectClassAttributeMap);
    }

    // normalize dn values
    if (normalizeDnAttributes != null) {
      normalizeDNValues(correctMap.values(), normalizeDnAttributes);
      normalizeDNValues(currentMap.values(), normalizeDnAttributes);
    }

    verifyLdif(correctMap, currentMap);
  }

  public static void verifyLdif(Map<LdapDN, LdifEntry> correctMap,
      Map<LdapDN, LdifEntry> currentMap) {
    for (LdapDN correctDn : correctMap.keySet()) {
      Assert
          .assertEquals("correct", correctMap.get(correctDn), currentMap.get(correctDn));
    }
    for (LdapDN currentDn : currentMap.keySet()) {
      Assert
          .assertEquals("current", correctMap.get(currentDn), currentMap.get(currentDn));
    }
  }

  public static Marshallable verifySpml(XMLMarshaller m, XMLUnmarshaller u,
      Marshallable testObject, File correctXMLFile) {
    return verifySpml(m, u, testObject, correctXMLFile, false);
  }

  public static Marshallable verifySpml(XMLMarshaller m, XMLUnmarshaller u,
      Marshallable testObject, File correctXMLFile, boolean testEquality) {

    try {
      String testXML = testObject.toXML(m);

      Marshallable unmarshalledObject = u.unmarshall(testXML);

      String unmarshalledTestXML = unmarshalledObject.toXML(m);

      String correctXML = readFile(correctXMLFile);

      Marshallable unmarshalledFromCorrectXMLFile = u.unmarshall(correctXML);

      if (LOG.isDebugEnabled()) {
        LOG.debug("current:\n{}", testXML);
        LOG.debug("unmarshalled:\n{}", unmarshalledTestXML);
        LOG.debug("correct:\n{}", correctXML);
      }

      // test objects
      if (testEquality) {
        Assert.assertEquals(testObject, unmarshalledObject);
        Assert.assertEquals(unmarshalledFromCorrectXMLFile, testObject);
      }

      // TODO test marshalling and unmarshalling objects
      // OCEtoMarshallableAdapter does not have an equals() method

      // test marshalling and unmarshalling xml
      DetailedDiff marshallingDiff = new DetailedDiff(new Diff(testXML,
          unmarshalledTestXML));
      Assert.assertTrue(marshallingDiff.identical());

      // ignore requestID, must test similar not identical
      DifferenceListener ignoreRequestID = new IgnoreRequestIDDifferenceListener();

      // test testXML against correctXML
      Diff correctDiff = new Diff(new FileReader(correctXMLFile), new StringReader(
          testXML));
      correctDiff.overrideDifferenceListener(ignoreRequestID);
      DetailedDiff correctDetailedDiff = new DetailedDiff(correctDiff);
      if (!correctDetailedDiff.getAllDifferences().isEmpty()) {
        LOG.debug("differences '{}'", correctDetailedDiff.getAllDifferences());
        LOG.debug("diff '{}'", correctDetailedDiff.toString());
      }
      Assert.assertTrue(correctDetailedDiff.getAllDifferences().isEmpty());
      Assert.assertTrue(correctDetailedDiff.similar());

      // test unmarshalledXML against correctXML
      Diff unmarshalledDiff = new Diff(new FileReader(correctXMLFile), new StringReader(
          unmarshalledTestXML));
      unmarshalledDiff.overrideDifferenceListener(ignoreRequestID);
      DetailedDiff unmarshalledDetailedDiff = new DetailedDiff(unmarshalledDiff);
      if (!unmarshalledDetailedDiff.getAllDifferences().isEmpty()) {
        LOG.debug("differences '{}'", unmarshalledDetailedDiff.getAllDifferences());
        LOG.debug("diff '{}'", unmarshalledDetailedDiff.toString());
      }
      Assert.assertTrue(unmarshalledDetailedDiff.getAllDifferences().isEmpty());
      Assert.assertTrue(unmarshalledDetailedDiff.similar());

      return unmarshalledObject;

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("An error occurred : " + e.getMessage());
      return null;
    }
  }
}
