const argInput = require('yargs').option('input', { type: 'array', desc: 'files which we want to flatten' }).argv;
const fs = require('fs');
const process = require('process');

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
      fs.readdir(`.././${folder}`, (err, fileNames) => {
        //optional param for files that need to be flattened, if no argument flatten every file

        argInput.input.length ? readWriteFlatten(folder, argInput.input) : readWriteFlatten(folder, fileNames);
      });
    }
  });
});

function readWriteFlatten(folder, fileNames) {
  fileNames.forEach((file) => {
    fs.readFile(`.././${folder}/${file}`, { encoding: 'utf-8' }, (err, fileContent) => {
      const fileDataFlattened = JSON.stringify(flattenObject(JSON.parse(fileContent)), null, 2);

      fs.writeFile(`.././${folder}/${file}`, fileDataFlattened, (err) => {
        //TODO bug where it errors but files still flatten
        console.error(`Could not flatten json for ${file}`);
        process.exit(1);
      });
    });
  });
}

function flattenObject(ob) {
  var toReturn = {};

  for (var i in ob) {
    if (!ob.hasOwnProperty(i)) continue;

    if (typeof ob[i] == 'object' && ob[i] !== null) {
      var flatObject = flattenObject(ob[i]);
      for (var x in flatObject) {
        if (!flatObject.hasOwnProperty(x)) continue;

        toReturn[i + '.' + x] = flatObject[x];
      }
    } else {
      toReturn[i] = ob[i];
    }
  }

  return toReturn;
}
