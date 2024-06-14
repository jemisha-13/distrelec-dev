# Readme

::: setup :::

- cd to \_translation-scripts directory
- yarn install

::: script for merging json files together :::

command takes 2 arguments --input (files you need merging) --output (destination of joined json files)

example below yarn merge --input common.json countries.json footer.json form.json breadcrumb.json httpHandlers.json nps.json system.json subscribe.json reportError.json validations.json updatePwd.json feedback.json metahd.json errorNotFound.json backorder.json article.json --output shared.json

specific locales will be created also but will be set to {} to avoid 404's from spartacus

::: script for flattening json keys :::

This script is used for flattening json keys to make them more readable example below

NOTE:: there is a bug when flattening json it errors in terminal but still flattens, this needs looking at.

::: original :::

{
"product":{
"downloads_section":{
"downloads":"Ke stažení"
},

}

::: flattened :::

{product.downloads_section.downloads}

This wont affect our templates for referencing translations as they use the . to drill in to the objects anyway.

by defualt you can run the script and it will flatten all files optionally you can pass an argument and specify what files you would like flattened, example below.

- yarn flatten

- yarn flatten --input product.json
