import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FUSION_FEEDBACK_CAMPAIGNS_NORMALIZER } from '@features/pages/product/core/fusion/converters/injection-tokens';
import { DistFusionFeedbackCampaigns } from '@features/pages/product/core/fusion/converters/dist-fusion-feedback-campaigns-normalizer';

@NgModule({
  imports: [CommonModule],
  providers: [
    {
      provide: FUSION_FEEDBACK_CAMPAIGNS_NORMALIZER,
      useExisting: DistFusionFeedbackCampaigns,
      multi: true,
    },
  ],
})
export class DistFusionFeedbackCampaignsModule {}
