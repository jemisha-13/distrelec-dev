// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html
const puppeteer = require('puppeteer');
process.env.CHROME_BIN = puppeteer.executablePath();

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma'),
      require('karma-spec-reporter'),
    ],
    client: {
      clearContext: false, // leave Jasmine Spec Runner output visible in browser
    },
    coverageIstanbulReporter: {
      dir: require('path').join(__dirname, './coverage/spartacus'),
      reports: ['html', 'lcovonly', 'text-summary'],
      fixWebpackSourcePaths: true,
    },
    coverageReporter: {
      subdir: '.',
      dir: require('path').join(__dirname, './coverage/spartacus'),
      reporters: [{ type: 'html' }, { type: 'text-summary' }, { type: 'lcovonly' }],
    },
    reporters: ['kjhtml', 'spec'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['ChromeHeadlessNoSandbox'],
    customLaunchers: {
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox'],
      },
    },
    singleRun: false,
    restartOnFileChange: true,
    specReporter: {
      maxLogLines: 5, // limit number of lines logged per test
      suppressSummary: false, // do not print summary
      suppressErrorSummary: false, // do not print error summary
      suppressFailed: false, // do not print information about failed tests
      suppressPassed: false, // do not print information about passed tests
      suppressSkipped: true, // do not print information about skipped tests
      showBrowser: false, // print the browser for each spec
      showSpecTiming: false, // print the time elapsed for each spec
      failFast: false, // test would finish with error when a first fail occurs
      prefixes: {
        success: '    OK: ', // override prefix for passed tests, default is '✓ '
        failure: 'FAILED: ', // override prefix for failed tests, default is '✗ '
        skipped: 'SKIPPED: ', // override prefix for skipped tests, default is '- '
      },
    },
  });
};
