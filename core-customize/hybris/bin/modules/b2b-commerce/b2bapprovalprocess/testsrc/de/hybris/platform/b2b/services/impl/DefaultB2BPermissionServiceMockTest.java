/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.B2BPermissionDao;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowActionDao;
import de.hybris.platform.b2b.dao.impl.DefaultB2BWorkflowDao;
import de.hybris.platform.b2b.mail.OrderInfoContextDtoFactory;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.ScheduleOrderService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Mock test for the B2BPermissionService
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BPermissionServiceMockTest extends HybrisMokitoTest
{

	private final DefaultB2BPermissionService defaultB2BPermissionService = new DefaultB2BPermissionService();
	@Mock
	private UserService mockUserService;
	@Mock
	private BaseDao mockBaseDao;
	@Mock
	private ModelService mockModelService;
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> mockB2bUnitService;
	@Mock
	private SessionService mockSessionService;
	@Mock
	private CommonI18NService mockCommonI18NService;
	@Mock
	private DefaultB2BWorkflowDao mockB2BWorkflowDao;
	@Mock
	private DefaultB2BWorkflowActionDao mockB2BWorkflowActionDao;
	@Mock
	private TenantScope mockTenantScope;
	@Mock
	private WorkflowActionService mockwWorkflowActionService;
	@Mock
	private WorkflowAttachmentService mockwWorkflowAttachmentService;
	@Mock
	private WorkflowProcessingService mockWorkflowProcessingService;
	@Mock
	private WorkflowService mockWorkflowService;
	@Mock
	private WorkflowTemplateService mockWorkflowTemplateService;
	@Mock
	private PlatformTransactionManager mockTxManager;
	@Mock
	private OrderInfoContextDtoFactory orderInfoContextDtoFactory;
	@Mock
	private RendererService rendererService;
	@Mock
	private B2BCurrencyConversionService mockB2BCurrencyConversionService;
	@Mock
	private B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BudgetService;
	@Mock
	private B2BCartService mockB2BCartService;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> mockB2BCustomerService;
	@Mock
	private ScheduleOrderService mockScheduleOrderService;
	@Mock
	private B2BApproverService mockB2BApproverService;
	@Mock
	private B2BPermissionResultHelperImpl mockB2BPermissionResultHelperImpl;
	@Mock
	private DefaultTypeService mockTypeService;
	@Mock
	private PrincipalGroupMembersDao mockPrincipalGroupMemberDao;
	@Mock
	private B2BPermissionDao mockB2BPermissionDao;
	@Mock
	private OrderModel order;
	@Mock
	private UserModel userModel;
	@Mock
	private B2BCommentModel mockB2BCommentModel;
	@Mock
	private List<Class<? extends B2BPermissionModel>> permissionsThatNeedApproval;
	@Mock
	private B2BCommentService<AbstractOrderModel> b2BCommentService;

	@Before
	public void setUp() throws Exception
	{
		defaultB2BPermissionService.setB2bApproverService(mockB2BApproverService);
		defaultB2BPermissionService.setPermissionResultHelper(mockB2BPermissionResultHelperImpl);
		defaultB2BPermissionService.setBaseDao(mockBaseDao);
		defaultB2BPermissionService.setB2bPermissionDao(mockB2BPermissionDao);
		defaultB2BPermissionService.setSessionService(mockSessionService);
		defaultB2BPermissionService.setModelService(mockModelService);
		defaultB2BPermissionService.setB2bCommentService(b2BCommentService);
	}

	@Test
	public void testEvaluatePermissions() throws Exception
	{
		//TODO: implement test

	}

	@Test
	@Ignore
	public void testGetApproversForOpenPermissions() throws Exception
	{
		//final AbstractOrderModel mockAbstractOrderModel = Mockito.mock(AbstractOrderModel.class);
		final B2BCustomerModel mockB2BCustomerModel = Mockito.mock(B2BCustomerModel.class);
		//final B2BPermissionModel mockB2BPermissionModel = Mockito.mock(B2BPermissionModel.class);
		//final B2BPermissionResultModel mockB2BPermissionResultModel = Mockito.mock(B2BPermissionResultModel.class);

		Mockito.when(mockB2BApproverService.getAllApprovers(mockB2BCustomerModel)).thenReturn(
				Collections.singletonList(mockB2BCustomerModel));

		//		Mockito.when(mockB2BPermissionResultHelperImpl.extractPermissionTypes(Collections.singleton(mockB2BPermissionResultModel)))
		//				.thenReturn(Collections.singleton(mockB2BPermissionModel));


		// assert here
		//		defaultB2BPermissionService.getApproversForOpenPermissions(mockAbstractOrderModel, mockB2BCustomerModel,
		//				Collections.singleton(mockB2BPermissionResultModel));

	}

	@Test
	@Ignore
	public void testNeedsApproval() throws Exception
	{
		final OrderModel mockOrder = mock(OrderModel.class);
		defaultB2BPermissionService.needsApproval(mockOrder);
	}

	@Test
	public void testGetEligableApprovers() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testGetOpenPermissions() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testGetB2BPermissionByCode() throws Exception
	{
		final String code1 = "code1";
		final B2BPermissionModel mockB2BPermissionModel = Mockito.mock(B2BPermissionModel.class);
		Mockito.when(mockB2BPermissionDao.findPermissionByCode(code1)).thenReturn(mockB2BPermissionModel);
		assertThat(defaultB2BPermissionService.getB2BPermissionForCode(code1), equalTo(mockB2BPermissionModel));
	}

	@Test
	public void testGetAllB2BPermissions() throws Exception
	{
		final B2BPermissionModel mockB2BPermissionModel = Mockito.mock(B2BPermissionModel.class);
		final List<B2BPermissionModel> permissions = new ArrayList<B2BPermissionModel>(1);
		permissions.add(mockB2BPermissionModel);
		when(mockBaseDao.findAll(-1, 0, B2BPermissionModel.class)).thenReturn(permissions);
		assertThat(defaultB2BPermissionService.getAllB2BPermissions(), equalTo(Collections.singleton(mockB2BPermissionModel)));
	}

	@Test
	public void testPermissionExists() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testFindAllB2BPermissionTypes() throws Exception
	{
		//TODO: implement test

	}

	@Test
	public void testCheckPermisssionsOfApproversEmptyApprovers()
	{
		final List<B2BCustomerModel> allApprovers = new ArrayList<>();
		when(order.getUser()).thenReturn(userModel);
		when(mockModelService.create(B2BCommentModel.class)).thenReturn(mockB2BCommentModel);

		Assert.assertEquals(Collections.emptySet(), defaultB2BPermissionService.checkPermissionsOfApprovers(order, permissionsThatNeedApproval, allApprovers, false));
	}

	@Test
	public void testCheckPermissionsOfApproversFastReturnFalse()
	{
		final B2BCustomerModel approver1 = new B2BCustomerModel();
		final List<B2BCustomerModel> allApprovers = new ArrayList<>();
		allApprovers.add(approver1);

		when(order.getUser()).thenReturn(userModel);
		when(mockModelService.create(B2BCommentModel.class)).thenReturn(mockB2BCommentModel);

		Assert.assertEquals(Collections.emptySet(), defaultB2BPermissionService.checkPermissionsOfApprovers(order, permissionsThatNeedApproval, allApprovers, false));
	}
}
