package edu.internet2.middleware.grouper.app.sqlProvisioning;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.app.provisioning.GrouperProvisioningFullSyncJob;
import edu.internet2.middleware.grouper.app.provisioning.ProvisioningConsumer;
import edu.internet2.middleware.grouper.cfg.dbConfig.GrouperDbConfig;
import edu.internet2.middleware.grouper.changeLog.esb.consumer.EsbConsumer;
import edu.internet2.middleware.grouperClient.config.ConfigPropertiesCascadeBase;

/**
 * @author 
 */
public class SqlProvisionerTestUtils {
  
  /**
   * 
   * @param sqlProvisioningTestConfigInput
   * @param suffix
   * @param value
   */
  public static void configureProvisionerSuffix(SqlProvisionerTestConfigInput sqlProvisioningTestConfigInput, String suffix, String value) {
    // if its overridden then dont set
    if (!sqlProvisioningTestConfigInput.getExtraConfig().containsKey(suffix)) {
      new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("provisioner." + sqlProvisioningTestConfigInput.getConfigId() + "." + suffix).value(value).store();
    }
  }
  
  public static void configureSqlProvisionerAcLilleDatabase(SqlProvisionerTestConfigInput provisioningTestConfigInput) {
    configureProvisionerSuffix(provisioningTestConfigInput, "addDisabledFullSyncDaemon", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "addDisabledIncrementalSyncDaemon", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "class", SqlProvisioner.class.getName());
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeMembershipCrud", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "dbExternalSystemConfigId", "grouper");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteMembershipsIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteMembershipsOnlyInTrackedGroups", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipAdvancedOptions", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipTableName", "testgrouper_prov_mship0");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfMembershipAttributes", "2");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperMemberships", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "provisioningType", "membershipObjects");
    configureProvisionerSuffix(provisioningTestConfigInput, "showAdvanced", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "logAllObjectsVerbose", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "startWith", "this is start with read only");
    configureProvisionerSuffix(provisioningTestConfigInput, "subjectSourcesToProvision", "jdbc");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.name", "group_name");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateFromGrouperProvisioningGroupField", "displayExtension");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.name", "subject_id");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateFromGrouperProvisioningEntityField", "subjectId");
    
  }
  
  public static void configureSqlProvisionerDentalDatabase(SqlProvisionerTestConfigInput provisioningTestConfigInput) {
    
    configureProvisionerSuffix(provisioningTestConfigInput, "class", SqlProvisioner.class.getName());
    configureProvisionerSuffix(provisioningTestConfigInput, "dbExternalSystemConfigId", "grouper");
    configureProvisionerSuffix(provisioningTestConfigInput, "debugLog", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeEntityCrud", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeGroupCrud", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeMembershipCrud", "true");
    
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteEntitiesIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteGroupsIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteMembershipsIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0entityAttribute", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0has", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0source", "target");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0type", "entityAttribute");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCacheHas", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttribute0name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttributeCount", "1");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0groupAttribute", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0has", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0source", "target");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0type", "groupAttribute");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCacheHas", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupTableIdColumn", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupTableName", "testgrouper_prov_group");
    configureProvisionerSuffix(provisioningTestConfigInput, "hasTargetEntityLink", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "hasTargetGroupLink", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "logAllObjectsVerbose", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "makeChangesToEntities", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipEntityForeignKeyColumn", "entity_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipGroupForeignKeyColumn", "group_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipTableName", "testgrouper_prov_mship3");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfEntityAttributes", "3");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfGroupAttributes", "2");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfMembershipAttributes", "2");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperEntities", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperGroups", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperMemberships", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "provisioningType", "membershipObjects");
    configureProvisionerSuffix(provisioningTestConfigInput, "selectAllEntities", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "showAdvanced", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "showAssigningProvisioning", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "startWith", "this is start with read only");
    
    configureProvisionerSuffix(provisioningTestConfigInput, "subjectSourcesToProvision", "jdbc");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateFromGrouperProvisioningEntityField", "id");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.name", "name");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateFromGrouperProvisioningEntityField", "subjectIdentifier0");

    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.name", "subject_id_or_identifier");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.translateFromGrouperProvisioningEntityField", "subjectId");
    
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateFromGrouperProvisioningGroupField", "id");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.name", "name");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateFromGrouperProvisioningGroupField", "name");
    
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.name", "group_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateFromGrouperProvisioningGroupField", "id");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.name", "entity_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateFromGrouperProvisioningEntityField", "id");
    configureProvisionerSuffix(provisioningTestConfigInput, "userPrimaryKey", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "userTableName", "testgrouper_prov_entity");
    
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("otherJob.provisioner_full_" + provisioningTestConfigInput.getConfigId() + ".class").value(GrouperProvisioningFullSyncJob.class.getName()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("otherJob.provisioner_full_" + provisioningTestConfigInput.getConfigId() + ".quartzCron").value("9 59 23 31 12 ? 2099").store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("otherJob.provisioner_full_" + provisioningTestConfigInput.getConfigId() + ".provisionerConfigId").value(provisioningTestConfigInput.getConfigId()).store();
    
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".class").value(EsbConsumer.class.getName()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".quartzCron").value("9 59 23 31 12 ? 2099").store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".provisionerConfigId").value(provisioningTestConfigInput.getConfigId()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".publisher.class").value(ProvisioningConsumer.class.getName()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".publisher.debug").value("true").store();
    
    
    
    
    /**
     * 
          provisioner.dentalDatabase.groupAllowedToAssign = penn\u003Adental\u003Aetc\u003Asecurity\u003AdentalAdmins

     */
    
    
  }
  
  /**
   * @param provisioningTestConfigInput     
   * SqlProvisionerTestUtils.configureSqlProvisioner(
   *       new SqlProvisioningTestConfigInput()
   *    .assignConfigId(string)
   *    .addExtraConfig("allowProvisionableRegexOverride", "true")
   *
   */
  public static void configureSqlProvisioner(SqlProvisionerTestConfigInput provisioningTestConfigInput) {
    
    configureProvisionerSuffix(provisioningTestConfigInput, "startWith", "this is start with read only");

    if (StringUtils.equals("pennListserv", provisioningTestConfigInput.getProvisioningStrategy())) {
    
      configurePennListserv(provisioningTestConfigInput);  
      
    } else if (StringUtils.equals("acLille", provisioningTestConfigInput.getProvisioningStrategy())) {
      configureSqlProvisionerAcLilleDatabase(provisioningTestConfigInput);  
    } else {
      configureProvisionerSuffix(provisioningTestConfigInput, "class", SqlProvisioner.class.getName());
      configureProvisionerSuffix(provisioningTestConfigInput, "dbExternalSystemConfigId", "grouper");
      configureProvisionerSuffix(provisioningTestConfigInput, "debugLog", "true");
      
      if (!StringUtils.isBlank(provisioningTestConfigInput.getEntityDeleteType())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "customizeEntityCrud", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "makeChangesToEntities", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, provisioningTestConfigInput.getEntityDeleteType(), "true");
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getGroupDeleteType())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "customizeGroupCrud", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, provisioningTestConfigInput.getGroupDeleteType(), "true");
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getMembershipDeleteType())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "customizeMembershipCrud", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, provisioningTestConfigInput.getMembershipDeleteType(), "true");
      }
      if (provisioningTestConfigInput.isEntityResolverGlobal()) {
  
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.baseDn").value("ou=People,dc=example,dc=edu").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.grouperAttributeThatMatchesRecord").value("subjectId").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.ldapAttributes").value("givenName,mail,objectClass").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.ldapConfigId").value("personLdap").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.resolverType").value("ldap").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.searchScope").value("SUBTREE_SCOPE").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalLdapConfig.subjectSearchMatchingAttribute").value("mail").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.columnNames").value("school,subject_id_or_identifier").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.grouperAttributeThatMatchesRow").value("subjectId").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.resolverType").value("sql").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.sqlConfigId").value("grouper").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.subjectSearchMatchingColumn").value("subject_id_or_identifier").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.subjectSourceIdColumn").value("jdbc").store();
        new GrouperDbConfig().configFileName("grouper.properties").propertyName("entityAttributeResolver.globalSqlEntityResolver.tableOrViewName").value("testgrouper_prov_entity1").store();
  
        
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.entityAttributesNotInSubjectSource", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.globalLDAPResolver", "globalLdapConfig");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.globalSQLResolver", "globalSqlEntityResolver");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.resolveAttributesWithLDAP", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.resolveAttributesWithSQL", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.useGlobalLDAPResolver", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.useGlobalSQLResolver", "true");
  
  
      } else if (provisioningTestConfigInput.isEntityResolverLocal()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.attributes", "givenName,mail,objectClass");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.baseDN", "ou=People,dc=example,dc=edu");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.entityAttributesNotInSubjectSource", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.filterAllLDAPOnFull", "false");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.ldapConfigId", "personLdap");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.ldapMappingEntityAttribute", "subjectId");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.ldapMappingType", "entityAttribute");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.ldapMatchingSearchAttribute", "mail");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.resolveAttributesWithLDAP", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.resolveAttributesWithSQL", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.searchScope", "SUBTREE_SCOPE");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.selectAllSQLOnFull", "false");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.useGlobalLDAPResolver", "false");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.useGlobalSQLResolver", "false");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.sqlConfigId", "grouper");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.sqlMappingEntityAttribute", "subjectId");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.sqlMappingType", "entityAttribute");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.subjectSearchMatchingColumn", "subject_id_or_identifier");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.tableOrViewName", "testgrouper_prov_entity1");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityResolver.columnNames", "subject_id_or_identifier, school, subject_source_id");
        
        
      }
      
      if (provisioningTestConfigInput.isEntityAttributesTable()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "useSeparateTableForEntityAttributes", "true");
  
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributesAttributeNameColumn", "entity_attribute_name");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributesAttributeValueColumn", "entity_attribute_value");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributesEntityForeignKeyColumn", "entity_uuid");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributesTableName", "testgrouper_pro_dap_entity_attr");
        configureProvisionerSuffix(provisioningTestConfigInput, "entity2advanced", "true");
