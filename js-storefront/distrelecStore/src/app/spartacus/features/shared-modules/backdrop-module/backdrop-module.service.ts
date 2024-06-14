import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BackDropModuleService {
  overlayClickSignal_: BehaviorSubject<boolean> = new BehaviorSubject(false);
}
