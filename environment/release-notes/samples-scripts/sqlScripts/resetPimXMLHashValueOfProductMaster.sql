/*
Reset hash values of all german (master) products
*/
UPDATE productslp SET p_pimXmlHashLocalized = null WHERE langpk IN (SELECT pk FROM languages lang WHERE lang.isocode='en');


/*
Reset hash values of all german (master) products having an article number ending with 1
*/
UPDATE
	productslp
SET
	p_pimXmlHashLocalized = NULL
WHERE
  langpk IN (SELECT pk FROM languages WHERE isocode='en') AND
  itempk IN (SELECT pk FROM products WHERE code LIKE '%1');
