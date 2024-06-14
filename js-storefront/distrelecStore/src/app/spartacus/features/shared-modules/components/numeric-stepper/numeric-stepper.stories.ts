/* eslint-disable @typescript-eslint/naming-convention */
import { applicationConfig, Meta, moduleMetadata, argsToTemplate, StoryObj } from '@storybook/angular';
import { CommonModule } from '@angular/common';
import { I18nModule, TranslationService } from '@spartacus/core';
import { TooltipComponentModule } from '@design-system/tooltip/tooltip.module';
import { importProvidersFrom } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { StorybookTranslationService } from '@design-system/services/storybook-translation.service';
import { AllsitesettingsService } from '@services/allsitesettings.service';
import MockAllsitesettingsService from '@features/mocks/mock-all-site-settings.service';
import { NumericStepperComponent } from './numeric-stepper.component';
import { MOCK_AVAILABILITY_FULLY_STOCKED } from '@features/mocks/mock-pdp-availability-SC-NORMAL';
import { FormControl } from '@angular/forms';
import { ProductQuantityService } from '@services/product-quantity.service';
import { DistCartService } from '@services/cart.service';
import { MockDistCartService } from '@features/mocks/mock-cart-store.service';
import { MockDistBaseSiteService } from '@features/mocks/services/mock-basesite.service';
import { HttpClientModule } from '@angular/common/http';
import { DistrelecBasesitesService } from '@services/basesites.service';
import { QuantitySelectorComponentModule } from '@design-system/quantity-selector/quantity-selector.module';
import { MinOrderQuantityPopupModule } from '../min-order-quantity-popup/min-order-quantity-popup.module';

export default {
  title: 'Numeric Stepper',
  component: NumericStepperComponent,
  tags: ['autodoc'],
  argTypes: {
    disabled: {
      control: {
        type: 'boolean',
      },
    },
    minimumQuantity: {
      control: {
        type: 'number',
      },
    },
    quantityStep: {
      control: {
        type: 'number',
      },
    },
    maximumQuantity: {
      control: {
        type: 'number',
      },
    },
  },
  decorators: [
    applicationConfig({
      providers: [
        { provide: DistCartService, useClass: MockDistCartService },
        { provide: TranslationService, useClass: StorybookTranslationService },
        { provide: AllsitesettingsService, useClass: MockAllsitesettingsService },
        { provide: DistrelecBasesitesService, useClass: MockDistBaseSiteService },
        importProvidersFrom(HttpClientModule),
        importProvidersFrom(CommonModule),
        importProvidersFrom(FontAwesomeModule),
        ProductQuantityService,
      ],
    }),
    moduleMetadata({
      imports: [
        CommonModule,
        QuantitySelectorComponentModule,
        TooltipComponentModule,
        MinOrderQuantityPopupModule,
        I18nModule,
      ],
    }),
  ],
  render: (args: NumericStepperComponent) => ({
    props: {
      ...args,
    },
    template: `
    <div class="row mt-5">
      <div class="col-1">
      </div>
      <div class="col-4 mt-5">
        <app-numeric-stepper ${argsToTemplate(args)}></app-numeric-stepper>
      </div>
    </div>
    `,
  }),
} as Meta;

type NumericStepperStory = StoryObj<NumericStepperComponent>;
const Template: NumericStepperStory = {
  args: {},
};

export const ActiveSelector: NumericStepperStory = {
  ...Template,
  args: {
    productCode: '30158807',
    minimumQuantity: 1,
    quantityStep: 1,
    availabilityData: MOCK_AVAILABILITY_FULLY_STOCKED,
    ids: {
      minusButtonId: 'minusButtonId',
      plusButtonId: 'plusButtonId',
      inputId: 'inputId',
      popupId: 'popupId',
    },
    control: new FormControl(1),
  },
};

export const BlockedProducts: NumericStepperStory = {
  args: {
    isBTR: true,
    maximumQuantity: 25,
    salesStatus: '50',
    // Shaping the stories through args composition.
    // Inherited data coming from the ActiveSelector story.
    ...ActiveSelector.args,
  },
};

export const DisabledSelector: NumericStepperStory = {
  args: {
    disabled: true,
    maximumQuantity: 25,
    // Shaping the stories through args composition.
    // Inherited data coming from the ActiveSelector story.
    ...ActiveSelector.args,
  },
};
