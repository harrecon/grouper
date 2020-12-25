<%@ include file="../assetsJsp/commonTaglib.jsp"%>


                        <tr>
                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="provisioningTargetNameId">${textContainer.text['provisioningTargetNameLabel']}</label></strong></td>
                          <td>
                            <input type="hidden" name="provisioningPreviousTargetName" value="${grouperRequestContainer.provisioningContainer.targetName}" />
                            <select name="provisioningTargetName" id="provisioningTargetNameId" style="width: 30em"
                            onchange="ajax('../app/UiV2Provisioning.editProvisioningOnGroupMembership', {formIds: 'editProvisioningFormId'}); return false;">
                              <option value=""></option>
                                <c:forEach items="${grouperRequestContainer.provisioningContainer.editableTargets}" var="target">
                                <option value="${target.name}"
                                  ${grouperRequestContainer.provisioningContainer.targetName == target.name ? 'selected="selected"' : '' }
                                  >${target.externalizedName}
                                </option>
                              </c:forEach>
                            </select>
                            <span class="requiredField" rel="tooltip" data-html="true" data-delay-show="200" data-placement="right"
                              data-original-title="${textContainer.textEscapeDouble['grouperRequiredTooltip']}">*</span>
                            <br />
                            <span class="description">${textContainer.text['provisioningTargetNameHint']}</span>
                          </td>
                        </tr>
      
                      <%-- if the target is selected --%>
                      <c:if test="${!grouper:isBlank(grouperRequestContainer.provisioningContainer.targetName)}">
                        <tr>
                          <td style="vertical-align: top; white-space: nowrap;"><strong><label for="grouperObjectTypeHasConfigurationId">${textContainer.text['provisioningDirectIndirectTypeLabel']}</label></strong></td>
                          <td>
                            <select name="hasMetadata" id="hasMetadataId" style="width: 30em"
                              onchange="ajax('../app/UiV2Provisioning.editProvisioningOnGroupMembership', {formIds: 'editProvisioningFormId'}); return false;">
                              <option value="false" ${grouperRequestContainer.provisioningContainer.grouperProvisioningAttributeValue.directAssignment ? '' : 'selected="selected"' } >${textContainer.textEscapeXml['provisioningNoDoesNotHaveDirectLabel']}</option>
                              <option value="true" ${grouperRequestContainer.provisioningContainer.grouperProvisioningAttributeValue.directAssignment ? 'selected="selected"'  : '' }>${textContainer.textEscapeXml['provisioningYesHasDirectLabel']}</option>
                            </select>
                            <br />
                            <span class="description">${textContainer.text['provisioningHasTypeHint']}</span>
                          </td>
                        </tr>
                      </c:if>
                        
                        <%-- if there is configuration then show the rest --%>
                        <c:if test="${grouperRequestContainer.provisioningContainer.grouperProvisioningAttributeValue.directAssignment == false}">
                        
                          <c:forEach items="${grouperRequestContainer.provisioningContainer.grouperProvisioningObjectMetadataItems}" var="metadataItem">
			  				
			  				<grouper:provisioningMetadataItemFormElement
			  				    name="${metadataItem.name}"
			  					formElementType="${metadataItem.formElementType}" 
			  					labelKey="${metadataItem.labelKey}"
			  					descriptionKey="${metadataItem.descriptionKey}"
			  					required="${metadataItem.required}"
			  					value="${metadataItem.defaultValue}"
			  					valuesAndLabels="${metadataItem.keysAndLabelsForDropdown}"
			  				/>
			  				
			  		    </c:forEach>
                          
                        </c:if>

