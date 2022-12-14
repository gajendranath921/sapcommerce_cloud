/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function (grunt) {
    // Note: The following methods register tasks so that they can be executed in a multi-folder environment. Their structure
    // is quite similar between each other, so potentially could be refactored to allow for code reuse.
    grunt.registerTask('multipleCopySources', function () {
        var conf = grunt.config.get('copy') || {};

        // read all subdirectories from your modules folder
        grunt.file
            .expand(
                {
                    filter: 'isDirectory'
                },
                'web/feature*/*'
            )
            .forEach(function (dir) {
                if (conf[dir]) {
                    grunt.fail.warn(
                        `multipleCopySources grunt task - Should not overrite existing copy target: ${dir}`
                    );
                }
                const key = dir.replace(/\//g, '-');
                conf[key] = {
                    expand: true,
                    flatten: false,
                    src: [dir + '/**/*.+(js|ts|scss|html)'],
                    dest: 'jsTarget/'
                };
                grunt.task.run('copy:' + key);
            });

        grunt.config.set('copy', conf);
    });
};
