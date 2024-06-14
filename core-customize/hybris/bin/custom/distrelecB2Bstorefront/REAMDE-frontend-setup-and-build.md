
# Setup and Build Frontend Files

## Setup Dev Environment


1. Install Node Version Manager (nvm) for OS X or Windows

	*If you only need one Version of node on your local machine, you can skip this step and
	install node from http://nodejs.org*

	OS X:

			download and install Node Version Manager from https://github.com/creationix/nvm

	Windows:

			download and install Node Version Manager from https://github.com/hakobera/nvmw

	Type *nvm* into your console to check, that nvm is installed.

	Install and set Node Version using the following command: $ nvm install 0.10.26


2. Add npm path to your PATH

	OX X:

			$ vim ~/.bash_profile
			export PATH="~/.nvm/:$PATH"

	Windows:

			Check if NPM and Node JS were added to the path variable

3. Install Grunt globally

	OS X:

	 		npm install -g grunt-cli

	Windows:

	 		npm install -g grunt-cli

4. Install node_modules

	Install the node modules

	 		$ npm install

	If an error occured, delete your local node_modules directory and try again:

			$ rm -Rf node_modules
			$ npm install


5. Start Grunt

	Change into storefront directory and execute the following tasks:

			$ cd <Project-Root>
			$ cd hybris/bin/custom/distrelecB2Bstorefront/
			$ grunt prod

	There are two Grunt Tasks:

			$ grunt dev: Does not minify, but rebuilds files on every filechange
			$ grunt prod: Does minify files for production, does not rebuild files on filechange



6. Optional: Install Glue, which is used for Sprites

	OS X:

			brew install jpeg
			sudo easy_install pip
			sudo pip install glue

	Windows:

			See http://glue.readthedocs.org/en/latest/installation.html

			1. Install Python: http://www.python.org/ftp/python/2.7.2/python-2.7.2.msi
			2. Install Pillow: https://code.google.com/p/ocr-uvg/downloads/detail?name=PIL-1.1.7.win32-py2.7.exe&can=2&q=
			3. Install Pythons Easy Install: http://pypi.python.org/packages/2.7/s/setuptools/setuptools-0.6c11.win32-py2.7.exe
			4. Add Python’s Scripts dir to your Path. Add ;C:\Python27\Scripts to the end of the line.
			5. Go to C:\Python27 (dir where you installed python) Start the cmd and type: easy_install glue
			6. Restart Console to have Glue available in path

	Use:

			1. Place images in directory:

				/web/webroot/WEB-INF/terrific/css/static/sprites

			2. Build sprite:

				$ grunt sprites

			3. Use in Less CSS:

				i {
					.s-facets-remove-filter-blue();
				}

	Naming:

			Icons must be named like this: s-module-iconname-color (Prefix, Modulename, Iconname, Color).

			Example: s-facets-remove-filter-blue



## Known Errors

When the Grunt Watch Tasks fails with an error "Fatal error: Cannot call method 'replace' of undefined", increase the
limit for concurrent open files using the following command on OS X:

$ ulimit -n 8192

