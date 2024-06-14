import { Injectable } from '@angular/core';
import { Occ, OccEndpointsService } from '@spartacus/core';
import { BaseOccUrlProperties } from '@spartacus/core/src/occ/services/occ-endpoints.service';

@Injectable({
  providedIn: 'root',
})
export class EnergyEfficiencyLabelService {
  constructor(private occEndpointsService: OccEndpointsService) {}

  isValid(energyEfficiency: string): boolean {
    // Do not display A++ or similar labels, as they are illegal,
    // and therefore we don't have images for them in distrelecStore/src/app/spartacus/assets/media/energy-labels
    return (
      energyEfficiency &&
      (energyEfficiency === 'A' ||
        energyEfficiency === 'B' ||
        energyEfficiency === 'C' ||
        energyEfficiency === 'D' ||
        energyEfficiency === 'E' ||
        energyEfficiency === 'F' ||
        energyEfficiency === 'G')
    );
  }

  hasValidEnergyEfficiencyLabel(energyEfficiencyJson: string): boolean {
    return this.isValid(this.getEnergyEfficiencyRating(energyEfficiencyJson));
  }

  getEnergyEfficiencyRating(energyEfficiencyJson?: string) {
    if (energyEfficiencyJson) {
      try {
        const data = JSON.parse(energyEfficiencyJson);
        return data.Energyclasses_LOV;
      } catch {
        return null;
      }
    }
    return null;
  }

  getAbsoluteImageUrl(uri: string): string {
    const props: BaseOccUrlProperties = {
      baseUrl: true,
      prefix: false,
      baseSite: false,
    };
    return uri ? this.occEndpointsService.getBaseUrl(props) + uri : null;
  }

  getEnergyEfficiencyLabelImageUrl(eelImageMap): string {
    if (!eelImageMap) {
      return null;
    }

    let image: Occ.Image = null;
    for (const img of eelImageMap?.values()) {
      if (
        (img?.key === 'landscape_large' ||
          img?.key === 'landscape_medium' ||
          img?.key === 'landscape_small' ||
          img?.key === 'portrait_medium' ||
          img?.key === 'portrait_small') &&
        img?.value
      ) {
        image = img.value;
      } else {
        image = null;
      }
    }

    return image?.url;
  }
}
