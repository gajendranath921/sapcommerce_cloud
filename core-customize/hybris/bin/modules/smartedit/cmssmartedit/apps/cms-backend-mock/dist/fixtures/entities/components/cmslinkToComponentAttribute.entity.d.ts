export interface IcmsLinkToComponentAttribute {
    cmsStructureType: string;
    collection: boolean;
    editable: boolean;
    i18nKey: string;
    mode: string;
    idAttribute: string;
    labelAttributes: string[];
    options: {
        label: string;
        id: string;
    }[];
    paged: boolean;
    qualifier: string;
    required: boolean;
}
