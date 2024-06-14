const argInput = require('yargs').option('input', { type: 'array', desc: 'files which we want to merge' }).argv;
const argOutput = require('yargs').option('output', { type: 'string', desc: 'output destination file' }).argv;
const fs = require('fs');
const process = require('process');

function getSpecificLocale(folderName) {
  //mac os file reader adding .Ds_Store folder which breaks
  return folderName.includes('_') && folderName.match(/[A-Z](?!.*[A-Z].*)/gs) && !folderName.includes('.');
}

function getBaseLanguage(folderName) {
  //mac os file reader adding .Ds_Store folder which breaks
  return (
    !folderName.includes('_') &&
    !folderName.match(/[A-Z](?!.*[A-Z].*)/gs) &&
    !folderName.includes('.') &&
    folderName !== 'tr' &&
    folderName !== 'ru'
  );
}

// Loop through all the folders in translations directory
fs.readdir(`../`, (err, folders) => {
  folders.forEach((folder) => {
    if (getBaseLanguage(folder)) {
      let writeJoinedJSON = [];

      fs.readdir(`.././${folder}`, (err) => {
        if (err) {
          console.error('Could not list the directory.');
          process.exit(1);
        }

        let itemsProcessed = 0;
        let formatttedJSONChunk = null;

        argInput.input.forEach((file, id, array) => {
          fs.readFile(`.././${folder}/${file}`, { encoding: 'utf-8' }, (err, fileContent) => {
            if (err) {
              console.error(`Could not read file ${file}.`);
              process.exit(1);
            }

            itemsProcessed++;
            formatttedJSONChunk = fileContent.substring(0, fileContent.lastIndexOf('}')).replace(/^{/, '');
            writeJoinedJSON.push(formatttedJSONChunk);

            //if we are on last file write new file and format
            if (itemsProcessed === array.length) {
              callback(folder, writeJoinedJSON);
            }
          });
        });
      });
    }

    function callback(folder, data) {
      fs.writeFile(`.././${folder}/${argOutput.output}`, `{${data.join(',')}}`, (err) => {
        if (err) {
          console.error(`Could not create output file ${argOutput.output}.`);
          process.exit(1);
        }
      });
    }

    if (getSpecificLocale(folder)) {
      fs.writeFile(`.././${folder}/${argOutput.output}`, '{}', (err) => {
        if (err) {
          console.error(`Could not create output file ${argOutput.output}.`);
          process.exit(1);
        }
      });
    }
  });
});
