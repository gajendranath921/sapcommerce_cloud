import { AttributePermissionsRestService, TypePermissionsRestService } from 'cmscommons';
import { ICatalogService, ICatalogVersionPermissionService, IExperienceService, IPermissionService, ISharedDataService, CMSModesService, IPageService, WorkflowService } from 'smarteditcommons';
import { CatalogVersionRestService } from '../dao';
export declare class RulesAndPermissionsRegistrationService {
    private readonly attributePermissionsRestService;
    private readonly catalogService;
    private readonly catalogVersionPermissionService;
    private readonly catalogVersionRestService;
    private readonly cMSModesService;
    private readonly experienceService;
    private readonly pageService;
    private readonly permissionService;
    private readonly sharedDataService;
    private readonly typePermissionsRestService;
    private readonly workflowService;
    constructor(attributePermissionsRestService: AttributePermissionsRestService, catalogService: ICatalogService, catalogVersionPermissionService: ICatalogVersionPermissionService, catalogVersionRestService: CatalogVersionRestService, cMSModesService: CMSModesService, experienceService: IExperienceService, pageService: IPageService, permissionService: IPermissionService, sharedDataService: ISharedDataService, typePermissionsRestService: TypePermissionsRestService, workflowService: WorkflowService);
    register(): void;
    private onSuccess;
    private onError;
    private getCurrentPageActiveWorkflow;
    private registerRules;
    private registerRulesForTypeCodeFromContext;
    private registerRulesForCurrentPage;
    private registerRulesForTypeCode;
    private registerRulesForTypeAndQualifier;
    private registerPermissions;
}