//      configureProvisionerSuffix(provisioningTestConfigInput, "sqlLastModifiedColumnName", "last_modified");
//      configureProvisionerSuffix(provisioningTestConfigInput, "sqlLastModifiedColumnType", "timestamp");
        
      }
      
      if (provisioningTestConfigInput.isFailsafeDefaults()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "showFailsafe", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeMaxOverallPercentGroupsRemove", "-1");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeMaxOverallPercentMembershipsRemove", "-1");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeMaxPercentRemove", "-1");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeMinGroupSize", "-1");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeMinManagedGroups", "-1");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeMinOverallNumberOfMembers", "-1");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeSendEmail", "false");
        configureProvisionerSuffix(provisioningTestConfigInput, "failsafeUse", "true");
      }
      
      if (provisioningTestConfigInput.isGroupAttributesTable()) {
        
        configureProvisionerSuffix(provisioningTestConfigInput, "useSeparateTableForGroupAttributes", "true");
  
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributesAttributeNameColumn", "attribute_name");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributesAttributeValueColumn", "attribute_value");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributesGroupForeignKeyColumn", "group_uuid");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributesTableName", "testgrouper_pro_ldap_group_attr");
        configureProvisionerSuffix(provisioningTestConfigInput, "group2advanced", "true");
