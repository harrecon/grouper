/* 
 * Copyright (C) 2004 Internet2
 * Copyright (C) 2004 The University Of Chicago
 * All Rights Reserved. 
 *
 * See the LICENSE file in the top-level directory of the 
 * distribution for licensing information.
 */

package edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  java.util.*;


/** 
 * {@link Grouper} group class.
 *
 * @author  blair christensen.
 * @version $Id: GrouperGroup.java,v 1.51 2004-10-11 20:20:37 blair Exp $
 */
public class GrouperGroup {

  // Operational attributes and information
  private String groupKey;
  private String groupType;
  // TODO Stuff into a map?
  private String createTime;
  private String createSubject;
  private String createSource;
  private String modifyTime;
  private String modifySubject;
  private String modifySource;
  private String comment;

  // Grouper attributes (fields)
  private Map  attributes;

  // Grouper Session
  private GrouperSession  grprSession;

  // Does the group exist?
  private boolean  exists;

  /**
   * Create a new object representing a {@link Grouper} group.
   * <p>
   * TODO Document further
   */
  public GrouperGroup() {
    attributes    = new HashMap();
    comment       = null;
    createTime    = null;
    createSubject = null;
    createSource  = null;
    exists        = false;
    groupKey      = null;
    groupType     = "base"; // TODO Don't hardcode this
    grprSession   = null;
    modifyTime    = null;
    modifySubject = null;
    modifySource  = null;
  }

  /*
   * CLASS METHODS
   */

  /**
   * Class method to create a group.
   *
   * @param   s           Session to create the group within.
   * @param   namespace   Namespace to create the group within.
   * @param   name        Name of the group to be created.
   */ 
  public static GrouperGroup create(GrouperSession s, 
                                    String namespace,
                                    String name)
  {
    GrouperGroup g = new GrouperGroup();

    // Initalize aspects of the group.
    g._create(s, namespace, name);

    // Verify that we have everything we need to create a group
    // and that this subject is privileged to create this group.
    if (g._validateCreate()) {
      // And now attempt to add the group to the store
      GrouperBackend.addGroup(s, g);
      g.exists = true;
    }
    return g;
  }

  /**
   * Class method to retrieve a group from the persistent store.
   *
   * @param   s           Session to create the group within.
   * @param   namespace   Namespace of the group to load.
   * @param   name        Name of the group to load.
   * @return  A {@link GrouperGroup} object.
   */
  public static GrouperGroup load(GrouperSession s, 
                                  String namespace,
                                  String name)
  {
    return GrouperBackend.group(s, namespace, name);
  }

  /*
   * PUBLIC METHODS
   */

  /**
   * Get a group attribute.
   *
   * @param   attribute The attribute to get.
   * @return  A {@link GrouperAttribute} object.
   */
  public GrouperAttribute attribute(String attribute) {
    return (GrouperAttribute) attributes.get(attribute);
  }

  /**
   * Set a group attribute.
   * 
   * @param attribute Attribute to set.
   * @param value     Value of attribute.
   */
  public void attribute(String attribute, String value) {
    GrouperAttribute attr = new GrouperAttribute();

    // Attempt to validate whether the attribute is allowed
    if (this._validateAttribute(attribute)) {
      // Setup the attribute, add it to the stash.
      // TODO Require a valid (?) groupKey?
      attr.set(this.groupKey, attribute, value);
      attributes.put(attribute, attr);
    }
  }

  /** 
   * Get all group attributes.
   *
   * @return  A map of all group attributes.
   */
  public Map attributes() {
    return this.attributes;
  }

