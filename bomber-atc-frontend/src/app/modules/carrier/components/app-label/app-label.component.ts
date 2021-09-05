import { Component, Input } from '@angular/core';
import { Application } from '../../../main/models/application';

@Component({
  selector: 'atc-app-label',
  templateUrl: './app-label.component.html'
})
export class AppLabelComponent {

  @Input()
  app: Application;

}
