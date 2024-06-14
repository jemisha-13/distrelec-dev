import { Component, Input } from '@angular/core';

import { Entry } from '@model/misc.model';

@Component({
  selector: 'app-feedback-campaign-text',
  templateUrl: './feedback-campaign-text.component.html',
  styleUrls: ['./feedback-campaign-text.component.scss'],
})
export class FeedbackCampaignTextComponent {
  @Input() feedbackText: Entry<string, string>;

  constructor() {}
}
