package com.namics.distrelec.b2b.core.newsletter.job;

public class NewLetterImageData {

	public static interface ImageType
	{
		String PRIMARY = "PRIMARY";
		String GALLERY = "GALLERY";
	}

	private String imageType;
	private String format;
	private String url;
	private String altText;
	private Integer galleryIndex;
	private String name;
	
	public String getFormat()
	{
		return format;
	}

	public void setFormat(final String format)
	{
		this.format = format;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public String getImageType()
	{
		return imageType;
	}

	public void setImageType(final String imageType)
	{
		this.imageType = imageType;
	}

	public String getAltText()
	{
		return altText;
	}

	public void setAltText(final String altText)
	{
		this.altText = altText;
	}

	public Integer getGalleryIndex()
	{
		return galleryIndex;
	}

	public void setGalleryIndex(final Integer galleryIndex)
	{
		this.galleryIndex = galleryIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
