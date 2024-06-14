# Environment configs

This folder contains the configuration files for the different builds of the angular application.

The prod and non-prod config files do not relate SAP production / test / pretest environments, but rather whether angular is built in production mode or not.

All SAP environments including test and pretest are built in production mode.

`environment.default.ts` is the base configuration used for all builds.

`environment.ts` is used for all builds in non-production mode, i.e. `yarn dev` and `yarn dev:ssr`

`environment.prod.ts` is used for all builds in production mode, i.e. `yarn prod:ssr`.

`environment.local.ts` can be used for local development overrides and changes will not be tracked by git and accidentally committed. **These will apply to both production and development builds.**
