import { Component, Input } from '@angular/core';
import { playCircle } from '@assets/icons/icon-index';

@Component({
  selector: 'app-video-holder',
  templateUrl: './video-holder.component.html',
  styleUrls: ['./video-holder.component.scss'],
})
export class VideoHolderComponent {
  @Input() videoHolderId;

  playCircle = playCircle;
}
