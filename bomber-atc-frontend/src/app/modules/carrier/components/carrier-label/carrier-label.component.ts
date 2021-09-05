import { Component, Input } from '@angular/core';
import { Carrier } from '../../models/carrier';

@Component({
  selector: 'atc-carrier-label',
  templateUrl: './carrier-label.component.html'
})
export class CarrierLabelComponent {

  @Input()
  carrier: Carrier;

}
