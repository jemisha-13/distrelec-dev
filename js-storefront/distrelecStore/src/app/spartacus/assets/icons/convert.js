/* eslint-env es6 */
const fs = require('fs');
const path = require('path');
const parseSvg = require('svg-parser');

const currentDir = process.cwd();
const inputFolder = '../media/icons/icon/';

let generatedIcons = [];

function formatFileName(name) {
  const pieces = name.split('-');

  if (pieces.length > 1) {
    let formatted = [pieces[0]];
    for (i = 1; i < pieces.length; i++) {
      let bits = pieces[i].split('');
      formatted.push(bits[0].toUpperCase() + pieces[i].substr(1, pieces[i].length));
    }
    return formatted.join('');
  }
  return pieces[0];
}

function generateDefintionFile(svgFiles) {
  svgFiles.forEach((file) => {
    const formattedName = formatFileName(file.replace('.svg', ''));
    generatedIcons.push({ fileName: file.replace('.svg', ''), iconName: formattedName });

    const contents = fs.readFileSync(inputFolder + file, 'utf-8');
    const fileName = formattedName;
    const parsedSvg = parseSvg.parse(contents);
    const svgDefinition = {
      name: fileName,
      viewbox: parsedSvg.children[0].properties.viewBox,
      dimensions: [
        parsedSvg.children[0].properties.width.toString(),
        parsedSvg.children[0].properties.height.toString(),
      ],
      colour: parsedSvg.children[0].properties.fill,
      elements: parsedSvg.children[0].children,
    };

    const fileContent = `import { DistIcon } from '@model/icon.model';

export const ${fileName}: DistIcon = {
  name: '${svgDefinition.name}',
  viewbox: '${svgDefinition.viewbox}',
  dimensions: [${svgDefinition.dimensions}],
  colour: '${svgDefinition.colour}',
  elements: ${JSON.stringify(svgDefinition.elements)},
};
`;

    writeFile(fileContent, file.replace('.svg', '') + '.ts', true);
  });

  generateIndexFile(generatedIcons);
}

function generateIndexFile(icons) {
  let content = '';

  icons.forEach((icon) => {
    if (icon) {
      content += `export * from './definitions/${icon.fileName}';\n`;
    }
  });

  writeFile(content, 'index.ts');
}

function writeFile(content, fileName, definitions = false) {
  let outputFilePath;

  if (definitions) {
    outputFilePath = path.join(currentDir + '/definitions', fileName);
  } else {
    outputFilePath = path.join('./', fileName);
  }
  fs.writeFileSync(outputFilePath, content, 'utf8');
  console.log(`${fileName} generated`);
}

let files = fs.readdirSync(inputFolder);
const svgFiles = files.filter((file) => path.extname(file) === '.svg');
generateDefintionFile(svgFiles);
console.log('Icon files and icon-index generated successfully!');
