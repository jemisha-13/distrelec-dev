module.exports = {
  stories: ['../src/design-system/**/*.stories.ts', '../src/design-system/**/*.stories.@(js|jsx|ts|tsx)',
  '../src/app/spartacus/features/shared-modules/components/**/*.stories.ts'],

  addons: [
    '@storybook/addon-links',
    '@storybook/addon-essentials',
    '@storybook/addon-interactions',
    '@storybook/addon-designs',
  ],

  framework: {
    name: '@storybook/angular',
    options: {},
  },

  docs: {
    autodocs: true,
  },

  staticDirs: ['../src'],
};
