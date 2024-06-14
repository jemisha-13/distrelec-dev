USE hybris_d;

CREATE TABLE hybris_d.internal_link_data (
    code text,
    type text,
    site text,
    cs text,
    da text,
    de text,
    en text,
    et text,
    fi text,
    fr text,
    hu text,
    it text,
    lt text,
    lv text,
    nl text,
    no text,
    p_timestamp timestamp,
    pl text,
    ro text,
    sk text,
    sv text,
    PRIMARY KEY (code, type, site)
);
CREATE INDEX site_index ON hybris_d.internal_link_data (p_timestamp);