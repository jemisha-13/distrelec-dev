# Icon Component

Distrelec icon component to make using the material icons from the design system without having to include imports all across the codebase and updated to be faster as there are less network requests to fetch the icon files.

## Setup

Copy the icon svg from figma and place it in the assets/media/icon/icons folder, give the icon an appropriate file name (this is what it will be referenced as in the component code).

```bash
npm run generate-icon-files
```

This script will read the media/icons directory and generate a typescript file with the definitions the app-icon component requires. Once this has been done the icon-index will update with the new icons and these can be imported into your component code.

**Do not modify the files in the assets/icons/definitions folder as these will be overwritten when the script is run.**

## Usage

The icons can be imported from the icon-index file in your component code:

```javascript
import { closeIcon, successTick } from '@assets/icons';

@Component({
  selector: 'app-newsletter-popup',
  templateUrl: './newsletter-popup.component.html',
  styleUrls: ['./newsletter-popup.component.scss'],
})
export class NewsletterPopupComponent implements OnInit, OnDestroy {
  subscribeForm: FormGroup;
  showConfirmModal_: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  distCloseIcon = closeIcon;
  distSuccessTick = successTick;
}
```

Then passed through the app-icon component using the [icon] parameter.

```html
<app-icon [icon]="distCloseIcon" iconAltText="close icon"></app-icon>
```

## Customisation

The icon component can be styled in a similar way to the font awesome icons, to apply custom styling you will need to add a class to the app-icon, please see below:

```html
<app-icon *ngIf="showSuccess" class="success-check" [icon]="distSuccessTick" iconAltText="success check"></app-icon>
```

once the class attribute has been added to the component it will ignore the height/ width as well as the colour provided in the svg definiton, this should then be added in the scss for the component.

If the class attribute is excluded then the height/ width and fill attributes will be used from the svg definiton.

## Errors

If you get an error that the icon property is not provided by any directive double check that the product has been imported in the required modules.
