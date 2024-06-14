var block = window.CKEDITOR.dtd.$block;
var inline = window.CKEDITOR.dtd.$inline;

function registerFromObjectKeys(container) {
    for (var tag in container) {
        if (container.hasOwnProperty(tag)) {
            window.CKEDITOR.dtd.a[tag]=1;
        }
    }
}

registerFromObjectKeys(block);
registerFromObjectKeys(inline);

window.CKEDITOR.dtd.$removeEmpty = {};