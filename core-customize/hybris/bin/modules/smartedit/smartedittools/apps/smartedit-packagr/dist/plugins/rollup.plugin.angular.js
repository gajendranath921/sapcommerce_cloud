"use strict";
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * This service is based on the NPM package called rollup-plugin-angular
 * The reason we rewrite it locally is because it has stopped maintainess and dependency libs have security issues
 * You can find original package from below git:
 * https://github.com/cebor/rollup-plugin-angular
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.angularPlugin = void 0;
const colors = require("colors");
const magic_string_1 = require("magic-string");
const rollup_pluginutils_1 = require("rollup-pluginutils");
const path = require('path');
const fs = require('fs');
const replace = require('replace');
const moduleIdRegex = /moduleId\s*:(.*)/g;
const componentRegex = /@Component\(\s?{([\s\S]*)}\s?\)$|type:\s?Component,\s?args:\s?\[\s?{([\s\S]*)},\s?\]/gm;
const commentRegex = /\/\*[\s\S]*?\*\/|([^\\:]|^)\/\/.*$/gm; // http://www.regextester.com/?fam=96247
const templateUrlRegex = /templateUrl\s*:(.*)/g;
const styleUrlsRegex = /styleUrls\s*:(\s*\[[\s\S]*?\])/g; // http://www.regextester.com/?fam=98594
const stringRegex = /(['"`])((?:[^\\]\\\1|.)*?)\1/g;
const insertText = (str, dir, preprocessor = (res, text) => res, processFilename = false, sourceType = 'ts') => {
    let quoteChar = sourceType === 'ts' ? '`' : '"';
    return str.replace(stringRegex, function (match, quote, url) {
        const includePath = path.join(dir, url);
        if (processFilename) {
            return quoteChar + preprocessor(includePath) + quoteChar;
        }
        const text = fs.readFileSync(includePath).toString();
        return quoteChar + preprocessor(text, includePath) + quoteChar;
    });
};
const angularPlugin = (options) => {
    options.preprocessors = options.preprocessors || {}; // set default preprocessors to `{}`
    options.replace = typeof options.replace === 'boolean' ? options.replace : true; // set default replace to `true`
    // ignore @angular/** modules
    options.exclude = options.exclude || [];
    if (typeof options.exclude === 'string' || options.exclude instanceof String) {
        options.exclude = [options.exclude];
    }
    if (options.exclude.indexOf('node_modules/@angular/**') === -1) {
        options.exclude.push('node_modules/@angular/**');
    }
    const filter = rollup_pluginutils_1.createFilter(options.include, options.exclude);
    return {
        name: 'angular',
        transform(source, map) {
            if (!filter(map))
                return;
            // replace comments in source
            source = source.replace(commentRegex, '');
            // use MagicString
            const magicString = new magic_string_1.default(source);
            // get dir from `map`
            const dir = path.parse(map).dir;
            // get file extension from `map`
            const fileExt = map.split('.').pop();
            let hasReplacements = false;
            let match;
            let start, end, replacement;
            while ((match = componentRegex.exec(source)) !== null) {
                start = match.index;
                end = start + match[0].length;
                replacement = match[0]
                    .replace(templateUrlRegex, function (match, url) {
                    hasReplacements = true;
                    const toReplace = 'template:' +
                        insertText(url, dir, options.preprocessors.template, options.processFilename, options.sourcetype);
                    if (fileExt === 'js' && options.replace === true) {
                        /* replace templateUrl in files generated by ngc */
                        replace({
                            regex: match,
                            replacement: toReplace,
                            paths: [map],
                            recursive: true,
                            silent: true
                        });
                        console.info(`templateUrl in file ${map} has been changed from ${colors.green(match)}  to ${colors.green(toReplace)}`);
                    }
                    return toReplace;
                })
                    .replace(styleUrlsRegex, function (match, urls) {
                    hasReplacements = true;
                    const toReplace = 'styles:' +
                        insertText(urls, dir, options.preprocessors.style, options.processFilename, options.sourcetype);
                    /* replace styles in files generated by ngc */
                    if (fileExt === 'js' && options.replace === true) {
                        replace({
                            regex: styleUrlsRegex,
                            replacement: toReplace,
                            paths: [map],
                            recursive: true,
                            silent: true
                        });
                        console.info(`styleUrls in file ${map} has been changed from ${colors.green(match)} to ${colors.green(toReplace)}`);
                    }
                    return toReplace;
                })
                    .replace(moduleIdRegex, function (match, moduleId) {
                    hasReplacements = true;
                    return '';
                });
                if (hasReplacements) {
                    magicString.overwrite(start, end, replacement);
                }
            }
            if (!hasReplacements) {
                return null;
            }
            let sourceMap;
            let result = { code: magicString.toString(), map: sourceMap };
            if (options.sourceMap !== false) {
                result.map = magicString.generateMap({ hires: true });
            }
            return result;
        }
    };
};
exports.angularPlugin = angularPlugin;
