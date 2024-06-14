# Project Meta Documentation

Diese Vorlage zeigt exemplarisch die Integration von Terrific in hybris.
Für die Frontend-Entwicklung müssen folgende Punkte beachtet werden:

## Nachladen von JavaScript Features

Auch für das Nachladen von JavaScript Features muss der ContextPath
berücksichtigt werden. Dieser wird im Bootstrap der Tc.Application mit
übergeben:

	(function($) {
   		$(document).ready(function() {
       		var $page = $('html');
       		var application = new Tc.Application($page);
       		application.registerModules();
       		application.contextPath("${data.meta.contextPath}");
       		application.start();
   		});
	})(Tc.$);

Sofern ein Framework wie Modernizr verwendet wird, muss dafür gesorgt werden,
dass beim Nachladen von weiteren JavaScript Dateien dieser Pfad vorgestellt
wird.

## i18n: Mehrsprachigkeit von clientseitig gerendertem Content

s. Read Me im Modul aa_i18n