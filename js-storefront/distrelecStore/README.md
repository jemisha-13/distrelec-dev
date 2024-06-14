# Distrelec Storefront (Spartacus)

## Version Info

- [Composable Storefront (Spartacus) v2211.19](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT?locale=en-US)
- Angular v17
- Node v20 LTS

### CLI Tools

We recommend using Node Version Manager (nvm) to manage your Node versions.
See the [documentation](https://github.com/nvm-sh/nvm#installing-and-updating) for more information.

[npm is the recommended package manager for Composable Storefront.](https://help.sap.com/docs/SAP_COMMERCE_COMPOSABLE_STOREFRONT/cfcf687ce2544bba9799aa6c8314ecd0/bf31098d779f4bdebb7a2d0591917363.html?locale=en-US#loiof7ff9bb1248e4c1da6f167f12ba38d2c)

`npm install -g @angular/cli` to install the Angular CLI.

## Development

Copy .npmrc file from environment/env-development/js-storefront/distrelecStore/.npmrc to the root folder before installing modules.

Run `npm install` to install the dependencies.

Set up your environment variables.

```shell
cp ./src/environments/environment.local.template.ts ./src/environments/environment.local.ts
```

Edit `environment.local.ts` and set the correct occBaseUrl.

Make sure you have [configured the storefront URLs in your hosts file](https://wiki.distrelec.com/display/distrelint/Step+08%3A+Hosts+File).

Run `npm run dev:ssr` to run the server in SSR mode.
If you run hybris locally and need to test checkout callbacks you will need to use the SSR server.

Run `npm run dev` to run the server in CSR mode.

### Remote debugging

`npm run dev:debug` will run the CSR server with HTTP so you can connect to it with a remote debugger.

- [Instructions for IntelliJ](https://www.jetbrains.com/help/idea/debugging-javascript-in-chrome.html)
- Instructions for VSCode
  - [Browser Debugging](https://code.visualstudio.com/docs/nodejs/browser-debugging)
  - [Debugging Angular](https://code.visualstudio.com/docs/nodejs/angular-tutorial#_debugging-angular)

### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).
You can use the `--code-coverage` flag to generate a code coverage report.

To run a specific test file run `ng test --code-coverage  -- --include src/path/to/your/test.spec.ts`

[Read the full documentation on the wiki.](https://wiki.distrelec.com/display/SD/Writing+tests)

### Code scaffolding

Run `ng generate component component-name` to generate a new component.
You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

When creating new components please follow the [Single Component Angular Module pattern](https://angular-training-guide.rangle.io/modules/module-scam-pattern).

### Design system & Storybook

The [Distrelec Design System](https://www.figma.com/file/sTywg49C2f7JlHf6uF1nL1) is used to create the UI components.

Standalone components can be found in the design system folder at `src/design-system` and imported with the alias `@design-system`.
Components in the design system folder should have a Storybook file to document the component and allow for easy development.

Run `npm run storybook` to run the Storybook server.

Run `npm run chromatic` to publish the changes to Chromatic.

[Read the full documentation on the wiki.](https://wiki.distrelec.com/pages/viewpage.action?spaceKey=distrelint&title=Storybook+Setup+and+Guidelines)

### Code formatting

Prettier is used to format the code. See the [instructions here](https://prettier.io/docs/en/editors.html) for setting up your IDE.

ESLint is used to lint the code. See the [instructions here](https://eslint.org/docs/user-guide/integrations) for setting up your IDE.

lint-staged will run Prettier, ESLint and Stylelint on the files you commit. To commit without running the precommit hooks use

```
git commit --no-verify
```

### Other tools

- [Angular DevTools](https://angular.io/guide/devtools). Browser to assist debugging Angular applications.
- [Redux DevTools](https://github.com/reduxjs/redux-devtools). Browser plugin for debugging and browsing redux state.

### Centralised SSR Cache using Blob Storage

Run Azurite emulator using Docker

```
docker run -p 10000:10000 mcr.microsoft.com/azure-storage/azurite azurite-blob --blobHost 0.0.0.0 --blobPort 10000
```

Enable the following settings in `environment.local.ts` using the default values from the template.

* `cacheSsrOutput`
* `useExternalCache`
* `externalCacheContainerName`
* `externalCacheConnectionString`

## Build for Production

Run `npm run build:ssr` to build the project.

Run `npm run serve:ssr` to run the server in SSR mode.

The server will be available on HTTP port 4200.
