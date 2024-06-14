package com.distrelec.smartedit.populator;

import com.namics.distrelec.b2b.core.model.cms2.components.DesignImgVidTextCol2Model;
import com.namics.distrelec.b2b.core.model.cms2.components.DesignImgVidTextCol3Model;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import static com.distrelec.smartedit.constants.DistrelecsmarteditConstants.*;

public class DistDesignImgVideoPopulator implements Populator<ItemModel, Map<String, Object>>
{
    @Override
    public void populate(final ItemModel source, final Map<String, Object> targetMap) throws ConversionException
    {
        if (source != null)
        {
            if(source instanceof DesignImgVidTextCol2Model) {
                if (null != ((DesignImgVidTextCol2Model) source).getVideo1()) {
                    targetMap.put(VIDEO1_COMPONENTS, ((DesignImgVidTextCol2Model) source).getVideo1().getBrightcoveVideoId());
                    targetMap.put(VIDEO1_YOUTUBE_COMPONENTS, ((DesignImgVidTextCol2Model) source).getVideo1().getYoutubeUrl());
                }
                if (null != ((DesignImgVidTextCol2Model) source).getVideo2()) {
                    targetMap.put(VIDEO2_COMPONENTS, ((DesignImgVidTextCol2Model) source).getVideo2().getBrightcoveVideoId());
                    targetMap.put(VIDEO2_YOUTUBE_COMPONENTS, ((DesignImgVidTextCol2Model) source).getVideo2().getYoutubeUrl());
                }
            }
            else if(source instanceof DesignImgVidTextCol3Model){
                if (null != ((DesignImgVidTextCol3Model) source).getVideo1())
                {
                    targetMap.put(VIDEO1_COMPONENTS, ((DesignImgVidTextCol3Model) source).getVideo1().getBrightcoveVideoId());
                    targetMap.put(VIDEO1_YOUTUBE_COMPONENTS, ((DesignImgVidTextCol3Model) source).getVideo1().getYoutubeUrl());
                }
                if (null != ((DesignImgVidTextCol3Model) source).getVideo2())
                {
                    targetMap.put(VIDEO2_COMPONENTS, ((DesignImgVidTextCol3Model) source).getVideo2().getBrightcoveVideoId());
                    targetMap.put(VIDEO2_YOUTUBE_COMPONENTS, ((DesignImgVidTextCol3Model) source).getVideo2().getYoutubeUrl());
                }
                if (null != ((DesignImgVidTextCol3Model) source).getVideo3())
                {
                    targetMap.put(VIDEO3_COMPONENTS, ((DesignImgVidTextCol3Model) source).getVideo3().getBrightcoveVideoId());
                    targetMap.put(VIDEO3_YOUTUBE_COMPONENTS, ((DesignImgVidTextCol3Model) source).getVideo3().getYoutubeUrl());
                }
            }
        }
    }
}
