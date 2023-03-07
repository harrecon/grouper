package edu.internet2.middleware.grouper.app.provisioning;

import java.util.Set;

import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;

public class ProvisioningStateEntity extends ProvisioningStateBase {
<<<<<<< GROUPER_5_BRANCH
<<<<<<< GROUPER_5_BRANCH
  
  /**
   * if this subject is marked as unresolvable in the members table
   */
  private boolean unresolvable;
  

  /**
   * if this subject is marked as unresolvable in the members table
   * @return
   */
  public boolean isUnresolvable() {
    return unresolvable;
  }

  /**
   * if this subject is marked as unresolvable in the members table
   * @param unresolvable
   */
  public void setUnresolvable(boolean unresolvable) {
    this.unresolvable = unresolvable;
  }

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  private boolean selectAllMembershipResultProcessed;
<<<<<<< GROUPER_5_BRANCH
=======
  
>>>>>>> 3c25747 Provisioning related changes - make incremental sync more robust
=======
>>>>>>> dad5d51 Provisioning related changes, wip
  
  /**
   * in incremental, if we're doing entity attributes and any membership events for this entity 
   * are recalc, then recalc all events for this entity in this incremental run and select
   * those memberships from the target. Since this is entity attributes, we need to select those via
   * the entity 
   */
  private boolean selectSomeMemberships;
  
  /**
   * in incremental, if we're doing entity attributes and any membership events for this entity 
   * are recalc, then recalc all events for this entity in this incremental run and select
   * those memberships from the target. Since this is entity attributes, we need to select those via
   * the entity 
   */
  public boolean isSelectSomeMemberships() {
    return selectSomeMemberships;
  }

  /**
   * in incremental, if we're doing entity attributes and any membership events for this entity 
   * are recalc, then recalc all events for this entity in this incremental run and select
   * those memberships from the target. Since this is entity attributes, we need to select those via
   * the entity 
   */
  public void setSelectSomeMemberships(boolean selectSomeMemberships) {
    this.selectSomeMemberships = selectSomeMemberships;
  }

  /**
   * set it to true if we want to select all memberships from the target for this group.
   * This happens when there are too many events for this group or if it's a new group or a deleted group
   * or manually kicked off from the UI
   */
  private boolean selectAllMemberships;
  
  /**
   * set it to true if we want to select all memberships from the target for this group.
   * This happens when there are too many events for this group or if it's a new group or a deleted group
   * or manually kicked off from the UI
   */
  public boolean isSelectAllMemberships() {
    return selectAllMemberships;
  }

  /**
   * set it to true if we want to select all memberships from the target for this group.
   * This happens when there are too many events for this group or if it's a new group or a deleted group
   * or manually kicked off from the UI
   */
  public void setSelectAllMemberships(boolean selectMemberships) {
    this.selectAllMemberships = selectMemberships;
  }
  
  
<<<<<<< HEAD
  private String memberId;
  /**
   * if recalcing the entity memberships 
   */
  private boolean recalcEntityMemberships;

  
  public String getMemberId() {
    return memberId;
  }

  
  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }
  
  private static Set<String> toStringFieldNamesToIgnore = GrouperClientUtils.toSet("provisioningEntityWrapper");
  
  /**
   * 
   */
  @Override
  public String toString() {
    return GrouperClientUtils.toStringReflection(this, toStringFieldNamesToIgnore, "id='" + this.getProvisioningEntityWrapper().getMemberId() + "'");
  }

  private ProvisioningEntityWrapper provisioningEntityWrapper = null;

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  private boolean selectAllMembershipsResultProcessed;

  private boolean selectSomeMembershipsResultProcessed;
  
  
  
  
  public ProvisioningEntityWrapper getProvisioningEntityWrapper() {
    return provisioningEntityWrapper;
  }


  
  public void setProvisioningEntityWrapper(
      ProvisioningEntityWrapper provisioningEntityWrapper) {
    this.provisioningEntityWrapper = provisioningEntityWrapper;
  }


  /**
   * if recalcing the entity memberships 
   * @return
   */
  public boolean isRecalcEntityMemberships() {
    return recalcEntityMemberships;
  }


  /**
   * if recalcing the entity memberships 
   * @param recalcEntityMemberships1
   */
  public void setRecalcEntityMemberships(boolean recalcEntityMemberships1) {
    this.recalcEntityMemberships = recalcEntityMemberships1;
  }

  
  public boolean isSelectAllMembershipResultProcessed() {
    return selectAllMembershipResultProcessed;
  }

  
  public void setSelectAllMembershipResultProcessed(
      boolean selectAllMembershipResultProcessed) {
    this.selectAllMembershipResultProcessed = selectAllMembershipResultProcessed;
  }

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  public boolean isSelectAllMembershipsResultProcessed() {
    return selectAllMembershipsResultProcessed;
  }

  public boolean isSelectSomeMembershipsResultProcessed() {
    return selectSomeMembershipsResultProcessed;
  }

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  public void setSelectAllMembershipsResultProcessed(boolean selectMembershipResultProcessed) {
    this.selectAllMembershipsResultProcessed = selectMembershipResultProcessed;
  }

  public void setSelectSomeMembershipsResultProcessed(
      boolean selectSomeMembershipsResultProcessed) {
    this.selectSomeMembershipsResultProcessed = selectSomeMembershipsResultProcessed;
  }
  
