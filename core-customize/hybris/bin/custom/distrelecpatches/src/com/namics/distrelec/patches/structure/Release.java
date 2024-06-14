package com.namics.distrelec.patches.structure;

/**
 * Represent project releases. String used in constructor will be used to create paths to impexes that should be imported in logs etc. (should be
 * unique).
 */
public enum Release implements de.hybris.platform.patches.Release {

    R1_1("r1_1"),
    R1_2("r1_2"),
    R1_4("r1_4"),
    R1_5("r1_5"),
    R1_6("r1_6"),
    R1_7("r1_7"),
    R1_8("r1_8"),
    R1_9("r1_9"),
    R1_10("r1_10"),
    R1_12("r1_12"),
    R1_13("r1_13"),
    R1_16("r1_16"),
    R2_1("r2_1"),
    R2_2("r2_2"),
    R2_3("r2_3"),
    R2_4("r2_4"),
    R2_5("r2_5"),
    R2_6("r2_6"),
    R3_1("r3_1"),
    R3_2("r3_2"),
    R3_3("r3_3"),
    R3_4("r3_4"),
    R3_5("r3_5"),
    R3_6("r3_6"),
    R3_7("r3_7"),
    R3_8("r3_8"),
    CCV2("CCV2MIGRATION"),
    HEADLESS("HEADLESS"),
    UPGRADE_2211("UPGRADE_2211"),
    FUSION_2("FUSION_2"),
    R15_1("15_1"),
    R15_2("15_2"),
    R15_3("15_3"),
    R15_4("15_4"),
    R15_5("15_5"),
    R15_6("15_6"),
    R15_7("15_7"),
    R15_8("15_8"),
    R16_0("16_0"),
    R16_1("16_1"),
    R16_3("16_3");

    private String releaseId;

    Release(final String releaseId) {
        this.releaseId = releaseId;
    }

    @Override
    public String getReleaseId() {
        return this.releaseId;
    }

}
