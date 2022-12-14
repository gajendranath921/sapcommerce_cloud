/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { TextTruncateService, TruncatedText } from 'smarteditcommons';

describe('TextTruncateService - ', () => {
    let textTruncateService: TextTruncateService;
    const TEXT = 'The quick brown fox jumps over the lazy dog';
    const TEXT_TRUNCATED = 'The quick brown';

    beforeEach(() => {
        textTruncateService = new TextTruncateService();
    });

    it('truncateToNearestWord should return truncated text', () => {
        // GIVEN
        const NUM_CHARACTERS = 18;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            TEXT
        );

        // THEN
        expect(returnedText.getTruncatedText()).toEqual(TEXT_TRUNCATED);
    });

    it('truncateToNearestWord should return untruncated text if number of characters are larger than text', () => {
        // GIVEN
        const NUM_CHARACTERS = 500;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            TEXT
        );

        // THEN
        expect(returnedText.getTruncatedText()).toEqual(TEXT);
    });

    it('truncateToNearestWord should return no text if number of characters is 0', () => {
        // GIVEN
        const NUM_CHARACTERS = 0;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            TEXT
        );

        // THEN
        expect(returnedText.getTruncatedText()).toEqual('');
    });

    it('truncateToNearestWord should return no text if text is null', () => {
        // GIVEN
        const NUM_CHARACTERS = 0;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            null
        );

        // THEN
        expect(returnedText.getTruncatedText()).toEqual('');
    });

    it('truncateToNearestWord should return no text if text is undefined', () => {
        // GIVEN
        const NUM_CHARACTERS = 0;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            undefined
        );

        // THEN
        expect(returnedText.getTruncatedText()).toEqual('');
    });

    it('truncateToNearestWord should should return TruncatedText with truncated set to true', () => {
        // GIVEN
        const NUM_CHARACTERS = 5;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            TEXT
        );

        // THEN
        expect(returnedText.isTruncated()).toEqual(true);
    });

    it('truncateToNearestWord should should return TruncatedText with truncated set to false', () => {
        // GIVEN
        const NUM_CHARACTERS = 500;

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            TEXT
        );

        // THEN
        expect(returnedText.isTruncated()).toEqual(false);
    });

    it('truncateToNearestWord should return truncated by character limit for a single word without space', () => {
        // GIVEN
        const SINGLE_WORD_TEXT = 'ThisIsASingleWordWithoutAnySpaces';
        const NUM_CHARACTERS = 15;
        const SINGLE_WORD_TEXT_TRUNCATED = 'ThisIsASingleWo';

        // WHEN
        const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(
            NUM_CHARACTERS,
            SINGLE_WORD_TEXT
        );

        // THEN
        expect(returnedText.getTruncatedText()).toEqual(SINGLE_WORD_TEXT_TRUNCATED);
    });
});
