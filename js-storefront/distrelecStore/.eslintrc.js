module.exports = {
  root: true,

  ignorePatterns: ['projects/**/*', '**/translations/**/*', '**/icons/definitions/*'],
  parser: '@typescript-eslint/parser',
  plugins: [
    '@typescript-eslint',
    'eslint-plugin-import',
    'eslint-plugin-jsdoc',
    'eslint-plugin-prefer-arrow',
    'eslint-plugin-storybook',
  ],
  extends: ['plugin:storybook/recommended', 'prettier'],

  overrides: [
    {
      files: ['*.ts'],
      parserOptions: {
        project: ['tsconfig.json', 'e2e/tsconfig.json'],
        tsconfigRootDir: __dirname,
        createDefaultProgram: true,
      },
      extends: ['plugin:@angular-eslint/recommended', 'plugin:@angular-eslint/template/process-inline-templates'],
      rules: {
        '@angular-eslint/component-selector': [
          'error',
          {
            type: 'element',
            prefix: 'app',
            style: 'kebab-case',
          },
        ],
        '@angular-eslint/directive-selector': [
          'error',
          {
            type: 'attribute',
            prefix: 'app',
            style: 'camelCase',
          },
        ],
        '@typescript-eslint/consistent-type-assertions': 'off',
        '@typescript-eslint/member-delimiter-style': 'off',
        '@typescript-eslint/naming-convention': [
          'error',
          {
            selector: 'default',
            format: ['camelCase'],
            leadingUnderscore: 'allow',
            trailingUnderscore: 'allow',
          },
          {
            selector: 'variable',
            format: ['camelCase', 'UPPER_CASE'],
            leadingUnderscore: 'forbid',
            trailingUnderscore: 'allow',
          },
          {
            selector: 'typeLike',
            format: ['PascalCase'],
          },
          {
            selector: 'enumMember',
            format: ['PascalCase', 'UPPER_CASE'],
          },
          {
            selector: 'classProperty',
            format: ['camelCase', 'UPPER_CASE'],
            leadingUnderscore: 'forbid', // Leading underscore for private properties is a convention from c#, not consistent with spartacus code
            trailingUnderscore: 'allow',
          },
          {
            selector: 'typeProperty',
            format: ['camelCase', 'UPPER_CASE'],
            leadingUnderscore: 'forbid',
            trailingUnderscore: 'allow',
          },
          {
            selector: 'objectLiteralProperty',
            format: [
              'camelCase', // Preferred format
              'PascalCase', // Allowed for configuring LayoutTemplateNames
            ],
          },
        ],
        '@typescript-eslint/no-inferrable-types': 'warn',
        '@typescript-eslint/quotes': [
          'off',
          'single',
          {
            allowTemplateLiterals: true,
          },
        ],
        '@typescript-eslint/semi': 'off',
        '@typescript-eslint/type-annotation-spacing': 'off',
        'brace-style': 'off',
        'comma-dangle': 'off',
        'eol-last': 'off',
        indent: 'off',
        'keyword-spacing': 'off',
        'max-len': 'off',
        'new-parens': 'off',
        'no-multiple-empty-lines': 'off',
        'no-trailing-spaces': 'off',
        'no-shadow': 'off',
        'no-underscore-dangle': 'off',
        'object-curly-spacing': 'off',
        'prefer-arrow/prefer-arrow-functions': [
          'warn',
          {
            allowStandaloneDeclarations: true,
          },
        ],
        'quote-props': 'off',
        'space-before-function-paren': 'off',
      },
    },
    {
      files: ['*.html'],
      extends: ['plugin:@angular-eslint/template/recommended'],
      rules: {},
    },
  ],
};
