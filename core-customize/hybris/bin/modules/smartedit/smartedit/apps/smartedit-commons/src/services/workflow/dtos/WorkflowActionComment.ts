/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Interface used by {@link WorkflowService} to query workflow action comments.
 */
export interface WorkflowActionComment {
    code: string;
    text: string;
    creationtime: string;
    authorName: string;
    decisionName?: string;
    decisionCode?: string;
    originalActionCode?: string;
    createdAgoInMillis?: number;
}
