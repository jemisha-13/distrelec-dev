BEGIN

UPDATE clattruntlp
SET p_pimxmlhashlocalized = NULL;

UPDATE distmanufacturerlp
SET p_pimXmlHashLocalized = NULL;

UPDATE mediacontainer
SET p_pimxmlhashmaster = NULL;

UPDATE medias
SET p_pimxmlhashmaster = NULL
WHERE TypePkString IN (SELECT pk FROM composedtypes WHERE internalcode ='DistDownloadMedia');

UPDATE productslp
SET p_pimxmlhashlocalized = NULL;

END;