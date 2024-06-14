package com.namics.distrelec.b2b.core.service.snapeda.response;

import java.util.List;

public class SnapEdaResponse {
    private boolean status;

    private boolean hasFootprint;

    private boolean hasSymbol;

    private Symbol symbol;

    private Uniparts uniparts;

    private boolean nativePartIds;

    private long partId;

    private String distributor;

    private long unipartId;

    private boolean has3D;

    private long stepModelId;

    private String html;

    private Unipart unipart;

    private boolean hasMultipleParts;

    public boolean getStatus() {return status;}

    public void setStatus(boolean value) {this.status = value;}

    public boolean getHasFootprint() {return hasFootprint;}

    public void setHasFootprint(boolean value) {this.hasFootprint = value;}

    public boolean getHasSymbol() {return hasSymbol;}

    public void setHasSymbol(boolean value) {this.hasSymbol = value;}

    public Symbol getSymbol() {return symbol;}

    public void setSymbol(Symbol value) {this.symbol = value;}

    public Uniparts getUniparts() {return uniparts;}

    public void setUniparts(Uniparts value) {this.uniparts = value;}

    public boolean getNativePartIds() {return nativePartIds;}

    public void setNativePartIds(boolean value) {this.nativePartIds = value;}

    public long getPartId() {return partId;}

    public void setPartId(long value) {this.partId = value;}

    public String getDistributor() {return distributor;}

    public void setDistributor(String value) {this.distributor = value;}

    public long getUnipartId() {return unipartId;}

    public void setUnipartId(long value) {this.unipartId = value;}

    public boolean getHas3D() {return has3D;}

    public void setHas3D(boolean value) {this.has3D = value;}

    public long getStepModelId() {return stepModelId;}

    public void setStepModelId(long value) {this.stepModelId = value;}

    public String getHtml() {return html;}

    public void setHtml(String value) {this.html = value;}

    public Unipart getUnipart() {return unipart;}

    public void setUnipart(Unipart value) {this.unipart = value;}

    public boolean getHasMultipleParts() {return hasMultipleParts;}

    public void setHasMultipleParts(boolean value) {this.hasMultipleParts = value;}

    public static class Uniparts {
        private List<Unipart> uniparts;

        public List<Unipart> getUniparts() {
            return uniparts;
        }

        public void setUniparts(List<Unipart> uniparts) {
            this.uniparts = uniparts;
        }
    }

    public static class Symbol {
        private long id;

        private String format;

        public long getId() {return id;}

        public void setId(long value) {this.id = value;}

        public String getFormat() {return format;}

        public void setFormat(String value) {this.format = value;}
    }

    public static class Unipart {
        private String footprintImage;

        private String description;

        private String threedUrl;

        private String symbolImage;

        private String part;

        private long partId;

        private String source3DModels;

        private String manufacturer;

        private Object cadAvailability;

        private String partUrl;

        private String threedImage;

        public String getFootprintImage() {return footprintImage;}

        public void setFootprintImage(String value) {this.footprintImage = value;}

        public String getDescription() {return description;}

        public void setDescription(String value) {this.description = value;}

        public String getThreedUrl() {return threedUrl;}

        public void setThreedUrl(String value) {this.threedUrl = value;}

        public String getSymbolImage() {return symbolImage;}

        public void setSymbolImage(String value) {this.symbolImage = value;}

        public String getPart() {return part;}

        public void setPart(String value) {this.part = value;}

        public long getPartId() {return partId;}

        public void setPartId(long value) {this.partId = value;}

        public String getSource3DModels() {return source3DModels;}

        public void setSource3DModels(String value) {this.source3DModels = value;}

        public String getManufacturer() {return manufacturer;}

        public void setManufacturer(String value) {this.manufacturer = value;}

        public Object getCadAvailability() {return cadAvailability;}

        public void setCadAvailability(Object value) {this.cadAvailability = value;}

        public String getPartUrl() {return partUrl;}

        public void setPartUrl(String value) {this.partUrl = value;}

        public String getThreedImage() {return threedImage;}

        public void setThreedImage(String value) {this.threedImage = value;}
    }
}
