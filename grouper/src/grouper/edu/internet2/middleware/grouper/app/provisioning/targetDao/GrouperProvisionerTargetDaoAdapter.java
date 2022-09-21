/**
 * 
 */
package edu.internet2.middleware.grouper.app.provisioning.targetDao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.app.ldapProvisioning.LdapSyncConfiguration;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioner;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningBehaviorMembershipType;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningConfigurationAttribute;
import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningLists;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningAttribute;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningEntity;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningEntityWrapper;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningGroup;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningGroupWrapper;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningMembership;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningMembershipWrapper;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningObjectChange;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningObjectChangeAction;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningUpdatable;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningUpdatableAttributeAndValue;
import edu.internet2.middleware.grouper.util.GrouperCallable;
import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 * wraps the dao so it can convert methods and see if things are available
 * @author mchyzer-local
 *
 */
public class GrouperProvisionerTargetDaoAdapter extends GrouperProvisionerTargetDaoBase {

  @Override
  public boolean loggingStart() {
    return this.wrappedDao.loggingStart();
  }

  @Override
  public String loggingStop() {
    return this.wrappedDao.loggingStop();
  }


  private GrouperProvisionerTargetDaoBase wrappedDao;
  
  /** logger */
  private static final Log LOG = GrouperUtil.getLog(GrouperProvisionerTargetDaoAdapter.class);
  
  @Override
  public GrouperProvisionerDaoCapabilities getGrouperProvisionerDaoCapabilities() {
    return this.wrappedDao.getGrouperProvisionerDaoCapabilities();
  }

  @Override
  public void setGrouperProvisionerDaoCapabilities(
      GrouperProvisionerDaoCapabilities grouperProvisionerDaoCapabilities) {
    this.wrappedDao.setGrouperProvisionerDaoCapabilities(grouperProvisionerDaoCapabilities);
  }


  @Override
  public void addTargetDaoTimingInfo(TargetDaoTimingInfo targetDaoTimingInfo) {
    this.wrappedDao.addTargetDaoTimingInfo(targetDaoTimingInfo);
  }


  @Override
  public List<TargetDaoTimingInfo> getTargetDaoTimingInfos() {
    return this.wrappedDao.getTargetDaoTimingInfos();
  }


  @Override
  public void setTargetDaoTimingInfos(List<TargetDaoTimingInfo> targetDaoTimingInfos) {
    this.wrappedDao.setTargetDaoTimingInfos(targetDaoTimingInfos);
  }


  public GrouperProvisionerTargetDaoBase getWrappedDao() {
    return wrappedDao;
  }

  
  public void setWrappedDao(GrouperProvisionerTargetDaoBase wrappedDao) {
    this.wrappedDao = wrappedDao;
  }

  public GrouperProvisionerTargetDaoAdapter(GrouperProvisioner grouperProvisioner, GrouperProvisionerTargetDaoBase wrappedDao) {
    super();
    super.setGrouperProvisioner(grouperProvisioner);
    this.wrappedDao = wrappedDao;
  }

  /**
   * 
   */
  public GrouperProvisionerTargetDaoAdapter() {
  }

  @Override
  public void registerGrouperProvisionerDaoCapabilities(
      GrouperProvisionerDaoCapabilities grouperProvisionerDaoCapabilities) {
    // this probably would never be called here, but just delegate just in case
    this.wrappedDao.registerGrouperProvisionerDaoCapabilities(grouperProvisionerDaoCapabilities);
  }


