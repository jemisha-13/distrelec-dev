// Import Gulp
const gulp = require('gulp');

// Import Plugins
let sass = require('gulp-sass')(require('sass'));
let cssMin = require('gulp-cssmin');
let concat = require('gulp-concat-css');
let rename = require('gulp-rename');
let minify = require('gulp-minify');

// Define Directories
let stylesDir = [
    'web/webroot/WEB-INF/terrific/modules/**/scss/*.scss',
    '!web/webroot/WEB-INF/terrific/modules/OCI/**/*.scss'
];
let stylesDest = 'web/webroot/_ui/all/matterhorn/';
let stylesOCIDir = 'web/webroot/WEB-INF/terrific/modules/OCI/**/scss-oci/*.scss';
let stylesOCIDest = 'web/webroot/_ui/all/matterhorn-oci/';
let dataLayerDest = 'web/webroot/_ui/desktop/common/js/';
let redRouteStylesDir = 'web/webroot/WEB-INF/terrific/scss/*.scss';

let sassOptions = {
    quietDeps: true, // Suppress math.div warnings in @fortawesome/fontawesome-free@5.15.4
    includePaths: ['node_modules']
};

// Begin Stylesheet Tasks

gulp.task('compileMainStyles', function () {
    return gulp.src(stylesDir)
        .pipe(sass(sassOptions)).on('error', sass.logError)
        .pipe(concat('main.css'))
        .pipe(gulp.dest(stylesDest));
});

gulp.task('compileRedRouteStyles', function () {
    return gulp.src(redRouteStylesDir)
        .pipe(sass(sassOptions)).on('error', sass.logError)
        .pipe(gulp.dest(stylesDest));
});

gulp.task('compileOciStyles', function () {
    return gulp.src(stylesOCIDir)
        .pipe(sass(sassOptions)).on('error', sass.logError)
        .pipe(concat('main.css'))
        .pipe(gulp.dest(stylesOCIDest));
});

gulp.task('compress-js', function(done) {
    gulp.src(['web/webroot/_ui/desktop/common/js/acc.datalayer.js'])
        .pipe(minify())
        .pipe(gulp.dest(dataLayerDest));
    done();
});

gulp.task('compileStyles', gulp.series('compileMainStyles', 'compileRedRouteStyles', 'compileOciStyles', 'compress-js'));

gulp.task('minifyAllStyles', function () {
    return gulp.src([
        stylesDest + '*.css',
        '!' + stylesDest + '*.min.css'
    ])
        .pipe(cssMin())
        .pipe(rename({suffix: '.min'}))
        .pipe(gulp.dest(stylesDest));
});

gulp.task('minifyOciStyles', function () {
    return gulp.src(stylesOCIDest + 'main.css')
        .pipe(cssMin())
        .pipe(rename({suffix: '.min'}))
        .pipe(gulp.dest(stylesOCIDest));
});

gulp.task('minifyStyles', gulp.series('minifyAllStyles', 'minifyOciStyles'));

// Begin Watch Task
gulp.task('watch', function() {

    gulp.watch('web/webroot/WEB-INF/terrific/modules/**/scss*/**/*.scss', function(){
        runSequence('compileStyles', 'minifyStyles');
    });

});

// Begin Build Tasks

gulp.task('build-styles', gulp.series('compileStyles', 'minifyStyles'));

gulp.task('default', function() {
    gulp.watch('**/**/*.scss', gulp.series('compileStyles'));
});
