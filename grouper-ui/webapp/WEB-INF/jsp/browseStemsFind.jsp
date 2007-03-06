<%-- @annotation@
		  Tile which is embedded in other pages - 
		  displays child stems and groups and group
		  memberships of the curent node
--%><%--
  @author Gary Brown.
  @version $Id: browseStemsFind.jsp,v 1.5 2007-03-06 11:05:49 isgwb Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<grouper:recordTile key="Not dynamic" tile="${requestScope['javax.servlet.include.servlet_path']}">
<tiles:importAttribute />
<c:if test="${empty groupSearchResultField}"><c:set scope="request" var="groupSearchResultField" value="${mediaMap['search.group.result-field']}"/></c:if>
<div class="browseChildren">
	
		<tiles:importAttribute name="browseChildGroup"/>
		<c:set var="browseMode" scope="request" value="Find"/>
		<tiles:insert attribute="browseLocation" controllerUrl="/prepareBrowsePath.do"/>

		<c:if test="${listFieldsSize gt 0}">
		<div id="browseStemsFindSelectList">
		<html:form action="/browseStemsFind" method="post">
			<html:hidden property="currentNode"/>
			<html:hidden property="groupId"/>
			<html:hidden property="callerPageId"/>

			
			<html:select property="expandListField">
				<option value=""><fmt:message bundle="${nav}" key="groups.list-members.scope.ordinary-membership"/></option>
				<html:options name="listFields"/>
			</html:select> 
			<input type="submit" value="<fmt:message bundle="${nav}" key="groups.list-members.scope.select-list"/>"/>	
			</html:form>
			</div>
	
		</c:if>



		<tiles:insert definition="dynamicTileDef">
			<tiles:put name="viewObject" beanName="pager" beanProperty="collection"/>
			<tiles:put name="view" value="findNew"/>
			<tiles:put name="headerView" value="browseStemsFindHeader"/>
			<tiles:put name="itemView" value="assignFoundMember"/>
			<tiles:put name="footerView" value="browseStemsFindFooter"/>
			<tiles:put name="pager" beanName="pager"/>
			<tiles:put name="skipText" value="${navMap['page.skip.children']} ${browseParent.displayExtension}"/>
			<tiles:put name="listInstruction" value="list.instructions.find-new"/>
		</tiles:insert>  
</div>
</grouper:recordTile>