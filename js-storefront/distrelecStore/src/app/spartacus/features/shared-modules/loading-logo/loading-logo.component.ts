import { Component } from '@angular/core';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-loading-logo',
  templateUrl: './loading-logo.component.html',
  styleUrls: ['./loading-logo.component.scss'],
})
export class LoadingLogoComponent {
  faSpinner = faSpinner;
}