//        configureProvisionerSuffix(provisioningTestConfigInput, "sqlLastModifiedColumnName", "last_modified");
//        configureProvisionerSuffix(provisioningTestConfigInput, "sqlLastModifiedColumnType", "timestamp");
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getGroupTableIdColumn())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "groupTableIdColumn", provisioningTestConfigInput.getGroupTableIdColumn());
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getGroupTableName())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "groupTableName", provisioningTestConfigInput.getGroupTableName());
      }
      if (provisioningTestConfigInput.isHasTargetEntityLink()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "hasTargetEntityLink", "true");
      }
      if (provisioningTestConfigInput.isHasTargetGroupLink()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "hasTargetGroupLink", "true");
      }
      
      if (provisioningTestConfigInput.isOperateOnGrouperMemberships()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "customizeMembershipCrud", "true");
      }
      
      configureProvisionerSuffix(provisioningTestConfigInput, "logAllObjectsVerbose", "true");
      if (!StringUtils.isBlank(provisioningTestConfigInput.getMembershipEntityForeignKeyColumn())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "membershipEntityForeignKeyColumn", provisioningTestConfigInput.getMembershipEntityForeignKeyColumn());
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getMembershipGroupForeignKeyColumn())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "membershipGroupForeignKeyColumn", provisioningTestConfigInput.getMembershipGroupForeignKeyColumn());
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getMembershipTableName())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "membershipTableName", provisioningTestConfigInput.getMembershipTableName());
      }
      
      if (provisioningTestConfigInput.isOperateOnGrouperMemberships()) {
  
        configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperEntities", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "subjectSourcesToProvision", "jdbc");
      }
      
      if (provisioningTestConfigInput.getEntityAttributeCount() > 0) {
        configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperEntities", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "numberOfEntityAttributes", "" + provisioningTestConfigInput.getEntityAttributeCount());
        configureProvisionerSuffix(provisioningTestConfigInput, "customizeEntityCrud", "true");
        if (provisioningTestConfigInput.getEntityAttributeCount() != 3) {
          configureProvisionerSuffix(provisioningTestConfigInput, "makeChangesToEntities", "true");
        }      
        configureProvisionerSuffix(provisioningTestConfigInput, "selectAllEntities", "true");
  
      } else {
        if (provisioningTestConfigInput.isOperateOnGrouperMemberships()) {
  
          configureProvisionerSuffix(provisioningTestConfigInput, "customizeEntityCrud", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "selectEntities", "false");
        }
      }
      
      if (provisioningTestConfigInput.getEntityAttributeCount() == 3) {
        
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.name", "dn");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.storageType", "separateAttributesTable");
        }
        
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCacheHas", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2has", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2source", "target");
        if (provisioningTestConfigInput.isCacheObjects()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2type", "entityObject");
        } else {
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2type", "entityAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2entityAttribute", "dn");
        }
  
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.name", "employeeId");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateFromGrouperProvisioningEntityField", "subjectId");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttributeCount", "1");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttribute0name", "employeeId");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.name", "entity_uuid");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.translateFromGrouperProvisioningEntityField", "id");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.storageType", "entityTableColumn");
        }
  
  
        
      } else if (provisioningTestConfigInput.getEntityAttributeCount() >= 5) {
  
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.name", "uuid");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.storageType", "entityTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateExpressionCreateOnly", "${edu.internet2.middleware.grouper.internal.util.GrouperUuid.getUuid()}");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateExpressionTypeCreateOnly", "translationScript");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.showAdvancedAttribute", "true");
        
        if (provisioningTestConfigInput.isCacheObjects()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCacheHas", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0source", "target");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0type", "entityObject");
        } else {
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCacheHas", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0source", "grouper");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0type", "entityAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0entityAttribute", "uuid");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache1has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache1source", "grouper");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache1type", "subjectTranslationScript");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache1translationScript", "${subject.name}");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2source", "target");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2type", "entityAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache2entityAttribute", "name");
        }
        
        configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttributeCount", "1");
        configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttribute0name", "name");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.name", "name");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.storageType", "entityTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateFromGrouperProvisioningEntityField", "entityAttributeValueCache1");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.name", "subject_id_or_identifier");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.storageType", "entityTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.2.translateFromGrouperProvisioningEntityField", "subjectId");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.3.name", "email");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.3.storageType", "entityTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.3.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.3.translateFromGrouperProvisioningEntityField", "email");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.4.name", "description");
        if (provisioningTestConfigInput.isEntityAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.4.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.4.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.4.translateFromGrouperProvisioningEntityField", "description");
  
        if (provisioningTestConfigInput.getEntityAttributeCount() == 6) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.5.name", "school");
          if (provisioningTestConfigInput.isEntityAttributesTable()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.5.storageType", "separateAttributesTable");
          }
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.5.translateExpressionType", "translationScript");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.5.translateExpression", "${grouperProvisioningEntity.retrieveAttributeValueString('entityAttributeResolverLdap__givenname')}");
          
        }
      }
  
      if (provisioningTestConfigInput.getGroupAttributeCount() > 0) {
        configureProvisionerSuffix(provisioningTestConfigInput, "numberOfGroupAttributes", "" + provisioningTestConfigInput.getGroupAttributeCount());
        configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperGroups", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "customizeGroupCrud", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "insertGroups", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "selectGroups", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "updateGroups", "true");
              
      }
      if (provisioningTestConfigInput.getGroupAttributeCount() == 1) {
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.name", "uuid");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.storageType", "groupTableColumn");
        }
        
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "uuid");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateFromGrouperProvisioningGroupField", "name");
      }
  
      
      if (provisioningTestConfigInput.getGroupAttributeCount() == 3) {
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "uuid");
  
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.name", "uuid");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.storageType", "groupTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateFromGrouperProvisioningGroupField", "name");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.name", "subjectId");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.storageType", "separateAttributesTable");
        }
  
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMembershipAttributeName", "subjectId");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMembershipAttributeValue", "entityAttributeValueCache0");

        if (provisioningTestConfigInput.getEntityAttributeCount() == 0) {
          configureProvisionerSuffix(provisioningTestConfigInput, "numberOfEntityAttributes", "1");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.name", "subjectId");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateExpressionType", "grouperProvisioningEntityField");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateFromGrouperProvisioningEntityField", "subjectId");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCacheHas", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0source", "grouper");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0type", "entityAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0entityAttribute", "subjectId");
        }
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.name", "groupName");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateFromGrouperProvisioningGroupField", "name");
  
      }
      
      if (provisioningTestConfigInput.isCacheObjects()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCacheHas", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0has", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0source", "target");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0type", "groupObject");
      }
      
      if (provisioningTestConfigInput.getGroupAttributeCount() == 4) {
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.name", "uuid");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.storageType", "groupTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionCreateOnly", "${edu.internet2.middleware.grouper.internal.util.GrouperUuid.getUuid()}");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionTypeCreateOnly", "translationScript");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.showAdvancedAttribute", "true");
  
        if (!provisioningTestConfigInput.isCacheObjects()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCacheHas", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0source", "grouper");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0type", "groupAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache0groupAttribute", "uuid");
        }
  
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.name", "description");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateFromGrouperProvisioningGroupField", "description");
  
        if (provisioningTestConfigInput.isPosixId()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.name", "name");
          
          if (!provisioningTestConfigInput.isCacheObjects()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1has", "true");
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1source", "target");
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1type", "groupAttribute");
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1groupAttribute", "name");
          }
          
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeSameAsSearchAttribute", "false");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupSearchAttributeCount", "1");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupSearchAttribute0name", "name");
  
          
          if (provisioningTestConfigInput.isGroupAttributesTable()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.storageType", "groupTableColumn");
          }
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateExpressionType", "grouperProvisioningGroupField");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateFromGrouperProvisioningGroupField", "name");
  
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "posix_id");
          
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.name", "posix_id");
          if (provisioningTestConfigInput.isGroupAttributesTable()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.storageType", "groupTableColumn");
          }
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.translateExpressionType", "grouperProvisioningGroupField");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.translateFromGrouperProvisioningGroupField", "idIndex");
          
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.showAdvancedAttribute", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.showAttributeValueSettings", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.valueType", "long");
          
        } else {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.name", "name");
          
          if (!provisioningTestConfigInput.isCacheObjects()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1has", "true");
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1source", "target");
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1type", "groupAttribute");
            configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache1groupAttribute", "name");
          }
          
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "name");
  
          if (provisioningTestConfigInput.isGroupAttributesTable()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.storageType", "separateAttributesTable");
          }
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateExpressionType", "grouperProvisioningGroupField");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateFromGrouperProvisioningGroupField", "name");
          
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMembershipAttributeName", "subjectId");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupMembershipAttributeValue", "entityAttributeValueCache0");
  
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.name", "subjectId");
          if (provisioningTestConfigInput.isGroupAttributesTable()) {
            configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.storageType", "separateAttributesTable");
          }
  
        }
        
        if (provisioningTestConfigInput.getEntityAttributeCount() == 0) {
          configureProvisionerSuffix(provisioningTestConfigInput, "numberOfEntityAttributes", "1");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.name", "subjectId");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateExpressionType", "grouperProvisioningEntityField");
          configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateFromGrouperProvisioningEntityField", "subjectId");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCacheHas", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0source", "grouper");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0type", "entityAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "entityAttributeValueCache0entityAttribute", "subjectId");
        }
      }
      
      if (provisioningTestConfigInput.getGroupAttributeCount() == 6) {
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.name", "cn");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateFromGrouperProvisioningGroupField", "name");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.name", "dn");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateExpression", "${'cn=' + grouperProvisioningGroup.getName() + ',OU=Grouper,OU=365Groups,DC=one,DC=upenn,DC=edu'}");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateExpressionType", "translationScript");
        
        if (!provisioningTestConfigInput.isCacheObjects()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCacheHas", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache2has", "true");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache2source", "grouper");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache2type", "groupAttribute");
          configureProvisionerSuffix(provisioningTestConfigInput, "groupAttributeValueCache2groupAttribute", "dn");
        }
        
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "gidNumber");
  
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.name", "gidNumber");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateFromGrouperProvisioningGroupField", "idIndex");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.showAdvancedAttribute", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.showAttributeValueSettings", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.multiValued", "true");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.name", "objectClass");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.storageType", "separateAttributesTable");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.translateExpression", "${grouperUtil.toSet('group')}");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.3.translateExpressionType", "translationScript");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.4.name", "member");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.4.storageType", "separateAttributesTable");
        }
        
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMembershipAttributeName", "member");
        configureProvisionerSuffix(provisioningTestConfigInput, "groupMembershipAttributeValue", "entityAttributeValueCache2");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.5.name", "uuid");
        if (provisioningTestConfigInput.isGroupAttributesTable()) {
          configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.5.storageType", "groupTableColumn");
        }
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.5.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.5.translateFromGrouperProvisioningGroupField", "name");
  
      }
      
      if (provisioningTestConfigInput.isOperateOnGrouperMemberships()) {
        configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperMemberships", "true");
      }
      
      if (provisioningTestConfigInput.getMembershipAttributeCount() > 0) {
        configureProvisionerSuffix(provisioningTestConfigInput, "numberOfMembershipAttributes", "" + provisioningTestConfigInput.getMembershipAttributeCount());
        
  //      for (int i=0;i<provisioningTestConfigInput.getMembershipAttributeCount();i++) {
  //        // note, we dont really need these...
  //        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute." + i + ".showAdvancedAttribute", "true");
  //        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute." + i + ".showAttributeCrud", "true");
  //        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute." + i + ".select", "true");
  //        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute." + i + ".insert", "true");
  //        
  //      }
        
      }
      if (provisioningTestConfigInput.getMembershipAttributeCount() == 2) {
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.name", "group_name");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateFromGrouperProvisioningGroupField", "name");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.name", "subject_id");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateFromGrouperProvisioningEntityField", "subjectId");
      }
      if (provisioningTestConfigInput.getMembershipAttributeCount() == 3) {
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.name", "uuid");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateExpressionCreateOnly", "${edu.internet2.middleware.grouper.internal.util.GrouperUuid.getUuid()}");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateExpressionTypeCreateOnly", "translationScript");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.showAdvancedAttribute", "true");
        
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.name", "group_uuid");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateExpressionType", "grouperProvisioningGroupField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateFromGrouperProvisioningGroupField", "groupAttributeValueCache0");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.2.name", "entity_uuid");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.2.translateExpressionType", "grouperProvisioningEntityField");
        configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.2.translateFromGrouperProvisioningEntityField", "entityAttributeValueCache0");
      }
      
      if (!StringUtils.isBlank(provisioningTestConfigInput.getProvisioningType())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "provisioningType", provisioningTestConfigInput.getProvisioningType());
      }
      configureProvisionerSuffix(provisioningTestConfigInput, "showAdvanced", "true");
      configureProvisionerSuffix(provisioningTestConfigInput, "logAllObjectsVerbose", "true");
  
      
      configureProvisionerSuffix(provisioningTestConfigInput, "showProvisioningDiagnostics", "true");
  
      
      if (!StringUtils.isBlank(provisioningTestConfigInput.getEntityTableIdColumn())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "userPrimaryKey", provisioningTestConfigInput.getEntityTableIdColumn());
      }
      if (!StringUtils.isBlank(provisioningTestConfigInput.getEntityTableName())) {
        configureProvisionerSuffix(provisioningTestConfigInput, "userTableName", provisioningTestConfigInput.getEntityTableName());
      }
    }    
    for (String key: provisioningTestConfigInput.getExtraConfig().keySet()) {
      String theValue = provisioningTestConfigInput.getExtraConfig().get(key);
      if (!StringUtils.isBlank(theValue)) {
        new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("provisioner." + provisioningTestConfigInput.getConfigId() + "." + key).value(theValue).store();
      }
    }

    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("otherJob.provisioner_full_" + provisioningTestConfigInput.getConfigId() + ".class").value(GrouperProvisioningFullSyncJob.class.getName()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("otherJob.provisioner_full_" + provisioningTestConfigInput.getConfigId() + ".quartzCron").value("9 59 23 31 12 ? 2099").store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("otherJob.provisioner_full_" + provisioningTestConfigInput.getConfigId() + ".provisionerConfigId").value(provisioningTestConfigInput.getConfigId()).store();
    
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".class").value(EsbConsumer.class.getName()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".quartzCron").value("9 59 23 31 12 ? 2099").store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".provisionerConfigId").value(provisioningTestConfigInput.getConfigId()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".publisher.class").value(ProvisioningConsumer.class.getName()).store();
    new GrouperDbConfig().configFileName("grouper-loader.properties").propertyName("changeLog.consumer.provisioner_incremental_" + provisioningTestConfigInput.getConfigId() + ".publisher.debug").value("true").store();
    
    ConfigPropertiesCascadeBase.clearCache();
  
  }

  private static void configurePennListserv(
      SqlProvisionerTestConfigInput provisioningTestConfigInput) {
    
    configureProvisionerSuffix(provisioningTestConfigInput, "addDisabledFullSyncDaemon", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "addDisabledIncrementalSyncDaemon", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "class", "edu.internet2.middleware.grouper.app.sqlProvisioning.SqlProvisioner");
    configureProvisionerSuffix(provisioningTestConfigInput, "configureMetadata", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeEntityCrud", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeGroupCrud", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "customizeMembershipCrud", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "dbExternalSystemConfigId", "grouper");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteEntitiesIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteGroupsIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "deleteMembershipsIfNotExistInGrouper", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttribute0name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "entityMatchingAttributeCount", "1");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttribute0name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupMatchingAttributeCount", "1");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupTableIdColumn", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "groupTableName", "testgrouper_prov_group");
    configureProvisionerSuffix(provisioningTestConfigInput, "logAllObjectsVerbose", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "makeChangesToEntities", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipEntityForeignKeyColumn", "entity_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipGroupForeignKeyColumn", "group_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "membershipTableName", "testgrouper_prov_mship3");
    configureProvisionerSuffix(provisioningTestConfigInput, "metadata.0.canChange", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "metadata.0.canUpdate", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "metadata.0.name", "md_grouper_emailListName");
    configureProvisionerSuffix(provisioningTestConfigInput, "metadata.0.required", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "metadata.0.showForGroup", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfEntityAttributes", "2");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfGroupAttributes", "3");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfMembershipAttributes", "2");
    configureProvisionerSuffix(provisioningTestConfigInput, "numberOfMetadata", "1");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperEntities", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperGroups", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "operateOnGrouperMemberships", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "provisioningType", "membershipObjects");
    configureProvisionerSuffix(provisioningTestConfigInput, "selectAllEntities", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "showAdvanced", "true");
    configureProvisionerSuffix(provisioningTestConfigInput, "startWith", "this is start with read only");
    configureProvisionerSuffix(provisioningTestConfigInput, "subjectSourcesToProvision", "jdbc");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.0.translateFromGrouperProvisioningEntityField", "subjectId");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.name", "email");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetEntityAttribute.1.translateFromGrouperProvisioningEntityField", "email");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.name", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.0.translateFromGrouperProvisioningGroupField", "id");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.name", "name");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.1.translateFromGrouperProvisioningGroupField", "name");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.name", "display_name");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateExpression", "grouperProvisioningGroup.retrieveAttributeValueString('md_grouper_emailListName')");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetGroupAttribute.2.translateExpressionType", "translationScript");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.name", "group_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateExpressionType", "grouperProvisioningGroupField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.0.translateFromGrouperProvisioningGroupField", "id");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.name", "entity_uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateExpressionType", "grouperProvisioningEntityField");
    configureProvisionerSuffix(provisioningTestConfigInput, "targetMembershipAttribute.1.translateFromGrouperProvisioningEntityField", "subjectId");
    configureProvisionerSuffix(provisioningTestConfigInput, "userPrimaryKey", "uuid");
    configureProvisionerSuffix(provisioningTestConfigInput, "userTableName", "testgrouper_prov_entity");


    
  }
}
