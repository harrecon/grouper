package edu.internet2.middleware.grouper.dataField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.cfg.dbConfig.OptionValueDriver;
import edu.internet2.middleware.grouper.tableIndex.TableIndex;
import edu.internet2.middleware.grouper.tableIndex.TableIndexType;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouperClient.collections.MultiKey;
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import edu.internet2.middleware.grouperClient.jdbc.GcDbVersionable;
import edu.internet2.middleware.grouperClient.jdbc.GcPersist;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableClass;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableField;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableHelper;
import edu.internet2.middleware.grouperClient.jdbc.GcSqlAssignPrimaryKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * loader config for grouper data field
 */
@GcPersistableClass(tableName="grouper_data_provider", defaultFieldPersist=GcPersist.doPersist)
public class GrouperDataProvider implements GcSqlAssignPrimaryKey, GcDbVersionable, OptionValueDriver {

  /**
   * some required config to see what the providers are
   */
  private static Pattern providerConfigIds = Pattern.compile("^grouperDataProvider\\.([^.]+)\\.name$");
  
  @Override
  public List<MultiKey> retrieveKeysAndLabels() {
        
    Set<String> configIds = GrouperConfig.retrieveConfig().propertyConfigIds(providerConfigIds);
    List<MultiKey> results = new ArrayList<>();
    for (String theConfigId : GrouperUtil.nonNull(configIds)) {
      results.add(new MultiKey(theConfigId, theConfigId));
    }
    return results;
  }


  @GcPersistableField(primaryKey=true, primaryKeyManuallyAssigned=true)
  private long internalId = -1;
  
  private String configId;
  
  private Timestamp createdOn = null;

  /**
   * version from db
   */
  @GcPersistableField(persist = GcPersist.dontPersist)
  private GrouperDataProvider dbVersion;

  
  public long getInternalId() {
    return internalId;
  }

  
  public void setInternalId(long internalId) {
    this.internalId = internalId;
  }

  
  public String getConfigId() {
    return configId;
  }

  
  public void setConfigId(String configId) {
    this.configId = configId;
  }

  
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }
  
  
  /**
   * 
   */
  @Override
  public boolean gcSqlAssignNewPrimaryKeyForInsert() {
    if (this.internalId != -1) {
      return false;
    }
    this.internalId = TableIndex.reserveId(TableIndexType.dataLoaderConfig);
    return true;
  }


  //########## END GENERATED BY GcDbVersionableGenerate.java ###########
  
  /**
   * deep clone the fields in this object
   */
  @Override
  public GrouperDataProvider clone() {
  
    GrouperDataProvider grouperDataLoaderConfig = new GrouperDataProvider();
  
    //dbVersion  DONT CLONE
  
    grouperDataLoaderConfig.configId = this.configId;
    grouperDataLoaderConfig.createdOn = this.createdOn;
    grouperDataLoaderConfig.internalId = this.internalId;
  
    return grouperDataLoaderConfig;
  }


  /**
   * db version
   */
  @Override
  public void dbVersionDelete() {
    this.dbVersion = null;
  }


  /**
   * if we need to update this object
   * @return if needs to update this object
   */
  @Override
  public boolean dbVersionDifferent() {
    return !this.equalsDeep(this.dbVersion);
  }


  /**
   * take a snapshot of the data since this is what is in the db
   */
  @Override
  public void dbVersionReset() {
    //lets get the state from the db so we know what has changed
    this.dbVersion = this.clone();
  }


  /**
   *
   */
  public boolean equalsDeep(Object obj) {
    if (this==obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof GrouperDataProvider)) {
      return false;
    }
    GrouperDataProvider other = (GrouperDataProvider) obj;
  
    return new EqualsBuilder()
  
  
      //dbVersion  DONT EQUALS
      .append(this.createdOn, other.createdOn)
      .append(this.internalId, other.internalId)
      .append(this.configId, other.configId)
        .isEquals();
  
  }


  public void storePrepare() {
    if (this.createdOn == null) {
      this.createdOn = new Timestamp(System.currentTimeMillis());
    }
  }


  /**
   * 
   */
  @Override
  public String toString() {
    return GrouperClientUtils.toStringReflection(this, null);
  }


}
