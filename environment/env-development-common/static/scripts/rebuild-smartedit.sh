#!/bin/bash

(cd ./core-customize/hybris/bin/custom/distrelecsmartedit && ant clean)
(cd ./core-customize/hybris/bin/modules/smartedit/smartedittools && ant build)
