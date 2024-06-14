@echo off
pushd %cd%
cd environment
call ant installAnt
cd ..
copy environment\common\static\manualscripts\setantenv.bat
call setantenv.bat
cd environment
call ant setupDeploymentScript
cd ..\init
call ant createDeploymentUnit
popd