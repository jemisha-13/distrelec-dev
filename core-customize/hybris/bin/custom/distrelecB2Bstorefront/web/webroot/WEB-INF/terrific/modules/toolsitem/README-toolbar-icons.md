Readme Frontend Toolbar Icons
=============================

We use one module for each toolbar icon.
This module has skins for it's specivic actions, if that is required.

!! Important: Toolsitems templates jsp files containing the word "lbl" in its filename contain a label with actual text in it !!

MODULE:
-------
toolsitem


IMPLEMENT ICON:
---------------

Add the following JSP for creating a new icon button:

<mod:toolsitem template="toolsitem-shopping" skin="shopping" tag="li" />


Create toggle button:
---------------------
find('.ico').addClass('ico-toggle')


Create toggle popover:
----------------------
find('.ico').addClass('popover-toggle')
find('.ico').attr('data-content-id', 'CONTENT_ID')

$ctx.html('<div class="hidden" id="CONTENT_ID">
               ##CONTENT##
           </div>')

INFO: that functionality use the function "popoverFromHtml"

Create hover popover:
---------------------
<terrific:mod name="toolsitem" tag="li" htmlClasses="tooltip-hover" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
find('.ico').addClass('popover-hover')

