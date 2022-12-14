/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import {
    moduleUtils,
    HttpBackendService,
    I18N_RESOURCE_URI,
    SeEntryModule
} from 'smarteditcommons';
/* tslint:disable:class-name */
const DATE_AND_TIME = 'Date and Time';
const EXTERNAL_LINK = 'External Link';
const i18nMock = {
    en: {
        dummyKey2: 'dummyText in english',
        'se.authentication.form.input.username': 'Name',
        'se.authentication.form.input.password': 'Password',
        'se.authentication.form.button.submit': 'Submit',
        'se.authentication.form.button.submit.sso': 'Sign with SSO',
        'se.perspective.all.name': 'All',
        'se.perspective.none.name': 'Preview',
        'localization.field': 'I am a localized link',
        'left.toolbar.cmsuser.name': 'CM User',
        'se.left.toolbar.sign.out': 'Sign Out',
        'experience.selector.language': 'Language',
        'experience.selector.date.and.time': DATE_AND_TIME,
        'experience.selector.catalog': 'Catalog',
        'experience.selector.activesite': 'ActiveSite'
    },
    fr: {
        dummyKey2: 'dummyText in french',
        'se.authentication.form.input.username': 'Nom',
        'se.authentication.form.input.password': 'Mot de passe',
        'se.authentication.form.button.submit': 'Soumettre',
        'se.authentication.form.button.submit.sso': 'se connecter avec SSO',
        'se.perspective.all.name': 'Tout',
        'se.perspective.none.name': 'Aperçu',
        'localization.field': 'Je suis localisée',
        'left.toolbar.cmsuser.name': 'Utilisateur',
        'se.left.toolbar.sign.out': 'Deconnexion',
        'experience.selector.language': 'Langue',
        'experience.selector.date.and.time': 'Date et Heure',
        'experience.selector.catalog': 'Catalogue'
    },
    kl: {
        dummyKey2: 'kl_dummyKey',
        'se.authentication.form.input.username': 'klName',
        'se.authentication.form.input.password': 'klPassword',
        'se.authentication.form.button.submit': 'Submit',
        'se.authentication.form.button.submit.sso': 'klSign with SSO',
        'se.perspective.all.name': 'kl_All',
        'se.perspective.none.name': 'kl_Preview',
        'localization.field': 'I am a localized link',
        'left.toolbar.cmsuser.name': 'CM User',
        'se.left.toolbar.sign.out': 'Sign Out',
        'experience.selector.language': 'Language',
        'experience.selector.date.and.time': DATE_AND_TIME,
        'experience.selector.catalog': 'Catalog',
        'experience.selector.activesite': 'ActiveSite'
    },
    en_US: {
        dummyKey2: 'en_US_dummyKey',
        'se.authentication.form.input.username': 'en_USName',
        'se.authentication.form.input.password': 'en_USPass',
        'se.authentication.form.button.submit': 'submit',
        'se.authentication.form.button.submit.sso': 'en_USSign with SSO',
        'se.perspective.all.name': 'All',
        'se.perspective.none.name': 'Preview',
        'localization.field': 'I am a localizesdasd link',
        'left.toolbar.cmsuser.name': 'CM User',
        'se.left.toolbar.sign.out': 'Sign Out',
        'experience.selector.language': 'Language',
        'experience.selector.date.and.time': DATE_AND_TIME,
        'experience.selector.catalog': 'Catalog',
        'experience.selector.activesite': 'ActiveSite',
        NgTestPage: 'ng test page link',
        setestpage: 'This is a test page',
        'se.modal.administration.configuration.edit.title': 'edit configuration',
        'se.configurationform.actions.cancel': 'cancel',
        'se.configurationform.actions.submit': 'submit',
        'se.configurationform.actions.close': 'close',
        'se.configurationform.json.parse.error': 'this value should be a valid JSON format',
        'se.configurationform.duplicate.entry.error': 'This is a duplicate key',
        'se.configurationform.save.error': 'Save error',
        'se.actions.loadpreview': 'load preview',
        'se.unknown.request.error': 'Your request could not be processed! Please try again later!',
        'se.logindialogform.username.or.password.invalid': 'Invalid username or password',
        'se.logindialogform.username.and.password.required': 'Username and password required',
        'type.componenttype1.content.name': 'Content',
        'type.componenttype1.name.name': 'Name',
        'type.componenttype1.mediaContainer.name': 'Media Container',
        'se.componentform.actions.exit': 'Exit',
        'se.componentform.actions.cancel': 'Cancel',
        'se.componentform.actions.submit': 'Submit',
        'abanalytics.popover.title': 'ab analytics',
        'type.componenttype1.content.tooltip': 'enter content',
        'se.cms.component.confirmation.modal.cancel': 'Cancel',
        'se.cms.component.confirmation.modal.save': 'Save',
        'toolbar.action.render.component': 'Render Component',
        'toolbar.action.render.slot': 'Render Slot',
        'se.moretext.more.link': 'MoreLink',
        'se.moretext.less.link': 'LessLink',
        'se.moretext.custom.more.link': 'CustomLinkMore',
        'se.moretext.custom.less.link': 'CustomLinkLess',
        'se.deviceorientation.vertical.label': 'Vertical',
        'se.deviceorientation.horizontal.label': 'Horizontal',
        'se.confirmation.modal.cancel': 'cancel',
        'se.confirmation.modal.ok': 'ok',
        'se.heartbeat.failure1': 'Heart beat failed',
        'se.heartbeat.failure2': 'Preview mode',
        'se.heartbeat.reconnection': 'Heart beat reconnected',
        'se.toolbar.sites': 'Sites',
        'se.route.storefront.title': 'Storefront',
        'sync.confirm.msg': 'this {{catalogName}}is a test',
        'action.complete': 'Complete',
        'se.action.done': 'DONE',
        'se.action.next': 'NEXT',
        'se.action.cancel': 'CANCEL',
        'se.action.back': 'BACK',
        'action.save': 'SAVE',
        'action.gotonext': 'Go to next step',
        'wizard.title.name': 'What is your name?',
        'wizard.title.sex': 'What gender do you identify yourself as?',
        'wizard.title.age': 'How old are you?',
        'wizard.title.extra': 'Some extra step',
        'wizard.title.save': 'Person Details',
        'someTitle.key': 'my translated title',
        'tab1.name': 'Tab 1',
        'tab2.name': 'Tab 2',
        'tab3.name': 'Tab 3',
        'tab4.name': 'Tab 4',
        'editorTabset.basicTab.title': 'BASIC INFO',
        'editorTabset.adminTab.title': 'ADMIN',
        'editorTabset.visibilityTab.title': 'VISIBILITY',
        'editorTabset.genericTab.title': 'GENERIC',
        'type.Item.name.name': 'Name',
        'type.AbstractItem.creationtime.name': 'Created',
        'type.AbstractItem.modifiedtime.name': 'Modified',
        'type.AbstractCMSComponent.visible.name': 'Component Visibility',
        'type.Item.uid.name': 'ID',
        'type.AbstractItem.pk.name': 'Key',
        'se.genericeditor.dropdown.placeholder': 'Select an image',
        'se.componentform.actions.replaceImage': 'Replace Image',
        'type.thesmarteditcomponenttype.id.name': 'id',
        'type.thesmarteditcomponenttype.headline.name': 'Headline',
        'type.thesmarteditcomponenttype.active.name': 'Activation',
        'type.thesmarteditcomponenttype.activationDate.name': 'Activation date',
        'type.thesmarteditcomponenttype.enabled.name': 'Enabled',
        'type.thesmarteditcomponenttype.content.name': 'Content',
        'type.thesmarteditcomponenttype.created.name': 'Creation date',
        'type.thesmarteditcomponenttype.media.name': 'Media',
        'type.thesmarteditcomponenttype.external.name': EXTERNAL_LINK,
        'type.thesmarteditcomponenttype.urlLink.name': 'Url Link',
        'se.editor.linkto.label': 'Link to',
        'se.editor.linkto.external.label': EXTERNAL_LINK,
        'se.editor.linkto.internal.label': 'Existing Page',
        'hello.message': 'Hello',
        'modal.actions.cancel': 'Cancel',
        'modal.actions.close': 'Close',
        'modal.title.lamp': 'I love lamp',
        'toolbar.action.action3': 'action3',
        'toolbar.action.action4': 'action4',
        'toolbar.action.action5': 'action5',
        'toolbar.action.action6': 'action6',
        'se.genericeditor.sedropdown.placeholder': 'Select an Option',
        'type.thesmarteditcomponenttype.dropdowna.name': 'Dropdown A',
        'type.thesmarteditcomponenttype.dropdownb.name': 'Dropdown B (depends on A)',
        'type.thesmarteditcomponenttype.dropdownc.name': 'Dropdown C (depends on A)',
        'type.thesmarteditcomponenttype.dropdownd.name': 'Dropdown D',
        'type.thesmarteditcomponenttype.dropdowne.name': 'Dropdown E (depends on B)',
        'type.thesmarteditcomponenttype.dropdownf.name': 'Dropdown F (strategy)',
        'type.thesmarteditComponentType.id.name': 'id',
        'type.thesmarteditComponentType.headline.name': 'Headline',
        'type.thesmarteditComponentType.active.name': 'Activation',
        'type.thesmarteditComponentType.content.name': 'Content',
        'type.thesmarteditComponentType.create.name': 'Creation date',
        'type.thesmarteditComponentType.media.name': 'Media',
        'type.thesmarteditComponentType.external.name': EXTERNAL_LINK,
        'type.thesmarteditComponentType.urlLink.name': 'Url Link',
        'experience.selector.catalogversions': 'PRODUCT CATALOGS',
        'se.componentform.actions.apply': 'APPLY',
        'se.componentform.select.date': 'Select a Date and Time',
        'se.cms.component.confirmation.modal.done': 'Done',
        'se.modal.product.catalog.configuration': 'Product Catalog Configuration',
        'se.some.label': 'Some Item',
        'type.thesmarteditComponentType.description.name': 'Description',
        'type.thesmarteditcomponenttype.orientation.name': 'Orientation',
        'type.thesmarteditcomponenttype.simpledropdown.name': 'Simple Dropdown',
        'type.thesmarteditComponentType.quantity.name': 'Quantity',
        'type.thesmarteditComponentType.price.name': 'Price',
        'se.landingpage.title': 'Your Touchpoints',
        'cataloginfo.pagelist': 'PAGE LIST',
        'cataloginfo.lastsynced': 'LAST SYNCED',
        'cataloginfo.button.sync': 'SYNC',
        'test.title': 'TEST TITLE',
        'test.description': 'TEST DESCRIPTION',
        'se.icon.tooltip.visibility': '{{numberOfRestrictions}} restrictions on this page',
        'se.product.catalogs.multiple.list.header': 'Select a desired version to preview the site:',
        'se.product.catalogs.selector.headline.tooltip': 'Product Catalogs'
    }
};
@SeEntryModule('i18nMocks')
@NgModule({
    providers: [
        moduleUtils.provideValues({
            SMARTEDIT_ROOT: 'web/webroot',
            SMARTEDIT_RESOURCE_URI_REGEXP: /^(.*)\/apps\/smartedit-e2e\/generated\/e2e/
        }),
        moduleUtils.initialize(
            (httpBackendService: HttpBackendService) => {
                httpBackendService
                    .whenGET(new RegExp(I18N_RESOURCE_URI + '/[\\w]+'))
                    .respond(function (method: string, url: string) {
                        const locale = url.substr(url.lastIndexOf('/') + 1);
                        return [200, (i18nMock as any)[locale]];
                    });
            },
            [HttpBackendService]
        )
    ]
})
export class i18nMocks {}

window.pushModules(i18nMocks);