<<<<<<< GROUPER_5_BRANCH
=======

=======
>>>>>>> cde0848eaefb94061e688bf390c1349ca347f98b
  private String memberId;
  /**
   * if recalcing the entity memberships 
   */
  private boolean recalcEntityMemberships;

  
  public String getMemberId() {
    return memberId;
  }

  
  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }
  
  private static Set<String> toStringFieldNamesToIgnore = GrouperClientUtils.toSet("provisioningEntityWrapper");
  
  /**
   * 
   */
  @Override
  public String toString() {
    return GrouperClientUtils.toStringReflection(this, toStringFieldNamesToIgnore, "id='" + this.getProvisioningEntityWrapper().getMemberId() + "'");
  }

  private ProvisioningEntityWrapper provisioningEntityWrapper = null;

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  private boolean selectAllMembershipsResultProcessed;

  private boolean selectSomeMembershipsResultProcessed;
  
  
  
  
  public ProvisioningEntityWrapper getProvisioningEntityWrapper() {
    return provisioningEntityWrapper;
  }


  
  public void setProvisioningEntityWrapper(
      ProvisioningEntityWrapper provisioningEntityWrapper) {
    this.provisioningEntityWrapper = provisioningEntityWrapper;
  }


  /**
   * if recalcing the entity memberships 
   * @return
   */
  public boolean isRecalcEntityMemberships() {
    return recalcEntityMemberships;
  }


  /**
   * if recalcing the entity memberships 
   * @param recalcEntityMemberships1
   */
  public void setRecalcEntityMemberships(boolean recalcEntityMemberships1) {
    this.recalcEntityMemberships = recalcEntityMemberships1;
  }

<<<<<<< HEAD
>>>>>>> 252ebc1 restructure how state is stored in provisioning wrappers
=======
>>>>>>> dad5d51 Provisioning related changes, wip
=======
  
  public boolean isSelectAllMembershipResultProcessed() {
    return selectAllMembershipResultProcessed;
  }

  
  public void setSelectAllMembershipResultProcessed(
      boolean selectAllMembershipResultProcessed) {
    this.selectAllMembershipResultProcessed = selectAllMembershipResultProcessed;
  }

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  public boolean isSelectAllMembershipsResultProcessed() {
    return selectAllMembershipsResultProcessed;
  }

  public boolean isSelectSomeMembershipsResultProcessed() {
    return selectSomeMembershipsResultProcessed;
  }

  /**
   * if the memberships for this entity was attempted to be selected from target
   */
  public void setSelectAllMembershipsResultProcessed(boolean selectMembershipResultProcessed) {
    this.selectAllMembershipsResultProcessed = selectMembershipResultProcessed;
  }

  public void setSelectSomeMembershipsResultProcessed(
      boolean selectSomeMembershipsResultProcessed) {
    this.selectSomeMembershipsResultProcessed = selectSomeMembershipsResultProcessed;
  }
  
>>>>>>> cde0848eaefb94061e688bf390c1349ca347f98b
}
