/**
 * Grunt Build Script
 * - based on grunt v0.4
 * - see README-frontend.md
 */
/*jshint node: true */
/*jshint globalstrict: true*/
"use strict";

module.exports = function (grunt) {


	////////////////////////////////////////////////////////////////////////////////


	//
	// NPM Tasks


	// Hint JS
	grunt.loadNpmTasks('grunt-contrib-jshint');

	// Concat JS Files
	grunt.loadNpmTasks('grunt-contrib-concat');

	// Import Less files instead of concat for error messages with file and line nr
	grunt.loadNpmTasks('grunt-less-imports');

	// Compile Less to CSS
	grunt.loadNpmTasks('grunt-contrib-less');

	// Minify CSS
	grunt.loadNpmTasks('grunt-contrib-cssmin');

	// Minify JS
	grunt.loadNpmTasks('grunt-contrib-uglify');

	// Watch Task
	grunt.loadNpmTasks('grunt-contrib-watch');

	// Generate Sprite Mixins
	grunt.loadNpmTasks('grunt-glue-nu');

	// Copy files from one place to another
	grunt.loadNpmTasks('grunt-contrib-copy');

	// Delete some temporary Files
	grunt.loadNpmTasks('grunt-contrib-clean');

	// Rename some Files i.e. less-generated
	grunt.loadNpmTasks('grunt-contrib-rename');

	// Add Banners with Date & Time on top of the generated Files
	grunt.loadNpmTasks('grunt-banner');

	// Concurrently run tasks
	grunt.loadNpmTasks('grunt-concurrent');

	// Compress task
	grunt.loadNpmTasks('grunt-contrib-compress');

	// Compress images, used for sprite image
	grunt.loadNpmTasks('grunt-pngmin');


	grunt.initConfig({

		pkg: grunt.file.readJSON('package.json'),

		banner: '\n/*\n Generated with Grunt on <%= grunt.template.today("dd.mm.yyyy") %> at <%= grunt.template.today("H:MM:ss") %>\n*/\n',


		////////////////////////////////////////////////////////////////////////////////

		// Directories

		dirs: {

			////////////////////////////////////////////////////////////////////////////

			// Scripts

			scriptsDyn: [
				"web/webroot/WEB-INF/terrific/js/dyn/*.js",
				"web/webroot/WEB-INF/terrific/js/dyn/*.js.gz"

			],

			scriptsHead: [
				"web/webroot/WEB-INF/terrific/js/static-head/*.js",
				"web/webroot/WEB-INF/terrific/js/static-head/**/*.js"
			],

			scriptsLibs: [
				"web/webroot/WEB-INF/terrific/js/static/libraries/*.js",
				"web/webroot/WEB-INF/terrific/js/static/plugins/*.js",
				"web/webroot/WEB-INF/terrific/js/static/plugins-src/*.js",
				"web/webroot/WEB-INF/terrific/js/static/utils/*.js"
			],

			scriptsProject: [
				"web/webroot/WEB-INF/terrific/js/static/objects/*.js",
				"web/webroot/WEB-INF/terrific/modules/aaa/*/js/*.js", 	//	aaa
				"web/webroot/WEB-INF/terrific/modules/*/js/*.js",		//	modules
				"web/webroot/WEB-INF/terrific/modules/zzz/*/js/*.js" 	//	zzz
			],

			scripts: [
				['<%=dirs.scriptsDyn%>', '<%=dirs.scriptsHead%>', '<%=dirs.scriptsLibs%>', '<%=dirs.scriptsProject%>']
			],


			////////////////////////////////////////////////////////////////////////////

			// Styles

			styles: [
				"web/webroot/WEB-INF/terrific/css/static/libraries/*.css",
				"web/webroot/WEB-INF/terrific/css/static/libraries/*.less",
				"web/webroot/WEB-INF/terrific/css/static/plugins/*.css",
				"web/webroot/WEB-INF/terrific/css/static/plugins/*.less",

				"web/webroot/WEB-INF/terrific/css/static/utils/*.less",
				"web/webroot/WEB-INF/terrific/css/static/elements/*.less",
				"web/webroot/WEB-INF/terrific/modules/**/**/*.less"
			],


			////////////////////////////////////////////////////////////////////////////

			// Markup

			markup: [
				"web/webroot/WEB-INF/terrific/modules/**/*.jsp",
				"web/webroot/WEB-INF/tags/terrific/views/*.tag",
				"web/webroot/WEB-INF/views/desktop/pages/terrific/**/*.jsp"
			],


			////////////////////////////////////////////////////////////////////////////

			// Sprites

			sprites_src: [
				"web/webroot/WEB-INF/terrific/css/static/sprites/*.png",
				"web/webroot/WEB-INF/terrific/modules/**/sprites/*.png"
			],

			sprites_tmp: [
				"web/webroot/_ui/all/media/sprites"
			],

			sprites_out_img: "web/webroot/_ui/all/media",

			sprites_out_url: "/_ui/all/media/",

			sprites_out_less: "web/webroot/WEB-INF/terrific/css/static/elements",


			////////////////////////////////////////////////////////////////////////////

			// Cache Dir

			cache: "web/webroot/_ui/all/cache"
		},

		////////////////////////////////////////////////////////////////////////////////

		// Hinting, Concatination, Compiling

		jshint: {

			gruntfile: {
				options: {
					'-W099': true,
					'-W030': true,
					'-W054': true,
					'-W065': true,
					laxcomma: true
				},
				src: ['Gruntfile.js']
			},

			scriptsProject: {
				options: {
					'-W099': true,
					'-W030': true,
					'-W054': true,
					'-W065': true,
					laxcomma: true
				},
				src: ['<%=dirs.scriptsProject%>']
			}
		},

		concat: {
			scriptsHead: {
				src: ['<%=dirs.scriptsHead%>'],
				dest: "<%=dirs.cache%>/base-head.js"
			},
			scripts: {
				src: ['<%=dirs.scriptsLibs%>', '<%=dirs.scriptsProject%>'],
				dest: "<%=dirs.cache%>/base.js"
			}
		},

		less_imports: {
			external_styles: {
				options: {},
				src: '<%=dirs.styles%>',
				dest: '<%=dirs.cache%>/temp-styles-imports.less'
			}
		},

		less: {
			development: {
				files: {
					"<%=dirs.cache%>/base.css": '<%=dirs.cache%>/temp-styles-imports.less'
				}
			}
		},


		////////////////////////////////////////////////////////////////////////////////

		// File Splitting for IE

		usebanner: {
			scriptsDev: {
				options: {
					position: 'top',
					banner: '<%= banner %>'
				},
				files: {
					src: [ '<%=dirs.cache%>/base-head.js', '<%=dirs.cache%>/base.js' ]
				}
			},
			stylesDev: {
				options: {
					position: 'top',
					banner: '<%= banner %>'
				},
				files: {
					src: [ '<%=dirs.cache%>/base.css' ]
				}
			},
			scriptsProd: {
				options: {
					position: 'top',
					banner: '<%= banner %>'
				},
				files: {
					src: [ '<%=dirs.cache%>/base-head.min.js', '<%=dirs.cache%>/base.min.js' ]
				}
			},
			stylesProd: {
				options: {
					position: 'top',
					banner: '<%= banner %>'
				},
				files: {
					src: [ '<%=dirs.cache%>/base.min.css']
				}
			}
		},

		////////////////////////////////////////////////////////////////////////////////

		// Minification

		uglify: {
			scriptsHead: {
				src: ['<%=concat.scriptsHead.dest%>'],
				dest: "<%=dirs.cache%>/base-head.min.js"
			},
			scripts: {
				options: {
					sourceMap: '<%=dirs.cache%>/base.min.map.js',
					sourceMapRoot: '/',
					sourceMapPrefix: 2,
					sourceMappingURL: 'base.min.map.js'
				},
				files: {
					'<%=dirs.cache%>/base.min.js': ['<%=concat.scripts.dest%>']
				}
			}
		},

		cssmin: {
			compress: {
				files: {
					"<%=dirs.cache%>/base.min.css": ['<%=dirs.cache%>/base.css']
				}
			}
		},


		////////////////////////////////////////////////////////////////////////////////

		// Maintenance Tasks: Copy, Clean, Rename

		copy: {
			scriptsDyn: {
				files: [
					{ expand: true, flatten: true, src: ['<%=dirs.scriptsDyn%>'], dest: '<%=dirs.cache%>', filter: 'isFile' }
				]
			},
			sprites: {
				files: [
					{ expand: true, flatten: true, src: ['<%=dirs.sprites_src%>'], dest: '<%=dirs.sprites_tmp%>/', filter: 'isFile' }
				]
			}
		},

		rename: {
			sprites: {
				files: [
					{    src: '<%=dirs.sprites_out_less%>/sprites.less', dest: '<%=dirs.sprites_out_less%>/020-sprites-generated.less'    }
				]
			}
		},

		clean: {
			sprites_temp: {
				src: ['<%=dirs.sprites_tmp%>']
			},
			less_imports: {
				src: ['<%=dirs.cache%>/temp-styles-imports.less']
			},
			// clean old css/js file after compression
			uncompressed_css: {
				src: ['<%=dirs.cache%>/base*.js']
			},
			uncompressed_js: {
				src: ['<%=dirs.cache%>/base*.css']
			}

		},

		// compress
		compress: {
			main: {
				options: {
					mode: 'gzip'
				},
				files: [
					{ expand: true, src: ['<%=dirs.cache%>/base*.js', '<%=dirs.cache%>/base*.css', '<%=dirs.cache%>/dyn*.js' ] }
				]
			}
		},


		////////////////////////////////////////////////////////////////////////////////

		// Watch

		watch: {
			gruntfile: {
				files: 'Gruntfile.js',
				tasks: ['jshint:gruntfile']
			},
			scripts: {
				files: ['<%=dirs.scripts%>'],
				tasks: ['build_scripts', 'uglify']
			},
			styles: {
				files: ['<%=dirs.styles%>'],
				tasks: ['build_styles', 'cssmin']
			},
			options: {
				livereload: true
			}
		},

		////////////////////////////////////////////////////////////////////////////////

		// Sprites

		glue: {
			sprites: { // filename will be sprites.png
				options: {
					'sprite-namespace': '', // namespace for each sprite class, empty because sprites are already prefixed
					namespace: '', // namespace for each sprite class, empty because sprites are already prefixed
					less: '<%=dirs.sprites_out_less%>', // path for the generated less file 020-sprites-generated.less
					url: '<%=dirs.sprites_out_url%>', // public url of the generated sprites.png
					crop: false, // do not crop images with whitespace
					'cachebuster-filename-only-sprites': true, // add cachebuster to filename instead as Query arg so that caching still works in all browsers
					margin: 2
				},
				src: '<%=dirs.sprites_tmp%>', // sprite temp source folder for generation
				dest: '<%=dirs.sprites_out_img%>' // public sprite location
			}
		},

		pngmin: {
			sprites: {
				options: {
					ext: '.png',
					force: true
				},
				files: [
					{
						src: 'web/webroot/_ui/all/media/sprites.png',
						dest: 'web/webroot/_ui/all/media/'
					}
				]
			}
		},

		////////////////////////////////////////////////////////////////////////////////////

		// Concurrent

		concurrent: {
			build: ['build_scripts', 'build_styles'],
			minify: ['uglify', 'cssmin']
		}

	});

	////////////////////////////////////////////////////////////////////////////////////

	// Tasks

	// Create Base Files
	grunt.registerTask('build_scripts', ['jshint', 'concat:scriptsHead', 'concat:scripts', 'copy:scriptsDyn', 'usebanner:scriptsDev']);
	grunt.registerTask('build_styles', ['less_imports', 'less', 'clean:less_imports','usebanner:stylesDev']);
	grunt.registerTask('build', ['concurrent:build']);

	// Create Base Files and watch
	grunt.registerTask('dev', ['build', 'watch']);

	// Create Base Files and minify
	grunt.registerTask('prod', ['build', 'concurrent:minify', 'usebanner:scriptsProd', 'usebanner:stylesProd', 'compress:main']);

	// Clean uncompressed file for prod deployment
	grunt.registerTask('prod_clean', ['clean:uncompressed_css', 'clean:uncompressed_js']);

	// Create Sprites
	grunt.registerTask('sprites', ['copy:sprites', 'glue', 'pngmin:sprites', 'clean:sprites_temp', 'rename:sprites']);

};