  @Override
  public TargetDaoRetrieveAllGroupsResponse retrieveAllGroups(
      TargetDaoRetrieveAllGroupsRequest targetDaoRetrieveAllGroupsRequest) {
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveAllGroups(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();
  
        TargetDaoRetrieveAllGroupsResponse targetDaoRetrieveAllGroupsResponse = this.wrappedDao.retrieveAllGroups(targetDaoRetrieveAllGroupsRequest);
        
        hasError = logGroups(targetDaoRetrieveAllGroupsResponse.getTargetGroups());
        return targetDaoRetrieveAllGroupsResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveAllGroups");
      }

    }
    throw new RuntimeException("Dao cannot retrieve all groups");
  }

  /**
   * finally block for command logging
   * @param hasError
   * @param method
   */
  private void commandLogFinallyBlock(boolean commandLogStarted, boolean hasError, String method) {
    if (!commandLogStarted) {
      return;
    }
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isLogCommandsAlways()
        || (hasError && this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isLogCommandsOnError())) {
      String debugInfo = this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().loggingStop();
      debugInfo = StringUtils.defaultString(debugInfo, "None implemented for this DAO");
      String theLog = "Command log for provisioner '" + this.getGrouperProvisioner().getConfigId() 
          + "' - '" + this.getGrouperProvisioner().getInstanceId() + "', " + method + ": " + debugInfo;
      if (hasError) {
        this.getGrouperProvisioner().retrieveGrouperProvisioningLogCommands().errorLog(theLog);
      } else {
        this.getGrouperProvisioner().retrieveGrouperProvisioningLogCommands().infoLog(theLog);
      }     
    }
  }


  @Override
  public TargetDaoRetrieveAllEntitiesResponse retrieveAllEntities(
      TargetDaoRetrieveAllEntitiesRequest targetDaoRetrieveAllEntitiesRequest) {
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveAllEntities(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoRetrieveAllEntitiesResponse targetDaoRetrieveAllEntitiesResponse = this.wrappedDao.retrieveAllEntities(targetDaoRetrieveAllEntitiesRequest);
        hasError = logEntities(targetDaoRetrieveAllEntitiesResponse.getTargetEntities());
        return targetDaoRetrieveAllEntitiesResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveAllEntities");
      }
        
    }
    
    throw new RuntimeException("Dao cannot retrieve all entities");
    
  }

  private boolean commandLogStartLoggingIfConfigured() {
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isLogCommandsAlways()
        || this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().isLogCommandsOnError()) {
      return this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().loggingStart();
    }
    return false;
  }


  @Override
  public TargetDaoRetrieveAllMembershipsResponse retrieveAllMemberships(
      TargetDaoRetrieveAllMembershipsRequest targetDaoRetrieveAllMembershipsRequest) {
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveAllMemberships(), false)) {
      
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();
      
        TargetDaoRetrieveAllMembershipsResponse targetDaoRetrieveAllMembershipsResponse = this.wrappedDao.retrieveAllMemberships(targetDaoRetrieveAllMembershipsRequest);
        hasError = logMemberships(targetDaoRetrieveAllMembershipsResponse.getTargetMemberships());
        return targetDaoRetrieveAllMembershipsResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveAllMemberships");
      }

    }

    throw new RuntimeException("Dao cannot retrieve all memberships");
  }


  /**
   * dont call this, call the plural
   */
  @Override
  public TargetDaoDeleteGroupResponse deleteGroup(
      TargetDaoDeleteGroupRequest targetDaoDeleteGroupRequest) {
    throw new RuntimeException("Dont call this, call the plural");
  }

  private int errorCountForDbLogs = 0;

  public void logError(String error) {
    LOG.error(error);
    if (errorCountForDbLogs++ < 100) {
      this.getGrouperProvisioner().retrieveGrouperProvisioningObjectLog().getObjectLog().append(new Timestamp(System.currentTimeMillis())).append(": ERROR: ").append(error).append("\n\n");
    }
  }
  
  /**
   * 
   * @param targetGroup
   */
  private void insertGroupHelper(ProvisioningGroup targetGroup) {
    TargetDaoInsertGroupRequest targetDaoInsertGroupRequest = new TargetDaoInsertGroupRequest(targetGroup);
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      this.wrappedDao.insertGroup(targetDaoInsertGroupRequest);
      hasError = logGroup(targetGroup);
      if (targetGroup.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set inserted group as provisioned: " + this.wrappedDao);
      }
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetGroup.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("groupInsert")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error inserting group " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetGroup.getProvisioned() == null) {
        targetGroup.setProvisioned(false);
      }
      if (targetGroup.getException() == null) {
        targetGroup.setException(e);
      }
      logGroup(targetGroup);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "insertGroup");
    }

  }
  
  @Override
  public TargetDaoInsertGroupResponse insertGroup(TargetDaoInsertGroupRequest targetDaoInsertGroupRequest) {

    throw new RuntimeException("Dont call this, call the plural");

  }

  @Override
  public TargetDaoSendChangesToTargetResponse sendChangesToTarget(
      TargetDaoSendChangesToTargetRequest targetDaoSendChangesToTargetRequest) {
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanSendChangesToTarget(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        this.wrappedDao.sendChangesToTarget(targetDaoSendChangesToTargetRequest);
        if (logProvisioningLists(targetDaoSendChangesToTargetRequest.getTargetObjectInserts())) {
          hasError = true;
        }
        if (logProvisioningLists(targetDaoSendChangesToTargetRequest.getTargetObjectUpdates())) {
          hasError = true;
        }
        if (logProvisioningLists(targetDaoSendChangesToTargetRequest.getTargetObjectDeletes())) {
          hasError = true;
        }
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "sendChangesToTarget");
      }

    }
    
    {
      TargetDaoSendMembershipChangesToTargetRequest targetDaoSendMembershipChangesToTargetRequest = new TargetDaoSendMembershipChangesToTargetRequest(
          new ArrayList<>(), 
          new ArrayList<>(),
          targetDaoSendChangesToTargetRequest.getTargetObjectDeletes().getProvisioningMemberships(),
          new HashMap<>());
      sendMembershipChangesToTarget(targetDaoSendMembershipChangesToTargetRequest);
    }
    
    {
      TargetDaoSendGroupChangesToTargetRequest targetDaoSendGroupChangesToTargetRequest = new TargetDaoSendGroupChangesToTargetRequest(
          targetDaoSendChangesToTargetRequest.getTargetObjectInserts().getProvisioningGroups(), 
          targetDaoSendChangesToTargetRequest.getTargetObjectUpdates().getProvisioningGroups(),
          targetDaoSendChangesToTargetRequest.getTargetObjectDeletes().getProvisioningGroups());
      sendGroupChangesToTarget(targetDaoSendGroupChangesToTargetRequest);
    }
    {
      TargetDaoSendEntityChangesToTargetRequest targetDaoSendEntityChangesToTargetRequest = new TargetDaoSendEntityChangesToTargetRequest(
          targetDaoSendChangesToTargetRequest.getTargetObjectInserts().getProvisioningEntities(), 
          targetDaoSendChangesToTargetRequest.getTargetObjectUpdates().getProvisioningEntities(),
          targetDaoSendChangesToTargetRequest.getTargetObjectDeletes().getProvisioningEntities());
      sendEntityChangesToTarget(targetDaoSendEntityChangesToTargetRequest);
    }
    {
      TargetDaoSendMembershipChangesToTargetRequest targetDaoSendMembershipChangesToTargetRequest = new TargetDaoSendMembershipChangesToTargetRequest(
          targetDaoSendChangesToTargetRequest.getTargetObjectInserts().getProvisioningMemberships(), 
          targetDaoSendChangesToTargetRequest.getTargetObjectUpdates().getProvisioningMemberships(),
          new ArrayList<>(),
          targetDaoSendChangesToTargetRequest.getTargetObjectReplaces().getProvisioningMemberships());
      sendMembershipChangesToTarget(targetDaoSendMembershipChangesToTargetRequest);
    }
    return null;

  }


  @Override
  public TargetDaoSendGroupChangesToTargetResponse sendGroupChangesToTarget(
      TargetDaoSendGroupChangesToTargetRequest targetDaoSendGroupChangesToTargetRequest) {

    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanSendGroupChangesToTarget(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoSendGroupChangesToTargetResponse targetDaoSendGroupChangesToTargetResponse = this.wrappedDao.sendGroupChangesToTarget(targetDaoSendGroupChangesToTargetRequest);
        if (logGroups(targetDaoSendGroupChangesToTargetRequest.getTargetGroupInserts())) {
          hasError = true;
        }
        if (logGroups(targetDaoSendGroupChangesToTargetRequest.getTargetGroupUpdates())) {
          hasError = true;
        }
        if (logGroups(targetDaoSendGroupChangesToTargetRequest.getTargetGroupDeletes())) {
          hasError = true;
        }
        
        Set<ProvisioningGroupWrapper> provisioningGroupWrappersSuccessfullyUpdated = new HashSet<ProvisioningGroupWrapper>();
        for (ProvisioningGroup provisioningGroup : targetDaoSendGroupChangesToTargetRequest.getTargetGroupInserts()) { 
          if (provisioningGroup.getProvisioned() != null && provisioningGroup.getProvisioned()) {
            provisioningGroupWrappersSuccessfullyUpdated.add(provisioningGroup.getProvisioningGroupWrapper());
          }
        }
        
        for (ProvisioningGroup provisioningGroup : targetDaoSendGroupChangesToTargetRequest.getTargetGroupUpdates()) { 
          if (provisioningGroup.getProvisioned() != null && provisioningGroup.getProvisioned()) {
            provisioningGroupWrappersSuccessfullyUpdated.add(provisioningGroup.getProvisioningGroupWrapper());
          }
        }
        
        for (ProvisioningGroup provisioningGroup : targetDaoSendGroupChangesToTargetRequest.getTargetGroupDeletes()) { 
          if (provisioningGroup.getProvisioned() != null && provisioningGroup.getProvisioned()) {
            provisioningGroupWrappersSuccessfullyUpdated.add(provisioningGroup.getProvisioningGroupWrapper());
          }
        }
        
        // update the cache
        this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateGroupLink(provisioningGroupWrappersSuccessfullyUpdated, false);
        
        return targetDaoSendGroupChangesToTargetResponse;

      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "sendGroupChangesToTarget");
      }

    }
    if (GrouperUtil.length(targetDaoSendGroupChangesToTargetRequest.getTargetGroupDeletes()) > 0){
      List<ProvisioningGroup> targetGroupDeletes = targetDaoSendGroupChangesToTargetRequest.getTargetGroupDeletes();
      TargetDaoDeleteGroupsRequest targetDaoDeleteGroupsRequest = new TargetDaoDeleteGroupsRequest();
      targetDaoDeleteGroupsRequest.setTargetGroups(targetGroupDeletes);
      this.deleteGroups(targetDaoDeleteGroupsRequest);
    }

    if (GrouperUtil.length(targetDaoSendGroupChangesToTargetRequest.getTargetGroupInserts()) > 0){
      List<ProvisioningGroup> targetGroupInserts = targetDaoSendGroupChangesToTargetRequest.getTargetGroupInserts();
      
      this.insertGroups(new TargetDaoInsertGroupsRequest(targetGroupInserts));
    }
    if (GrouperUtil.length(targetDaoSendGroupChangesToTargetRequest.getTargetGroupUpdates()) > 0){
      List<ProvisioningGroup> targetGroupUpdates = targetDaoSendGroupChangesToTargetRequest.getTargetGroupUpdates();
      
      this.updateGroups(new TargetDaoUpdateGroupsRequest(targetGroupUpdates));
    }
    return null;

  }

  private TargetDaoUpdateGroupsResponse updateGroupsHelper(
      TargetDaoUpdateGroupsRequest targetDaoUpdateGroupsRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoUpdateGroupsResponse targetDaoUpdateGroupsResponse = this.wrappedDao.updateGroups(targetDaoUpdateGroupsRequest);
      hasError = logGroups(targetDaoUpdateGroupsRequest.getTargetGroups());
      
      for (ProvisioningGroup provisioningGroup : targetDaoUpdateGroupsRequest.getTargetGroups()) { 
        if (provisioningGroup.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set updated group as provisioned: " + this.wrappedDao);
        }
      }

      return targetDaoUpdateGroupsResponse;
    } catch (RuntimeException e) {
      boolean first = true;
      hasError = true;

      for (ProvisioningGroup targetGroup : targetDaoUpdateGroupsRequest.getTargetGroups()) { 
        
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityUpdate")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error updating groups, e.g. " + (targetGroup == null ? null : targetGroup.toString())+ "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        first = false;

        if (targetGroup.getProvisioned() == null) {
          targetGroup.setProvisioned(false);
        }
        if (targetGroup.getException() == null) {
          targetGroup.setException(e);
        }
        setExceptionForMembershipsWhenGroupOrEntityAttributes(null, targetGroup, e);
      }
      logGroups(targetDaoUpdateGroupsRequest.getTargetGroups());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "updateGroups");
    }
    return null;
  }


  @Override
  public TargetDaoUpdateGroupsResponse updateGroups(
      TargetDaoUpdateGroupsRequest targetDaoUpdateGroupsRequest) {
    
    if (GrouperUtil.length(targetDaoUpdateGroupsRequest.getTargetGroups()) == 0) {
      return new TargetDaoUpdateGroupsResponse();
    }
    List<ProvisioningGroup> targetGroups = targetDaoUpdateGroupsRequest.getTargetGroups();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanUpdateGroups(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getUpdateGroupsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetGroups, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningGroup> batchTargetGroups = GrouperUtil.batchList(targetGroups, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("updateGroups_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoUpdateGroupsRequest targetDaoUpdateGroupsRequestLocal = new TargetDaoUpdateGroupsRequest();
            targetDaoUpdateGroupsRequestLocal.setTargetGroups(batchTargetGroups);
            updateGroupsHelper(targetDaoUpdateGroupsRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanUpdateGroup(), false)) {

      for (ProvisioningGroup targetGroup : targetGroups) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("updateGroup_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            TargetDaoUpdateGroupRequest targetDaoUpdateGroupRequest = new TargetDaoUpdateGroupRequest();
            targetDaoUpdateGroupRequest.setTargetGroup(targetGroup);
            updateGroupHelper(targetDaoUpdateGroupRequest);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot update group or groups");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningGroupWrapper> provisioningGroupWrappersSuccessfullyUpdateed = new HashSet<ProvisioningGroupWrapper>();
    for (ProvisioningGroup targetGroup : targetGroups) { 
      if (targetGroup.getProvisioned() != null && targetGroup.getProvisioned()) {
        provisioningGroupWrappersSuccessfullyUpdateed.add(targetGroup.getProvisioningGroupWrapper());
      }

    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateGroupLink(provisioningGroupWrappersSuccessfullyUpdateed, false);
    
    return new TargetDaoUpdateGroupsResponse();

  }

  private void deleteMembershipsHelper(List<ProvisioningMembership> targetMemberships) {
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoDeleteMembershipsRequest targetDaoDeleteMembershipsRequest = new TargetDaoDeleteMembershipsRequest(targetMemberships);

      this.wrappedDao.deleteMemberships(targetDaoDeleteMembershipsRequest);

      for (ProvisioningMembership provisioningMembership : targetMemberships) { 
        if (provisioningMembership.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set deleted membership as provisioned: " + this.wrappedDao);
        }
      }

      hasError = logMemberships(targetMemberships);

    } catch (RuntimeException e) {
      hasError = true;
      boolean first = true;
      for (ProvisioningMembership targetMembership : targetMemberships) { 
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("membershipDelete")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error deleting memberships, e.g. " + (targetMembership == null ? null : targetMembership.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        
        first = false;

        if (targetMembership.getProvisioned() == null) {
          targetMembership.setProvisioned(false);
        }
        if (targetMembership.getException() == null) {
          targetMembership.setException(e);
        }
      }
      logMemberships(targetMemberships);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "deleteMemberships");
    }

  }

  @Override
  public TargetDaoDeleteMembershipsResponse deleteMemberships(
      TargetDaoDeleteMembershipsRequest targetDaoDeleteMembershipsRequest) {

    if (GrouperUtil.length(targetDaoDeleteMembershipsRequest.getTargetMemberships()) == 0) {
      return new TargetDaoDeleteMembershipsResponse();
    }
    List<ProvisioningMembership> targetMemberships = targetDaoDeleteMembershipsRequest.getTargetMemberships();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanDeleteMemberships(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getDeleteMembershipsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetMemberships, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningMembership> batchTargetMemberships = GrouperUtil.batchList(targetMemberships, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("deleteMemberships_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            
            deleteMembershipsHelper(batchTargetMemberships);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanDeleteMembership(), false)) {

      for (ProvisioningMembership targetMembership : targetMemberships) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("deleteMembership_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            deleteMembershipHelper(targetMembership);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot delete membership or memberships");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    return new TargetDaoDeleteMembershipsResponse();
    
  }


  @Override
  public TargetDaoRetrieveAllDataResponse retrieveAllData(
      TargetDaoRetrieveAllDataRequest targetDaoRetrieveAllDataRequest) {

    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveAllData(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();
        TargetDaoRetrieveAllDataResponse targetDaoRetrieveAllDataResponse = this.wrappedDao.retrieveAllData(targetDaoRetrieveAllDataRequest);
        if (targetDaoRetrieveAllDataResponse.getTargetData()!=null) {
          if (logEntities(targetDaoRetrieveAllDataResponse.getTargetData().getProvisioningEntities())) {
            hasError = true;
          }
          if (logGroups(targetDaoRetrieveAllDataResponse.getTargetData().getProvisioningGroups())) {
            hasError = true;
          }
          if (logMemberships(targetDaoRetrieveAllDataResponse.getTargetData().getProvisioningMemberships())) {
            hasError = true;
          }
        }
        return targetDaoRetrieveAllDataResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveAllData");
      }
        
    }

    GrouperProvisioningLists targetObjects = new GrouperProvisioningLists();
    TargetDaoRetrieveAllDataResponse result = new TargetDaoRetrieveAllDataResponse(targetObjects);
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().isSelectGroupsAll()) {
      
      TargetDaoRetrieveAllGroupsResponse targetDaoRetrieveAllGroupsResponse = this.retrieveAllGroups(new TargetDaoRetrieveAllGroupsRequest(true));
      List<ProvisioningGroup> targetGroups = targetDaoRetrieveAllGroupsResponse == null ? null : targetDaoRetrieveAllGroupsResponse.getTargetGroups();
      targetObjects.setProvisioningGroups(targetGroups);

    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().isSelectEntitiesAll()) {
      
      TargetDaoRetrieveAllEntitiesResponse targetDaoRetrieveAllEntitiesResponse = this.retrieveAllEntities(new TargetDaoRetrieveAllEntitiesRequest(true));
      List<ProvisioningEntity> targetEntities = targetDaoRetrieveAllEntitiesResponse == null ? null : targetDaoRetrieveAllEntitiesResponse.getTargetEntities();
      targetObjects.setProvisioningEntities(targetEntities);

    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().isSelectMembershipsAll()) {
      
      TargetDaoRetrieveAllMembershipsResponse targetDaoRetrieveAllMembershipsResponse = this.retrieveAllMemberships(new TargetDaoRetrieveAllMembershipsRequest());
      List<ProvisioningMembership> targetMemberships = targetDaoRetrieveAllMembershipsResponse == null ? null : targetDaoRetrieveAllMembershipsResponse.getTargetMemberships();
      targetObjects.setProvisioningMemberships(targetMemberships);

    }
    return result;
  }


  @Override
  public TargetDaoRetrieveIncrementalDataResponse retrieveIncrementalData(
      TargetDaoRetrieveIncrementalDataRequest targetDaoRetrieveIncementalDataRequest) {
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveIncrementalData(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoRetrieveIncrementalDataResponse targetDaoRetrieveIncrementalDataResponse = this.wrappedDao.retrieveIncrementalData(targetDaoRetrieveIncementalDataRequest);
        if (logEntities(targetDaoRetrieveIncrementalDataResponse.getProvisioningEntities())) {
          hasError = true;
        }
        if (logGroups(targetDaoRetrieveIncrementalDataResponse.getProvisioningGroups())) {
          hasError = true;
        }
        if (logObjects(targetDaoRetrieveIncrementalDataResponse.getProvisioningMemberships())) {
          hasError = true;
        }
        return targetDaoRetrieveIncrementalDataResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveIncrementalData");
      }
        
    }
    TargetDaoRetrieveIncrementalDataResponse result = new TargetDaoRetrieveIncrementalDataResponse();
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().isSelectGroups()) {
      List<ProvisioningGroup> targetGroups = null;
      targetGroups = targetDaoRetrieveIncementalDataRequest.getTargetGroupsForGroupOnly();
      if (GrouperUtil.length(targetGroups) > 0) {
        // if there are groups then this must be implemented
        TargetDaoRetrieveGroupsResponse targetDaoRetrieveGroupsResponse = this.retrieveGroups(
            new TargetDaoRetrieveGroupsRequest(targetGroups, false));
        List<ProvisioningGroup> targetGroupsResult = targetDaoRetrieveGroupsResponse == null ? null : targetDaoRetrieveGroupsResponse.getTargetGroups();
        result.setProvisioningGroups(targetGroupsResult);
      }
    }
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().isSelectEntities()) {
      List<ProvisioningEntity> targetEntities = null;
      targetEntities = targetDaoRetrieveIncementalDataRequest.getTargetEntitiesForEntityOnly();
      if (GrouperUtil.length(targetEntities) > 0) {
        TargetDaoRetrieveEntitiesResponse targetDaoRetrieveEntitiesResponse = this.retrieveEntities(
            new TargetDaoRetrieveEntitiesRequest(targetEntities, false));
        List<ProvisioningEntity> targetEntitiesResult = targetDaoRetrieveEntitiesResponse == null ?
            null : targetDaoRetrieveEntitiesResponse.getTargetEntities();
        result.setProvisioningEntities(targetEntitiesResult);
      }
    }
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().isSelectMemberships()) {
      List<ProvisioningGroup> targetGroups = targetDaoRetrieveIncementalDataRequest.getTargetGroupsForGroupMembershipSync();
      List<ProvisioningEntity> targetEntities = targetDaoRetrieveIncementalDataRequest.getTargetEntitiesForEntityMembershipSync();
      List<Object> targetGroupsEntitiesMemberships = targetDaoRetrieveIncementalDataRequest.getTargetMembershipObjectsForMembershipSync();
      if (GrouperUtil.length(targetGroupsEntitiesMemberships) > 0 || GrouperUtil.length(targetGroups) > 0 || GrouperUtil.length(targetEntities) > 0) {
        TargetDaoRetrieveMembershipsBulkResponse retrieveMembershipsBulk = this.retrieveMembershipsBulk(new TargetDaoRetrieveMembershipsBulkRequest(targetGroups, targetEntities, targetGroupsEntitiesMemberships));
        List<Object> targetMemberships = retrieveMembershipsBulk == null ? null : retrieveMembershipsBulk.getTargetMemberships();
        result.setProvisioningMemberships(targetMemberships);
      }
    }
    return result;

  }

  private TargetDaoRetrieveGroupsResponse retrieveGroupsHelper(
      TargetDaoRetrieveGroupsRequest targetDaoRetrieveGroupsRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      
      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) == 0) {
        TargetDaoRetrieveGroupsResponse targetDaoRetrieveGroupsResponse = this.wrappedDao.retrieveGroups(targetDaoRetrieveGroupsRequest);
        hasError = logGroups(targetDaoRetrieveGroupsResponse.getTargetGroups());
        return targetDaoRetrieveGroupsResponse;
      }
      
      TargetDaoRetrieveGroupsResponse overallResponse = new TargetDaoRetrieveGroupsResponse();
      
      List<ProvisioningGroup> targetGroupsFound = new ArrayList<ProvisioningGroup>();
      
      Set<ProvisioningGroup> groupsRemainingToFind = new HashSet<ProvisioningGroup>(targetDaoRetrieveGroupsRequest.getTargetGroups());

      // cycle through search attributes and past values
      int retrieveGroupsFromCache = 0;
      int retrieveGroupsFromAlternateSearchAttr = 0;
      boolean first = true;
      // current value or historical values
      OUTER: for (boolean currentValue : new boolean[] {true, false}) {
      
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          Set<Object> searchAttributeValues = new HashSet<Object>();
          Map<String, ProvisioningGroup> searchValueToSearchGrouperTargetGroup = new HashMap<String, ProvisioningGroup>();
          for (ProvisioningGroup grouperTargetGroup : groupsRemainingToFind) {
            
            for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(grouperTargetGroup.getSearchIdAttributeNameToValues())) {

              if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
                continue;
              }
              if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
                continue;
              }

              searchValueToSearchGrouperTargetGroup.put(GrouperUtil.stringValue(provisioningUpdatableAttributeAndValue.getAttributeValue()), grouperTargetGroup);
              searchAttributeValues.add(provisioningUpdatableAttributeAndValue.getAttributeValue());
            }              
          }
          if (searchValueToSearchGrouperTargetGroup.size() > 0) {
            // search based on those
            TargetDaoRetrieveGroupsRequest targetDaoRetrieveGroupsRequestNew = new TargetDaoRetrieveGroupsRequest();
            targetDaoRetrieveGroupsRequestNew.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveGroupsRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveGroupsRequestNew.setTargetGroups(new ArrayList<ProvisioningGroup>(searchValueToSearchGrouperTargetGroup.values()));
            targetDaoRetrieveGroupsRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveGroupsRequestNew.setSearchAttributeValues(searchAttributeValues);
            
            TargetDaoRetrieveGroupsResponse targetDaoRetrieveGroupsResponse = this.wrappedDao.retrieveGroups(targetDaoRetrieveGroupsRequestNew);
            hasError = logGroups(targetDaoRetrieveGroupsResponse.getTargetGroups()) || hasError;
            
            // add these to the overall result
            targetGroupsFound.addAll(GrouperUtil.nonNull(targetDaoRetrieveGroupsResponse.getTargetGroups()));

            // pluck each one out from the remaining groups to find
            for (ProvisioningGroup retrievedTargetGroup : GrouperUtil.nonNull(targetDaoRetrieveGroupsResponse.getTargetGroups())) {
              Object targetGroupValue = retrievedTargetGroup.retrieveAttributeValue(searchAttributeName);
              if(!GrouperUtil.isBlank(targetGroupValue)) {
                ProvisioningGroup grouperTargetGroup = searchValueToSearchGrouperTargetGroup.get(GrouperUtil.stringValue(targetGroupValue));
                if (grouperTargetGroup != null) {
                  if (!currentValue) {
                    retrieveGroupsFromCache++;
                  } else if (!first) {
                    retrieveGroupsFromAlternateSearchAttr++;
                  }
                  groupsRemainingToFind.remove(grouperTargetGroup);
                  ProvisioningGroupWrapper provisioningGroupWrapper = grouperTargetGroup.getProvisioningGroupWrapper();
                  if (provisioningGroupWrapper != null) {
                    // if its not null, we should not mess up the object model...
                    if (provisioningGroupWrapper.getTargetProvisioningGroup() == null) {
                      provisioningGroupWrapper.setTargetProvisioningGroup(retrievedTargetGroup);
                      retrievedTargetGroup.setProvisioningGroupWrapper(provisioningGroupWrapper);
                      if (targetDaoRetrieveGroupsResponse.getTargetGroupToTargetNativeGroup() != null) {
                        provisioningGroupWrapper.setTargetNativeGroup(targetDaoRetrieveGroupsResponse.getTargetGroupToTargetNativeGroup().get(retrievedTargetGroup));
                      }
                    }
                  }
                }
              }
            }
          }
          if (groupsRemainingToFind.size() == 0) {
            break OUTER;
          }
        }
        first = false;
      }
      
      if (retrieveGroupsFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromAlternateSearchAttr", oldCount + retrieveGroupsFromAlternateSearchAttr);
      }
      if (retrieveGroupsFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromCache", oldCount + retrieveGroupsFromCache);
      }
      overallResponse.setTargetGroups(targetGroupsFound);
      return overallResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveGroups");
    }
  }


  @Override
  public TargetDaoRetrieveGroupsResponse retrieveGroups(
      TargetDaoRetrieveGroupsRequest targetDaoRetrieveGroupsRequest) {
    
    List<ProvisioningGroup> targetGroups = new ArrayList<ProvisioningGroup>(GrouperUtil.nonNull(targetDaoRetrieveGroupsRequest.getTargetGroups()));
    TargetDaoRetrieveGroupsResponse targetDaoRetrieveGroupsResponse = new TargetDaoRetrieveGroupsResponse();
    
    if (GrouperUtil.length(targetGroups) == 0) {
      return targetDaoRetrieveGroupsResponse;
    }
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveGroups(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getRetrieveGroupsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetGroups, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningGroup> batchTargetGroups = GrouperUtil.batchList(targetGroups, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveGroups_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoRetrieveGroupsRequest targetDaoRetrieveGroupsRequestLocal = new TargetDaoRetrieveGroupsRequest();
            targetDaoRetrieveGroupsRequestLocal.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveGroupsRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveGroupsRequestLocal.setTargetGroups(batchTargetGroups);
            TargetDaoRetrieveGroupsResponse targetDaoRetrieveGroupsResponseLocal = retrieveGroupsHelper(targetDaoRetrieveGroupsRequestLocal);
            synchronized(targetDaoRetrieveGroupsResponse) {
              if (targetDaoRetrieveGroupsResponse.getTargetGroups() == null) {
                targetDaoRetrieveGroupsResponse.setTargetGroups(new ArrayList<ProvisioningGroup>());
              }
              if (targetDaoRetrieveGroupsResponse.getTargetGroupToTargetNativeGroup() == null) {
                targetDaoRetrieveGroupsResponse.setTargetGroupToTargetNativeGroup(new HashMap<ProvisioningGroup,Object>());
              }
              targetDaoRetrieveGroupsResponse.getTargetGroups().addAll(GrouperUtil.nonNull(targetDaoRetrieveGroupsResponseLocal.getTargetGroups()));
              targetDaoRetrieveGroupsResponse.getTargetGroupToTargetNativeGroup().putAll(GrouperUtil.nonNull(targetDaoRetrieveGroupsResponseLocal.getTargetGroupToTargetNativeGroup()));
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveGroup(), false)) {

      for (ProvisioningGroup targetGroup : targetGroups) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveGroup_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            TargetDaoRetrieveGroupRequest targetDaoRetrieveGroupRequestLocal = new TargetDaoRetrieveGroupRequest();
            targetDaoRetrieveGroupRequestLocal.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveGroupsRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveGroupRequestLocal.setTargetGroup(targetGroup);
            TargetDaoRetrieveGroupResponse targetDaoRetrieveGroupResponseLocal = retrieveGroupHelper(targetDaoRetrieveGroupRequestLocal);
            synchronized(targetDaoRetrieveGroupsResponse) {
              if (targetDaoRetrieveGroupsResponse.getTargetGroups() == null) {
                targetDaoRetrieveGroupsResponse.setTargetGroups(new ArrayList<ProvisioningGroup>());
              }
              if (targetDaoRetrieveGroupsResponse.getTargetGroupToTargetNativeGroup() == null) {
                targetDaoRetrieveGroupsResponse.setTargetGroupToTargetNativeGroup(new HashMap<ProvisioningGroup,Object>());
              }
              if (targetDaoRetrieveGroupResponseLocal.getTargetGroup() != null) {
                targetDaoRetrieveGroupsResponse.getTargetGroups().add(targetDaoRetrieveGroupResponseLocal.getTargetGroup());
                if (targetDaoRetrieveGroupResponseLocal.getTargetNativeGroup() != null) {
                  targetDaoRetrieveGroupsResponse.getTargetGroupToTargetNativeGroup().put(targetDaoRetrieveGroupResponseLocal.getTargetGroup(), targetDaoRetrieveGroupResponseLocal.getTargetNativeGroup());
                }
              }
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot retrieve group or groups");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningGroupWrapper> provisioningGroupWrappersSuccessfullyRetrieved = new HashSet<ProvisioningGroupWrapper>();
    for (ProvisioningGroup targetGroup : GrouperUtil.nonNull(targetDaoRetrieveGroupsResponse.getTargetGroups())) { 
      if (targetGroup.getProvisioningGroupWrapper() != null) {
        provisioningGroupWrappersSuccessfullyRetrieved.add(targetGroup.getProvisioningGroupWrapper());
      }
    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateGroupLink(provisioningGroupWrappersSuccessfullyRetrieved, false);
    
    return targetDaoRetrieveGroupsResponse;

  }


  @Override
  public TargetDaoRetrieveMembershipsBulkResponse retrieveMembershipsBulk(
      TargetDaoRetrieveMembershipsBulkRequest targetDaoRetrieveMembershipsBulkRequest) {
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsBulk(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoRetrieveMembershipsBulkResponse targetDaoRetrieveMembershipsBulkResponse = this.wrappedDao.retrieveMembershipsBulk(targetDaoRetrieveMembershipsBulkRequest);
        hasError = logObjects(targetDaoRetrieveMembershipsBulkResponse.getTargetMemberships());
        return targetDaoRetrieveMembershipsBulkResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMembershipsBulk");
      }

    }

    List<Object> targetMembershipsResults = new ArrayList<Object>();
    
    List<ProvisioningGroup> targetGroups = targetDaoRetrieveMembershipsBulkRequest == null ? null : targetDaoRetrieveMembershipsBulkRequest.getTargetGroupsForAllMemberships();
    
    if (GrouperUtil.length(targetGroups) > 0) {
      TargetDaoRetrieveMembershipsByGroupsResponse retrieveMembershipsByGroups = this.retrieveMembershipsByGroups(new TargetDaoRetrieveMembershipsByGroupsRequest(targetGroups));
      if (retrieveMembershipsByGroups != null && GrouperUtil.length(retrieveMembershipsByGroups.getTargetMemberships()) > 0) {
        targetMembershipsResults.addAll(retrieveMembershipsByGroups.getTargetMemberships());
      }
    }
    
    List<ProvisioningEntity> targetEntities = targetDaoRetrieveMembershipsBulkRequest == null ? null : targetDaoRetrieveMembershipsBulkRequest.getTargetEntitiesForAllMemberships();
    if (GrouperUtil.length(targetEntities) > 0) {
      TargetDaoRetrieveMembershipsByEntitiesResponse targetDaoRetrieveMembershipsByEntitiesResponse =
          this.retrieveMembershipsByEntities(new TargetDaoRetrieveMembershipsByEntitiesRequest(targetEntities));
      List<Object> targetMembershipsResult = targetDaoRetrieveMembershipsByEntitiesResponse == null ? null : targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships();
      targetMembershipsResults.addAll(targetMembershipsResult);
    }
    
    List<Object> targetMembershipsInput = targetDaoRetrieveMembershipsBulkRequest == null ? 
        null : targetDaoRetrieveMembershipsBulkRequest.getTargetMemberships();
    if (GrouperUtil.length(targetMembershipsInput) > 0) {
      
      TargetDaoRetrieveMembershipsResponse retrieveMembershipsResponse 
        = this.retrieveMemberships(new TargetDaoRetrieveMembershipsRequest(
          targetMembershipsInput));
      if (retrieveMembershipsResponse != null && GrouperUtil.length(retrieveMembershipsResponse.getTargetMemberships()) > 0) {
        targetMembershipsResults.addAll(retrieveMembershipsResponse.getTargetMemberships());
      }
    }
    
    return new TargetDaoRetrieveMembershipsBulkResponse(targetMembershipsResults);
  }

  private TargetDaoRetrieveMembershipsByGroupsResponse retrieveMembershipsByGroupsHelper(
      TargetDaoRetrieveMembershipsByGroupsRequest targetDaoRetrieveMembershipsByGroupsRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoRetrieveMembershipsByGroupsResponse overallResponse = new TargetDaoRetrieveMembershipsByGroupsResponse();

      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) == 0) {
        TargetDaoRetrieveMembershipsByGroupsResponse targetDaoRetrieveMembershipsByGroupsResponse = this.wrappedDao.retrieveMembershipsByGroups(targetDaoRetrieveMembershipsByGroupsRequest);
        hasError = logObjects(targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships());
        return overallResponse;
      }
              
      List<ProvisioningGroup> targetGroupsFound = new ArrayList<ProvisioningGroup>();
      
      Set<ProvisioningGroup> groupsRemainingToFind = new HashSet<ProvisioningGroup>(targetDaoRetrieveMembershipsByGroupsRequest.getTargetGroups());

      // cycle through search attributes and past values
      int retrieveGroupsFromCache = 0;
      int retrieveGroupsFromAlternateSearchAttr = 0;
      boolean first = true;
      // current value or historical values
      OUTER: for (boolean currentValue : new boolean[] {true, false}) {
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          Set<Object> searchAttributeValues = new HashSet<Object>();
          Map<Object, ProvisioningGroup> searchValueToSearchGrouperTargetGroup = new HashMap<Object, ProvisioningGroup>();
          for (ProvisioningGroup grouperTargetGroup : groupsRemainingToFind) {
            for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(grouperTargetGroup.getSearchIdAttributeNameToValues())) {

              if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
                continue;
              }
              if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
                continue;
              }

              searchValueToSearchGrouperTargetGroup.put(GrouperUtil.stringValue(provisioningUpdatableAttributeAndValue.getAttributeValue()), grouperTargetGroup);
              searchAttributeValues.add(provisioningUpdatableAttributeAndValue.getAttributeValue());
            }              
            
          }
          if (searchValueToSearchGrouperTargetGroup.size() > 0) {
            // search based on those
            TargetDaoRetrieveMembershipsByGroupsRequest targetDaoRetrieveMembershipsByGroupsRequestNew = new TargetDaoRetrieveMembershipsByGroupsRequest();
            targetDaoRetrieveMembershipsByGroupsRequestNew.setTargetGroups(new ArrayList<ProvisioningGroup>(searchValueToSearchGrouperTargetGroup.values()));
            targetDaoRetrieveMembershipsByGroupsRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveMembershipsByGroupsRequestNew.setSearchAttributeValues(searchAttributeValues);
            
            TargetDaoRetrieveMembershipsByGroupsResponse targetDaoRetrieveMembershipsByGroupsResponse = this.wrappedDao.retrieveMembershipsByGroups(targetDaoRetrieveMembershipsByGroupsRequestNew);
            hasError = logObjects(targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships()) || hasError;

            // we cant keep track of what groups were retrieved if not doing group memberships
            if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() != GrouperProvisioningBehaviorMembershipType.groupAttributes) {
              return targetDaoRetrieveMembershipsByGroupsResponse;
            }

            // add these to the overall result
            for (Object membershipObject : targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships()) {
              ProvisioningGroup retrievedTargetGroup = (ProvisioningGroup)membershipObject;
              targetGroupsFound.add(retrievedTargetGroup);

              // pluck each one out from the remaining groups to find
              Object targetGroupValue = retrievedTargetGroup.retrieveAttributeValue(searchAttributeName);
              if(!GrouperUtil.isBlank(targetGroupValue)) {
                ProvisioningGroup grouperTargetGroup = searchValueToSearchGrouperTargetGroup.get(GrouperUtil.stringValue(targetGroupValue));
                if (grouperTargetGroup != null) {
                  if (!currentValue) {
                    retrieveGroupsFromCache++;
                  } else if (!first) {
                    retrieveGroupsFromAlternateSearchAttr++;
                  }
                  groupsRemainingToFind.remove(grouperTargetGroup);
                  ProvisioningGroupWrapper provisioningGroupWrapper = grouperTargetGroup.getProvisioningGroupWrapper();
                  if (provisioningGroupWrapper != null) {
                    // if its not null, we should not mess up the object model...
                    if (provisioningGroupWrapper.getTargetProvisioningGroup() == null) {
                      provisioningGroupWrapper.setTargetProvisioningGroup(retrievedTargetGroup);
                      retrievedTargetGroup.setProvisioningGroupWrapper(provisioningGroupWrapper);
                      if (targetDaoRetrieveMembershipsByGroupsResponse.getTargetGroupToTargetNativeGroup() != null) {
                        provisioningGroupWrapper.setTargetNativeGroup(targetDaoRetrieveMembershipsByGroupsResponse.getTargetGroupToTargetNativeGroup().get(retrievedTargetGroup));
                      }
                    }
                  }

                }
              }
            }
          }
          if (groupsRemainingToFind.size() == 0) {
            break OUTER;
          }
        }
        first = false;
      }
      
      if (retrieveGroupsFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromAlternateSearchAttr", oldCount + retrieveGroupsFromAlternateSearchAttr);
      }
      if (retrieveGroupsFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromCache", oldCount + retrieveGroupsFromCache);
      }
      overallResponse.setTargetMemberships((List<Object>)(Object)targetGroupsFound);
      return overallResponse;
      
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMembershipsByGroups");
    }
  }


  @Override
  public TargetDaoRetrieveMembershipsByGroupsResponse retrieveMembershipsByGroups(
      TargetDaoRetrieveMembershipsByGroupsRequest targetDaoRetrieveMembershipsByGroupsRequest) {
    
    List<ProvisioningGroup> targetGroups = new ArrayList<ProvisioningGroup>(GrouperUtil.nonNull(targetDaoRetrieveMembershipsByGroupsRequest.getTargetGroups()));
    TargetDaoRetrieveMembershipsByGroupsResponse targetDaoRetrieveMembershipsByGroupsResponse = new TargetDaoRetrieveMembershipsByGroupsResponse();
    
    if (GrouperUtil.length(targetGroups) == 0) {
      return targetDaoRetrieveMembershipsByGroupsResponse;
    }
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroups(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getRetrieveGroupsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetGroups, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningGroup> batchTargetGroups = GrouperUtil.batchList(targetGroups, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveGroups_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoRetrieveMembershipsByGroupsRequest targetDaoRetrieveMembershipsByGroupsRequestLocal = new TargetDaoRetrieveMembershipsByGroupsRequest();
            targetDaoRetrieveMembershipsByGroupsRequestLocal.setTargetGroups(batchTargetGroups);
            TargetDaoRetrieveMembershipsByGroupsResponse targetDaoRetrieveGroupsResponseLocal = retrieveMembershipsByGroupsHelper(targetDaoRetrieveMembershipsByGroupsRequestLocal);
            synchronized(targetDaoRetrieveMembershipsByGroupsResponse) {
              if (targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships() == null) {
                targetDaoRetrieveMembershipsByGroupsResponse.setTargetMemberships(new ArrayList<Object>());
              }
              if (targetDaoRetrieveMembershipsByGroupsResponse.getTargetGroupToTargetNativeGroup() == null) {
                targetDaoRetrieveMembershipsByGroupsResponse.setTargetGroupToTargetNativeGroup(new HashMap<ProvisioningGroup,Object>());
              }
              targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships().addAll(GrouperUtil.nonNull(targetDaoRetrieveGroupsResponseLocal.getTargetMemberships()));
              targetDaoRetrieveMembershipsByGroupsResponse.getTargetGroupToTargetNativeGroup().putAll(GrouperUtil.nonNull(targetDaoRetrieveGroupsResponseLocal.getTargetGroupToTargetNativeGroup()));
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroup(), false)) {

      for (ProvisioningGroup targetGroup : targetGroups) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveGroup_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            TargetDaoRetrieveMembershipsByGroupRequest targetDaoRetrieveMembershipsByGroupRequestLocal = new TargetDaoRetrieveMembershipsByGroupRequest();
            targetDaoRetrieveMembershipsByGroupRequestLocal.setTargetGroup(targetGroup);
            TargetDaoRetrieveMembershipsByGroupResponse targetDaoRetrieveMembershipsByGroupResponseLocal = retrieveMembershipsByGroupHelper(targetDaoRetrieveMembershipsByGroupRequestLocal);
            synchronized(targetDaoRetrieveMembershipsByGroupsResponse) {
              if (targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships() == null) {
                targetDaoRetrieveMembershipsByGroupsResponse.setTargetMemberships(new ArrayList<Object>());
              }
              if (targetDaoRetrieveMembershipsByGroupsResponse.getTargetGroupToTargetNativeGroup() == null) {
                targetDaoRetrieveMembershipsByGroupsResponse.setTargetGroupToTargetNativeGroup(new HashMap<ProvisioningGroup,Object>());
              }
              targetDaoRetrieveMembershipsByGroupsResponse.getTargetMemberships().addAll(GrouperUtil.nonNull(targetDaoRetrieveMembershipsByGroupResponseLocal.getTargetMemberships()));
              if (targetDaoRetrieveMembershipsByGroupResponseLocal.getTargetNativeGroup() != null) {
                targetDaoRetrieveMembershipsByGroupsResponse.getTargetGroupToTargetNativeGroup().put(targetGroup, targetDaoRetrieveMembershipsByGroupResponseLocal.getTargetNativeGroup());
              }
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot retrieve memberships by group or groups");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    return targetDaoRetrieveMembershipsByGroupsResponse;
  }


  private TargetDaoRetrieveMembershipsByGroupResponse retrieveMembershipsByGroupHelper(
      TargetDaoRetrieveMembershipsByGroupRequest targetDaoRetrieveMembershipsByGroupRequest) {

    boolean hasError = false;
    boolean commandLogStarted = false;

    int retrieveGroupsFromCache = 0;
    int retrieveGroupsFromAlternateSearchAttr = 0;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      
      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) == 0) {

        TargetDaoRetrieveMembershipsByGroupResponse targetDaoRetrieveMembershipsByGroupResponse = this.wrappedDao.retrieveMembershipsByGroup(targetDaoRetrieveMembershipsByGroupRequest);
        hasError = logGroup(targetDaoRetrieveMembershipsByGroupRequest.getTargetGroup());
        return targetDaoRetrieveMembershipsByGroupResponse;
      }
      
      TargetDaoRetrieveMembershipsByGroupResponse targetDaoRetrieveMembershipsByGroupResponse = new TargetDaoRetrieveMembershipsByGroupResponse();
      
      // cycle through search attributes and past values and find the first one
      boolean first = true;
      // current value or historical values
      for (boolean currentValue : new boolean[] {true, false}) {
      
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          Set<Object> searchAttributeValues = new HashSet<Object>();
          
          for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(
              targetDaoRetrieveMembershipsByGroupRequest.getTargetGroup().getSearchIdAttributeNameToValues())) {

            if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
              continue;
            }
            if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
              continue;
            }

            searchAttributeValues.add(provisioningUpdatableAttributeAndValue.getAttributeValue());
          }              

          for (Object searchAttributeValue : searchAttributeValues) {
            // search based on those
            TargetDaoRetrieveMembershipsByGroupRequest targetDaoRetrieveMembershipsByGroupRequestNew = new TargetDaoRetrieveMembershipsByGroupRequest();
            targetDaoRetrieveMembershipsByGroupRequestNew.setTargetGroup(targetDaoRetrieveMembershipsByGroupRequest.getTargetGroup());
            targetDaoRetrieveMembershipsByGroupRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveMembershipsByGroupRequestNew.setSearchAttributeValue(searchAttributeValue);
            
            targetDaoRetrieveMembershipsByGroupResponse = this.wrappedDao.retrieveMembershipsByGroup(targetDaoRetrieveMembershipsByGroupRequestNew);
            hasError = logObjects(targetDaoRetrieveMembershipsByGroupResponse.getTargetMemberships()) || hasError;
            
            // if not group attributes, as soon as we find a value, just run that and return.  if the group cant be found it will do a full recalc
            if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() != GrouperProvisioningBehaviorMembershipType.groupAttributes) {
              return targetDaoRetrieveMembershipsByGroupResponse;
            }

            if (GrouperUtil.length(targetDaoRetrieveMembershipsByGroupResponse.getTargetMemberships()) > 0) {
              if (!currentValue) {
                retrieveGroupsFromCache++;
              } else if (!first) {
                retrieveGroupsFromAlternateSearchAttr++;
              }
              ProvisioningGroupWrapper provisioningGroupWrapper = targetDaoRetrieveMembershipsByGroupRequest.getTargetGroup().getProvisioningGroupWrapper();
              if (provisioningGroupWrapper != null && GrouperUtil.length(targetDaoRetrieveMembershipsByGroupResponse.getTargetMemberships()) == 1
                  && targetDaoRetrieveMembershipsByGroupResponse.getTargetMemberships().get(0) instanceof ProvisioningGroup ){
                // if its not null, we should not mess up the object model...
                if (provisioningGroupWrapper.getTargetProvisioningGroup() == null) {
                  provisioningGroupWrapper.setTargetProvisioningGroup((ProvisioningGroup)targetDaoRetrieveMembershipsByGroupResponse.getTargetMemberships().get(0));
                  ((ProvisioningGroup)targetDaoRetrieveMembershipsByGroupResponse.getTargetMemberships().get(0)).setProvisioningGroupWrapper(provisioningGroupWrapper);
                  provisioningGroupWrapper.setTargetNativeGroup(targetDaoRetrieveMembershipsByGroupResponse.getTargetNativeGroup());
                }
              }

              return targetDaoRetrieveMembershipsByGroupResponse;
            }

          }
        }
        first = false;
      }
      if (retrieveGroupsFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromAlternateSearchAttr", oldCount + retrieveGroupsFromAlternateSearchAttr);
      }
      if (retrieveGroupsFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromCache", oldCount + retrieveGroupsFromCache);
      }

      return targetDaoRetrieveMembershipsByGroupResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMembershipsByGroup");
    }

  }

  @Override
  public TargetDaoRetrieveMembershipsByGroupResponse retrieveMembershipsByGroup(
      TargetDaoRetrieveMembershipsByGroupRequest targetDaoRetrieveMembershipsByGroupRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }

  private TargetDaoRetrieveMembershipsByEntitiesResponse retrieveMembershipsByEntitiesHelper(
      TargetDaoRetrieveMembershipsByEntitiesRequest targetDaoRetrieveMembershipsByEntitiesRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoRetrieveMembershipsByEntitiesResponse overallResponse = new TargetDaoRetrieveMembershipsByEntitiesResponse();

      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) == 0) {
        TargetDaoRetrieveMembershipsByEntitiesResponse targetDaoRetrieveMembershipsByEntitiesResponse = this.wrappedDao.retrieveMembershipsByEntities(targetDaoRetrieveMembershipsByEntitiesRequest);
        hasError = logObjects(targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships());
        return overallResponse;
      }
              
      List<ProvisioningEntity> targetEntitiesFound = new ArrayList<ProvisioningEntity>();
      
      Set<ProvisioningEntity> entitiesRemainingToFind = new HashSet<ProvisioningEntity>(targetDaoRetrieveMembershipsByEntitiesRequest.getTargetEntities());

      // cycle through search attributes and past values
      int retrieveEntitiesFromCache = 0;
      int retrieveEntitiesFromAlternateSearchAttr = 0;
      boolean first = true;
      // current value or historical values
      OUTER: for (boolean currentValue : new boolean[] {true, false}) {
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          Set<Object> searchAttributeValues = new HashSet<Object>();
          Map<Object, ProvisioningEntity> searchValueToSearchGrouperTargetEntity = new HashMap<Object, ProvisioningEntity>();
          for (ProvisioningEntity grouperTargetEntity : entitiesRemainingToFind) {
            for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(grouperTargetEntity.getSearchIdAttributeNameToValues())) {

              if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
                continue;
              }
              if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
                continue;
              }

              searchValueToSearchGrouperTargetEntity.put(GrouperUtil.stringValue(provisioningUpdatableAttributeAndValue.getAttributeValue()), grouperTargetEntity);
              searchAttributeValues.add(provisioningUpdatableAttributeAndValue.getAttributeValue());
            }              
            
          }
          if (searchValueToSearchGrouperTargetEntity.size() > 0) {
            // search based on those
            TargetDaoRetrieveMembershipsByEntitiesRequest targetDaoRetrieveMembershipsByEntitiesRequestNew = new TargetDaoRetrieveMembershipsByEntitiesRequest();
            targetDaoRetrieveMembershipsByEntitiesRequestNew.setTargetEntities(new ArrayList<ProvisioningEntity>(searchValueToSearchGrouperTargetEntity.values()));
            targetDaoRetrieveMembershipsByEntitiesRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveMembershipsByEntitiesRequestNew.setSearchAttributeValues(searchAttributeValues);
            
            TargetDaoRetrieveMembershipsByEntitiesResponse targetDaoRetrieveMembershipsByEntitiesResponse = this.wrappedDao.retrieveMembershipsByEntities(targetDaoRetrieveMembershipsByEntitiesRequestNew);
            hasError = logObjects(targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships()) || hasError;

            // we cant keep track of what entities were retrieved if not doing entity memberships
            if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() != GrouperProvisioningBehaviorMembershipType.entityAttributes) {
              return targetDaoRetrieveMembershipsByEntitiesResponse;
            }

            // add these to the overall result
            for (Object membershipObject : targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships()) {
              ProvisioningEntity retrievedTargetEntity = (ProvisioningEntity)membershipObject;
              targetEntitiesFound.add(retrievedTargetEntity);

              // pluck each one out from the remaining entities to find
              Object targetEntityValue = retrievedTargetEntity.retrieveAttributeValue(searchAttributeName);
              if(!GrouperUtil.isBlank(targetEntityValue)) {
                ProvisioningEntity grouperTargetEntity = searchValueToSearchGrouperTargetEntity.get(GrouperUtil.stringValue(targetEntityValue));
                if (grouperTargetEntity != null) {
                  if (!currentValue) {
                    retrieveEntitiesFromCache++;
                  } else if (!first) {
                    retrieveEntitiesFromAlternateSearchAttr++;
                  }
                  entitiesRemainingToFind.remove(grouperTargetEntity);
                  ProvisioningEntityWrapper provisioningEntityWrapper = grouperTargetEntity.getProvisioningEntityWrapper();
                  if (provisioningEntityWrapper != null) {
                    // if its not null, we should not mess up the object model...
                    if (provisioningEntityWrapper.getTargetProvisioningEntity() == null) {
                      provisioningEntityWrapper.setTargetProvisioningEntity(retrievedTargetEntity);
                      retrievedTargetEntity.setProvisioningEntityWrapper(provisioningEntityWrapper);
                      if (targetDaoRetrieveMembershipsByEntitiesResponse.getTargetEntityToTargetNativeEntity() != null) {
                        provisioningEntityWrapper.setTargetNativeEntity(targetDaoRetrieveMembershipsByEntitiesResponse.getTargetEntityToTargetNativeEntity().get(retrievedTargetEntity));
                      }
                    }
                  }

                }
              }
            }
          }
          if (entitiesRemainingToFind.size() == 0) {
            break OUTER;
          }
        }
        first = false;
      }
      
      if (retrieveEntitiesFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromAlternateSearchAttr", oldCount + retrieveEntitiesFromAlternateSearchAttr);
      }
      if (retrieveEntitiesFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromCache", oldCount + retrieveEntitiesFromCache);
      }
      overallResponse.setTargetMemberships((List<Object>)(Object)targetEntitiesFound);
      return overallResponse;
      
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMembershipsByEntities");
    }
  }


  @Override
  public TargetDaoRetrieveMembershipsByEntitiesResponse retrieveMembershipsByEntities(
      TargetDaoRetrieveMembershipsByEntitiesRequest targetDaoRetrieveMembershipsByEntitiesRequest) {
    
    List<ProvisioningEntity> targetEntities = new ArrayList<ProvisioningEntity>(GrouperUtil.nonNull(targetDaoRetrieveMembershipsByEntitiesRequest.getTargetEntities()));
    TargetDaoRetrieveMembershipsByEntitiesResponse targetDaoRetrieveMembershipsByEntitiesResponse = new TargetDaoRetrieveMembershipsByEntitiesResponse();
    
    if (GrouperUtil.length(targetEntities) == 0) {
      return targetDaoRetrieveMembershipsByEntitiesResponse;
    }
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntities(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getRetrieveEntitiesBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetEntities, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningEntity> batchTargetEntities = GrouperUtil.batchList(targetEntities, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveEntities_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoRetrieveMembershipsByEntitiesRequest targetDaoRetrieveMembershipsByEntitiesRequestLocal = new TargetDaoRetrieveMembershipsByEntitiesRequest();
            targetDaoRetrieveMembershipsByEntitiesRequestLocal.setTargetEntities(batchTargetEntities);
            TargetDaoRetrieveMembershipsByEntitiesResponse targetDaoRetrieveMembershipsByEntitiesResponseLocal = retrieveMembershipsByEntitiesHelper(targetDaoRetrieveMembershipsByEntitiesRequestLocal);
            synchronized(targetDaoRetrieveMembershipsByEntitiesResponse) {
              if (targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships() == null) {
                targetDaoRetrieveMembershipsByEntitiesResponse.setTargetMemberships(new ArrayList<Object>());
              }
              if (targetDaoRetrieveMembershipsByEntitiesResponse.getTargetEntityToTargetNativeEntity() == null) {
                targetDaoRetrieveMembershipsByEntitiesResponse.setTargetEntityToTargetNativeEntity(new HashMap<ProvisioningEntity,Object>());
              }
              targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships().addAll(GrouperUtil.nonNull(targetDaoRetrieveMembershipsByEntitiesResponseLocal.getTargetMemberships()));
              targetDaoRetrieveMembershipsByEntitiesResponse.getTargetEntityToTargetNativeEntity().putAll(GrouperUtil.nonNull(targetDaoRetrieveMembershipsByEntitiesResponseLocal.getTargetEntityToTargetNativeEntity()));
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntity(), false)) {

      for (ProvisioningEntity targetEntity : targetEntities) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveEntity_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            TargetDaoRetrieveMembershipsByEntityRequest targetDaoRetrieveMembershipsByEntityRequestLocal = new TargetDaoRetrieveMembershipsByEntityRequest();
            targetDaoRetrieveMembershipsByEntityRequestLocal.setTargetEntity(targetEntity);
            TargetDaoRetrieveMembershipsByEntityResponse targetDaoRetrieveMembershipsByEntityResponseLocal = retrieveMembershipsByEntityHelper(targetDaoRetrieveMembershipsByEntityRequestLocal);
            synchronized(targetDaoRetrieveMembershipsByEntitiesResponse) {
              if (targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships() == null) {
                targetDaoRetrieveMembershipsByEntitiesResponse.setTargetMemberships(new ArrayList<Object>());
              }
              if (targetDaoRetrieveMembershipsByEntitiesResponse.getTargetEntityToTargetNativeEntity() == null) {
                targetDaoRetrieveMembershipsByEntitiesResponse.setTargetEntityToTargetNativeEntity(new HashMap<ProvisioningEntity,Object>());
              }
              if (targetDaoRetrieveMembershipsByEntityResponseLocal.getTargetNativeEntity() != null) {
                targetDaoRetrieveMembershipsByEntitiesResponse.getTargetEntityToTargetNativeEntity().put(targetEntity, targetDaoRetrieveMembershipsByEntityResponseLocal.getTargetNativeEntity());
              }
              targetDaoRetrieveMembershipsByEntitiesResponse.getTargetMemberships().addAll(GrouperUtil.nonNull(targetDaoRetrieveMembershipsByEntityResponseLocal.getTargetMemberships()));
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot retrieve memberships by entity or entities");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    return targetDaoRetrieveMembershipsByEntitiesResponse;
  }

  /**
   * 
   * @param targetDaoRetrieveMembershipsByEntityRequest
   * @return result
   */
  private TargetDaoRetrieveMembershipsByEntityResponse retrieveMembershipsByEntityHelper(
      TargetDaoRetrieveMembershipsByEntityRequest targetDaoRetrieveMembershipsByEntityRequest) {
    boolean hasError = false;
    boolean commandLogStarted = false;

    int retrieveEntitiesFromCache = 0;
    int retrieveEntitiesFromAlternateSearchAttr = 0;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      
      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) == 0) {

        TargetDaoRetrieveMembershipsByEntityResponse targetDaoRetrieveMembershipsByEntityResponse = this.wrappedDao.retrieveMembershipsByEntity(targetDaoRetrieveMembershipsByEntityRequest);
        hasError = logEntity(targetDaoRetrieveMembershipsByEntityRequest.getTargetEntity());
        return targetDaoRetrieveMembershipsByEntityResponse;
      }
      
      TargetDaoRetrieveMembershipsByEntityResponse targetDaoRetrieveMembershipsByEntityResponse = new TargetDaoRetrieveMembershipsByEntityResponse();
      
      // cycle through search attributes and past values and find the first one
      boolean first = true;
      // current value or historical values
      for (boolean currentValue : new boolean[] {true, false}) {
      
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          Set<Object> searchAttributeValues = new HashSet<Object>();
          
          for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(
              targetDaoRetrieveMembershipsByEntityRequest.getTargetEntity().getSearchIdAttributeNameToValues())) {

            if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
              continue;
            }
            if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
              continue;
            }

            searchAttributeValues.add(provisioningUpdatableAttributeAndValue.getAttributeValue());
          }              

          for (Object searchAttributeValue : searchAttributeValues) {
            // search based on those
            TargetDaoRetrieveMembershipsByEntityRequest targetDaoRetrieveMembershipsByEntityRequestNew = new TargetDaoRetrieveMembershipsByEntityRequest();
            targetDaoRetrieveMembershipsByEntityRequestNew.setTargetEntity(targetDaoRetrieveMembershipsByEntityRequest.getTargetEntity());
            targetDaoRetrieveMembershipsByEntityRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveMembershipsByEntityRequestNew.setSearchAttributeValue(searchAttributeValue);
            
            targetDaoRetrieveMembershipsByEntityResponse = this.wrappedDao.retrieveMembershipsByEntity(targetDaoRetrieveMembershipsByEntityRequestNew);
            hasError = logObjects(targetDaoRetrieveMembershipsByEntityResponse.getTargetMemberships()) || hasError;
            
            // if not entity attributes, as soon as we find a value, just run that and return.  if the entity cant be found it will do a full recalc
            if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() != GrouperProvisioningBehaviorMembershipType.entityAttributes) {
              return targetDaoRetrieveMembershipsByEntityResponse;
            }

            if (GrouperUtil.length(targetDaoRetrieveMembershipsByEntityResponse.getTargetMemberships()) > 0) {
              if (!currentValue) {
                retrieveEntitiesFromCache++;
              } else if (!first) {
                retrieveEntitiesFromAlternateSearchAttr++;
              }
              ProvisioningEntityWrapper provisioningEntityWrapper = targetDaoRetrieveMembershipsByEntityRequest.getTargetEntity().getProvisioningEntityWrapper();
              if (provisioningEntityWrapper != null && GrouperUtil.length(targetDaoRetrieveMembershipsByEntityResponse.getTargetMemberships()) == 1
                  && targetDaoRetrieveMembershipsByEntityResponse.getTargetMemberships().get(0) instanceof ProvisioningEntity ){
                // if its not null, we should not mess up the object model...
                if (provisioningEntityWrapper.getTargetProvisioningEntity() == null) {
                  provisioningEntityWrapper.setTargetProvisioningEntity((ProvisioningEntity)targetDaoRetrieveMembershipsByEntityResponse.getTargetMemberships().get(0));
                  ((ProvisioningEntity)targetDaoRetrieveMembershipsByEntityResponse.getTargetMemberships().get(0)).setProvisioningEntityWrapper(provisioningEntityWrapper);
                  provisioningEntityWrapper.setTargetNativeEntity(targetDaoRetrieveMembershipsByEntityResponse.getTargetNativeEntity());
                }
              }

              return targetDaoRetrieveMembershipsByEntityResponse;
            }

          }
        }
        first = false;
      }
      if (retrieveEntitiesFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromAlternateSearchAttr", oldCount + retrieveEntitiesFromAlternateSearchAttr);
      }
      if (retrieveEntitiesFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromCache", oldCount + retrieveEntitiesFromCache);
      }

      return targetDaoRetrieveMembershipsByEntityResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMembershipsByEntity");
    }
    
  }

  @Override
  public TargetDaoRetrieveMembershipsByEntityResponse retrieveMembershipsByEntity(
      TargetDaoRetrieveMembershipsByEntityRequest targetDaoRetrieveMembershipsByEntityRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }

  public TargetDaoRetrieveMembershipsResponse retrieveMembershipsHelper(
      TargetDaoRetrieveMembershipsRequest targetDaoRetrieveMembershipsRequest) {
    
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();

    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMemberships(), false)) {
      
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoRetrieveMembershipsResponse targetDaoRetrieveMembershipsResponse = this.wrappedDao.retrieveMemberships(targetDaoRetrieveMembershipsRequest);
        hasError = logObjects(targetDaoRetrieveMembershipsResponse.getTargetMemberships());
        return targetDaoRetrieveMembershipsResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMemberships");
      }
    }

    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembership(), false)) {
        
        List<Object> results = new ArrayList<Object>();
        
        for (Object provisioningMembership : targetDaoRetrieveMembershipsRequest.getTargetMemberships()) {

          TargetDaoRetrieveMembershipResponse targetDaoRetrieveMembershipResponse = this.retrieveMembership(new TargetDaoRetrieveMembershipRequest(provisioningMembership));
          if (targetDaoRetrieveMembershipResponse != null && targetDaoRetrieveMembershipResponse.getTargetMembership() != null) {
            results.add(targetDaoRetrieveMembershipResponse.getTargetMembership());
          }
        }
        return new TargetDaoRetrieveMembershipsResponse(results);
      }
    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.groupAttributes) {
      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembership(), false)) {
        
        List<Object> results = new ArrayList<Object>();
        
        String attributeForMemberships = this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getAttributeNameForMemberships();
        
        for (Object provisioningMembership : targetDaoRetrieveMembershipsRequest.getTargetMemberships()) {

          ProvisioningGroup provisioningGroup = (ProvisioningGroup) provisioningMembership;
          
          
          Set<Object> attributeValueSet = (Set<Object>) provisioningGroup.retrieveAttributeValueSet(attributeForMemberships);
          
          for (Object attributeValue: GrouperUtil.nonNull(attributeValueSet)) {
            
            ProvisioningGroup clonedProvisioningGroup = provisioningGroup.clone();
            ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
            provisioningAttribute.setValue(GrouperUtil.toSet(attributeValue));
            provisioningAttribute.setName(attributeForMemberships);
            
            clonedProvisioningGroup.getAttributes().put(attributeForMemberships, provisioningAttribute);
            
            TargetDaoRetrieveMembershipResponse targetDaoRetrieveMembershipResponse = this.retrieveMembership(new TargetDaoRetrieveMembershipRequest(clonedProvisioningGroup));
            if (targetDaoRetrieveMembershipResponse != null && targetDaoRetrieveMembershipResponse.getTargetMembership() != null) {
              results.add(targetDaoRetrieveMembershipResponse.getTargetMembership());
            }
            
          }
        
        }
        return new TargetDaoRetrieveMembershipsResponse(results);
      }
    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.entityAttributes) {
      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembership(), false)) {
        
        List<Object> results = new ArrayList<Object>();
        
        String attributeForMemberships = this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getAttributeNameForMemberships();
        
        for (Object provisioningMembership : targetDaoRetrieveMembershipsRequest.getTargetMemberships()) {

          ProvisioningEntity provisioningEntity = (ProvisioningEntity) provisioningMembership;
          
          
          Set<Object> attributeValueSet = (Set<Object>) provisioningEntity.retrieveAttributeValueSet(attributeForMemberships);
          
          for (Object attributeValue: GrouperUtil.nonNull(attributeValueSet)) {
            
            ProvisioningEntity clonedProvisioningEntity = provisioningEntity.clone();
            ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
            provisioningAttribute.setValue(GrouperUtil.toSet(attributeValue));
            provisioningAttribute.setName(attributeForMemberships);
            
            clonedProvisioningEntity.getAttributes().put(attributeForMemberships, provisioningAttribute);
            
            TargetDaoRetrieveMembershipResponse targetDaoRetrieveMembershipResponse = this.retrieveMembership(new TargetDaoRetrieveMembershipRequest(clonedProvisioningEntity));
            if (targetDaoRetrieveMembershipResponse != null && targetDaoRetrieveMembershipResponse.getTargetMembership() != null) {
              results.add(targetDaoRetrieveMembershipResponse.getTargetMembership());
            }
            
          }
        
        }
        return new TargetDaoRetrieveMembershipsResponse(results);
      }
    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.membershipObjects) {
      
      Map<ProvisioningEntity, Set<String>> grouperTargetEntityToGroupIds = new HashMap<>();
      
      Map<ProvisioningGroup, Set<String>> grouperTargetGroupToEntityIds = new HashMap<>();

      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntity(), false) || 
          GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntities(), false)) {
        
        List<Object> targetMemberships = targetDaoRetrieveMembershipsRequest.getTargetMemberships();
        
        for (Object targetMembership: targetMemberships) {
          
          ProvisioningMembership grouperTargetMembership = (ProvisioningMembership) targetMembership;
          
          Set<String> groupIds = grouperTargetEntityToGroupIds.get(grouperTargetMembership.getProvisioningEntity());
          
          if (groupIds == null) {
            groupIds = new HashSet<>();
            grouperTargetEntityToGroupIds.put(grouperTargetMembership.getProvisioningEntity(), groupIds);
          }
          groupIds.add(grouperTargetMembership.getProvisioningGroupId());
          
        }
      }
      
      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroup(), false) || 
          GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroups(), false)) {
        
        List<Object> targetMemberships = targetDaoRetrieveMembershipsRequest.getTargetMemberships();
        
        for (Object targetMembership: targetMemberships) {
          
          ProvisioningMembership grouperTargetMembership = (ProvisioningMembership) targetMembership;
          
          Set<String> entityIds = grouperTargetGroupToEntityIds.get(grouperTargetMembership.getProvisioningGroup());
          
          if (entityIds == null) {
            entityIds = new HashSet<>();
            grouperTargetGroupToEntityIds.put(grouperTargetMembership.getProvisioningGroup(), entityIds);
          }
          entityIds.add(grouperTargetMembership.getProvisioningEntityId());
          
        }
      }
      
      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntities(), false)) {
        
        Map<String, ProvisioningEntity> entityIdToEntity = new HashMap<>();
        
        for (ProvisioningEntity provisioningEntity: grouperTargetEntityToGroupIds.keySet()) {
          entityIdToEntity.put(provisioningEntity.getId(), provisioningEntity);
        }
        
        List<Object> provisioningMemberships = new ArrayList<>();
        
        TargetDaoRetrieveMembershipsByEntitiesRequest request = new TargetDaoRetrieveMembershipsByEntitiesRequest();
        request.setTargetEntities(new ArrayList<ProvisioningEntity>(entityIdToEntity.values()));
        TargetDaoRetrieveMembershipsByEntitiesResponse membershipsByEntities = this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().retrieveMembershipsByEntities(request);
        
        
        for (Object targetMembershipObj: GrouperUtil.nonNull(membershipsByEntities.getTargetMemberships())) {
          
          ProvisioningMembership targetMembership = (ProvisioningMembership)targetMembershipObj;
          
          ProvisioningEntity provisioningEntity = entityIdToEntity.get(targetMembership.getProvisioningEntityId());
          
          Set<String> groupIds = grouperTargetEntityToGroupIds.get(provisioningEntity);
          
          if (groupIds.contains(targetMembership.getProvisioningGroupId())) {
            provisioningMemberships.add(targetMembership);
          }
          
        }
        
        return new TargetDaoRetrieveMembershipsResponse(provisioningMemberships);
        
      } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByEntity(), false)) {
       
        List<Object> provisioningMemberships = new ArrayList<>();
        
        for (ProvisioningEntity provisioningEntity: grouperTargetEntityToGroupIds.keySet()) {
          TargetDaoRetrieveMembershipsByEntityResponse membershipsByEntity = this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().retrieveMembershipsByEntity(new TargetDaoRetrieveMembershipsByEntityRequest(provisioningEntity));
          
          Set<String> groupIds = grouperTargetEntityToGroupIds.get(provisioningEntity);
          
          for (Object targetMembership: GrouperUtil.nonNull(membershipsByEntity.getTargetMemberships())) {
            
            ProvisioningMembership grouperTargetMembership = (ProvisioningMembership) targetMembership;
            
            if (groupIds.contains(grouperTargetMembership.getProvisioningGroupId())) {
              provisioningMemberships.add(grouperTargetMembership);
            }
            
          }
          
        }
        
        return new TargetDaoRetrieveMembershipsResponse(provisioningMemberships);
        
      }
      
      if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroups(), false)) {
        
        Map<String, ProvisioningGroup> groupIdToGroup = new HashMap<>();
        
        for (ProvisioningGroup provisioningGroup: grouperTargetGroupToEntityIds.keySet()) {
          groupIdToGroup.put(provisioningGroup.getId(), provisioningGroup);
        }
        
        List<Object> provisioningMemberships = new ArrayList<>();
        
        TargetDaoRetrieveMembershipsByGroupsRequest request = new TargetDaoRetrieveMembershipsByGroupsRequest();
        request.setTargetGroups(new ArrayList<ProvisioningGroup>(groupIdToGroup.values()));
        TargetDaoRetrieveMembershipsByGroupsResponse membershipsByGroups = this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().retrieveMembershipsByGroups(request);
        
        
        for (Object targetMembershipObj: GrouperUtil.nonNull(membershipsByGroups.getTargetMemberships())) {
          
          ProvisioningMembership targetMembership = (ProvisioningMembership)targetMembershipObj;
          
          ProvisioningGroup provisioningGroup = groupIdToGroup.get(targetMembership.getProvisioningGroupId());
          
          Set<String> entityIds = grouperTargetGroupToEntityIds.get(provisioningGroup);
          
          if (entityIds.contains(targetMembership.getProvisioningEntityId())) {
            provisioningMemberships.add(targetMembership);
          }
          
        }
        
        return new TargetDaoRetrieveMembershipsResponse(provisioningMemberships);
        
      } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembershipsByGroup(), false)) {
       
        List<Object> provisioningMemberships = new ArrayList<>();
        
        for (ProvisioningGroup provisioningGroup: grouperTargetGroupToEntityIds.keySet()) {
          TargetDaoRetrieveMembershipsByGroupResponse membershipsByGroup = this.getGrouperProvisioner().retrieveGrouperProvisioningTargetDaoAdapter().retrieveMembershipsByGroup(new TargetDaoRetrieveMembershipsByGroupRequest(provisioningGroup));
          
          Set<String> entityIds = grouperTargetGroupToEntityIds.get(provisioningGroup);
          
          for (Object targetMembership: GrouperUtil.nonNull(membershipsByGroup.getTargetMemberships())) {
            
            ProvisioningMembership grouperTargetMembership = (ProvisioningMembership) targetMembership;
            
            if (entityIds.contains(grouperTargetMembership.getProvisioningEntityId())) {
              provisioningMemberships.add(grouperTargetMembership);
            }
            
          }
          
        }
        
        return new TargetDaoRetrieveMembershipsResponse(provisioningMemberships);
        
      }
      
    }
    
    throw new RuntimeException("Dao cannot retrieve memberships or membership");
  }

  @Override
  public TargetDaoRetrieveMembershipsResponse retrieveMemberships(
      TargetDaoRetrieveMembershipsRequest targetDaoRetrieveMembershipsRequest) {
    
    TargetDaoRetrieveMembershipsResponse targetDaoRetrieveMembershipsResponse = new TargetDaoRetrieveMembershipsResponse();
    if (GrouperUtil.length(targetDaoRetrieveMembershipsRequest.getTargetMemberships()) == 0) {
      return targetDaoRetrieveMembershipsResponse;
    }

    List<Object> targetMemberships = targetDaoRetrieveMembershipsRequest.getTargetMemberships();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();

    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMemberships(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getRetrieveMembershipsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetMemberships, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<Object> batchTargetMemberships = GrouperUtil.batchList(targetMemberships, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveMemberships_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoRetrieveMembershipsRequest targetDaoRetrieveMembershipsRequestLocal = new TargetDaoRetrieveMembershipsRequest();
            targetDaoRetrieveMembershipsRequestLocal.setTargetMemberships(batchTargetMemberships);
            retrieveMembershipsHelper(targetDaoRetrieveMembershipsRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveMembership(), false)) {

      List<Object> processedTargetMemberships = new ArrayList<>();
      
      for (Object targetMembership : targetMemberships) {
        
        if (targetMembership instanceof ProvisioningGroup) {
          ProvisioningGroup targetGroup = (ProvisioningGroup) targetMembership;
          
          LdapSyncConfiguration ldapSyncConfiguration = (LdapSyncConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
          String membershipAttributeName = ldapSyncConfiguration.getGroupMembershipAttributeName();
          
          Set<?> membershipAttributeValueSet = targetGroup.retrieveAttributeValueSet(membershipAttributeName);
         
          for (Object membershipValue: GrouperUtil.nonNull(membershipAttributeValueSet)) {
            
            
            ProvisioningGroup clonedTargetGroup = targetGroup.clone();
            ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
            provisioningAttribute.setValue(GrouperUtil.toSet(membershipValue));
            provisioningAttribute.setName(membershipAttributeName);
            
            clonedTargetGroup.getAttributes().put(membershipAttributeName, provisioningAttribute);
            processedTargetMemberships.add(clonedTargetGroup);
            
          }
          
          
        } else  if (targetMembership instanceof ProvisioningEntity) {
          ProvisioningEntity targetEntity = (ProvisioningEntity) targetMembership;
          
          LdapSyncConfiguration ldapSyncConfiguration = (LdapSyncConfiguration) this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration();
          String membershipAttributeName = ldapSyncConfiguration.getEntityMembershipAttributeName();
          
          Set<?> membershipAttributeValueSet = targetEntity.retrieveAttributeValueSet(membershipAttributeName);
         
          for (Object membershipValue: GrouperUtil.nonNull(membershipAttributeValueSet)) {
            
            ProvisioningEntity clonedTargetEntity = targetEntity.clone();
            ProvisioningAttribute provisioningAttribute = new ProvisioningAttribute();
            provisioningAttribute.setValue(GrouperUtil.toSet(membershipValue));
            provisioningAttribute.setName(membershipAttributeName);
            
            clonedTargetEntity.getAttributes().put(membershipAttributeName, provisioningAttribute);
            processedTargetMemberships.add(clonedTargetEntity);
            
          }
          
        } else if (targetMembership instanceof ProvisioningMembership) {
          processedTargetMemberships.add(targetMembership);
        } else {
          throw new RuntimeException("Invalid type of " + targetMembership);
        }
        
        for (Object object: processedTargetMemberships) {
          GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveMembership_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
            
            @Override
            public Void callLogic() {
              TargetDaoRetrieveMembershipRequest targetDaoRetrieveMembershipRequestLocal = new TargetDaoRetrieveMembershipRequest();
              targetDaoRetrieveMembershipRequestLocal.setTargetMembership(object);
              TargetDaoRetrieveMembershipResponse targetDaoRetrieveMembershipResponseLocal = retrieveMembershipHelper(targetDaoRetrieveMembershipRequestLocal);
              synchronized(targetDaoRetrieveMembershipsResponse) {
                if (targetDaoRetrieveMembershipsResponse.getTargetMemberships() == null) {
                  targetDaoRetrieveMembershipsResponse.setTargetMemberships(new ArrayList<Object>());
                }
                if (targetDaoRetrieveMembershipResponseLocal.getTargetMembership() != null) {
                  targetDaoRetrieveMembershipsResponse.getTargetMemberships().add(targetDaoRetrieveMembershipResponseLocal.getTargetMembership());
                }
              }

              return null;
            }
          };
          grouperCallables.add(grouperCallable);
        }
      }
    } else {

      throw new RuntimeException("Dao cannot retrieve membership or memberships");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    return targetDaoRetrieveMembershipsResponse;
  }

  private TargetDaoRetrieveEntitiesResponse retrieveEntitiesHelper(
      TargetDaoRetrieveEntitiesRequest targetDaoRetrieveEntitiesRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      
      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) == 0) {
        TargetDaoRetrieveEntitiesResponse targetDaoRetrieveEntitiesResponse = this.wrappedDao.retrieveEntities(targetDaoRetrieveEntitiesRequest);
        hasError = logEntities(targetDaoRetrieveEntitiesResponse.getTargetEntities());
        return targetDaoRetrieveEntitiesResponse;
      }
      
      TargetDaoRetrieveEntitiesResponse overallResponse = new TargetDaoRetrieveEntitiesResponse();
      
      List<ProvisioningEntity> targetEntitiesFound = new ArrayList<ProvisioningEntity>();
      
      Set<ProvisioningEntity> entitiesRemainingToFind = new HashSet<ProvisioningEntity>(targetDaoRetrieveEntitiesRequest.getTargetEntities());

      // cycle through search attributes and past values
      int retrieveEntitiesFromCache = 0;
      int retrieveEntitiesFromAlternateSearchAttr = 0;
      boolean first = true;
      // current value or historical values
      OUTER: for (boolean currentValue : new boolean[] {true, false}) {
      
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          Set<Object> searchAttributeValues = new HashSet<Object>();
          Map<String, ProvisioningEntity> searchValueToSearchGrouperTargetEntity = new HashMap<String, ProvisioningEntity>();
          for (ProvisioningEntity grouperTargetEntity : entitiesRemainingToFind) {
            
            for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(grouperTargetEntity.getSearchIdAttributeNameToValues())) {

              if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
                continue;
              }
              if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
                continue;
              }

              searchValueToSearchGrouperTargetEntity.put(GrouperUtil.stringValue(provisioningUpdatableAttributeAndValue.getAttributeValue()), grouperTargetEntity);
              searchAttributeValues.add(provisioningUpdatableAttributeAndValue.getAttributeValue());
            }              
          }
          if (searchValueToSearchGrouperTargetEntity.size() > 0) {
            // search based on those
            TargetDaoRetrieveEntitiesRequest targetDaoRetrieveEntitiesRequestNew = new TargetDaoRetrieveEntitiesRequest();
            targetDaoRetrieveEntitiesRequestNew.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveEntitiesRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveEntitiesRequestNew.setTargetEntities(new ArrayList<ProvisioningEntity>(searchValueToSearchGrouperTargetEntity.values()));
            targetDaoRetrieveEntitiesRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveEntitiesRequestNew.setSearchAttributeValues(searchAttributeValues);
            
            TargetDaoRetrieveEntitiesResponse targetDaoRetrieveEntitiesResponse = this.wrappedDao.retrieveEntities(targetDaoRetrieveEntitiesRequestNew);
            hasError = logEntities(targetDaoRetrieveEntitiesResponse.getTargetEntities()) || hasError;
            
            // add these to the overall result
            targetEntitiesFound.addAll(GrouperUtil.nonNull(targetDaoRetrieveEntitiesResponse.getTargetEntities()));

            // pluck each one out from the remaining entities to find
            for (ProvisioningEntity retrievedTargetEntity : GrouperUtil.nonNull(targetDaoRetrieveEntitiesResponse.getTargetEntities())) {
              Object targetEntityValue = retrievedTargetEntity.retrieveAttributeValue(searchAttributeName);
              if(!GrouperUtil.isBlank(targetEntityValue)) {
                ProvisioningEntity grouperTargetEntity = searchValueToSearchGrouperTargetEntity.get(GrouperUtil.stringValue(targetEntityValue));
                if (grouperTargetEntity != null) {
                  if (!currentValue) {
                    retrieveEntitiesFromCache++;
                  } else if (!first) {
                    retrieveEntitiesFromAlternateSearchAttr++;
                  }
                  entitiesRemainingToFind.remove(grouperTargetEntity);
                  ProvisioningEntityWrapper provisioningEntityWrapper = grouperTargetEntity.getProvisioningEntityWrapper();
                  if (provisioningEntityWrapper != null) {
                    // if its not null, we should not mess up the object model...
                    if (provisioningEntityWrapper.getTargetProvisioningEntity() == null) {
                      provisioningEntityWrapper.setTargetProvisioningEntity(retrievedTargetEntity);
                      retrievedTargetEntity.setProvisioningEntityWrapper(provisioningEntityWrapper);
                      if (targetDaoRetrieveEntitiesResponse.getTargetEntityToTargetNativeEntity() != null) {
                        provisioningEntityWrapper.setTargetNativeEntity(targetDaoRetrieveEntitiesResponse.getTargetEntityToTargetNativeEntity().get(retrievedTargetEntity));
                      }
                    }
                  }
                }
              }
            }
          }
          if (entitiesRemainingToFind.size() == 0) {
            break OUTER;
          }
        }
        first = false;
      }
      
      if (retrieveEntitiesFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromAlternateSearchAttr", oldCount + retrieveEntitiesFromAlternateSearchAttr);
      }
      if (retrieveEntitiesFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromCache", oldCount + retrieveEntitiesFromCache);
      }
      overallResponse.setTargetEntities(targetEntitiesFound);
      return overallResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveEntities");
    }

  }



  @Override
  public TargetDaoRetrieveEntitiesResponse retrieveEntities(
      TargetDaoRetrieveEntitiesRequest targetDaoRetrieveEntitiesRequest) {
    
    List<ProvisioningEntity> targetEntities = new ArrayList<ProvisioningEntity>(GrouperUtil.nonNull(targetDaoRetrieveEntitiesRequest.getTargetEntities()));
    TargetDaoRetrieveEntitiesResponse targetDaoRetrieveEntitiesResponse = new TargetDaoRetrieveEntitiesResponse();
    
    if (GrouperUtil.length(targetEntities) == 0) {
      return targetDaoRetrieveEntitiesResponse;
    }
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveEntities(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getRetrieveEntitiesBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetEntities, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningEntity> batchTargetEntities = GrouperUtil.batchList(targetEntities, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveEntities_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoRetrieveEntitiesRequest targetDaoRetrieveEntitiesRequestLocal = new TargetDaoRetrieveEntitiesRequest();
            targetDaoRetrieveEntitiesRequestLocal.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveEntitiesRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveEntitiesRequestLocal.setTargetEntities(batchTargetEntities);
            TargetDaoRetrieveEntitiesResponse targetDaoRetrieveEntitiesResponseLocal = retrieveEntitiesHelper(targetDaoRetrieveEntitiesRequestLocal);
            synchronized(targetDaoRetrieveEntitiesResponse) {
              if (targetDaoRetrieveEntitiesResponse.getTargetEntities() == null) {
                targetDaoRetrieveEntitiesResponse.setTargetEntities(new ArrayList<ProvisioningEntity>());
              }
              if (targetDaoRetrieveEntitiesResponse.getTargetEntityToTargetNativeEntity() == null) {
                targetDaoRetrieveEntitiesResponse.setTargetEntityToTargetNativeEntity(new HashMap<ProvisioningEntity,Object>());
              }
              targetDaoRetrieveEntitiesResponse.getTargetEntities().addAll(GrouperUtil.nonNull(targetDaoRetrieveEntitiesResponseLocal.getTargetEntities()));
              targetDaoRetrieveEntitiesResponse.getTargetEntityToTargetNativeEntity().putAll(GrouperUtil.nonNull(targetDaoRetrieveEntitiesResponseLocal.getTargetEntityToTargetNativeEntity()));
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanRetrieveEntity(), false)) {

      for (ProvisioningEntity targetEntity : targetEntities) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("retrieveEntity_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            TargetDaoRetrieveEntityRequest targetDaoRetrieveEntityRequestLocal = new TargetDaoRetrieveEntityRequest();
            targetDaoRetrieveEntityRequestLocal.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveEntitiesRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveEntityRequestLocal.setTargetEntity(targetEntity);
            TargetDaoRetrieveEntityResponse targetDaoRetrieveEntityResponseLocal = retrieveEntityHelper(targetDaoRetrieveEntityRequestLocal);
            synchronized(targetDaoRetrieveEntitiesResponse) {
              if (targetDaoRetrieveEntitiesResponse.getTargetEntities() == null) {
                targetDaoRetrieveEntitiesResponse.setTargetEntities(new ArrayList<ProvisioningEntity>());
              }
              if (targetDaoRetrieveEntitiesResponse.getTargetEntityToTargetNativeEntity() == null) {
                targetDaoRetrieveEntitiesResponse.setTargetEntityToTargetNativeEntity(new HashMap<ProvisioningEntity,Object>());
              }
              if (targetDaoRetrieveEntityResponseLocal.getTargetEntity() != null) {
                targetDaoRetrieveEntitiesResponse.getTargetEntities().add(targetDaoRetrieveEntityResponseLocal.getTargetEntity());
                if (targetDaoRetrieveEntityResponseLocal.getTargetNativeEntity() != null) {
                  targetDaoRetrieveEntitiesResponse.getTargetEntityToTargetNativeEntity().put(targetDaoRetrieveEntityResponseLocal.getTargetEntity(), targetDaoRetrieveEntityResponseLocal.getTargetNativeEntity());
                }
              }
            }
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot retrieve entity or entities");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningEntityWrapper> provisioningEntityWrappersSuccessfullyRetrieved = new HashSet<ProvisioningEntityWrapper>();
    for (ProvisioningEntity targetEntity : GrouperUtil.nonNull(targetDaoRetrieveEntitiesResponse.getTargetEntities())) { 
      if (targetEntity.getProvisioningEntityWrapper() != null) {
        provisioningEntityWrappersSuccessfullyRetrieved.add(targetEntity.getProvisioningEntityWrapper());
      }
    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateEntityLink(provisioningEntityWrappersSuccessfullyRetrieved, false);
    
    return targetDaoRetrieveEntitiesResponse;

  }

  /**
   * @param targetDaoRetrieveGroupRequest 
   * @return result
   * 
   */
  private TargetDaoRetrieveGroupResponse retrieveGroupHelper(
      TargetDaoRetrieveGroupRequest targetDaoRetrieveGroupRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    int retrieveGroupsFromCache = 0;
    int retrieveGroupsFromAlternateSearchAttr = 0;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      
      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) == 0) {

        TargetDaoRetrieveGroupResponse targetDaoRetrieveGroupResponse = this.wrappedDao.retrieveGroup(targetDaoRetrieveGroupRequest);
        hasError = logGroup(targetDaoRetrieveGroupRequest.getTargetGroup());
        return targetDaoRetrieveGroupResponse;
      }
      
      TargetDaoRetrieveGroupResponse targetDaoRetrieveGroupResponse = new TargetDaoRetrieveGroupResponse();
      
      // cycle through search attributes and past values
      boolean first = true;
      // current value or historical values
      for (boolean currentValue : new boolean[] {true, false}) {
      
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getGroupSearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(targetDaoRetrieveGroupRequest.getTargetGroup().getSearchIdAttributeNameToValues())) {
            if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
              continue;
            }
            if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
              continue;
            }
            // search based on those
            TargetDaoRetrieveGroupRequest targetDaoRetrieveGroupRequestNew = new TargetDaoRetrieveGroupRequest();
            targetDaoRetrieveGroupRequestNew.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveGroupRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveGroupRequestNew.setTargetGroup(targetDaoRetrieveGroupRequest.getTargetGroup());
            targetDaoRetrieveGroupRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveGroupRequestNew.setSearchAttributeValue(provisioningUpdatableAttributeAndValue.getAttributeValue());
            
            targetDaoRetrieveGroupResponse = this.wrappedDao.retrieveGroup(targetDaoRetrieveGroupRequestNew);
            if (targetDaoRetrieveGroupResponse.getTargetGroup() != null) {
              if (!currentValue) {
                retrieveGroupsFromCache++;
              } else if (!first) {
                retrieveGroupsFromAlternateSearchAttr++;
              }
              hasError = logGroup(targetDaoRetrieveGroupResponse.getTargetGroup()) || hasError;
              ProvisioningGroupWrapper provisioningGroupWrapper = targetDaoRetrieveGroupRequest.getTargetGroup().getProvisioningGroupWrapper();
              if (provisioningGroupWrapper != null) {
                // if its not null, we should not mess up the object model...
                if (provisioningGroupWrapper.getTargetProvisioningGroup() == null) {
                  // if its not null, we should not mess up the object model...
                  if (provisioningGroupWrapper.getTargetProvisioningGroup() == null) {
                    provisioningGroupWrapper.setTargetProvisioningGroup(targetDaoRetrieveGroupResponse.getTargetGroup());
                    targetDaoRetrieveGroupResponse.getTargetGroup().setProvisioningGroupWrapper(provisioningGroupWrapper);
                    provisioningGroupWrapper.setTargetNativeGroup(targetDaoRetrieveGroupResponse.getTargetNativeGroup());
                  }                    
                }
              }
              return targetDaoRetrieveGroupResponse;
            }
          }
        }
        first = false;
      }
      
      return targetDaoRetrieveGroupResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      if (retrieveGroupsFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromAlternateSearchAttr", oldCount + retrieveGroupsFromAlternateSearchAttr);
      }
      if (retrieveGroupsFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveGroupsFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveGroupsFromCache", oldCount + retrieveGroupsFromCache);
      }
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveGroups");
    }

  }
  
  @Override
  public TargetDaoRetrieveGroupResponse retrieveGroup(
      TargetDaoRetrieveGroupRequest targetDaoRetrieveGroupRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }

  /**
   * 
   * @param targetDaoRetrieveEntityRequest
   */
  private TargetDaoRetrieveEntityResponse retrieveEntityHelper(TargetDaoRetrieveEntityRequest targetDaoRetrieveEntityRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    int retrieveEntitiesFromCache = 0;
    int retrieveEntitiesFromAlternateSearchAttr = 0;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      if (GrouperUtil.length(this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) == 0) {

        TargetDaoRetrieveEntityResponse targetDaoRetrieveEntityResponse = this.wrappedDao.retrieveEntity(targetDaoRetrieveEntityRequest);
        hasError = logEntity(targetDaoRetrieveEntityRequest.getTargetEntity());
        return targetDaoRetrieveEntityResponse;
      }
      
      TargetDaoRetrieveEntityResponse targetDaoRetrieveEntityResponse = new TargetDaoRetrieveEntityResponse();
      
      // cycle through search attributes and past values
      boolean first = true;
      // current value or historical values
      for (boolean currentValue : new boolean[] {true, false}) {
      
        for (GrouperProvisioningConfigurationAttribute searchAttribute : this.getGrouperProvisioner().retrieveGrouperProvisioningConfiguration().getEntitySearchAttributes()) {
          String searchAttributeName = searchAttribute.getName();

          for (ProvisioningUpdatableAttributeAndValue provisioningUpdatableAttributeAndValue : GrouperUtil.nonNull(targetDaoRetrieveEntityRequest.getTargetEntity().getSearchIdAttributeNameToValues())) {
            if (currentValue != provisioningUpdatableAttributeAndValue.getCurrentValue().booleanValue()) {
              continue;
            }
            if (!StringUtils.equals(searchAttributeName, provisioningUpdatableAttributeAndValue.getAttributeName())) {
              continue;
            }
            // search based on those
            TargetDaoRetrieveEntityRequest targetDaoRetrieveEntityRequestNew = new TargetDaoRetrieveEntityRequest();
            targetDaoRetrieveEntityRequestNew.setIncludeAllMembershipsIfApplicable(targetDaoRetrieveEntityRequest.isIncludeAllMembershipsIfApplicable());
            targetDaoRetrieveEntityRequestNew.setTargetEntity(targetDaoRetrieveEntityRequest.getTargetEntity());
            targetDaoRetrieveEntityRequestNew.setSearchAttribute(searchAttributeName);
            targetDaoRetrieveEntityRequestNew.setSearchAttributeValue(provisioningUpdatableAttributeAndValue.getAttributeValue());
            
            targetDaoRetrieveEntityResponse = this.wrappedDao.retrieveEntity(targetDaoRetrieveEntityRequestNew);
            if (targetDaoRetrieveEntityResponse.getTargetEntity() != null) {
              if (!currentValue) {
                retrieveEntitiesFromCache++;
              } else if (!first) {
                retrieveEntitiesFromAlternateSearchAttr++;
              }
              hasError = logEntity(targetDaoRetrieveEntityResponse.getTargetEntity()) || hasError;
              ProvisioningEntityWrapper provisioningEntityWrapper = targetDaoRetrieveEntityRequest.getTargetEntity().getProvisioningEntityWrapper();
              if (provisioningEntityWrapper != null) {
                // if its not null, we should not mess up the object model...
                if (provisioningEntityWrapper.getTargetProvisioningEntity() == null) {
                  provisioningEntityWrapper.setTargetProvisioningEntity(targetDaoRetrieveEntityResponse.getTargetEntity());
                  targetDaoRetrieveEntityResponse.getTargetEntity().setProvisioningEntityWrapper(provisioningEntityWrapper);
                  provisioningEntityWrapper.setTargetNativeEntity(targetDaoRetrieveEntityResponse.getTargetNativeEntity());
                }
              }
              return targetDaoRetrieveEntityResponse;
            }
          }
        }
        first = false;
      }
      
      return targetDaoRetrieveEntityResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      if (retrieveEntitiesFromAlternateSearchAttr > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromAlternateSearchAttr"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromAlternateSearchAttr", oldCount + retrieveEntitiesFromAlternateSearchAttr);
      }
      if (retrieveEntitiesFromCache > 0) {
        Integer oldCount = GrouperUtil.defaultIfNull((Integer)this.getGrouperProvisioner().getDebugMap().get("retrieveEntitiesFromCache"), 0);
        this.getGrouperProvisioner().getDebugMap().put("retrieveEntitiesFromCache", oldCount + retrieveEntitiesFromCache);
      }
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveEntities");
    }
    
  }

  @Override
  public TargetDaoRetrieveEntityResponse retrieveEntity(
      TargetDaoRetrieveEntityRequest targetDaoRetrieveEntityRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }

  /**
   * 
   * @param targetDaoRetrieveMembershipRequest
   * @return response
   */
  private TargetDaoRetrieveMembershipResponse retrieveMembershipHelper(
      TargetDaoRetrieveMembershipRequest targetDaoRetrieveMembershipRequest) {

    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoRetrieveMembershipResponse targetDaoRetrieveMembershipResponse = this.wrappedDao.retrieveMembership(targetDaoRetrieveMembershipRequest);
      hasError = logObject(targetDaoRetrieveMembershipResponse.getTargetMembership());
      return targetDaoRetrieveMembershipResponse;
    } catch (RuntimeException e) {
      hasError = true;
      throw e;
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "retrieveMembership");
    }
    
  }

  @Override
  public TargetDaoRetrieveMembershipResponse retrieveMembership(
      TargetDaoRetrieveMembershipRequest targetDaoRetrieveMembershipRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }

  private TargetDaoUpdateGroupResponse updateGroupHelper(
      TargetDaoUpdateGroupRequest targetDaoUpdateGroupRequest) {
    ProvisioningGroup targetGroup = targetDaoUpdateGroupRequest.getTargetGroup();

    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoUpdateGroupResponse targetDaoUpdateGroupResponse = this.wrappedDao.updateGroup(targetDaoUpdateGroupRequest);
      hasError = logGroup(targetGroup);
      if (targetGroup.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set updated group as provisioned: " + this.wrappedDao);
      }

      return targetDaoUpdateGroupResponse;
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetGroup.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("groupUpdate")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error updating group " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetGroup.getProvisioned() == null) {
        targetGroup.setProvisioned(false);
      }
      if (targetGroup.getException() == null) {
        targetGroup.setException(e);
      }
      
      setExceptionForMembershipsWhenGroupOrEntityAttributes(null, targetGroup, e);
      
      logGroup(targetGroup);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "updateGroup");
    }
    return null;
  }

  @Override
  public TargetDaoUpdateGroupResponse updateGroup(
      TargetDaoUpdateGroupRequest targetDaoUpdateGroupRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

    
  }


  private TargetDaoInsertGroupsResponse insertGroupsHelper(
      TargetDaoInsertGroupsRequest targetDaoInsertGroupsRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoInsertGroupsResponse targetDaoInsertGroupsResponse = this.wrappedDao.insertGroups(targetDaoInsertGroupsRequest);
      hasError = logGroups(targetDaoInsertGroupsRequest.getTargetGroups());
      
      for (ProvisioningGroup provisioningGroup : targetDaoInsertGroupsRequest.getTargetGroups()) { 
        if (provisioningGroup.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set inserted group as provisioned: " + this.wrappedDao);
        }
      }
      
      return targetDaoInsertGroupsResponse;
    } catch (RuntimeException e) {
      hasError = true;

      boolean first = true;
      for (ProvisioningGroup targetGroup : targetDaoInsertGroupsRequest.getTargetGroups()) { 
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("groupInsert")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error inserting groups, e.g. " + (targetGroup == null ? null : targetGroup.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        first = false;

        if (targetGroup.getProvisioned() == null) {
          targetGroup.setProvisioned(false);
        }
        if (targetGroup.getException() == null) {
          targetGroup.setException(e);
        }
      }
      logGroups(targetDaoInsertGroupsRequest.getTargetGroups());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "insertGroups");
    }
    return null;
  }

  @Override
  public TargetDaoInsertGroupsResponse insertGroups(
      TargetDaoInsertGroupsRequest targetDaoInsertGroupsRequest) {
    
    if (GrouperUtil.length(targetDaoInsertGroupsRequest.getTargetGroups()) == 0) {
      return new TargetDaoInsertGroupsResponse();
    }
    List<ProvisioningGroup> targetGroups = targetDaoInsertGroupsRequest.getTargetGroups();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanInsertGroups(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getInsertGroupsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetGroups, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningGroup> batchTargetGroups = GrouperUtil.batchList(targetGroups, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("insertGroups_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoInsertGroupsRequest targetDaoInsertGroupsRequestLocal = new TargetDaoInsertGroupsRequest();
            targetDaoInsertGroupsRequestLocal.setTargetGroups(batchTargetGroups);
            insertGroupsHelper(targetDaoInsertGroupsRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanInsertGroup(), false)) {

      for (ProvisioningGroup targetGroup : targetGroups) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("insertGroup_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            insertGroupHelper(targetGroup);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot insert group or groups");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningGroupWrapper> provisioningGroupWrappersSuccessfullyInserted = new HashSet<ProvisioningGroupWrapper>();
    for (ProvisioningGroup targetGroup : targetGroups) { 
      if (targetGroup.getProvisioned() != null && targetGroup.getProvisioned()) {
        provisioningGroupWrappersSuccessfullyInserted.add(targetGroup.getProvisioningGroupWrapper());
      }

    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateGroupLink(provisioningGroupWrappersSuccessfullyInserted, false);
    
    return new TargetDaoInsertGroupsResponse();

  }

  /**
   * 
   */
  @Override
  public TargetDaoDeleteEntityResponse deleteEntity(
      TargetDaoDeleteEntityRequest targetDaoDeleteEntityRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }

  /**
   * 
   * @param targetEntity
   */
  private void deleteEntityHelper(ProvisioningEntity targetEntity) {
    
    TargetDaoDeleteEntityRequest targetDaoDeleteEntityRequest = new TargetDaoDeleteEntityRequest(targetEntity);

    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      this.wrappedDao.deleteEntity(targetDaoDeleteEntityRequest);

      if (targetEntity.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set deleted entity as provisioned: " + this.wrappedDao);
      }

      hasError = logEntity(targetEntity);
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetEntity.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityDelete")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error deleting entity, " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetEntity.getProvisioned() == null) {
        targetEntity.setProvisioned(false);
      }
      if (targetEntity.getException() == null) {
        targetEntity.setException(e);
      }
      logEntity(targetEntity);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "deleteEntity");
    }

  }
  
  /**
   * log grouper provisioning lists
   * @param grouperProvisioningLists
   */
  private boolean logProvisioningLists(GrouperProvisioningLists grouperProvisioningLists) {
    boolean hasError = false;
    if (grouperProvisioningLists != null) {
      if (logGroups(grouperProvisioningLists.getProvisioningGroups())) {
        hasError = true;
      }
      if (logEntities(grouperProvisioningLists.getProvisioningEntities())) {
        hasError = true;
      }
      if (logMemberships(grouperProvisioningLists.getProvisioningMemberships())) {
        hasError = true;
      }
    }
    return hasError;
  }

  /**
   * log errors in entity
   * @param provisioningEntities
   * @return if error
   */
  private boolean logEntities(List<ProvisioningEntity> provisioningEntities) {
    
    boolean hasError = false;
    for (ProvisioningEntity provisioningEntity : GrouperUtil.nonNull(provisioningEntities)) {
      if (logEntity(provisioningEntity)) {
        hasError = true;
      }
    }
    return hasError;
  }

  /**
   * log errors in entities
   * @param provisioningEntities
   * @return if error
   */
  private boolean logEntity(ProvisioningEntity provisioningEntity) {
    if (provisioningEntity != null && provisioningEntity.getException() != null) {
      logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
          "Error in provisioner '" + this.getGrouperProvisioner().getConfigId() + "' - '" + this.getGrouperProvisioner().getInstanceId() + "' with entity: " + provisioningEntity
           + "\n" + GrouperUtil.getFullStackTrace(provisioningEntity.getException())));
      if (this.getGrouperProvisioner().retrieveGrouperProvisioningDiagnosticsContainer().isInDiagnostics()) {
        this.getGrouperProvisioner().retrieveGrouperProvisioningDiagnosticsContainer().appendReportLineIfNotBlank("Error in entity: " + provisioningEntity + ", " + GrouperUtil.getFullStackTrace(provisioningEntity.getException()));
      }
      return true;
    }
    return false;
  }

  /**
   * log errors in groups
   * @param provisioningGroups
   * @return if has error
   */
  private boolean logGroups(List<ProvisioningGroup> provisioningGroups) {
    boolean hasError = false;
    for (ProvisioningGroup provisioningGroup : GrouperUtil.nonNull(provisioningGroups)) {
      if (logGroup(provisioningGroup)) {
        hasError = true;
      }
    }
    return hasError;
  }

  /**
   * log errors in group
   * @param provisioningEntities
   */
  private boolean logGroup(ProvisioningGroup provisioningGroup) {
    // TODO only log 10 based on config and type of log...
    if (provisioningGroup != null && provisioningGroup.getException() != null) {
      logError("Error in provisioner '" + this.getGrouperProvisioner().getConfigId() + "' - '" 
          + this.getGrouperProvisioner().getInstanceId() + "' with group: " + provisioningGroup + "\n" 
          + GrouperUtil.getFullStackTrace(provisioningGroup.getException()));
      if (this.getGrouperProvisioner().retrieveGrouperProvisioningDiagnosticsContainer().isInDiagnostics()) {
        this.getGrouperProvisioner().retrieveGrouperProvisioningDiagnosticsContainer().appendReportLineIfNotBlank("Error in group: " + provisioningGroup + ", " + GrouperUtil.getFullStackTrace(provisioningGroup.getException()));
      }
      return true;
    }
    return false;
  }

  /**
   * log errors in object
   * @param provisioningObjects
   */
  private boolean logObjects(List<Object> provisioningObjects) {
    boolean hasError = false;
    for (Object provisioningObject : GrouperUtil.nonNull(provisioningObjects)) {
      if (logObject(provisioningObject)) {
        hasError = true;
      }
    }
    return hasError;
  }

  /**
   * log errors in group
   * @param provisioningEntities
   */
  private boolean logObject(Object provisioningObject) {
    
    if (provisioningObject instanceof ProvisioningGroup) {
      return logGroup((ProvisioningGroup)provisioningObject);
    }
    if (provisioningObject instanceof ProvisioningEntity) {
      return logEntity((ProvisioningEntity)provisioningObject);
    }
    if (provisioningObject instanceof ProvisioningMembership) {
      return logMembership((ProvisioningMembership)provisioningObject);
    }
    return false;
  }

  /**
   * log errors in membership
   * @param provisioningMemberships
   */
  private boolean logMemberships(List<ProvisioningMembership> provisioningMemberships) {
    
    boolean hasError = false;
    for (ProvisioningMembership provisioningMembership : GrouperUtil.nonNull(provisioningMemberships)) {
      if (logMembership(provisioningMembership)) {
        hasError = true;
      }
    }
    return hasError;
  }

  /**
   * log errors in entities
   * @param provisioningEntities
   */
  private boolean logMembership(ProvisioningMembership provisioningMembership) {
    if (provisioningMembership != null && provisioningMembership.getException() != null) {
      logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
          "Error with provisioner '" + this.getGrouperProvisioner().getConfigId() + "' - '" + this.getGrouperProvisioner().getInstanceId() + "' with membership: " + provisioningMembership
          + "\n" + GrouperUtil.getFullStackTrace(provisioningMembership.getException())));
      if (this.getGrouperProvisioner().retrieveGrouperProvisioningDiagnosticsContainer().isInDiagnostics()) {
        this.getGrouperProvisioner().retrieveGrouperProvisioningDiagnosticsContainer().appendReportLineIfNotBlank("Error in membership: " + provisioningMembership + ", " + GrouperUtil.getFullStackTrace(provisioningMembership.getException()));
      }
      return true;
    }
    return false;
  }

  /**
   * delete entities logic
   * @param targetEntities
   */
  private void deleteEntitiesHelper(List<ProvisioningEntity> targetEntities) {
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoDeleteEntitiesRequest targetDaoDeleteEntitiesRequest = new TargetDaoDeleteEntitiesRequest(targetEntities);

      this.wrappedDao.deleteEntities(targetDaoDeleteEntitiesRequest);
      for (ProvisioningEntity provisioningEntity : targetEntities) { 
        if (provisioningEntity.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set deleted entity as provisioned: " + this.wrappedDao);
        }
      }
      
      hasError = logEntities(targetEntities);
      
    } catch (RuntimeException e) {
      hasError = true;
      boolean first = true;
      for (ProvisioningEntity targetEntity : targetEntities) { 
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityDelete")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error deleting entities, e.g. " + (targetEntity == null ? null : targetEntity.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        
        first = false;
        if (targetEntity.getProvisioned() == null) {
          targetEntity.setProvisioned(false);
        }
        if (targetEntity.getException() == null) {
          targetEntity.setException(e);
        }
      }
      logEntities(targetEntities);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "deleteEntities");
    }

  }

  @Override
  public TargetDaoDeleteEntitiesResponse deleteEntities(
      TargetDaoDeleteEntitiesRequest targetDaoDeleteEntitiesRequest) {

    if (GrouperUtil.length(targetDaoDeleteEntitiesRequest.getTargetEntities()) == 0) {
      return new TargetDaoDeleteEntitiesResponse();
    }
    List<ProvisioningEntity> targetEntities = targetDaoDeleteEntitiesRequest.getTargetEntities();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanDeleteEntities(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getDeleteEntitiesBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetEntities, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningEntity> batchTargetEntities = GrouperUtil.batchList(targetEntities, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("deleteEntities_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            
            deleteEntitiesHelper(batchTargetEntities);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanDeleteEntity(), false)) {

      for (ProvisioningEntity targetEntity : targetEntities) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("deleteEntity_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            deleteEntityHelper(targetEntity);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot delete entity or entities");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningEntityWrapper> provisioningEntityWrappersSuccessfullyDeleted = new HashSet<ProvisioningEntityWrapper>();
    for (ProvisioningEntity targetEntity : targetEntities) { 
      if (targetEntity.getProvisioned() != null && targetEntity.getProvisioned()) {
        provisioningEntityWrappersSuccessfullyDeleted.add(targetEntity.getProvisioningEntityWrapper());
      }

    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateEntityLink(provisioningEntityWrappersSuccessfullyDeleted, false);
    
    return new TargetDaoDeleteEntitiesResponse();
        
  }

  /**
   * 
   * @param targetEntity
   */
  private void insertEntityHelper(ProvisioningEntity targetEntity) {
    
    TargetDaoInsertEntityRequest targetDaoInsertEntityRequest = new TargetDaoInsertEntityRequest(targetEntity);

    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      this.wrappedDao.insertEntity(targetDaoInsertEntityRequest);
      hasError = logEntity(targetEntity);
      if (targetEntity.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set inserted entity as provisioned: " + this.wrappedDao);
      }
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetEntity.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityInsert")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error inserting entity " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetEntity.getProvisioned() == null) {
        targetEntity.setProvisioned(false);
      }
      if (targetEntity.getException() == null) {
        targetEntity.setException(e);
      }
      logEntity(targetEntity);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "insertEntity");
    }

  }

  @Override
  public TargetDaoInsertEntityResponse insertEntity(
      TargetDaoInsertEntityRequest targetDaoInsertEntityRequest) {

    throw new RuntimeException("Dont call this, call the plural");
  }

  /**
   * 
   * @param targetDaoInsertEntitiesRequest
   * @return response
   */
  private TargetDaoInsertEntitiesResponse insertEntitiesHelper(
      TargetDaoInsertEntitiesRequest targetDaoInsertEntitiesRequest) {

    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoInsertEntitiesResponse targetDaoInsertEntitiesResponse = this.wrappedDao.insertEntities(targetDaoInsertEntitiesRequest);
      hasError = logEntities(targetDaoInsertEntitiesRequest.getTargetEntityInserts());
      
      for (ProvisioningEntity provisioningEntity : targetDaoInsertEntitiesRequest.getTargetEntityInserts()) { 
        if (provisioningEntity.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set inserted entity as provisioned: " + this.wrappedDao);
        }
      }
      
      return targetDaoInsertEntitiesResponse;
    } catch (RuntimeException e) {
      hasError = true;
      boolean first = true;
      for (ProvisioningEntity targetEntity : targetDaoInsertEntitiesRequest.getTargetEntityInserts()) { 
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityInsert")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error inserting entities, e.g. " + (targetEntity == null ? null : targetEntity.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        first = false;

        if (targetEntity.getProvisioned() == null) {
          targetEntity.setProvisioned(false);
        }
        if (targetEntity.getException() == null) {
          targetEntity.setException(e);
        }
      }
      logEntities(targetDaoInsertEntitiesRequest.getTargetEntityInserts());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "insertEntities");
    }
    return null;
  }


  @Override
  public TargetDaoInsertEntitiesResponse insertEntities(
      TargetDaoInsertEntitiesRequest targetDaoInsertEntitiesRequest) {

    List<ProvisioningEntity> targetEntities = targetDaoInsertEntitiesRequest.getTargetEntityInserts();
    if (GrouperUtil.length(targetEntities) == 0) {
      return new TargetDaoInsertEntitiesResponse();
    }
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanInsertEntities(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getInsertEntitiesBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetEntities, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningEntity> batchTargetEntities = GrouperUtil.batchList(targetEntities, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("insertEntities_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoInsertEntitiesRequest targetDaoInsertEntitiesRequestLocal = new TargetDaoInsertEntitiesRequest();
            targetDaoInsertEntitiesRequestLocal.setTargetEntityInserts(batchTargetEntities);
            insertEntitiesHelper(targetDaoInsertEntitiesRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanInsertEntity(), false)) {

      for (ProvisioningEntity targetEntity : targetEntities) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("insertEntity_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            insertEntityHelper(targetEntity);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot insert entity or entities");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningEntityWrapper> provisioningEntityWrappersSuccessfullyInserted = new HashSet<ProvisioningEntityWrapper>();
    for (ProvisioningEntity targetEntity : targetEntities) { 
      if (targetEntity.getProvisioned() != null && targetEntity.getProvisioned()) {
        provisioningEntityWrappersSuccessfullyInserted.add(targetEntity.getProvisioningEntityWrapper());
      }

    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateEntityLink(provisioningEntityWrappersSuccessfullyInserted, false);
    
    return new TargetDaoInsertEntitiesResponse();
 
  }

  /**
   * 
   * @param targetDaoUpdateEntityRequest
   * @return response
   */
  private TargetDaoUpdateEntityResponse updateEntityHelper(TargetDaoUpdateEntityRequest targetDaoUpdateEntityRequest) {

    ProvisioningEntity targetEntity = targetDaoUpdateEntityRequest.getTargetEntity();
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();

      TargetDaoUpdateEntityResponse targetDaoUpdateEntityResponse = this.wrappedDao.updateEntity(targetDaoUpdateEntityRequest);
      hasError = logEntity(targetEntity);
      if (targetEntity.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set updateed entity as provisioned: " + this.wrappedDao);
      }

      return targetDaoUpdateEntityResponse;
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetEntity.toString());
      hasError = true;
      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityUpdate")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error updating entity " + GrouperUtil.getFullStackTrace(e)));
      }
      if (targetEntity.getProvisioned() == null) {
        targetEntity.setProvisioned(false);
      }
      if (targetEntity.getException() == null) {
        targetEntity.setException(e);
      }
      
      setExceptionForMembershipsWhenGroupOrEntityAttributes(targetEntity, null, e);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "updateEntity");
    }
    return null;

  }
  
  
  @Override
  public TargetDaoUpdateEntityResponse updateEntity(
      TargetDaoUpdateEntityRequest targetDaoUpdateEntityRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");

  }


  private void setExceptionForMembershipsWhenGroupOrEntityAttributes(ProvisioningEntity targetEntity, ProvisioningGroup targetGroup, Exception e) {
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.entityAttributes) {
      
      Set<ProvisioningObjectChange> provisionObjectChanges = targetEntity.getInternal_objectChanges();
      for (ProvisioningObjectChange provisioningObjectChange: provisionObjectChanges) {
        if (provisioningObjectChange.getException() != null) {
          
          ProvisioningAttribute provisioningAttribute = targetEntity.getAttributes().get(provisioningObjectChange.getAttributeName());
          
          if (provisioningAttribute != null) {
            Map<Object, ProvisioningMembershipWrapper> valueToProvisioningMembershipWrapper = provisioningAttribute.getValueToProvisioningMembershipWrapper();
            
            if (valueToProvisioningMembershipWrapper != null) {
              
              
              ProvisioningMembershipWrapper provisioningMembershipWrapper = null;
              if (provisioningObjectChange.getProvisioningObjectChangeAction() == ProvisioningObjectChangeAction.insert || 
                  provisioningObjectChange.getProvisioningObjectChangeAction() == ProvisioningObjectChangeAction.update) {
                
                provisioningMembershipWrapper = valueToProvisioningMembershipWrapper.get(provisioningObjectChange.getNewValue());
              } else {
                provisioningMembershipWrapper = valueToProvisioningMembershipWrapper.get(provisioningObjectChange.getOldValue());
              }
              
              if (provisioningMembershipWrapper.getGrouperTargetMembership() != null) {
                provisioningMembershipWrapper.getGrouperTargetMembership().setException(e);
                provisioningMembershipWrapper.getGrouperTargetMembership().setProvisioned(false);
              } else {
                if (provisioningMembershipWrapper.getGcGrouperSyncMembership() != null) {
                  provisioningMembershipWrapper.getGcGrouperSyncMembership().setErrorMessage(GrouperUtil.getFullStackTrace(e));
                  provisioningMembershipWrapper.getGcGrouperSyncMembership().setErrorTimestamp(new Timestamp(System.currentTimeMillis()));
                }
                
              }
             
            }
          }
          
        }
        
      }
    }
    
    if (this.getGrouperProvisioner().retrieveGrouperProvisioningBehavior().getGrouperProvisioningBehaviorMembershipType() == GrouperProvisioningBehaviorMembershipType.groupAttributes) {
      
      Set<ProvisioningObjectChange> provisionObjectChanges = targetGroup.getInternal_objectChanges();
      for (ProvisioningObjectChange provisioningObjectChange: provisionObjectChanges) {
        if (provisioningObjectChange.getException() != null) {
          
          ProvisioningAttribute provisioningAttribute = targetGroup.getAttributes().get(provisioningObjectChange.getAttributeName());
          
          if (provisioningAttribute != null) {
            Map<Object, ProvisioningMembershipWrapper> valueToProvisioningMembershipWrapper = provisioningAttribute.getValueToProvisioningMembershipWrapper();
            
            if (valueToProvisioningMembershipWrapper != null) {
              
              ProvisioningMembershipWrapper provisioningMembershipWrapper = null;
              if (provisioningObjectChange.getProvisioningObjectChangeAction() == ProvisioningObjectChangeAction.insert || 
                  provisioningObjectChange.getProvisioningObjectChangeAction() == ProvisioningObjectChangeAction.update) {
                
                provisioningMembershipWrapper = valueToProvisioningMembershipWrapper.get(provisioningObjectChange.getNewValue());
              } else {
                provisioningMembershipWrapper = valueToProvisioningMembershipWrapper.get(provisioningObjectChange.getOldValue());
              }
              
              if (provisioningMembershipWrapper == null) {
                continue;
              }
              
              if (provisioningMembershipWrapper.getGrouperTargetMembership() != null) {
                provisioningMembershipWrapper.getGrouperTargetMembership().setException(e);
                provisioningMembershipWrapper.getGrouperTargetMembership().setProvisioned(false);
              } else {
                if (provisioningMembershipWrapper.getGcGrouperSyncMembership() != null) {
                  provisioningMembershipWrapper.getGcGrouperSyncMembership().setErrorMessage(GrouperUtil.getFullStackTrace(e));
                  provisioningMembershipWrapper.getGcGrouperSyncMembership().setErrorTimestamp(new Timestamp(System.currentTimeMillis()));
                }
                
              }
              
             
            }
          }
          
        }
        
      }
    }
    
  }
  
  private TargetDaoUpdateEntitiesResponse updateEntitiesHelper(
      TargetDaoUpdateEntitiesRequest targetDaoUpdateEntitiesRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoUpdateEntitiesResponse targetDaoUpdateEntitiesResponse = this.wrappedDao.updateEntities(targetDaoUpdateEntitiesRequest);
      hasError = logEntities(targetDaoUpdateEntitiesRequest.getTargetEntities());
      
      for (ProvisioningEntity provisioningEntity : targetDaoUpdateEntitiesRequest.getTargetEntities()) { 
        if (provisioningEntity.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set updated entity as provisioned: " + this.wrappedDao);
        }
      }
      
      return targetDaoUpdateEntitiesResponse;
    } catch (RuntimeException e) {
      hasError = true;

      boolean first = true;
      for (ProvisioningEntity targetEntity : targetDaoUpdateEntitiesRequest.getTargetEntities()) { 
        
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("entityUpdate")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error updating entities, e.g. " + (targetEntity == null ? null : targetEntity.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        first = false;

        if (targetEntity.getProvisioned() == null) {
          targetEntity.setProvisioned(false);
        }
        if (targetEntity.getException() == null) {
          targetEntity.setException(e);
        }
        
        setExceptionForMembershipsWhenGroupOrEntityAttributes(targetEntity, null, e);
      }
      
      logEntities(targetDaoUpdateEntitiesRequest.getTargetEntities());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "updateEntities");
    }
    return null;
  }


  @Override
  public TargetDaoUpdateEntitiesResponse updateEntities(
      TargetDaoUpdateEntitiesRequest targetDaoUpdateEntitiesRequest) {
    
    List<ProvisioningEntity> targetEntities = targetDaoUpdateEntitiesRequest.getTargetEntities();
    if (GrouperUtil.length(targetEntities) == 0) {
      return new TargetDaoUpdateEntitiesResponse();
    }
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanUpdateEntities(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getUpdateEntitiesBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetEntities, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningEntity> batchTargetEntities = GrouperUtil.batchList(targetEntities, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("updateEntities_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoUpdateEntitiesRequest targetDaoUpdateEntitiesRequestLocal = new TargetDaoUpdateEntitiesRequest();
            targetDaoUpdateEntitiesRequestLocal.setTargetEntities(batchTargetEntities);
            updateEntitiesHelper(targetDaoUpdateEntitiesRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanUpdateEntity(), false)) {

      for (ProvisioningEntity targetEntity : targetEntities) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("updateEntity_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            TargetDaoUpdateEntityRequest targetDaoUpdateEntityRequestLocal = new TargetDaoUpdateEntityRequest();
            targetDaoUpdateEntityRequestLocal.setTargetEntity(targetEntity);
            updateEntityHelper(targetDaoUpdateEntityRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot update entity or entities");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningEntityWrapper> provisioningEntityWrappersSuccessfullyUpdated = new HashSet<ProvisioningEntityWrapper>();
    for (ProvisioningEntity targetEntity : targetEntities) { 
      if (targetEntity.getProvisioned() != null && targetEntity.getProvisioned()) {
        provisioningEntityWrappersSuccessfullyUpdated.add(targetEntity.getProvisioningEntityWrapper());
      }

    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateEntityLink(provisioningEntityWrappersSuccessfullyUpdated, false);
    
    return new TargetDaoUpdateEntitiesResponse();
 
  }

  /**
   * 
   * @param targetMembership
   */
  private void deleteMembershipHelper(ProvisioningMembership targetMembership) {
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoDeleteMembershipRequest targetDaoDeleteMembershipRequest = new TargetDaoDeleteMembershipRequest(targetMembership);
      this.wrappedDao.deleteMembership(targetDaoDeleteMembershipRequest);
      
      if (targetMembership.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set deleted membership as provisioned: " + this.wrappedDao);
      }

      hasError = logMembership(targetMembership);
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetMembership.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("membershipDelete")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error deleting membership, " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetMembership.getProvisioned() == null) {
        targetMembership.setProvisioned(false);
      }
      if (targetMembership.getException() == null) {
        targetMembership.setException(e);
      }
      logMembership(targetMembership);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "deleteMembership");
    }

  }

  @Override
  public TargetDaoDeleteMembershipResponse deleteMembership(
      TargetDaoDeleteMembershipRequest targetDaoDeleteMembershipRequest) {

    throw new RuntimeException("Dont call this, call the plural");

  }


  @Override
  public TargetDaoDeleteGroupsResponse deleteGroups(
      TargetDaoDeleteGroupsRequest targetDaoDeleteGroupsRequest) {

    if (GrouperUtil.length(targetDaoDeleteGroupsRequest.getTargetGroups()) == 0) {
      return new TargetDaoDeleteGroupsResponse();
    }
    List<ProvisioningGroup> targetGroups = targetDaoDeleteGroupsRequest.getTargetGroups();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanDeleteGroups(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getDeleteGroupsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetGroups, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningGroup> batchTargetGroups = GrouperUtil.batchList(targetGroups, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("deleteGroups_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            
            deleteGroupsHelper(batchTargetGroups);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanDeleteGroup(), false)) {

      for (ProvisioningGroup targetGroup : targetGroups) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("deleteGroup_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            deleteGroupHelper(targetGroup);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot delete group or groups");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    Set<ProvisioningGroupWrapper> provisioningGroupWrappersSuccessfullyDeleted = new HashSet<ProvisioningGroupWrapper>();
    for (ProvisioningGroup targetGroup : targetGroups) { 
      if (targetGroup.getProvisioned() != null && targetGroup.getProvisioned()) {
        provisioningGroupWrappersSuccessfullyDeleted.add(targetGroup.getProvisioningGroupWrapper());
      }

    }

    // update the cache
    this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateGroupLink(provisioningGroupWrappersSuccessfullyDeleted, false);
    
    return new TargetDaoDeleteGroupsResponse();

    
  }

  /**
   * delete groups logic
   * @param targetGroups
   */
  private void deleteGroupsHelper(List<ProvisioningGroup> targetGroups) {
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoDeleteGroupsRequest targetDaoDeleteGroupsRequest = new TargetDaoDeleteGroupsRequest(targetGroups);
      this.wrappedDao.deleteGroups(targetDaoDeleteGroupsRequest);
      
      for (ProvisioningGroup targetGroup : targetGroups) { 
        if (targetGroup.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set deleted group as provisioned: " + this.wrappedDao);
        }
      }
      
      hasError = logGroups(targetDaoDeleteGroupsRequest.getTargetGroups());

    } catch (RuntimeException e) {
      hasError = true;
      
      boolean first = true;

      for (ProvisioningGroup targetGroup : targetGroups) { 
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("groupDelete")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error deleting groups, e.g. " + (targetGroup == null ? null : targetGroup.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));

          }
        }
        
        first = false;

        if (targetGroup.getProvisioned() == null) {
          targetGroup.setProvisioned(false);
        }
        if (targetGroup.getException() == null) {
          targetGroup.setException(e);
        }
      }
      logGroups(targetGroups);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "deleteGroups");
    }

  }
  
  /**
   * logic to delete a group
   * @param targetGroup
   */
  private void deleteGroupHelper(ProvisioningGroup targetGroup) {
    TargetDaoDeleteGroupRequest targetDaoDeleteGroupRequest = new TargetDaoDeleteGroupRequest(targetGroup);
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      this.wrappedDao.deleteGroup(targetDaoDeleteGroupRequest);

      if (targetGroup.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set deleted group as provisioned: " + this.wrappedDao);
      }

      hasError = logGroup(targetGroup);
      
    } catch (RuntimeException e) {

      GrouperUtil.injectInException(e, targetGroup.toString());
      hasError = true;
      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("groupDelete")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error deleting group, " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetGroup.getProvisioned() == null) {
        targetGroup.setProvisioned(false);
      }
      if (targetGroup.getException() == null) {
        targetGroup.setException(e);
      }
      logGroup(targetGroup);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "deleteGroup");
    }

  }

  /**
   * 
   * @param targetMembership
   */
  private void insertMembershipHelper(ProvisioningMembership targetMembership) {
    
    TargetDaoInsertMembershipRequest targetDaoInsertMembershipRequest = new TargetDaoInsertMembershipRequest(targetMembership);
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      this.wrappedDao.insertMembership(targetDaoInsertMembershipRequest);
      hasError = logMembership(targetMembership);
      if (targetMembership.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set inserted membership as provisioned: " + this.wrappedDao);
      }
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetMembership.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("membershipInsert")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error inserting membership " + (targetMembership == null ? null : targetMembership.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetMembership.getProvisioned() == null) {
        targetMembership.setProvisioned(false);
      }
      if (targetMembership.getException() == null) {
        targetMembership.setException(e);
      }
      logMembership(targetMembership);
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "insertMembership");
    }
    

  }
  
  @Override
  public TargetDaoInsertMembershipResponse insertMembership(
      TargetDaoInsertMembershipRequest targetDaoInsertMembershipRequest) {

    throw new RuntimeException("Dont call this, call the plural");

  }

  private TargetDaoInsertMembershipsResponse insertMembershipsHelper(
      TargetDaoInsertMembershipsRequest targetDaoInsertMembershipsRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoInsertMembershipsResponse targetDaoInsertMembershipsResponse = this.wrappedDao.insertMemberships(targetDaoInsertMembershipsRequest);
      hasError = logMemberships(targetDaoInsertMembershipsRequest.getTargetMemberships());
      for (ProvisioningMembership provisioningMembership : targetDaoInsertMembershipsRequest.getTargetMemberships()) { 
        if (provisioningMembership.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set inserted membership as provisioned: " + this.wrappedDao);
        }
      }
      return targetDaoInsertMembershipsResponse;
    } catch (RuntimeException e) {
      hasError = true;
      boolean first = true;
      for (ProvisioningMembership targetMembership : targetDaoInsertMembershipsRequest.getTargetMemberships()) { 
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("membershipInsert")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error inserting memberships, e.g. " + (targetMembership == null ? null : targetMembership.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        first = false;

        if (targetMembership.getProvisioned() == null) {
          targetMembership.setProvisioned(false);
        }
        if (targetMembership.getException() == null) {
          targetMembership.setException(e);
        }
      }
      logMemberships(targetDaoInsertMembershipsRequest.getTargetMemberships());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "insertMemberships");
    }
    return new TargetDaoInsertMembershipsResponse();
  }


  @Override
  public TargetDaoInsertMembershipsResponse insertMemberships(
      TargetDaoInsertMembershipsRequest targetDaoInsertMembershipsRequest) {

    if (GrouperUtil.length(targetDaoInsertMembershipsRequest.getTargetMemberships()) == 0) {
      return new TargetDaoInsertMembershipsResponse();
    }
    List<ProvisioningMembership> targetMemberships = targetDaoInsertMembershipsRequest.getTargetMemberships();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanInsertMemberships(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getInsertMembershipsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetMemberships, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningMembership> batchTargetMemberships = GrouperUtil.batchList(targetMemberships, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("insertMemberships_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoInsertMembershipsRequest targetDaoInsertMembershipsRequestLocal = new TargetDaoInsertMembershipsRequest();
            targetDaoInsertMembershipsRequestLocal.setTargetMemberships(batchTargetMemberships);
            insertMembershipsHelper(targetDaoInsertMembershipsRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanInsertMembership(), false)) {

      for (ProvisioningMembership targetMembership : targetMemberships) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("insertMembership_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            
            insertMembershipHelper(targetMembership);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot insert membership or memberships");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    return new TargetDaoInsertMembershipsResponse();

  }


  /**
   * @param targetDaoUpdateMembershipRequest
   * @return response
   */
  private TargetDaoUpdateMembershipResponse updateMembershipHelper (
      TargetDaoUpdateMembershipRequest targetDaoUpdateMembershipRequest) {

    ProvisioningMembership targetMembership = targetDaoUpdateMembershipRequest.getTargetMembership();
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoUpdateMembershipResponse targetDaoUpdateMembershipResponse = this.wrappedDao.updateMembership(targetDaoUpdateMembershipRequest);
      hasError = logMembership(targetDaoUpdateMembershipRequest.getTargetMembership());
      if (targetMembership.getProvisioned() == null) {
        throw new RuntimeException("Dao did not set updated membership as provisioned: " + this.wrappedDao);
      }
      return targetDaoUpdateMembershipResponse;
    } catch (RuntimeException e) {
      GrouperUtil.injectInException(e, targetMembership.toString());
      hasError = true;

      if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("membershipUpdate")) {
        logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
            "Error updating membership " + GrouperUtil.getFullStackTrace(e)));
      }

      if (targetMembership.getProvisioned() == null) {
        targetMembership.setProvisioned(false);
      }
      if (targetMembership.getException() == null) {
        targetMembership.setException(e);
      }
      logMembership(targetDaoUpdateMembershipRequest.getTargetMembership());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "methodName");
    }
    return null;
  }

  @Override
  public TargetDaoUpdateMembershipResponse updateMembership(
      TargetDaoUpdateMembershipRequest targetDaoUpdateMembershipRequest) {
    
    throw new RuntimeException("Dont call this, call the plural");
  }

  private TargetDaoUpdateMembershipsResponse updateMembershipsHelper(
      TargetDaoUpdateMembershipsRequest targetDaoUpdateMembershipsRequest) {
    
    boolean hasError = false;
    boolean commandLogStarted = false;
    try {
      commandLogStarted = commandLogStartLoggingIfConfigured();
      TargetDaoUpdateMembershipsResponse targetDaoUpdateMembershipsResponse = this.wrappedDao.updateMemberships(targetDaoUpdateMembershipsRequest);
      hasError = logMemberships(targetDaoUpdateMembershipsRequest.getTargetMemberships());
      for (ProvisioningMembership provisioningMembership : targetDaoUpdateMembershipsRequest.getTargetMemberships()) { 
        if (provisioningMembership.getProvisioned() == null) {
          throw new RuntimeException("Dao did not set updated membership as provisioned: " + this.wrappedDao);
        }
      }
      return targetDaoUpdateMembershipsResponse;
    } catch (RuntimeException e) {
      hasError = true;

      boolean first = true;
      for (ProvisioningMembership targetMembership : targetDaoUpdateMembershipsRequest.getTargetMemberships()) { 
        
        if(first) {
          if (this.getGrouperProvisioner().retrieveGrouperProvisioningLog().shouldLogError("membershipUpdate")) {
            logError(this.getGrouperProvisioner().retrieveGrouperProvisioningLog().prefixLogLinesWithInstanceId(
                "Error updating memberships, e.g. " + (targetMembership == null ? null : targetMembership.toString()) + "\n" + GrouperUtil.getFullStackTrace(e)));
          }
        }
        first = false;

        if (targetMembership.getProvisioned() == null) {
          targetMembership.setProvisioned(false);
        }
        if (targetMembership.getException() == null) {
          targetMembership.setException(e);
        }
      }
      logMemberships(targetDaoUpdateMembershipsRequest.getTargetMemberships());
    } finally {
      commandLogFinallyBlock(commandLogStarted, hasError, "updateMemberships");
    }
    return null;
  }

  @Override
  public TargetDaoUpdateMembershipsResponse updateMemberships(
      TargetDaoUpdateMembershipsRequest targetDaoUpdateMembershipsRequest) {
    
    if (GrouperUtil.length(targetDaoUpdateMembershipsRequest.getTargetMemberships()) == 0) {
      return new TargetDaoUpdateMembershipsResponse();
    }
    List<ProvisioningMembership> targetMemberships = targetDaoUpdateMembershipsRequest.getTargetMemberships();
    List<GrouperCallable<Void>> grouperCallables = new ArrayList<GrouperCallable<Void>>();
    
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanUpdateMemberships(), false)) {
      
      int batchSize = this.wrappedDao.getGrouperProvisionerDaoCapabilities().getUpdateMembershipsBatchSize();
      int numberOfBatches = GrouperUtil.batchNumberOfBatches(targetMemberships, batchSize, true);

      for (int i=0;i<numberOfBatches;i++) {
        
        final List<ProvisioningMembership> batchTargetMemberships = GrouperUtil.batchList(targetMemberships, batchSize, i);
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("updateMemberships_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
          
          @Override
          public Void callLogic() {
            TargetDaoUpdateMembershipsRequest targetDaoUpdateMembershipsRequestLocal = new TargetDaoUpdateMembershipsRequest();
            targetDaoUpdateMembershipsRequestLocal.setTargetMemberships(batchTargetMemberships);
            updateMembershipsHelper(targetDaoUpdateMembershipsRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanUpdateMembership(), false)) {

      for (ProvisioningMembership targetMembership : targetMemberships) {
        GrouperCallable<Void> grouperCallable = new GrouperCallable<Void>("updateMembership_" + this.getGrouperProvisioner().getConfigId() + "_" + this.getGrouperProvisioner().getInstanceId()) {
  
          @Override
          public Void callLogic() {
            TargetDaoUpdateMembershipRequest targetDaoUpdateMembershipRequestLocal = new TargetDaoUpdateMembershipRequest();
            targetDaoUpdateMembershipRequestLocal.setTargetMembership(targetMembership);
            updateMembershipHelper(targetDaoUpdateMembershipRequestLocal);
            return null;
          }
        };
        grouperCallables.add(grouperCallable);
        
      }
    } else {

      throw new RuntimeException("Dao cannot update membership or memberships");
    }
    
    GrouperUtil.executorServiceSubmit(this.getGrouperProvisioner().retrieveExecutorService(), grouperCallables);
    
    return new TargetDaoUpdateMembershipsResponse();
  }


  @Override
  public TargetDaoSendEntityChangesToTargetResponse sendEntityChangesToTarget(
      TargetDaoSendEntityChangesToTargetRequest targetDaoSendEntityChangesToTargetRequest) {
    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanSendEntityChangesToTarget(), false)) {
      
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoSendEntityChangesToTargetResponse targetDaoSendEntityChangesToTargetResponse = this.wrappedDao.sendEntityChangesToTarget(targetDaoSendEntityChangesToTargetRequest);
        if (logEntities(targetDaoSendEntityChangesToTargetRequest.getTargetEntityInserts())) {
          hasError = true;
        }
        if (logEntities(targetDaoSendEntityChangesToTargetRequest.getTargetEntityUpdates())) {
          hasError = true;
        }
        if (logEntities(targetDaoSendEntityChangesToTargetRequest.getTargetEntityDeletes())) {
          hasError = true;
        }
        
        Set<ProvisioningEntityWrapper> provisioningEntityWrappersSuccessfullyUpdated = new HashSet<ProvisioningEntityWrapper>();
        for (ProvisioningEntity provisioningEntity : targetDaoSendEntityChangesToTargetRequest.getTargetEntityInserts()) { 
          if (provisioningEntity.getProvisioned() != null && provisioningEntity.getProvisioned()) {
            provisioningEntityWrappersSuccessfullyUpdated.add(provisioningEntity.getProvisioningEntityWrapper());
          }
        }
        
        for (ProvisioningEntity provisioningEntity : targetDaoSendEntityChangesToTargetRequest.getTargetEntityUpdates()) { 
          if (provisioningEntity.getProvisioned() != null && provisioningEntity.getProvisioned()) {
            provisioningEntityWrappersSuccessfullyUpdated.add(provisioningEntity.getProvisioningEntityWrapper());
          }
        }
        
        for (ProvisioningEntity provisioningEntity : targetDaoSendEntityChangesToTargetRequest.getTargetEntityDeletes()) { 
          if (provisioningEntity.getProvisioned() != null && provisioningEntity.getProvisioned()) {
            provisioningEntityWrappersSuccessfullyUpdated.add(provisioningEntity.getProvisioningEntityWrapper());
          }
        }
        
        // update the cache
        this.getGrouperProvisioner().retrieveGrouperProvisioningLinkLogic().updateEntityLink(provisioningEntityWrappersSuccessfullyUpdated, false);
        
        return targetDaoSendEntityChangesToTargetResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "sendEntityChangesToTarget");
      }
      
    }
    if (GrouperUtil.length(targetDaoSendEntityChangesToTargetRequest.getTargetEntityDeletes()) > 0){
      List<ProvisioningEntity> targetEntityDeletes = targetDaoSendEntityChangesToTargetRequest.getTargetEntityDeletes();
      TargetDaoDeleteEntitiesRequest targetDaoDeleteEntitiesRequest = new TargetDaoDeleteEntitiesRequest();
      targetDaoDeleteEntitiesRequest.setTargetEntities(targetEntityDeletes);
      this.deleteEntities(targetDaoDeleteEntitiesRequest);
    }

    if (GrouperUtil.length(targetDaoSendEntityChangesToTargetRequest.getTargetEntityInserts()) > 0) {
      List<ProvisioningEntity> targetEntityInserts = targetDaoSendEntityChangesToTargetRequest.getTargetEntityInserts();
      
      this.insertEntities(new TargetDaoInsertEntitiesRequest(targetEntityInserts));
    }
    if (GrouperUtil.length(targetDaoSendEntityChangesToTargetRequest.getTargetEntityUpdates()) > 0) {
      List<ProvisioningEntity> targetEntityUpdates = targetDaoSendEntityChangesToTargetRequest.getTargetEntityUpdates();
      
      this.updateEntities(new TargetDaoUpdateEntitiesRequest(targetEntityUpdates));
    }
    return null;
  }


  @Override
  public TargetDaoSendMembershipChangesToTargetResponse sendMembershipChangesToTarget(
      TargetDaoSendMembershipChangesToTargetRequest targetDaoSendMembershipChangesToTargetRequest) {

    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanSendMembershipChangesToTarget(), false)) {
      
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();

        TargetDaoSendMembershipChangesToTargetResponse targetDaoSendMembershipChangesToTargetResponse = this.wrappedDao.sendMembershipChangesToTarget(targetDaoSendMembershipChangesToTargetRequest);
        if (logMemberships(targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipInserts())) {
          hasError = true;
        }
        if (logMemberships(targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipUpdates())) {
          hasError = true;
        }
        if (logMemberships(targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipDeletes())) {
          hasError = true;
        }
        
        Collection<List<ProvisioningMembership>> provisioningMembershipsLists = targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipReplaces().values();
        
        List<ProvisioningMembership> provisioningMembershipsToLog = new ArrayList<ProvisioningMembership>();
        for (List<ProvisioningMembership> provisioningMemberships: provisioningMembershipsLists) {
          provisioningMembershipsToLog.addAll(provisioningMemberships);
        }
        
        if (logMemberships(provisioningMembershipsToLog)) {
          hasError = true;
        }
        return targetDaoSendMembershipChangesToTargetResponse;
      } catch (RuntimeException e) {
        hasError = true;
        throw e;
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "sendMembershipChangesToTarget");
      }

    }
    {
      List<ProvisioningMembership> targetMembershipDeletes = targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipDeletes();
      TargetDaoDeleteMembershipsRequest targetDaoDeleteMembershipsRequest = new TargetDaoDeleteMembershipsRequest();
      targetDaoDeleteMembershipsRequest.setTargetMemberships(targetMembershipDeletes);
      this.deleteMemberships(targetDaoDeleteMembershipsRequest);
    }

    {
      List<ProvisioningMembership> targetMembershipInserts = targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipInserts();
      
      this.insertMemberships(new TargetDaoInsertMembershipsRequest(targetMembershipInserts));
    }
    {
      List<ProvisioningMembership> targetMembershipUpdates = targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipUpdates();
      
      this.updateMemberships(new TargetDaoUpdateMembershipsRequest(targetMembershipUpdates));
    }
    {
      Map<ProvisioningGroup, List<ProvisioningMembership>> targetMembershipReplaces = targetDaoSendMembershipChangesToTargetRequest.getTargetMembershipReplaces();
      
      for (ProvisioningGroup targetGroup: targetMembershipReplaces.keySet()) {
        
        this.replaceGroupMemberships(new TargetDaoReplaceGroupMembershipsRequest(targetGroup, targetMembershipReplaces.get(targetGroup)));
      }
      
      
    }
    return null;

  }

  @Override
  public TargetDaoReplaceGroupMembershipsResponse replaceGroupMemberships(TargetDaoReplaceGroupMembershipsRequest targetDaoReplaceGroupMembershipsRequest) {
    
    if (GrouperUtil.length(targetDaoReplaceGroupMembershipsRequest.getTargetMemberships()) == 0) {
      return new TargetDaoReplaceGroupMembershipsResponse();
    }

    if (GrouperUtil.booleanValue(this.wrappedDao.getGrouperProvisionerDaoCapabilities().getCanReplaceGroupMemberships(), false)) {
      boolean hasError = false;
      boolean commandLogStarted = false;
      try {
        commandLogStarted = commandLogStartLoggingIfConfigured();
        TargetDaoReplaceGroupMembershipsResponse targetDaoReplaceGroupMembershipsResponse = this.wrappedDao.replaceGroupMemberships(targetDaoReplaceGroupMembershipsRequest);
        hasError = logMemberships(targetDaoReplaceGroupMembershipsRequest.getTargetMemberships());
        for (ProvisioningMembership provisioningMembership : targetDaoReplaceGroupMembershipsRequest.getTargetMemberships()) { 
          if (provisioningMembership.getProvisioned() == null) {
            throw new RuntimeException("Dao did not set updated membership as provisioned: " + this.wrappedDao);
          }
        }
        return targetDaoReplaceGroupMembershipsResponse;
      } catch (RuntimeException e) {
        hasError = true;
        for (ProvisioningMembership targetMembership : targetDaoReplaceGroupMembershipsRequest.getTargetMemberships()) { 
          
          if (targetMembership.getProvisioned() == null) {
            targetMembership.setProvisioned(false);
          }
          if (targetMembership.getException() == null) {
            GrouperUtil.injectInException(e, targetMembership.toString());
            targetMembership.setException(e);
          }
        }
        logMemberships(targetDaoReplaceGroupMembershipsRequest.getTargetMemberships());
      } finally {
        commandLogFinallyBlock(commandLogStarted, hasError, "replaceGroupMemberships");
      }
      return null;
    }

    throw new RuntimeException("Dao cannot replace group memberships");
    
  }

}