  /**
   * Does this {@link Grouper} group exist?
   *
   * @return Boolean true if the group exists, false otherwise.
   */
  public boolean exists() {
    if (this.exists == true) {
      // We are already marked as existing.  Assume that our status
      // hasn't changed.
      return this.exists;
    } else {
      // Otherwise attempt to find and load the group from the
      // persistent store.
      if (this.attributes.containsKey("stem")) {
        // We need a stem
        // BDC String stem = this.attribute("stem").value();
        if (this.attributes.containsKey("descriptor")) {
          // And a descriptor
          // BDC String desc = this.attribute("descriptor").value();
          if (this.grprSession != null) {
            // And a session to load a group
            // FIXME Provide a method of confirming a group's existence
            //       that doesn't rely upon loading a group and checking for
            //       the presence of a `groupKey'.
            GrouperGroup g = GrouperBackend.group(this.grprSession,
                                                  this.attribute("stem").value(),
                                                  this.attribute("descriptor").value());
            // Does the returned GrouperGroup object contain a group
            // key?  If so, the group is considered to exist.
            if (g.groupKey() != null) {
              this.exists = true;
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * Return group's unique key (UUID).
   * <p>
   * FIXME Do I really want to this to be public information?  Scale
   *       back if at all possible.
   *
   * @return Group key
   */
  public String groupKey() {
    return this.getGroupKey();
  }

  /**
   * Return group's type.
   *
   * @return Group type
   */
  public String groupType() {
    return this.groupType();
  }

  /**
   * Returns a string representation of the {@link GrouperGroup}
   * object.
   *
   * @return  A string representation of the object.
   */
  public String toString() {
    GrouperAttribute stem = (GrouperAttribute) attributes.get("stem");
    GrouperAttribute desc = (GrouperAttribute) attributes.get("descriptor");
    return this.getClass()  + ":" +
           this.groupKey    + ":" + 
           stem.value()     + ":" +
           desc.value(); 
  }

  /*
   * PRIVATE METHODS
   */

  /*
   * Initialize aspects of the group before creating it.
   *
   * @param s           Session to create the group within.
   * @param stem        Stem of the group to be created.
   * @param descriptor  Descriptor of group to be created.
   */
  private void _create(GrouperSession s, String stem, String descriptor) {
    // Attach session
    this.grprSession  = s;

    // Generate the UUID (groupKey)
    this.setGroupKey( GrouperBackend.uuid() );

    this.attribute("stem", stem);
    this.attribute("descriptor", descriptor);

    // Set some of the operational attributes
    // TODO Most, if not all, of the operational attributes should be
    //      handled by Hibernate interceptors.  A task for another day.
    java.util.Date now = new java.util.Date();
    this.setCreateTime( Long.toString(now.getTime()) );
    this.setCreateSubject( s.whoAmI() );
  }

  /*
   * Validate whether an attribute is valid for the current group type.
   *
   * @return Boolean true if attribute is valid for type or we are
   * unable to valid the attribute at this type, false otherwise.
   */
  private boolean _validateAttribute(String attribute) {
    boolean rv = false;
    if (this.groupType != null) { // FIXME I can do better than this.
      // We have a group type.  Now what?
      if (Grouper.groupField(this.groupType, attribute) == true) {
        // Our attribute passes muster.
        rv = true;
      }
    } else {
      // We don't know the group type so we can't validate.  Shrug our
      // shoulders and say "good enough" for now.
      rv = true;
    }
    return rv;
  }

  /*
   * Validate whether all attributes are valid for the current group
   * type.
   *
   * @return Boolean true if the attributes are valid for the group
   * type, false otherwise.
   */
  private boolean _validateAttributes() {
    for (Iterator iter = attributes.keySet().iterator(); iter.hasNext();) {
      GrouperAttribute attr = (GrouperAttribute) attributes.get( iter.next() );
      // TODO I should (possibly) revalidate the attributes due to
      //      lack of a group type -- or a changed group type.  Add
      //      some sort of dirty flag to trigger|!trigger this from
      //      occurring.
      if ( !this._validateAttribute( attr.field()) ) {
        return false;
      }
    }
    return true;
  }
 
  /*
   * Validate whether a group can be created.
   *
   * @return Boolean true if the group is valid to be created,
   * false otherwise.
   */
  private boolean _validateCreate() {
    if (
        // Do we have a valid group type?
        (Grouper.groupType(this.groupType) == true) &&
        // And a stem?
        (attributes.containsKey("stem"))            &&
        // And a descriptor?
        (attributes.containsKey("descriptor"))      && 
        // And do the stem and descriptor already exist?
        (this.exists() == false)                    && 
        // And are the group attributes valid?
        (this._validateAttributes()) 
        // TODO Member Object for the admin of the group
        // TODO CREATE priv for stem
       )
    {
      return true;
    }
    return false;
  }

  /*
   * Below for Hibernate
   */

  private String getGroupKey() {
    return this.groupKey;
  }

  private void setGroupKey(String groupKey) {
    this.groupKey = groupKey;
  }

  private String getCreateTime() {
    return this.createTime;
  }
 
  private void setCreateTime(String createTime) {
    this.createTime = createTime;
  }
 
  private String getCreateSubject() {
    return this.createSubject;
  }
 
  private void setCreateSubject(String createSubject) {
    this.createSubject = createSubject;
  }
 
  private String getCreateSource() {
    return this.createSource;
  }
 
  private void setCreateSource(String createSource) {
    this.createSource = createSource;
  }
 
  private String getModifyTime() {
    return this.modifyTime;
  }
 
  private void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }
 
  private String getModifySubject() {
    return this.modifySubject;
  }
 
  private void setModifySubject(String modifySubject) {
    this.modifySubject = modifySubject;
  }
 
  private String getModifySource() {
    return this.modifySource;
  }
 
  private void setModifySource(String modifySource) {
    this.modifySource = modifySource;
  }

  private String getComment() {
    return this.comment;
  }

  private void setComment(String comment) {
    this.comment = comment;
  } 

}

