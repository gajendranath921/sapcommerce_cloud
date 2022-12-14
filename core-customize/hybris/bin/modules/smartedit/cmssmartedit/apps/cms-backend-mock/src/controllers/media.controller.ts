/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Controller, Get, HttpCode, Param, Post, Query } from '@nestjs/common';
import { IMedia } from '../../fixtures/entities/media';
import { MediaService } from '../services';

@Controller()
export class MediaController {
    constructor(private readonly mediaService: MediaService) {}
    @Post('cmswebservices/v1/catalogs/:catalogId/versions/:versionId/media*')
    @HttpCode(200)
    createMediaItem() {
        const mediaCount = this.mediaService.getMediaCount();
        const MORE_BCKG_PNG = 'more_bckg.png';
        const media: IMedia = {
            id: mediaCount + '',
            uuid: MORE_BCKG_PNG,
            code: MORE_BCKG_PNG,
            description: MORE_BCKG_PNG,
            altText: MORE_BCKG_PNG,
            realFileName: MORE_BCKG_PNG,
            mime: 'image/png',
            url: '/apps/cms-smartedit-e2e/generated/images/more_bckg.png'
        };
        this.mediaService.addMedia(media);
        return media;
    }

    @Get('cmswebservices/v1/media/:uuid')
    getMediaItemByUUID(@Param('uuid') uuid: string) {
        const resultMedia: IMedia | undefined = this.mediaService.getMediaByCode(uuid);
        if (resultMedia) {
            return resultMedia;
        }
        return this.mediaService.getFirstMedia();
    }

    @Get('cmswebservices/v1/media*')
    getMediaItemByNamedQuery(@Query() query: any) {
        let resultMedia: IMedia[] = [];
        if (query.currentPage === '0') {
            const search: Map<string, string> = new Map();
            query.params.split(',').forEach((param: string) => {
                const paramSplit: string[] = param.split(':');
                search.set(paramSplit[0], paramSplit.length === 2 ? paramSplit[1] : '');
            });

            resultMedia = this.mediaService.filterMediaByInput(search.get('code'));
        }
        return { media: resultMedia };
    }
}
