USE hybris_d;
CREATE TABLE IF NOT EXISTS INTERNAL_LINK_DATA (code text, type text, site text, p_timestamp timestamp, en text, de text, it text, lt text, fr text, sv text, nl text, fi text, pl text, no text, ro text, cs text, sk text, hu text, lv text, da text, et text, PRIMARY KEY (code, type, site));
CREATE INDEX site_index ON INTERNAL_LINK_DATA (p_timestamp);
